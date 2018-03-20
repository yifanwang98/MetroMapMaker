package control;

import app.MetroEditor;
import app.MetroMapMaker;
import data.DesignConstants;
import data.MetroData;
import data.MetroFile;
import data.Station;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
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

/**
 * @author Yifan Wang
 */
public class EditStationDialog {

    private static final String TITLE = "Edit Line";

    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private static Stage stage;
    private static boolean isNull = true;
    private static Station station;
    private static String originalName;

    private EditStationDialog() {

    }

    public static void show(Station station) {
        EditStationDialog.station = station;
        EditStationDialog.originalName = station.getName();

        if (isNull) {
            stage = new Stage();
            isNull = false;
            init();
        }
        if (stage.isShowing()) {
            return;
        }

        nameTf.clear();
        nameTf.setText(station.getName());
        colorCp.setValue(station.getColor());

        stage.setTitle(TITLE);
        stage.show();
    }

    private static TextField nameTf;
    private static ColorPicker colorCp;

    private static void init() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(400);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(25);

        Text title = new Text("Edit Station");
        title.setFont(TITLE_FONT);
        vbox.getChildren().add(title);

        // Name
        VBox block1 = new VBox();
        block1.setAlignment(Pos.CENTER);
        block1.setSpacing(5);
        Label name = new Label("Name");
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

        // Color
        VBox block2 = new VBox();
        block2.setAlignment(Pos.CENTER);
        block2.setSpacing(5);
        Label color = new Label("Color");
        color.setFont(TEXT_FONT);
        block2.getChildren().add(color);
        colorCp = new ColorPicker();
        colorCp.setMinWidth(200);
        colorCp.setMaxWidth(200);
        block2.getChildren().add(colorCp);
        vbox.getChildren().add(block2);

        // Buttons
        Button doneBTN = new Button("Done");
        doneBTN.setFont(TITLE_FONT);
        doneBTN.setMinWidth(100);
        doneBTN.setMaxWidth(100);

        Button cancelBTN = new Button("Cancel");
        cancelBTN.setFont(TITLE_FONT);
        cancelBTN.setMinWidth(100);
        cancelBTN.setMaxWidth(100);
        cancelBTN.setOnAction(e -> {
            stage.close();
        });

        HBox btns = new HBox();
        btns.setAlignment(Pos.CENTER);
        btns.setSpacing(25);
        btns.getChildren().add(doneBTN);
        btns.getChildren().add(cancelBTN);
        vbox.getChildren().add(btns);

        Label error = new Label("Duplicate Station Name");
        error.setFont(TITLE_FONT);
        error.setTextFill(Color.RED);
        error.setVisible(false);
        vbox.getChildren().add(error);

        // Finally
        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 310.0, 330.0);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(TITLE);
        stage.setAlwaysOnTop(true);

        scene.getWindow().setOnCloseRequest(e -> {
            MetroMapMaker.getAppPane().setDisable(false);
        });

        doneBTN.setOnAction(e -> {
            String name1 = EditStationDialog.getName();
            if (!name1.equals(EditStationDialog.originalName)) {
                if (MetroData.isValidStationName(name1) > 0) {
                    error.setVisible(true);
                    return;
                }
            }

            Color color1 = EditStationDialog.getColor();
            station.setColor(color1);
            station.setName(name1);

            MetroEditor.refresh();
            MetroFile.markModified();

            stage.close();
        });

        nameTf.setOnMouseClicked(e -> {
            error.setVisible(false);
        });

    }

    public static String getName() {
        return nameTf.getText();
    }

    public static Color getColor() {
        return colorCp.getValue();
    }

}
