
package app;

import data.DesignConstants;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class SaveOrDiscardDialog {

    private static final String TITLE = "Save?";
    private static final Font TITLE_FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 14);
    private static final Font TEXT_FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 14);

    private static Stage stage;
    private static boolean isNull = true;
    public static boolean canExit = false;

    private SaveOrDiscardDialog() {

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
        canExit = false;
        stage.showAndWait();
    }

    private static void init() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(500);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(25);

        // First the logo
        Image img = new Image("file:./icons/Logo.png/");
        ImageView logo = new ImageView(img);
        logo.setFitWidth(100.0);
        logo.setFitHeight(100.0);
        vbox.getChildren().add(logo);

        // Name of the app
        Text name = new Text("Do you want to save modified work first?");
        name.setFont(TITLE_FONT);
        vbox.getChildren().add(name);

        // Developer credits
        Button yes = new Button("Yes");
        yes.setPrefSize(100, 40);
        yes.setFont(TITLE_FONT);
        Button no = new Button("No");
        no.setDefaultButton(true);
        no.setPrefSize(100, 40);
        no.setFont(TITLE_FONT);
        no.setOnAction(e -> {
            stage.close();
            canExit = true;
        });
        Button cancel = new Button("Cancel");
        cancel.setPrefSize(100, 40);
        cancel.setFont(TITLE_FONT);
        cancel.setOnAction(e -> {
            canExit = false;
            stage.close();
        });

        HBox btns = new HBox();
        btns.setAlignment(Pos.CENTER);
        btns.setSpacing(10);
        btns.getChildren().add(yes);
        btns.getChildren().add(no);
        btns.getChildren().add(cancel);
        vbox.getChildren().add(btns);

        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 400, 300);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(TITLE);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
    }

}
