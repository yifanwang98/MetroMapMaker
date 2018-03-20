package control;

import app.MetroEditor;
import app.MetroMapMaker;
import app.MetroWorkspace;
import data.DesignConstants;
import data.MetroData;
import data.MetroFile;
import data.MetroLine;
import data.Station;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import transactions.AddStationAction;

/**
 * @author Yifan Wang
 */
public class AddStationDialog {

    private static final String TITLE = "Add Station";
    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private static Stage stage;
    private static boolean isNull = true;

    private static TextField nameTf;

    private AddStationDialog() {

    }

    public static void show() {
        if (isNull) {
            stage = new Stage();
            isNull = false;
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.setTitle(TITLE);
            stage.setAlwaysOnTop(true);
        }
        if (stage.isShowing()) {
            return;
        }

        init();
        MetroMapMaker.getAppPane().setDisable(true);
        stage.setTitle(TITLE);
        stage.show();
    }

    private static void init() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(400);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        // Title
        Text title = new Text("Add Station");
        title.setFont(TITLE_FONT);
        vbox.getChildren().add(title);

        // Line
        Label chooseLine = new Label("Line");
        chooseLine.setFont(TEXT_FONT);
        final ChoiceBox lineCb = new ChoiceBox();
        lineCb.setMinWidth(200.0);
        lineCb.setMaxWidth(200.0);
        lineCb.setItems(MetroData.getLineNames());
        if (MetroData.getSelectedLine() != null) {
            lineCb.setValue(MetroData.getSelectedLine());
        }
        final VBox block0 = new VBox();
        block0.setSpacing(5.0);
        block0.setAlignment(Pos.CENTER);
        block0.getChildren().add(chooseLine);
        block0.getChildren().add(lineCb);
        vbox.getChildren().add(block0);

        // Name
        final VBox block1 = new VBox();
        block1.setAlignment(Pos.CENTER);
        block1.setSpacing(5);
        final Label name = new Label("Name");
        name.setFont(TEXT_FONT);
        block1.getChildren().add(name);
        nameTf = new TextField();
        nameTf.setPromptText("Enter Station Name");
        nameTf.setAlignment(Pos.CENTER);
        nameTf.setMinWidth(200);
        nameTf.setMaxWidth(200);
        nameTf.setFont(TEXT_FONT);
        block1.getChildren().add(nameTf);
        vbox.getChildren().add(block1);

        // Buttons
        final Button addBTN = new Button("Add");
        addBTN.setFont(TITLE_FONT);
        addBTN.setMinWidth(90.0);
        addBTN.setMaxWidth(90.0);

        final Button cancelBTN = new Button("Cancel");
        cancelBTN.setFont(TITLE_FONT);
        cancelBTN.setMinWidth(90.0);
        cancelBTN.setMaxWidth(90.0);
        cancelBTN.setOnAction(e -> {
            MetroMapMaker.getAppPane().setDisable(false);
            stage.close();
        });

        final VBox vbox1 = new VBox();
        vbox1.setSpacing(10.0);
        vbox1.setAlignment(Pos.CENTER);
        final HBox btns = new HBox();
        btns.setAlignment(Pos.CENTER);
        btns.setSpacing(25);
        btns.getChildren().add(addBTN);
        btns.getChildren().add(cancelBTN);
        vbox1.getChildren().add(btns);

        final Label error = new Label("Duplicate Station Name");
        error.setFont(TITLE_FONT);
        error.setTextFill(Color.RED);
        error.setVisible(false);
        vbox1.getChildren().add(error);
        vbox.getChildren().add(vbox1);

        addBTN.setOnAction(e -> {
            final String name1 = nameTf.getText();
            if (name1.length() <= 0 || MetroData.isValidStationName(name1) != 0) {
                error.setVisible(true);
                return;
            }

            final Station sta = new Station();
            final MetroLine line = (MetroLine) lineCb.getValue();
            sta.setName(name1);
            line.addStation(sta);
            MetroData.stations.remove(sta);
            final AddStationAction asa = new AddStationAction(sta);
            MetroData.addTransaction(asa);
            
            MetroFile.markModified();
            MetroEditor.refresh();
            sta.relocateLabel();
            //MetroWorkspace.getCanvas().getChildren().add(sta.getCircle());
            //MetroWorkspace.getCanvas().getChildren().add(sta.getLabel());
            MetroMapMaker.getAppPane().setDisable(false);
            stage.close();
        });

        nameTf.setOnMouseClicked(e -> {
            error.setVisible(false);
        });

        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 290.0, 320.0);
        scene.setCursor(Cursor.WAIT);

        stage.setScene(scene);
    }

}
