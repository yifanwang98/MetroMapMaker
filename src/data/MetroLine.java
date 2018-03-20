package data;

import app.MetroEditor;
import app.MetroWorkspace;
import control.EditController;
import control.EditLineDialog;
import control.EditLineLabelDialog;
import framework.MetroComponent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import transactions.AddStationAction;
import transactions.MoveLineAction;
import transactions.MoveLineEndAction;

/**
 * @author Yifan Wang
 */
public class MetroLine implements MetroComponent {

    private final ArrayList<Station> stations;
    private String name;
    private Color color;
    private double thickness;
    private Line line;
    private final ArrayList<Line> lines;
    private MetroLineType lineType;
    private boolean isCircular;

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public static Label labelToBeChanged1;
    public static Label labelToBeChanged2;

    private final Label labelA;
    private final Label labelB;
    private boolean isLabelLocated;

    private double dragA;
    private double dragB;
    private double dragC;
    private double dragD;
    private double dragE;
    private double dragF;
    private double dragG;
    private double dragH;
    private double dragI;
    private double dragJ;

    private boolean dragged = false;
    private final int LINE_ID;

    public MetroLine(String name, Color color) {
        this.stations = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.name = name;
        this.color = color;
        this.thickness = DesignConstants.DEFAULT_LINE_THICKNESS;
        LINE_ID = MetroLineID.generate();

        this.line = new Line();
        this.lines.add(this.line);
        initLine(this.line);

        this.labelA = new Label(name);
        this.labelB = new Label(name);
        initLabels();
    }

    private MoveLineAction mla;

    private void initLine(Line line) {
        line.setStrokeWidth(thickness);
        line.setStroke(color);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        line.setOnMousePressed(e -> {
            this.dragA = e.getSceneX();
            this.dragB = e.getSceneY();
            this.dragC = line.getStartX();
            this.dragD = line.getStartY();
            this.dragE = line.getEndX();
            this.dragF = line.getEndY();

            for (Station s : stations) {
                s.dragX = s.getCircle().getCenterX();
                s.dragY = s.getCircle().getCenterY();
            }

            dragged = false;
            mla = new MoveLineAction(this, line);
        });

        line.setOnMouseDragged(e -> {
            dragged = true;
            double scale = EditController.getScale();
            double deltaA = (e.getSceneX() - dragA) / scale;
            double deltaB = (e.getSceneY() - dragB) / scale;

            try {
                line.setStartX(deltaA + dragC);
                line.setStartY(deltaB + dragD);
            } catch (Exception ex) {

            }
            try {
                line.setEndX(deltaA + dragE);
                line.setEndY(deltaB + dragF);
            } catch (Exception ex) {

            }

            // Station
            for (Station s : stations) {
                Line l = lines.get(s.getLineNumber());
                double x = Math.abs(l.getEndX() - l.getStartX());
                double y = Math.abs(l.getEndY() - l.getStartY());
                if (x >= y) {
                    // MetroLineType.HORIZONTAL;
                    s.getCircle().setCenterX(l.getStartX() + (l.getEndX() - l.getStartX()) / s.lambda);
                    s.getCircle().setCenterY(l.getStartY() + (l.getEndY() - l.getStartY()) / s.lambda);
                } else {
                    // MetroLineType.VERTICAL;
                    s.getCircle().setCenterX(l.getStartX() + (l.getEndX() - l.getStartX()) / s.lambda);
                    s.getCircle().setCenterY(l.getStartY() + (l.getEndY() - l.getStartY()) / s.lambda);
                }
                s.relocateLabel();
            }

            relocateLabels();
        });

        line.setOnMouseReleased(e -> {
            if (dragged) {
                //MetroFile.markModified();
                mla.setNewValues(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                MetroData.addTransaction(mla);
            }
        });

        line.setOnMouseEntered(e -> {
            if (MetroData.mode == Mode.ADD_STATION) {
                line.setCursor(Cursor.HAND);
            } else {
                line.setCursor(Cursor.MOVE);
            }
        }
        );

        line.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                EditLineDialog.show(this);

            } else if (e.getClickCount() == 1
                    && MetroData.mode == Mode.ADD_STATION) {
                MetroData.clickOnLine = true;
                this.setLineType(line);
                final Station station;
                if (this.lineType == MetroLineType.HORIZONTAL) {
                    double x = e.getX();
                    double lam = (x - line.getStartX()) / (line.getEndX() - line.getStartX());
                    double y = line.getStartY() + lam * (line.getEndY() - line.getStartY());
                    station = new Station(x, y);
                    station.lambda = 1.0 / lam;
                } else {
                    double y = e.getY();
                    double lam = (y - line.getStartY()) / (line.getEndY() - line.getStartY());
                    double x = line.getStartX() + lam * (line.getEndX() - line.getStartX());
                    station = new Station(x, y);
                    station.lambda = 1.0 / lam;
                }

                station.addParent(this);
                station.relocateLabel();
                station.setLineNumber(lines.indexOf(line));
                final AddStationAction asa = new AddStationAction(station);
                MetroData.addTransaction(asa);
                if (MetroData.getSelectedLine().name.equals(this.name)) {
                    MetroData.setSelectedStation(station);
                }
                sortStations();

                MetroEditor.refresh();
                MetroFile.markModified();
                MetroWorkspace.refreshTopToolbar();

            } else if (e.isShiftDown()) {
                MetroData.resetJTPS();

                setLineType(line);

                final double eX = e.getX();
                final double eY = e.getY();
                final int index = lines.indexOf(line);
                final double lineStartX = line.getStartX();
                final double lineStartY = line.getStartY();
                final double lineEndX = line.getEndX();
                final double lineEndY = line.getEndY();

                if (this.lineType == MetroLineType.HORIZONTAL) {
                    final Line temp = new Line(eX, eY - 25, lineEndX, lineEndY);

                    line.setEndX(eX);
                    line.setEndY(eY - 25);

                    if (index + 1 != lines.size()) {
                        final Line next = lines.get(index + 1);
                        next.setStartX(lineEndX);
                        next.setStartY(lineEndY);
                        line.endXProperty().unbindBidirectional(next.startXProperty());
                        line.endYProperty().unbindBidirectional(next.startYProperty());
                        temp.endXProperty().bindBidirectional(next.startXProperty());
                        temp.endYProperty().bindBidirectional(next.startYProperty());
                    } else {
                        if (this.isCircular) {
                            final Line next = lines.get(0);
                            line.endXProperty().unbindBidirectional(next.startXProperty());
                            line.endYProperty().unbindBidirectional(next.startYProperty());
                            temp.endXProperty().bindBidirectional(next.startXProperty());
                            temp.endYProperty().bindBidirectional(next.startYProperty());
                        }
                    }

                    line.endXProperty().bindBidirectional(temp.startXProperty());
                    line.endYProperty().bindBidirectional(temp.startYProperty());
                    lines.add(index + 1, temp);
                    initLine(temp);

                    if (MetroData.hasBackgroundImage()) {
                        MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, temp);
                    } else {
                        MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, temp);
                    }
                    // Stations
                    final double lambdaThreshold = (lineEndX - lineStartX) / (eX - lineStartX);
                    for (int i = 0; i < this.stations.size(); i++) {
                        final Station s = this.stations.get(i);
                        if (s.getLineNumber() == index) {
                            final Circle c = s.getCircle();
                            if (s.lambda >= lambdaThreshold) {
                                c.setCenterX(line.getStartX() + (line.getEndX() - line.getStartX()) / s.lambda);
                                c.setCenterY(line.getStartY() + (line.getEndY() - line.getStartY()) / s.lambda);
                            } else {
                                s.setLineNumber(s.getLineNumber() + 1);
                                //s.lambda = lambdaThreshold - s.lambda;
                                c.setCenterX(temp.getStartX() + (temp.getEndX() - temp.getStartX()) / s.lambda);
                                c.setCenterY(temp.getStartY() + (temp.getEndY() - temp.getStartY()) / s.lambda);
                            }
                        } else if (s.getLineNumber() > index) {
                            s.setLineNumber(s.getLineNumber() + 1);
                        }
                        s.relocateLabel();
                    }

                } else { // MetroLineType.VERTICAL
                    final Line temp = new Line(eX + 25, eY, lineEndX, lineEndY);

                    line.setEndX(eX + 25);
                    line.setEndY(eY);

                    if (index + 1 != lines.size()) {
                        final Line next = lines.get(index + 1);
                        next.setStartX(lineEndX);
                        next.setStartY(lineEndY);
                        line.endXProperty().unbindBidirectional(next.startXProperty());
                        line.endYProperty().unbindBidirectional(next.startYProperty());
                        temp.endXProperty().bindBidirectional(next.startXProperty());
                        temp.endYProperty().bindBidirectional(next.startYProperty());
                    } else {
                        if (this.isCircular) {
                            final Line next = lines.get(0);
                            line.endXProperty().unbindBidirectional(next.startXProperty());
                            line.endYProperty().unbindBidirectional(next.startYProperty());
                            temp.endXProperty().bindBidirectional(next.startXProperty());
                            temp.endYProperty().bindBidirectional(next.startYProperty());
                        }
                    }

                    line.endXProperty().bindBidirectional(temp.startXProperty());
                    line.endYProperty().bindBidirectional(temp.startYProperty());
                    lines.add(index + 1, temp);
                    initLine(temp);
                    if (MetroData.hasBackgroundImage()) {
                        MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, temp);
                    } else {
                        MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, temp);
                    }
                    // Stations
                    final double lambdaThreshold = (lineEndY - lineStartY) / (eY - lineStartY);
                    for (int i = 0; i < this.stations.size(); i++) {
                        final Station s = this.stations.get(i);
                        if (s.getLineNumber() == index) {
                            final Circle c = s.getCircle();
                            if (s.lambda >= lambdaThreshold) {
                                c.setCenterX(line.getStartX() + (line.getEndX() - line.getStartX()) / s.lambda);
                                c.setCenterY(line.getStartY() + (line.getEndY() - line.getStartY()) / s.lambda);
                            } else {
                                s.setLineNumber(s.getLineNumber() + 1);
                                s.lambda = s.lambda - lambdaThreshold;
                                c.setCenterX(temp.getStartX() + (temp.getEndX() - temp.getStartX()) / s.lambda);
                                c.setCenterY(temp.getStartY() + (temp.getEndY() - temp.getStartY()) / s.lambda);
                            }
                        } else if (s.getLineNumber() > index) {
                            s.setLineNumber(s.getLineNumber() + 1);
                        }
                        s.relocateLabel();
                    }
                }

            }
        });
    }

    public void relocateStations() {
        for (Station s : stations) {
            Line l = lines.get(s.getLineNumber());
            double x = Math.abs(l.getEndX() - l.getStartX());
            double y = Math.abs(l.getEndY() - l.getStartY());
            if (x >= y) {
                // MetroLineType.HORIZONTAL;
                s.getCircle().setCenterX(l.getStartX() + (l.getEndX() - l.getStartX()) / s.lambda);
                s.getCircle().setCenterY(l.getStartY() + (l.getEndY() - l.getStartY()) / s.lambda);
            } else {
                // MetroLineType.VERTICAL;
                s.getCircle().setCenterX(l.getStartX() + (l.getEndX() - l.getStartX()) / s.lambda);
                s.getCircle().setCenterY(l.getStartY() + (l.getEndY() - l.getStartY()) / s.lambda);
            }
            s.relocateLabel();
        }
        relocateLabels();
    }

    private void relocateLabels() {
        // Label A
        final Line firstLine = lines.get(0);
        this.setLineType(firstLine);
        if (this.lineType == MetroLineType.HORIZONTAL) {
            final double x = firstLine.getStartX();
            if (x <= firstLine.getEndX()) {
                this.labelA.setLayoutX(x - name.length() * 8.5);
            } else {
                this.labelA.setLayoutX(x + 5.0);
            }
            this.labelA.setLayoutY(firstLine.getStartY() + 2.0);

        } else {
            final double y = firstLine.getStartY();
            if (y <= firstLine.getEndY()) {
                this.labelA.setLayoutY(y - 25.0);
            } else {
                this.labelA.setLayoutY(y + 10.0);
            }
            this.labelA.setLayoutX(firstLine.getStartX() - name.length() * 4.5);
        }

        // Label B
        final Line lastLine = lines.get(lines.size() - 1);
        this.setLineType(lastLine);
        if (this.lineType == MetroLineType.HORIZONTAL) {
            final double x = lastLine.getEndX();
            if (x >= lastLine.getStartX()) {
                this.labelB.setLayoutX(x + 5.0);
            } else {
                this.labelB.setLayoutX(x - name.length() * 8.5);
            }
            this.labelB.setLayoutY(lastLine.getEndY() + 2);

        } else {
            final double y = lastLine.getEndY();
            if (y >= lastLine.getStartY()) {
                this.labelB.setLayoutY(y + 10.0);
            } else {
                this.labelB.setLayoutY(y - 25.0);
            }
            this.labelB.setLayoutX(lastLine.getEndX() - name.length() * 4.5);

        }
    }

    private int stationIndex;
    private MoveLineEndAction mlea;

    private void initLabels() {
        this.labelA.setFont(DesignConstants.LINE_LABEL_FONT);
        this.labelB.setFont(DesignConstants.LINE_LABEL_FONT);
        this.isLabelLocated = false;

        // Label A
        this.labelA.setOnMousePressed(e -> {
            this.line = lines.get(0);

            this.dragA = e.getSceneX();
            this.dragB = e.getSceneY();
            this.dragC = line.getStartX();
            this.dragD = line.getStartY();

            this.dragG = labelA.getLayoutX();
            this.dragH = labelA.getLayoutY();

            this.dragged = false;

            this.mlea = new MoveLineEndAction(this, line);

            //double lineLambda = getTangent();
            final double lineLambda = getTangent();
            //final String lineLambdaS = "" + lineLambda;

            for (stationIndex = 0; stationIndex < stations.size(); stationIndex++) {
                final Station s = stations.get(stationIndex);

                final double sX = s.getCircle().getCenterX();
                if ((sX < dragC && sX < line.getEndX()) || (sX > dragC && sX > line.getEndX())) {
                    break;
                }
                final double sY = s.getCircle().getCenterY();
                if ((sY < dragD && sY < line.getEndY()) || (sY > dragD && sY > line.getEndY())) {
                    break;
                }
                //System.out.println(stationIndex + "\t" + s.toString());
                // Calculate lambda
                double stationLambda = s.getCircle().getCenterX() - dragC;
                stationLambda /= s.getCircle().getCenterY() - dragD;

                //String stationLambdaS = "" + stationLambda;
                if (checkLambdaEquality(stationLambda, lineLambda)) {
                    stationLambda = line.getEndX() - dragC;
                    stationLambda /= s.getCircle().getCenterX() - dragC;
                    String ss = "" + stationLambda;
                    if (ss.equals("NaN")) {
                        stationLambda = line.getEndY() - dragD;
                        stationLambda /= s.getCircle().getCenterY() - dragD;
                    }
                    s.lambda = stationLambda;
                } else {
                    break;
                }
            }
        });

        this.labelA.setOnMouseDragged(e -> {
            dragged = true;
            double scale = EditController.getScale();
            double deltaA = (e.getSceneX() - dragA) / scale;
            double deltaB = (e.getSceneY() - dragB) / scale;

            double sX = deltaA + dragC;
            double sY = deltaB + dragD;
            try {
                this.line.setStartX(sX);
                this.line.setStartY(sY);
            } catch (Exception ex) {

            }

            this.labelA.setLayoutX(deltaA + dragG);
            this.labelA.setLayoutY(deltaB + dragH);

            // Stations
            double lineEndX = line.getEndX();
            double lineEndY = line.getEndY();
            for (int i = 0; i < this.stationIndex; i++) {
                final Station s = this.stations.get(i);
                final Circle c = s.getCircle();
                c.setCenterX(sX + (lineEndX - sX) / s.lambda);
                c.setCenterY(sY + (lineEndY - sY) / s.lambda);
                s.relocateLabel();
            }
        });

        this.labelA.setOnMouseReleased(e -> {
            if (dragged) {
                mlea.setNewValues(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                MetroData.addTransaction(mlea);
            }
        });

        this.labelA.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                labelToBeChanged1 = this.labelA;
                labelToBeChanged2 = this.labelB;
                EditLineLabelDialog.show();
            }
        });

        // Label B
        this.labelB.setOnMousePressed(e -> {
            this.line = lines.get(lines.size() - 1);

            this.dragA = e.getSceneX();
            this.dragB = e.getSceneY();
            this.dragE = line.getEndX();
            this.dragF = line.getEndY();

            this.dragI = labelB.getLayoutX();
            this.dragJ = labelB.getLayoutY();

            dragged = false;

            this.mlea = new MoveLineEndAction(this, line);

            final double lineLambda = getTangent();
            final String lineLambdaS = "" + lineLambda;
            for (stationIndex = stations.size() - 1; stationIndex >= 0; stationIndex--) {
                final Station s = stations.get(stationIndex);

                final double sX = s.getCircle().getCenterX();
                if ((sX < dragE && sX < line.getStartX()) || (sX > dragE && sX > line.getStartX())) {
                    break;
                }
                final double sY = s.getCircle().getCenterY();
                if ((sY < dragF && sY < line.getStartY()) || (sY > dragF && sY > line.getStartY())) {
                    break;
                }

                // Calculate lambda
                double stationLambda = dragE - s.getCircle().getCenterX();
                stationLambda /= dragF - s.getCircle().getCenterY();

                String stationLambdaS = "" + stationLambda;
                if (checkLambdaEquality(stationLambda, lineLambda)) {
                    stationLambda = dragE - line.getStartX();
                    stationLambda /= s.getCircle().getCenterX() - line.getStartX();
                    String ss = "" + stationLambda;
                    if (ss.equals("NaN")) {
                        stationLambda = dragF - line.getStartY();
                        stationLambda /= s.getCircle().getCenterY() - line.getStartY();
                    }
                    s.lambda = stationLambda;
                } else {
                    break;
                }
            }
        });

        this.labelB.setOnMouseDragged(e -> {
            dragged = true;
            double scale = EditController.getScale();
            double deltaA = (e.getSceneX() - dragA) / scale;
            double deltaB = (e.getSceneY() - dragB) / scale;

            double sX = deltaA + dragE;
            double sY = deltaB + dragF;
            try {
                this.line.setEndX(sX);
                this.line.setEndY(sY);
            } catch (Exception ex) {

            }

            this.labelB.setLayoutX(deltaA + dragI);
            this.labelB.setLayoutY(deltaB + dragJ);

            // Stations
            double lineStartX = line.getStartX();
            double lineStartY = line.getStartY();
            for (int i = stations.size() - 1; i > stationIndex; i--) {
                final Station s = this.stations.get(i);
                final Circle c = s.getCircle();
                c.setCenterX(lineStartX + (sX - lineStartX) / s.lambda);
                c.setCenterY(lineStartY + (sY - lineStartY) / s.lambda);
                s.relocateLabel();
            }
        });

        this.labelB.setOnMouseReleased(e -> {
            if (dragged) {
                mlea.setNewValues(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                MetroData.addTransaction(mlea);
            }
        });

        this.labelB.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                labelToBeChanged1 = this.labelA;
                labelToBeChanged2 = this.labelB;
                EditLineLabelDialog.show();
            }
        });
    }

    public void setStartNEnd(double a, double b, double c, double d) {
        try {
            this.line.setStartX(a);
            this.line.setStartY(b);
            this.line.setEndX(c);
            this.line.setEndY(d);
        } catch (Exception ex) {

        }

        relocateLabels();
    }

    public boolean addStation(Station station) {
        stations.add(station);
        MetroData.stations.add(station);
        station.addParent(this);
        station.setLineNumber(0);
        MetroData.setSelectedStation(station);
        final Line l = lines.get(0);
        double random = Math.random() * 10.0 + 1;
        station.getCircle().setCenterX(l.getStartX() + (l.getEndX() - l.getStartX()) / random);
        station.getCircle().setCenterY(l.getStartY() + (l.getEndY() - l.getStartY()) / random);
        station.lambda = random;
        sortStations();
        return true;
    }

    public boolean addEmptyStation(Station station) {
        stations.add(station);
        station.addParent(this);
        MetroData.setSelectedStation(station);
        sortStations();
        return true;
    }

    private void autoAlign() {

    }

    public boolean addExsistingStation(Station station) {
        if (stations.contains(station)) {
            return false;
        }
        this.stations.add(station);
        try {
            sortStations();
        } catch (Exception ex) {
        }
        return true;
    }

    public Station getFirstStation() {
        if (isEmpty()) {
            return null;
        }
        return stations.get(0);
    }

    public boolean removeStation(Station station) {
        return stations.remove(station);
    }

    public int getSize() {
        return this.stations.size();
    }

    public int getSectionSize() {
        return this.lines.size();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null) {
            return;
        }
        this.name = name;
        //this.labelA.setLayoutX(this.line.getStartX() - name.length() * 8.5);
        this.labelA.setText(name);
        this.labelB.setText(name);
        this.relocateLabels();
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
        for (Line l : lines) {
            l.setStroke(color);
        }
    }

    public double getThickness() {
        return this.thickness;
    }

    public void setThickness(double newThickness) {
        this.thickness = newThickness;
        for (Line l : lines) {
            l.setStrokeWidth(thickness);
        }
    }

    public Line getLine() {
        return this.line;
    }

    public ArrayList<Line> getLines() {
        return this.lines;
    }

    public Label getLabelA() {
        return this.labelA;
    }

    public Label getLabelB() {
        return this.labelB;
    }

    public double getLineStartX() {
        return this.line.getStartX();
    }

    public double getLineStartY() {
        return this.line.getStartY();
    }

    public double getLineEndX() {
        return this.line.getEndX();
    }

    public double getLineEndY() {
        return this.line.getEndY();
    }

    public boolean isCircular() {
        return this.isCircular;
    }

    public void setCircular() {
        if (this.isCircular) {
            return;
        }

        // Get endpoints of the connecting line
        final double sX = this.lines.get(lines.size() - 1).getEndX();
        final double sY = this.lines.get(lines.size() - 1).getEndY();
        final double eX = this.lines.get(0).getStartX();
        final double eY = this.lines.get(0).getStartY();

        // Create the connecting line
        final Line connectingLine = new Line(sX, sY, eX, eY);

        // Bind
        this.lines.get(0).startXProperty().bindBidirectional(connectingLine.endXProperty());
        this.lines.get(0).startYProperty().bindBidirectional(connectingLine.endYProperty());
        this.lines.get(lines.size() - 1).endXProperty().bindBidirectional(connectingLine.startXProperty());
        this.lines.get(lines.size() - 1).endYProperty().bindBidirectional(connectingLine.startYProperty());
        initLine(connectingLine);

        // Labels
        this.labelA.setVisible(false);
        //this.labelB.setVisible(false);
        this.labelA.setDisable(true);
        //this.labelB.setDisable(true);

        // Add to lines
        this.lines.add(connectingLine);
        MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, connectingLine);
        // Set isCircular
        this.isCircular = true;
    }

    public void setUncircular() {
        if (!this.isCircular) {
            return;
        }

        // Labels
        this.labelA.setDisable(false);
        this.labelB.setDisable(false);
        this.labelA.setVisible(true);
        this.labelB.setVisible(true);

        // Unbind
        this.lines.get(0).startXProperty().unbindBidirectional(this.lines.get(lines.size() - 1).endXProperty());
        this.lines.get(0).startYProperty().unbindBidirectional(this.lines.get(lines.size() - 1).endYProperty());

        // Set isCircular
        this.isCircular = false;
    }

    public void setHighlighted() {

    }

    public void setUnhighlighted() {

    }

    public void snapToGrid() {

    }

    private void setLineType(Line line) {
        double x = Math.abs(line.getEndX() - line.getStartX());
        double y = Math.abs(line.getEndY() - line.getStartY());
        if (x >= y) {
            this.lineType = MetroLineType.HORIZONTAL;
        } else {
            this.lineType = MetroLineType.VERTICAL;
        }
    }

    public ArrayList<Station> getStation() {
        return this.stations;
    }

    public ObservableList<Station> getStationList() {
        return FXCollections.observableList(this.stations);
    }

    public double[] getDragProperties(int lineIndex) {
        final Line currentLine = this.lines.get(lineIndex);
        final double[] dragProperties = new double[6];

        // MetroLineType[0] -> 0: Horizontal; 1: Vertical
        double x = Math.abs(currentLine.getEndX() - currentLine.getStartX());
        double y = Math.abs(currentLine.getEndY() - currentLine.getStartY());
        dragProperties[0] = (x >= y) ? 0.0 : 1.0;

        // isLineEnd[1] -> 0: not; 1: front; 2: end; 3: only line
        final int size = lines.size();
        if (size == 1) {
            dragProperties[1] = 3.0;
        } else if (lineIndex == size - 1) {
            dragProperties[1] = 2.0;
        } else if (lineIndex == 0) {
            dragProperties[1] = 1.0;
        } else {
            dragProperties[1] = 0.0;
        }

        // StartPoint[2],   EndPoint[3],    interception[4],    Lambda[5]
        if (dragProperties[0] == 0.0) {
            dragProperties[2] = currentLine.getStartX();
            dragProperties[3] = currentLine.getEndX();
            dragProperties[4] = currentLine.getStartY();
            dragProperties[5] = (currentLine.getEndY() - currentLine.getStartY())
                    / (currentLine.getEndX() - currentLine.getStartX());
        } else {
            dragProperties[2] = currentLine.getStartY();
            dragProperties[3] = currentLine.getEndY();
            dragProperties[4] = currentLine.getStartX();
            dragProperties[5] = (currentLine.getEndX() - currentLine.getStartX())
                    / (currentLine.getEndY() - currentLine.getStartY());
        }

        // Return
        return dragProperties;
    }

    public boolean isIntersecting(double x, double y) {
        for (Line l : lines) {
            final double a = Math.abs(l.getEndX() - l.getStartX());
            final double b = Math.abs(l.getEndY() - l.getStartY());

            if (a >= b) { // MetroLineType.HORIZONTAL;
                if (l.getEndX() - l.getStartX() > 0) {
                    if (x > l.getEndX() || x < l.getStartX()) {
                        continue;
                    }
                } else {
                    if (x < l.getEndX() || x > l.getStartX()) {
                        continue;
                    }
                }
                final double tangent = (l.getEndY() - l.getStartY())
                        / (l.getEndX() - l.getStartX());
                double lineY = tangent * (x - l.getStartX()) + l.getStartY();

                DecimalFormat aDF = new DecimalFormat("#");
                String aC = aDF.format(lineY);
                String bC = aDF.format(y);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineY - 0.5);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineY + 0.5);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineY - 1);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineY + 1);
                if (aC.equals(bC)) {
                    return true;
                }

            } else { // MetroLineType.VERTICAL;
                if (l.getEndY() - l.getStartY() > 0) {
                    if (y > l.getEndY() || y < l.getStartY()) {
                        continue;
                    }
                } else {
                    if (y < l.getEndY() || y > l.getStartY()) {
                        continue;
                    }
                }

                final double tangent = (l.getEndX() - l.getStartX()) / (l.getEndY() - l.getStartY());
                double lineX = tangent * (y - l.getStartY()) + l.getStartX();

                DecimalFormat aDF = new DecimalFormat("#");
                String aC = aDF.format(lineX);
                String bC = aDF.format(x);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineX - 0.5);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineX + 0.5);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineX - 1);
                if (aC.equals(bC)) {
                    return true;
                }
                aC = aDF.format(lineX + 1);
                if (aC.equals(bC)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getInorderLineNames() {
        if (this.stations.isEmpty()) {
            return "[Empty]";
        }

        String str = "";
        for (int i = 0; i < stations.size(); i++) {
            str += i + " -\t" + stations.get(i).getName() + "\n";
        }

        return str;
    }

    public boolean isEmpty() {
        return this.stations.isEmpty();
    }

    public double getTangent() {
        return (line.getEndX() - line.getStartX()) / (line.getEndY() - line.getStartY());
    }

    public void remove() {
        ObservableList<Node> children = MetroWorkspace.getCanvas().getChildren();
        children.remove(this.labelA);
        children.remove(this.labelB);
        for (Line l : lines) {
            children.remove(l);
        }
        for (Station s : stations) {
            s.remove();
        }
    }

    public void putBackOnCanvas() {
        ObservableList<Node> children = MetroWorkspace.getCanvas().getChildren();
        children.add(this.labelA);
        children.add(this.labelB);
        for (Line l : lines) {
            children.add(DesignConstants.INDEX, l);
        }
        for (Station s : stations) {
            s.putBackOnCanvas();
        }
    }

    public int isValidStationName(String name) {
        int i = 0;
        for (Station s : stations) {
            if (s.getName().equals(name)) {
                i++;
            }
        }
        return i;
    }

    public void setLambdas() {
        for (Station s : stations) {
            Line l = lines.get(s.getLineNumber());
            double lambda = l.getEndX() - l.getStartX();
            lambda /= s.getCircle().getCenterX() - l.getStartX();
            s.lambda = lambda;
        }
    }

    private boolean checkLambdaEquality(double a, double b) {
        DecimalFormat aDF = new DecimalFormat("#.####");
        String aC = aDF.format(a);
        String bC = aDF.format(b);
        return aC.equals(bC);
    }

    public void sortStations() {
        Collections.sort(stations);
        /*
        final Queue<Station> temp = new LinkedList<>();
        for(Station s : this.stations){
            if(s.isIntersection()){
                temp.add(s);
                this.stations.remove(s);
            }
        }*/
    }

    public void assignNeighbours() {
        for (int i = 0; i < this.stations.size(); i++) {
            if (i != 0) {
                this.stations.get(i).addNeighbour(this.stations.get(i - 1));
            } else {
                if (this.isCircular && this.stations.size() != 1) {
                    this.stations.get(i).addNeighbour(this.stations.get(this.stations.size() - 1));
                }
            }
            if (i != this.stations.size() - 1) {
                this.stations.get(i).addNeighbour(this.stations.get(i + 1));
            } else {
                if (this.isCircular && this.stations.size() != 1) {
                    this.stations.get(i).addNeighbour(this.stations.get(0));
                }
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MetroLine) {
            return ((MetroLine) o).LINE_ID == this.LINE_ID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + this.LINE_ID;
        return hash;
    }

    static final String JSON_RED = "red";
    static final String JSON_GREEN = "green";
    static final String JSON_BLUE = "blue";
    static final String JSON_ALPHA = "alpha";
    static final String JSON_COLOR = "color";
    static final String JSON_STATION_NAME = "station_names";
    static final String JSON_NAME = "name";
    static final String JSON_CIRCULAR = "circular";

    static final String JSON_START_X = "startX";
    static final String JSON_START_Y = "startY";
    static final String JSON_END_X = "endX";
    static final String JSON_END_Y = "endY";
    static final String JSON_THICKNESS = "thickness";
    static final String JSON_LINES = "lines";
    static final String JSON_STATIONS = "stations";
    static final String JSON_LABEL_COLOR = "label_color";

    @Override
    public JsonObject save() {
        // Line
        JsonArrayBuilder lineArr = Json.createArrayBuilder();
        for (Line line1 : this.lines) {
            JsonObject lineObject = Json.createObjectBuilder()
                    .add(JSON_START_X, "" + line1.getStartX())
                    .add(JSON_START_Y, "" + line1.getStartY())
                    .add(JSON_END_X, "" + line1.getEndX())
                    .add(JSON_END_Y, "" + line1.getEndY())
                    .build();
            lineArr.add(lineObject);
        }
        JsonArray lArr = lineArr.build();

        // Stations
        /*JsonArrayBuilder stationNameArr = Json.createArrayBuilder();
        for (int i = 0; i < this.stations.size(); i++) {
            //if (!this.stations.get(i).isVisited()) {
            stationNameArr.add(this.stations.get(i).save());
            //    this.stations.get(i).setVisited();
            //}
        }
        JsonArray sArr = stationNameArr.build();*/
        // In all
        JsonObject jsonObject = Json.createObjectBuilder()
                .add(JSON_NAME, this.name)
                .add(JSON_CIRCULAR, this.isCircular)
                .add(JSON_COLOR, this.color.toString())
                .add(JSON_THICKNESS, "" + this.thickness)
                .add(JSON_LABEL_COLOR, this.labelA.getTextFill().toString())
                .add(JSON_LINES, lArr)
                //.add(JSON_STATIONS, sArr)
                .build();
        return jsonObject;
    }

    @Override
    public void load(JsonObject jsonObject) {
        try {
            this.name = jsonObject.getString(JSON_NAME);
            this.isCircular = jsonObject.getBoolean(JSON_CIRCULAR, false);

            // Lines
            this.lines.clear();
            JsonArray lineArray = jsonObject.getJsonArray(JSON_LINES);
            for (int i = 0; i < lineArray.size(); i++) {
                JsonObject l = lineArray.getJsonObject(i);
                final Line loadedLine = new Line();
                loadedLine.setStartX(Double.parseDouble(l.getString(JSON_START_X)));
                loadedLine.setStartY(Double.parseDouble(l.getString(JSON_START_Y)));
                loadedLine.setEndX(Double.parseDouble(l.getString(JSON_END_X)));
                loadedLine.setEndY(Double.parseDouble(l.getString(JSON_END_Y)));
                initLine(loadedLine);
                lines.add(loadedLine);
            }

            for (int i = 0; i < lines.size() - 1; i++) {
                lines.get(i).endXProperty().bindBidirectional(lines.get(i + 1).startXProperty());
                lines.get(i).endYProperty().bindBidirectional(lines.get(i + 1).startYProperty());
            }

            if (this.isCircular) {
                lines.get(lines.size() - 1).endXProperty()
                        .bindBidirectional(lines.get(0).startXProperty());
                lines.get(lines.size() - 1).endYProperty()
                        .bindBidirectional(lines.get(0).startYProperty());
                this.labelA.setVisible(false);
            }

            for (Line l : lines) {
                MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, l); // Add to canvas
            }

            // Labels
            this.setName(name);
            Color labelC = Color.web(jsonObject.getString(JSON_LABEL_COLOR));
            this.labelA.setTextFill(labelC);
            this.labelB.setTextFill(labelC);
            try {
                MetroWorkspace.getCanvas().getChildren().add(labelA);
            } catch (Exception ex) {

            }
            try {
                MetroWorkspace.getCanvas().getChildren().add(labelB);
            } catch (Exception ex) {

            }

            // Thickness && Color
            this.setThickness(Double.parseDouble(jsonObject.getString(JSON_THICKNESS)));
            this.setColor(Color.web(jsonObject.getString(JSON_COLOR)));

        } catch (Exception ex) {

        }
    }

    @Override
    public JsonObject export() {
        // Color
        JsonObject colorJson = Json.createObjectBuilder()
                .add(JSON_RED, color.getRed())
                .add(JSON_GREEN, color.getGreen())
                .add(JSON_BLUE, color.getBlue())
                .add(JSON_ALPHA, 1.0).build();

        // Stations
        JsonArrayBuilder stationNameArr = Json.createArrayBuilder();
        for (int i = 0; i < this.stations.size(); i++) {
            stationNameArr.add(this.stations.get(i).getName());
        }
        JsonArray sArr = stationNameArr.build();

        // All
        JsonObject jsonObject = Json.createObjectBuilder()
                .add(JSON_NAME, this.name)
                .add(JSON_CIRCULAR, this.isCircular)
                .add(JSON_COLOR, colorJson)
                .add(JSON_STATION_NAME, sArr)
                .build();
        return jsonObject;
    }

}

class MetroLineID {

    private static int id = 0;

    public static final int generate() {
        return id++;
    }

}

enum MetroLineType {

    HORIZONTAL,
    VERTICAL;

}
