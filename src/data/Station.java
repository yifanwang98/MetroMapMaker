package data;

import app.MetroEditor;
import app.MetroWorkspace;
import control.EditStationDialog;
import framework.MetroComponent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import transactions.MoveStationAction;

/**
 * @author Yifan Wang
 */
public class Station implements Comparable, MetroComponent {

    private String name;
    private Color color;
    private final ArrayList<Station> neighbors;
    private final HashSet<MetroLine> parents;
    private MetroLine realParent;
    private boolean visited;
    private boolean isIntersection;
    private int lineNumber;
    private LinkedList<Station> path;

    private final Circle circle;
    public double dragA;
    public double dragB;
    public double dragX;
    public double dragY;
    private double dragMin;
    private double dragMax;
    private double ratioX;
    private double ratioY;
    private double mlSumX;
    private double mlSumY;
    private double tangent;
    private boolean dragged;
    public double lambda; // λ

    /**
     * An array of information related to drag a station <code>
     * <dt>[0]</dt>
     * <dd>MetroLineType: 0 for HORIZONTAL, 1 for VERTICAL</dd>
     * <dt>[1]</dt>
     * <dd>isLineEnd: 0 for not, 1 for front, 2 for end, 3 for only line</dd>
     * <dt>[2]</dt>
     * <dd>startPoint(dragMin)</dd>
     * <dt>[3]</dt>
     * <dd>endPoint(dragMax)</dd>
     * <dt>[4]</dt>
     * <dd>y-intercept or x-intercept</dd>
     * <dt>[5]</dt>
     * <dd>lambda, tangent of the line</dd>
     *
     * </code>
     */
    private double[] dragProperties;

    private final Label label;

    private final int stationID;

    private MoveStationAction msa;

    public Station() {
        stationID = StationID.generate();
        this.neighbors = new ArrayList<>();
        this.parents = new HashSet<>();
        this.visited = false;
        this.name = "S" + stationID;
        this.color = Color.WHITE;
        this.circle = new Circle(10.0);
        this.circle.setFill(color);
        this.circle.setStroke(Color.BURLYWOOD);
        this.circle.setStrokeWidth(2.0);
        this.label = new Label(name);
        this.path = new LinkedList<>();

        //relocateLabel();
        this.circle.setOnMousePressed(e -> {
            Iterator<MetroLine> a = this.parents.iterator();
            MetroLine ml = a.next();

            this.dragA = e.getSceneX();
            this.dragB = e.getSceneY();

            this.dragX = this.circle.getCenterX();
            this.dragY = this.circle.getCenterY();

            this.dragProperties = realParent.getDragProperties(lineNumber);
            this.dragged = false;

            msa = new MoveStationAction(this);
        });

        this.circle.setOnMouseDragged(e -> {
            if (dragProperties[0] == 0.0) { // HORIZONTAL
                double x = e.getSceneX() - dragA + dragX;

                // Check Validity of X coordinate
                switch ((int) dragProperties[1]) {
                    case 3: // Only Line
                        if (dragProperties[2] > dragProperties[3]) {
                            if (x > dragProperties[2] || x < dragProperties[3]) {
                                return;
                            }
                        } else {
                            if (x < dragProperties[2] || x > dragProperties[3]) {
                                return;
                            }
                        }
                        break;
                    case 2: // End
                        if (dragProperties[2] > dragProperties[3] && x > dragProperties[2]) {
                            return;
                        }
                        if (dragProperties[2] <= dragProperties[3] && x > dragProperties[3]) {
                            return;
                        }
                        break;
                    case 1: // Front
                        if (dragProperties[2] > dragProperties[3] && x < dragProperties[3]) {
                            return;
                        }
                        if (dragProperties[2] <= dragProperties[3] && x < dragProperties[2]) {
                            return;
                        }
                        break;
                    default:
                        break;
                }

                // Move
                double y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                this.circle.setCenterX(x);
                this.circle.setCenterY(y);

                final boolean out;
                if (dragProperties[3] >= dragProperties[2]) {
                    out = (x >= dragProperties[3]) || isCoordinateEqual(x, dragProperties[3]);
                } else {
                    out = (x <= dragProperties[3]) || isCoordinateEqual(x, dragProperties[3]);
                }

                final boolean anotherOut;
                if (dragProperties[3] >= dragProperties[2]) {
                    anotherOut = (x <= dragProperties[2]) || isCoordinateEqual(x, dragProperties[2]);
                } else {
                    anotherOut = (x >= dragProperties[2]) || isCoordinateEqual(x, dragProperties[2]);
                }

                // If is front or middle (move to end)
                if (out
                        && (dragProperties[1] == 1.0 || dragProperties[1] == 0.0)) {
                    this.lineNumber++;
                    Iterator<MetroLine> a = this.parents.iterator();
                    MetroLine ml = a.next();
                    this.dragProperties = realParent.getDragProperties(lineNumber);

                    /*x = dragProperties[2] + 1;
                    y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];*/
                    //
                    if (dragProperties[0] == 0.0) {// Next Line is Horizontal
                        if (dragProperties[2] < dragProperties[3]) {
                            x = dragProperties[2] + 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        } else {
                            x = dragProperties[2] - 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        }
                    } else { // Vertical
                        if (dragProperties[2] < dragProperties[3]) {
                            y = dragProperties[2] + 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        } else {
                            y = dragProperties[2] - 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        }
                    }
                    this.circle.setCenterX(x);
                    this.circle.setCenterY(y);

                    this.dragA = e.getSceneX();
                    this.dragB = e.getSceneY();

                    this.dragX = this.circle.getCenterX();
                    this.dragY = this.circle.getCenterY();

                } else if (anotherOut
                        && dragProperties[1] != 1.0 && dragProperties[1] != 3.0) { //== 2.0) { // at End, move to front
                    this.lineNumber--;
                    Iterator<MetroLine> a = this.parents.iterator();
                    MetroLine ml = a.next();
                    this.dragProperties = realParent.getDragProperties(lineNumber);

                    /*x = dragProperties[3] - 1;
                    y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];*/
                    if (dragProperties[0] == 0.0) {// Next Line is Horizontal
                        if (dragProperties[2] < dragProperties[3]) {
                            x = dragProperties[3] - 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        } else {
                            x = dragProperties[3] + 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        }
                    } else { // Vertical
                        if (dragProperties[2] < dragProperties[3]) {
                            y = dragProperties[3] - 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        } else {
                            y = dragProperties[3] + 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        }
                    }

                    this.circle.setCenterX(x);
                    this.circle.setCenterY(y);

                    this.dragA = e.getSceneX();
                    this.dragB = e.getSceneY();

                    this.dragX = this.circle.getCenterX();
                    this.dragY = this.circle.getCenterY();
                }

                /*this.label.setLayoutX(this.circle.getCenterX() + 1.0);
                this.label.setLayoutY(this.circle.getCenterY() + this.circle.getRadius() + 2.0);*/
                this.relocateLabel();

            } else { // VERTICAL
                double y = e.getSceneY() - dragB + dragY;

                // Check Validity of Y coordinate
                switch ((int) dragProperties[1]) {
                    case 3: // Only Line
                        if (dragProperties[2] > dragProperties[3]) {
                            if (y > dragProperties[2] || y < dragProperties[3]) {
                                return;
                            }
                        } else {
                            if (y < dragProperties[2] || y > dragProperties[3]) {
                                return;
                            }
                        }
                        break;
                    case 2: // End
                        if (dragProperties[2] > dragProperties[3] && y > dragProperties[2]) {
                            return;
                        }
                        if (dragProperties[2] <= dragProperties[3] && y > dragProperties[3]) {
                            return;
                        }
                        break;
                    case 1: // Front
                        if (dragProperties[2] > dragProperties[3]
                                && y < dragProperties[3]) {
                            return;
                        }
                        if (dragProperties[2] <= dragProperties[3]
                                && y < dragProperties[2]) {
                            return;
                        }
                        break;
                    default:
                        break;
                }

                // Move
                double x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                this.circle.setCenterX(x);
                this.circle.setCenterY(y);

                final boolean out;
                if (dragProperties[3] >= dragProperties[2]) {
                    out = (y >= dragProperties[3]) || isCoordinateEqual(y, dragProperties[3]);
                } else {
                    out = (y <= dragProperties[3]) || isCoordinateEqual(y, dragProperties[3]);
                }

                final boolean anotherOut;
                if (dragProperties[3] >= dragProperties[2]) {
                    anotherOut = (y <= dragProperties[2]) || isCoordinateEqual(y, dragProperties[2]);
                } else {
                    anotherOut = (y >= dragProperties[2]) || isCoordinateEqual(y, dragProperties[2]);
                }

                if (out && (dragProperties[1] == 1.0 || dragProperties[1] == 0.0)) {
                    this.lineNumber++;
                    Iterator<MetroLine> a = this.parents.iterator();
                    MetroLine ml = a.next();
                    this.dragProperties = realParent.getDragProperties(lineNumber);

                    /*y = dragProperties[2] + 1;
                    x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];*/
                    if (dragProperties[0] == 0.0) {// Next Line is Horizontal
                        if (dragProperties[2] < dragProperties[3]) {
                            x = dragProperties[2] + 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        } else {
                            x = dragProperties[2] - 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        }
                    } else { // Vertical
                        if (dragProperties[2] < dragProperties[3]) {
                            y = dragProperties[2] + 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        } else {
                            y = dragProperties[2] - 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        }
                    }

                } else if (anotherOut
                        && dragProperties[1] != 1.0 && dragProperties[1] != 3.0) {//dragProperties[1] == 2.0) {
                    this.lineNumber--;
                    Iterator<MetroLine> a = this.parents.iterator();
                    MetroLine ml = a.next();
                    this.dragProperties = realParent.getDragProperties(lineNumber);

                    /*y = dragProperties[3] - 1;
                    x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];*/
                    if (dragProperties[0] == 0.0) {// Next Line is Horizontal
                        if (dragProperties[2] < dragProperties[3]) {
                            x = dragProperties[3] - 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        } else {
                            x = dragProperties[3] + 1;
                            y = dragProperties[5] * (x - dragProperties[2]) + dragProperties[4];
                        }
                    } else { // Vertical
                        if (dragProperties[2] < dragProperties[3]) {
                            y = dragProperties[3] - 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        } else {
                            y = dragProperties[3] + 1;
                            x = dragProperties[5] * (y - dragProperties[2]) + dragProperties[4];
                        }
                    }

                } else {

                }

                this.circle.setCenterX(x);
                this.circle.setCenterY(y);

                this.dragA = e.getSceneX();
                this.dragB = e.getSceneY();

                this.dragX = this.circle.getCenterX();
                this.dragY = this.circle.getCenterY();

                /*this.label.setLayoutX(this.circle.getCenterX() + this.circle.getRadius() + 2.0);
                this.label.setLayoutY(this.circle.getCenterY() + 1.0);*/
                this.relocateLabel();
            }
            dragged = true;
        }
        );

        this.circle.setOnMouseReleased(e -> {
            if (dragged) {
                realParent.setLambdas();
                msa.setNewValues(this.circle.getCenterX(), this.circle.getCenterY(), lineNumber, lambda);
                MetroData.addTransaction(msa);

                MetroFile.markModified();
                MetroEditor.refresh();
            }
        }
        );

        this.circle.setOnMouseEntered(e -> {
            if (MetroData.mode == Mode.REMOVE_STATION) {
                this.circle.setCursor(Cursor.HAND);
            } else {
                this.circle.setCursor(Cursor.MOVE);
            }
        }
        );

        this.circle.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                EditStationDialog.show(this);
            } else if (MetroData.mode == Mode.REMOVE_STATION) {
                parents.forEach((l) -> {
                    l.removeStation(this);
                });
                this.remove();
                MetroEditor.refresh();
                MetroData.clickOnStation = true;
            }
        }
        );
    }

    public Station(String name, Color color) {
        this();
        this.name = name;
        this.color = color;
        this.circle.setFill(color);
        this.label.setText(name);
    }

    public Station(double x, double y) {
        this();
        this.circle.setCenterX(x);
        this.circle.setCenterY(y);
    }

    public Station(String name, Color color, double radius) {
        this(name, color);
        this.circle.setRadius(radius);
    }

    public void checkIntersection() {
        Iterator<MetroLine> aa = this.parents.iterator();
        while (aa.hasNext()) {
            MetroLine ml = aa.next();
            if (ml != this.realParent) {

                ml.removeStation(this);
            }
        }
        this.parents.clear();
        this.parents.add(realParent);
        this.parents.addAll(MetroData.isAnIntersection(this.circle.getCenterX(), this.circle.getCenterY()));
        Iterator<MetroLine> a = this.parents.iterator();
        while (a.hasNext()) {
            MetroLine ml = a.next();
            if (ml != this.realParent) {
                ml.addExsistingStation(this);
            }
            ml.sortStations();
        }

        if (this.parents.size() > 1) {
            this.isIntersection = true;
            this.circle.setStroke(Color.web("#1874cd"));
        } else if (this.parents.size() == 1) {
            this.isIntersection = false;
            this.circle.setStroke(Color.BURLYWOOD);
        } else {
            this.circle.setStroke(Color.BURLYWOOD);
        }
    }

    /**
     * Marked as visited
     */
    public void setVisited() {
        this.visited = true;
    }

    /**
     * Reset <code>visited</code> to false
     */
    public void resetVisited() {
        this.visited = false;
    }

    public boolean isVisited() {
        return this.visited;
    }
    
    public boolean isParent(MetroLine line){
        if(this.parents.contains(line)){
            return true;
        }
        return false;
    }
    
    public boolean isRealParent(MetroLine line){
        return this.realParent.getName().equals(line.getName());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        if (newName == null) {
            return;
        }
        this.name = newName;
        this.label.setText(name);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color newColor) {
        if (newColor == null) {
            return;
        }
        this.color = newColor;
        this.circle.setFill(color);
    }

    public double getRadius() {
        return this.circle.getRadius();
    }

    public void setRadius(double radius) {
        if (radius <= 0) {
            return;
        }
        this.circle.setRadius(radius);
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(int num) {
        this.lineNumber = num;
    }

    public ArrayList<Station> getNeighbours() {
        return this.neighbors;
    }

    public Station getNeighbour(String name) {
        for (Station s : this.neighbors) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public void setIntersection(boolean value) {
        this.isIntersection = value;
    }

    public boolean isIntersection() {
        return this.isIntersection;
    }

    public Circle getCircle() {
        return this.circle;
    }

    public Label getLabel() {
        return this.label;
    }

    public LinkedList<Station> getPath() {
        return path;
    }

    public void setPath(LinkedList<Station> path) {
        this.path = path;
    }

    public boolean addNeighbour(Station station) {
        if (station == null) {
            return false;
        }
        return this.neighbors.add(station);
    }
    
    public ArrayList<String> getParentsName(){
        final ArrayList<String> names = new ArrayList<>();
        
        for(MetroLine p : this.parents){
            names.add(p.getName());
        }
        
        return names;
    }

    public void addParent(MetroLine line) {
        if (realParent == null) {
            realParent = line;
        }
        this.parents.add(line);
    }

    public final void relocateLabel() {
        /*Iterator<MetroLine> a = this.parents.iterator();
        MetroLine ml = a.next();*/
        //if (this.realParent.getDragProperties(lineNumber)[0] == 0.0) {
        switch (labelPos) {
            case 0: {
                this.label.setLayoutX(this.circle.getCenterX() - this.circle.getRadius());
                this.label.setLayoutY(this.circle.getCenterY() + this.circle.getRadius() + 2.0);
                break;
            }
            case 1: {
                this.label.setLayoutX(this.circle.getCenterX() + this.circle.getRadius() / 2.0);
                this.label.setLayoutY(this.circle.getCenterY() + this.circle.getRadius() + 2.0);
                break;
            }
            case 2: {
                this.label.setLayoutX(this.circle.getCenterX() + this.circle.getRadius() / 2.0);
                this.label.setLayoutY(this.circle.getCenterY() - this.circle.getRadius() - 18.0);
                break;
            }
            case 3: {
                this.label.setLayoutX(this.circle.getCenterX() - this.circle.getRadius());
                this.label.setLayoutY(this.circle.getCenterY() - this.circle.getRadius() - 18.0);
                break;
            }
        }

        /*} else {
            this.label.setLayoutX(this.circle.getCenterX() + this.circle.getRadius() + 2.0);
            this.label.setLayoutY(this.circle.getCenterY() + 1.0);
        }*/
    }

    private int labelPos = 0;

    public void moveLabel() {
        labelPos = (labelPos + 1) % 4;
        relocateLabel();
    }

    public void rotateLabel() {
        if (this.label.getRotate() == 0.0) {
            this.label.setRotate(90.0);
        } else {
            this.label.setRotate(0.0);
        }
    }

    public void snapToGrid() {
        this.dragProperties = this.realParent.getDragProperties(lineNumber);
        if (dragProperties[5] == 0.0) {
            msa = new MoveStationAction(this);
            if (dragProperties[0] == 0.0) {
                double x = this.circle.getCenterX() % 50;
                if (x <= 25.0) {
                    x = this.circle.getCenterX() - x;
                } else {
                    x = this.circle.getCenterX() - x + 50.0;
                }
                if (dragProperties[2] < dragProperties[3]) {
                    if (x <= dragProperties[3] && x >= dragProperties[2]) {
                        this.circle.setCenterX(x);
                    }
                } else {
                    if (x >= dragProperties[3] && x <= dragProperties[2]) {
                        this.circle.setCenterX(x);
                    }
                }

            } else {
                double y = this.circle.getCenterY() % 50;
                if (y <= 25.0) {
                    y = this.circle.getCenterY() - y;
                } else {
                    y = this.circle.getCenterY() - y + 50.0;
                }
                if (dragProperties[2] < dragProperties[3]) {
                    if (y <= dragProperties[3] && y >= dragProperties[2]) {
                        this.circle.setCenterY(y);
                    }
                } else {
                    if (y >= dragProperties[3] && y <= dragProperties[2]) {
                        this.circle.setCenterY(y);
                    }
                }
            }
            this.relocateLabel();
            realParent.setLambdas();
            msa.setNewValues(this.circle.getCenterX(), this.circle.getCenterY(), lineNumber, lambda);
            MetroData.addTransaction(msa);
        }
    }

    public boolean snapToGridable() {
        this.dragProperties = this.realParent.getDragProperties(lineNumber);
        return dragProperties[5] == 0.0;
    }

    public void remove() {
        MetroData.stations.remove(this);
        ObservableList<Node> children = MetroWorkspace.getCanvas().getChildren();
        children.remove(this.circle);
        children.remove(this.label);
    }

    public void removeFromParent() {
        for (MetroLine line : this.parents) {
            line.removeStation(this);
        }
    }

    public void putBackOnCanvas() {
        MetroData.stations.add(this);
        ObservableList<Node> children = MetroWorkspace.getCanvas().getChildren();
        try {
            children.add(this.circle);
            children.add(this.label);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public void addToParent() {
        for (MetroLine line : this.parents) {
            line.addExsistingStation(this);
        }
    }

    public boolean isCoordinateEqual(double a, double b) {
        DecimalFormat aDF = new DecimalFormat("#.");
        aDF.setRoundingMode(RoundingMode.UP);
        String aC = aDF.format(a);
        String bC = aDF.format(b);
        return aC.equals(bC);
    }

    private void updateDragProps() {
        /*Iterator<MetroLine> a = this.parents.iterator();
        MetroLine ml = a.next();*/
        this.dragProperties = this.realParent.getDragProperties(lineNumber);
    }

    public void resetNeighbours() {
        this.neighbors.clear();
        this.visited = false;
        this.path.clear();
    }

    @Override
    public boolean equals(Object s) {
        if (!(s instanceof Station)) {
            return false;
        }
        if (((Station) s).name.equals(name)) {
            //if (((Station) s).stationID == this.stationID) {
            return true;
            //}
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.name);
        //hash = 17 * hash + this.stationID;
        return hash;
    }

    @Override
    public String toString() {
        return name;

    }

    static final String JSON_NAME = "name";
    static final String JSON_COLOR = "color";
    static final String JSON_RADIUS = "radius";
    static final String JSON_LINE_NUM = "line";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";
    static final String JSON_IS_INTERSECTION = "isIntersection";
    static final String JSON_LAMBDA = "lambda";
    static final String JSON_PARENT = "parent";
    static final String JSON_LABEL_POS = "labelPosition";
    static final String JSON_ROTATION = "rotation";

    @Override
    public JsonObject save() {
        JsonArrayBuilder parentArr = Json.createArrayBuilder();
        for (MetroLine p : this.parents) {
            parentArr.add(p.getName());
        }
        JsonArray parentArray = parentArr.build();

        JsonObject jsonObject = Json.createObjectBuilder()
                .add(JSON_NAME, this.name)
                .add(JSON_COLOR, this.color.toString())
                .add(JSON_RADIUS, ("" + this.circle.getRadius()))
                .add(JSON_X, ("" + this.circle.getCenterX()))
                .add(JSON_Y, "" + this.circle.getCenterY())
                .add(JSON_LINE_NUM, this.lineNumber)
                .add(JSON_IS_INTERSECTION, this.isIntersection)
                .add(JSON_LAMBDA, "" + this.lambda)
                .add(JSON_PARENT, parentArray)
                .add(JSON_LABEL_POS, "" + this.labelPos)
                .add(JSON_ROTATION, this.label.getRotate() == 90.0)
                .build();
        return jsonObject;
    }

    @Override
    public void load(JsonObject jsonObject) {
        try {
            this.name = jsonObject.getString(JSON_NAME);
            this.label.setText(name);
            this.color = Color.web(jsonObject.getString(JSON_COLOR));
            this.circle.setRadius(Double.parseDouble(jsonObject.getString(JSON_RADIUS, "10.0")));
            this.circle.setCenterX(Double.parseDouble(jsonObject.getString(JSON_X)));
            this.circle.setCenterY(Double.parseDouble(jsonObject.getString(JSON_Y)));
            this.lineNumber = jsonObject.getInt(JSON_LINE_NUM, 0);
            this.isIntersection = jsonObject.getBoolean(JSON_IS_INTERSECTION, false);
            this.lambda = Double.parseDouble(jsonObject.getString(JSON_LAMBDA));
            
            // Parent
            JsonArray parent = jsonObject.getJsonArray(JSON_PARENT);
            for (int i = 0; i < parent.size(); i++) {
                try {
                    MetroLine p = MetroData.assignParent(this, parent.getString(i));
                    this.parents.add(p);
                    if (i == 0) {
                        this.realParent = p;
                    }
                } catch (Exception ex) {

                }
            }

            // Label Pos
            try {
                this.labelPos = Integer.parseInt(jsonObject.getString(JSON_LABEL_POS, "0"));
            } catch (Exception ex) {

            }

            // Rotation
            try {
                boolean rotate = jsonObject.getBoolean(JSON_ROTATION, false);
                this.label.setRotate(rotate ? 90.0 : 0.0);
            } catch (Exception ex) {

            }

        } catch (Exception ex) {

        } finally {
            try {
                MetroWorkspace.getCanvas().getChildren().add(circle);
            } catch (Exception ex) {

            }
            try {
                MetroWorkspace.getCanvas().getChildren().add(label);
                relocateLabel();
            } catch (Exception ex) {

            }
        }
    }

    @Override
    public JsonObject export() {
        JsonObject JsonObject = Json.createObjectBuilder()
                .add(JSON_NAME, name)
                .add(JSON_X, this.circle.getCenterX())
                .add(JSON_Y, this.circle.getCenterY())
                .build();
        return JsonObject;
    }

    @Override
    public int compareTo(Object o) {
        Station s = (Station) o;
        if (this.lineNumber < s.lineNumber) {
            return -1;
        }
        if (this.lineNumber > s.lineNumber) {
            return 1;
        }
        updateDragProps();
        if (this.dragProperties[0] == 0.0) {
            double startX = this.dragProperties[2];
            double a = Math.abs(this.circle.getCenterX() - startX);
            double b = Math.abs(s.circle.getCenterX() - startX);
            if (a == b) {
                return 0;
            }
            if (a < b) {
                return -1;
            }
            return 1;
        } else {
            double startY = this.dragProperties[2];
            double a = Math.abs(this.circle.getCenterY() - startY);
            double b = Math.abs(s.circle.getCenterY() - startY);
            if (a == b) {
                return 0;
            }
            if (a < b) {
                return -1;
            }
            return 1;
        }
    }
}

class StationID {

    private static int id = 100;

    public static final int generate() {
        return id++;
    }

    public static final void loaded() {
        id *= 2;
        id++;
    }

}
