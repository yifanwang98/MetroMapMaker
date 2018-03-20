package control;

import app.MetroMapMaker;
import data.DesignConstants;
import data.MetroLine;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
public class EditLineLabelDialog {

    private static final String TITLE = "Edit Line Label";
    private static final Font TITLE_FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 14);
    private static final Font TEXT_FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 14);

    private static Stage stage;
    private static boolean isNull = true;

    private EditLineLabelDialog() {

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
        stage.show();
    }

    private static void init() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(300);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5);

        Text empty;

        // Name
        Label name = new Label("Choose Line Label Color");
        name.setFont(TITLE_FONT);
        vbox.getChildren().add(name);

        // Developer credits
        ColorPicker cp = new ColorPicker();
        cp.setValue((Color) MetroLine.labelToBeChanged1.getTextFill());
        cp.setOnAction(e->{
            MetroLine.labelToBeChanged1.setTextFill(cp.getValue());
            MetroLine.labelToBeChanged2.setTextFill(cp.getValue());
        });
        vbox.getChildren().add(cp);

        // Close
        empty = new Text("");
        vbox.getChildren().add(empty);
        Label close = new Label("Close");
        close.setUnderline(true);
        close.setFont(TEXT_FONT);
        close.setOnMouseClicked(e -> {
            stage.close();
        });
        vbox.getChildren().add(close);

        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 250, 150);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setTitle(TITLE);
    }

}
