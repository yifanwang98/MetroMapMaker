package control;

import app.MetroEditor;
import app.MetroMapMaker;
import app.MetroWorkspace;
import data.DesignConstants;
import data.MetroData;
import data.MetroFile;
import data.MetroLine;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import transactions.EditLineAction;

/**
 * @author Yifan Wang
 */
public class EditLineDialog {

    private static final String TITLE = "Edit Line";

    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private static Stage stage;
    private static boolean isNull = true;
    private static MetroLine line;
    private static String originalName;

    private EditLineDialog() {

    }

    public static void show(MetroLine line) {
        EditLineDialog.line = line;

        if (isNull) {
            stage = new Stage();
            isNull = false;
            init();
        }
        if (stage.isShowing()) {
            return;
        }

        nameTf.clear();
        nameTf.setText(line.getName());
        colorCp.setValue(line.getColor());
        chB.setSelected(line.isCircular());
        originalName = line.getName();
        circularH.setDisable(line.getSectionSize() < 3);

        stage.setTitle(TITLE);
        stage.show();
    }

    private static TextField nameTf;
    private static ColorPicker colorCp;
    private static CheckBox chB;
    private static HBox circularH;

    private static void init() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(400);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        Text title = new Text("Edit Line");
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
        nameTf.setPromptText("Enter Line Name");
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

        // Circular
        circularH = new HBox();
        chB = new CheckBox();
        Label cir = new Label("Circular");
        cir.setFont(TEXT_FONT);
        circularH.setAlignment(Pos.CENTER);
        circularH.setSpacing(3.0);
        circularH.getChildren().add(chB);
        circularH.getChildren().add(cir);

        vbox.getChildren().add(circularH);

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

        Label error = new Label("Duplicate Line Name");
        error.setFont(TITLE_FONT);
        error.setTextFill(Color.RED);
        error.setVisible(false);
        VBox vbox1 = new VBox();
        vbox1.setAlignment(Pos.CENTER);
        vbox1.setSpacing(5.0);
        vbox1.getChildren().add(btns);
        vbox1.getChildren().add(error);
        vbox.getChildren().add(vbox1);

        // Finally
        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 310.0, 340.0);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(TITLE);
        stage.setAlwaysOnTop(true);

        scene.getWindow().setOnCloseRequest(e -> {
            MetroMapMaker.getAppPane().setDisable(false);
        });

        doneBTN.setOnAction(e -> {
            String name1 = EditLineDialog.getName();
            Color color1 = EditLineDialog.getColor();

            if (!name1.equals(originalName)) {
                if (!MetroData.isValidLineName(name1)) {
                    error.setVisible(true);
                    return;
                }
            }

            //EditLineDialog.line.setColor(color1);
            //EditLineDialog.line.setName(name1);
            if (chB.isSelected()) {
                EditLineDialog.line.setCircular();
            } else {
                EditLineDialog.line.setUncircular();
            }
            
            final EditLineAction ela = new EditLineAction(EditLineDialog.line, color1, name1);
            MetroData.addTransaction(ela);
            
            /*MetroEditor.refresh();
            MetroFile.markModified();
            MetroWorkspace.refreshTopToolbar();*/

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
