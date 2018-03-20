package control;

import app.MetroMapMaker;
import app.MetroWorkspace;
import data.DesignConstants;
import data.MetroData;
import data.MetroMapLabel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import transactions.AddLabelAction;

/**
 * @author Yifan Wang
 */
public class AddLabelDialog {

    private static final String TITLE = "Add Label";
    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private static Stage stage;
    private static boolean isNull = true;

    private AddLabelDialog() {

    }

    public static void show() {
        if (isNull) {
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            isNull = false;
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
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(190.0);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20.0);

        // title
        final Label title = new Label("Add Label");
        title.setFont(TITLE_FONT);
        vbox.getChildren().add(title);

        // TextField
        final TextField tf = new TextField();
        tf.setFont(TITLE_FONT);
        tf.setMinWidth(150.0);
        tf.setMaxWidth(150.0);
        vbox.getChildren().add(tf);

        // ADD / Change
        final Button addBTN = new Button("Add");
        addBTN.setMinWidth(150.0);

        // Close
        final Button close = new Button("Close");
        close.setOnAction(e -> {
            stage.close();
            MetroMapMaker.getAppPane().setDisable(false);
        });
        close.setMinWidth(150.0);

        VBox btnVbox = new VBox();
        btnVbox.setAlignment(Pos.CENTER);
        btnVbox.setSpacing(5.0);
        btnVbox.getChildren().add(addBTN);
        btnVbox.getChildren().add(close);
        vbox.getChildren().add(btnVbox);

        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 190.0, 180.0);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(TITLE);
        stage.setAlwaysOnTop(true);

        scene.getWindow().setOnCloseRequest(e -> {
            MetroMapMaker.getAppPane().setDisable(false);
        });

        addBTN.setOnAction(e -> {
            try {
                if (tf.getText().length() > 0) {
                    final MetroMapLabel mml = new MetroMapLabel(tf.getText());
                    final AddLabelAction ala = new AddLabelAction(mml);
                    MetroData.addTransaction(ala);
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            } finally {
                stage.close();
                MetroMapMaker.getAppPane().setDisable(false);
            }
        });
    }
}
