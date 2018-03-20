package control;

import app.MetroEditor;
import app.MetroMapMaker;
import app.MetroWorkspace;
import data.DesignConstants;
import data.MetroData;
import data.MetroFile;
import data.MetroLine;
import data.MetroMapImage;
import data.Mode;
import data.Station;
import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import transactions.AddImageAction;
import transactions.BackgroundStyleAction;
import transactions.LineColorChange;

/**
 * @author Yifan Wang
 */
public class EditController {

    private final MetroMapMaker app;
    private final MetroWorkspace workspace;

    public EditController(MetroMapMaker app, MetroWorkspace workspace) {
        this.app = app;
        this.workspace = workspace;
    }

    public static void chooseLineHandler(MetroLine value) {
        MetroData.setSelectedLine(value);
    }

    public static void processLineColorChange(Color value) {
        final MetroLine a = MetroData.getSelectedLine();
        final LineColorChange lcc = new LineColorChange(a, value);
        MetroData.addTransaction(lcc);
    }

    public static void processAddLine() {
        try {
            AddLineDialog.show();
        } catch (Exception ex) {

        }
    }

    public static void processRemoveLine() {
        MetroData.removeSelectedLine();
        /*MetroEditor.refresh();
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();*/
    }

    public static void processAddEmptyStationToLine() {
        /*if (MetroData.getSelectedLine() != null) {
            final Station a = new Station();
            MetroData.getSelectedLine().addStation(a);
            MetroWorkspace.getCanvas().getChildren().add(a.getCircle());
            MetroEditor.refresh();
            MetroWorkspace.refreshTopToolbar();
        }*/
        if (MetroData.isEmpty()) {
            return;
        }
        MetroData.mode = Mode.ADD_STATION;
    }

    public static void processRemoveEmptyStaionFromLine() {
        MetroData.mode = Mode.REMOVE_STATION;
    }

    public static void listStationsOfLine() {
        if (MetroData.getSelectedLine() != null) {
            ListStationDialog.show();
        }
    }

    public static void processLineThicknessChange(double value) {
        if (MetroData.getSelectedLine() != null) {
            MetroData.getSelectedLine().setThickness(value);
            MetroFile.markModified();
            MetroWorkspace.refreshTopToolbar();
        }
    }

    public static void chooseStationHandler() {

    }

    public static void processStationColorChange() {

    }

    public static void processAddStation() {
        AddStationDialog.show();
    }

    public static void processRemoveStation() {
        MetroData.removeSelectedStation();
        MetroEditor.refresh();
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void processSnap() {
        MetroData.getSelectedStation().snapToGrid();
    }

    public static void processMoveLabel() {
        if (MetroData.getSelectedStation() != null) {
            MetroData.getSelectedStation().moveLabel();
            MetroFile.markModified();
            MetroWorkspace.refreshTopToolbar();
        }
    }

    public static void processRatation() {
        if (MetroData.getSelectedStation() != null) {
            MetroData.getSelectedStation().rotateLabel();
        }
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void processRadiusChange() {

    }

    public static void processFindRoute(Station from, Station to) {
        String str = MetroData.findRoute(from, to);

        // Show
        final Font TITLE_FONT
                = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 21.0);
        final Font TEXT_FONT
                = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);
        Text title;
        TextArea ta;
        Stage stage = new Stage();
        VBox vbox = new VBox();
        vbox.setPrefWidth(330);
        vbox.setPadding(new Insets(15.0, 15.0, 15.0, 15.0));
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(25);

        // Title
        HBox titleHbox = new HBox();
        titleHbox.setAlignment(Pos.CENTER_LEFT);
        titleHbox.setPrefWidth(300.0);
        title = new Text("Find Route");
        title.setFont(TITLE_FONT);
        titleHbox.getChildren().add(title);
        vbox.getChildren().add(titleHbox);

        // Name
        ta = new TextArea();
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefWidth(300.0);
        ta.setPrefHeight(300.0);
        ta.setFont(TEXT_FONT);
        ta.setText(str);
        vbox.getChildren().add(ta);

        // Close
        Label close = new Label("Close");
        close.setUnderline(true);
        close.setFont(TEXT_FONT);
        close.setOnMouseClicked(e -> {
            stage.close();
        });
        vbox.getChildren().add(close);

        // Finally
        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        vbox.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 330.0, 450.0);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Find Route");
        stage.setAlwaysOnTop(true);
        stage.show();
        stage.setX(MetroMapMaker.getStage().getX() + 35.0);
    }

    public static void processBackgroundColorChange(Color color) {
        String hex = "#" + Integer.toHexString(color.hashCode());
        String style = "-fx-background-color:" + hex;
        if (hex.equals("#ff")) {
            style = "-fx-background-color:#000000";
        }

        BackgroundStyleAction bsa = new BackgroundStyleAction(MetroData.getBgStyle(), style);
        MetroData.addTransaction(bsa);
        MetroFile.markModified();
    }

    public static void processSetBackgroundImage() {
        SetBackgroundDialog.show();
    }

    public static void processAddImage() {
        try {
            final FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
            File file = fc.showOpenDialog(MetroMapMaker.getStage());
            String path = file.getAbsolutePath();
            if (path != null) {
                final MetroMapImage mmi = new MetroMapImage(path);
                final AddImageAction aia = new AddImageAction(mmi);
                MetroData.addTransaction(aia);
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public static void processAddLabel() {
        AddLabelDialog.show();
    }

    public static void processRemoveElement() {
        MetroData.mode = Mode.REMOVE_ELEMENT;
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void processTextColorChange() {
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void processBoldText() {
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void processItalicText() {
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void changeTextSize() {
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void changeTextFont() {
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void showGrid() {

    }

    public static int increaseCounter = 0;

    public static void increaseMapSize() {
        if (increaseCounter < 5) {
            increaseCounter++;
            MetroWorkspace.increaseMapSize();
        }
    }

    public static void decreaseMapSize() {
        if (increaseCounter > 0) {
            increaseCounter--;
            MetroWorkspace.decreaseMapSize();
        }
    }

    public static int zoomCounter = 5;
    private static double scale = 1;

    public static void processZoomIn() {
        Pane canvas = MetroWorkspace.getCanvas();
        double x = canvas.getScaleX();
        canvas.setScaleX(x * 1.1);
        canvas.setScaleY(x * 1.1);
        scale *= 1.1;
        zoomCounter++;
    }

    public static void processZoomOut() {
        Pane canvas = MetroWorkspace.getCanvas();
        double x = canvas.getScaleX();
        canvas.setScaleX(x / 1.1);
        canvas.setScaleY(x / 1.1);
        scale /= 1.1;
        zoomCounter--;
    }

    public static double getScale() {
        return scale;
    }

}
