
package app;

import data.DesignConstants;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
public final class AboutView {

    private static final String TITLE = "About";
    private static final Font TITLE_FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 14);
    private static final Font TEXT_FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 14);

    private static Stage stage;
    private static boolean isNull = true;

    private AboutView() {

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

        // First the logo
        Image img = new Image("file:./icons/Logo.png/");
        ImageView logo = new ImageView(img);
        logo.setFitWidth(100.0);
        logo.setFitHeight(100.0);
        vbox.getChildren().add(logo);

        // Name of the app
        Text name = new Text("Metro Map Maker");
        name.setFont(Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 23));
        vbox.getChildren().add(name);

        // Developer credits
        empty = new Text("");
        vbox.getChildren().add(empty);
        Text credit = new Text("Credit");
        credit.setFont(TITLE_FONT);
        Text credits = new Text("Ritwik Banerjee, Yifan Wang");
        credits.setFont(TEXT_FONT);
        vbox.getChildren().add(credit);
        vbox.getChildren().add(credits);

        // Year of work
        empty = new Text("");
        vbox.getChildren().add(empty);
        Text yearOfWork = new Text("Year of Work");
        yearOfWork.setFont(TITLE_FONT);
        Text year = new Text("2017");
        year.setFont(TEXT_FONT);
        vbox.getChildren().add(yearOfWork);
        vbox.getChildren().add(year);
        
        // Version
        empty = new Text("");
        vbox.getChildren().add(empty);
        Label version = new Label(DesignConstants.VERSION);
        version.setFont(TEXT_FONT);
        vbox.getChildren().add(version);

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

        Scene scene = new Scene(pane, 300, 400);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setTitle(TITLE);
    }

}
