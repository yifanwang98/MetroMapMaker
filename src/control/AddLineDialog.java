package control;

import app.MetroMapMaker;
import data.DesignConstants;
import data.MetroData;
import data.MetroFile;
import data.MetroLine;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import transactions.AddLineAction;

/**
 * @author Yifan Wang
 */
public class AddLineDialog {

    private static final String TITLE = "Add Line";
    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private static Stage stage;
    private static boolean isNull = true;
    public static boolean addClicked = false;

    private AddLineDialog() {

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

        nameTf.clear();
        colorCp.setValue(Color.BLACK);
        addClicked = false;

        MetroMapMaker.getAppPane().setDisable(true);
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

        Text title = new Text("Add Line");
        title.setFont(TITLE_FONT);
        vbox.getChildren().add(title);

        VBox block1 = new VBox();
        block1.setAlignment(Pos.CENTER);
        block1.setSpacing(5);
        Label name = new Label("Name");
        name.setFont(TEXT_FONT);
        block1.getChildren().add(name);
        nameTf = new TextField();
        nameTf.setPromptText("Enter Line Name");
        nameTf.setAlignment(Pos.CENTER);
        nameTf.setMinWidth(200);
        nameTf.setMaxWidth(200);
        nameTf.setFont(TEXT_FONT);
        block1.getChildren().add(nameTf);
        vbox.getChildren().add(block1);

        VBox block2 = new VBox();
        block2.setAlignment(Pos.CENTER);
        block2.setSpacing(5);
        Label color = new Label("Color");
        color.setFont(TEXT_FONT);
        block2.getChildren().add(color);
        colorCp = new ColorPicker();
        colorCp.setValue(Color.BLACK);
        colorCp.setMinWidth(200);
        colorCp.setMaxWidth(200);
        colorCp.setCursor(Cursor.HAND);
        block2.getChildren().add(colorCp);
        vbox.getChildren().add(block2);

        Button addBTN = new Button("Add");
        addBTN.setFont(TITLE_FONT);
        addBTN.setMinWidth(90.0);
        addBTN.setMaxWidth(90.0);

        Button cancelBTN = new Button("Cancel");
        cancelBTN.setFont(TITLE_FONT);
        cancelBTN.setMinWidth(90.0);
        cancelBTN.setMaxWidth(90.0);
        cancelBTN.setOnAction(e -> {
            MetroMapMaker.getAppPane().setDisable(false);
            stage.close();
        });

        HBox btns = new HBox();
        btns.setAlignment(Pos.CENTER);
        btns.setSpacing(25);
        btns.getChildren().add(addBTN);
        btns.getChildren().add(cancelBTN);
        vbox.getChildren().add(btns);

        Label error = new Label("Duplicate Line Name");
        error.setFont(TITLE_FONT);
        error.setTextFill(Color.RED);
        error.setVisible(false);
        vbox.getChildren().add(error);

        addBTN.setOnAction(e -> {
            addClicked = true;
            String name1 = AddLineDialog.getName();
            Color color1 = AddLineDialog.getColor();

            if (name1.length() <= 0 || !MetroData.isValidLineName(name1)) {
                error.setVisible(true);
                return;
            }

            final MetroLine line1 = new MetroLine(name1, color1);
            final AddLineAction ala = new AddLineAction(line1);
            MetroData.addTransaction(ala);
            //MetroData.addLine(line1);
            //MetroFile.markModified();
            MetroMapMaker.getAppPane().setDisable(false);
            stage.close();
        });

        nameTf.setOnMouseClicked(e -> {
            error.setVisible(false);
        });

        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 290.0, 350.0);
        scene.setCursor(Cursor.WAIT);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(TITLE);
        stage.setAlwaysOnTop(true);

        scene.getWindow().setOnCloseRequest(e -> {
            MetroMapMaker.getAppPane().setDisable(false);
        });
    }

    public static String getName() {
        return nameTf.getText();
    }

    public static Color getColor() {
        return colorCp.getValue();
    }

}
