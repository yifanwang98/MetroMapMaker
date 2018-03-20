package control;

import app.MetroMapMaker;
import data.DesignConstants;
import data.MetroData;
import data.MetroLine;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Yifan Wang
 */
public class ListStationDialog {

    private static final String TITLE = "Station List of Line - ";

    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 21.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private static Stage stage;
    private static boolean isNull = true;

    private ListStationDialog() {

    }

    public static void show() {
        if (isNull) {
            stage = new Stage();
            isNull = false;
            init();
        }
        if (stage.isShowing()) {
            return;
        }

        MetroLine line = MetroData.getSelectedLine();
        title.setText("Stations of " + line.getName());
        ta.setText(line.getInorderLineNames());
        stage.setTitle(TITLE + line.getName());

        stage.show();
    }

    private static Text title;
    private static TextArea ta;

    private static void init() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(330);
        vbox.setPadding(new Insets(15.0, 15.0, 15.0, 15.0));
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(25);

        MetroLine line = MetroData.getSelectedLine();

        // Title
        HBox titleHbox = new HBox();
        titleHbox.setAlignment(Pos.CENTER_LEFT);
        titleHbox.setPrefWidth(300.0);
        title = new Text("Stations of " + line.getName());
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
        ta.setText(line.getInorderLineNames());
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
        stage.setTitle(TITLE + line.getName());
        stage.setAlwaysOnTop(true);
    }
}
