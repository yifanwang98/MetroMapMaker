package control;

import app.MetroMapMaker;
import app.MetroWorkspace;
import data.DesignConstants;
import data.MetroData;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import transactions.BackgroundImageOpacity;
import transactions.SetBackgroundImageAction;

/**
 * @author Yifan Wang
 */
public class SetBackgroundDialog {

    private static final String TITLE = "Set Image Background";
    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private static Stage stage;
    private static boolean isNull = true;
    private static double oldVal = 1.0;

    private SetBackgroundDialog() {

    }

    public static void show() {
        if (isNull) {
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            isNull = false;
            //init();
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
        vbox.setPrefWidth(300);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(25);

        // title
        final Label title = new Label("Set Background Image");
        title.setFont(TITLE_FONT);
        vbox.getChildren().add(title);

        // Current
        HBox curHbox = new HBox();
        curHbox.setAlignment(Pos.CENTER);
        curHbox.setSpacing(3.0);
        final Label current = new Label("Current: ");
        current.setFont(TEXT_FONT);
        final Label currentPath = new Label();
        currentPath.setMinWidth(200.0);
        currentPath.setMaxWidth(200.0);
        currentPath.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        final Label remove = new Label("Remove");
        remove.setUnderline(true);
        remove.setFont(TEXT_FONT);
        remove.setOnMouseClicked(e -> {
            SetBackgroundImageAction sbia = new SetBackgroundImageAction(MetroData.getBackgroundImagePath(), null);
            MetroData.addTransaction(sbia);
            //MetroData.removeBackgroundImage();
            currentPath.setText("--- N/A ---");
            remove.setDisable(true);
        });
        currentPath.setFont(TEXT_FONT);
        if (MetroData.hasBackgroundImage()) {
            String path = MetroData.getBackgroundImagePath();
            currentPath.setText(path);
            remove.setDisable(false);
        } else {
            currentPath.setText("--- N/A ---");
            remove.setDisable(true);
        }
        curHbox.getChildren().add(current);
        curHbox.getChildren().add(currentPath);
        curHbox.getChildren().add(remove);
        vbox.getChildren().add(curHbox);

        // Opacity
        HBox opaHbox = new HBox();
        opaHbox.setSpacing(3.0);
        opaHbox.setAlignment(Pos.CENTER);
        final Label opacity = new Label("Opacity: ");
        opacity.setFont(TEXT_FONT);
        final Slider opaS = new Slider(0.0, 1.0, 1.0);
        if (MetroData.hasBackgroundImage()) {
            oldVal = MetroData.getBackgroundOpacity();
            opaS.setValue(oldVal);
            opaS.setDisable(false);
        } else {
            opaS.setDisable(true);
        }
        opaS.setOnMouseDragged(e -> {
            MetroData.setBackgroundOpacity(opaS.getValue());
        });
        opaS.setOnMouseReleased(e -> {
            final BackgroundImageOpacity bgio = new BackgroundImageOpacity(oldVal, opaS.getValue());
            MetroData.addTransaction(bgio);
            //MetroWorkspace.refreshTopToolbar();
        });
        opaHbox.getChildren().add(opacity);
        opaHbox.getChildren().add(opaS);
        vbox.getChildren().add(opaHbox);

        // ADD / Change
        final Button addOrChange = new Button("Set / Change Image");
        addOrChange.setMinWidth(200.0);
        vbox.getChildren().add(addOrChange);

        // Close
        final Button close = new Button("Close");
        close.setOnAction(e -> {
            stage.close();
            MetroMapMaker.getAppPane().setDisable(false);
        });
        close.setMinWidth(200.0);
        vbox.getChildren().add(close);

        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 380.0, 280.0);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle(TITLE);
        stage.setAlwaysOnTop(true);

        scene.getWindow().setOnCloseRequest(e -> {
            MetroMapMaker.getAppPane().setDisable(false);
        });

        addOrChange.setOnAction(e -> {
            try {
                final FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
                stage.close();
                MetroMapMaker.getAppPane().setDisable(false);
                File file = fc.showOpenDialog(MetroMapMaker.getStage());
                String path = file.getAbsolutePath();
                if (path != null) {
                    SetBackgroundImageAction sbia = new SetBackgroundImageAction(MetroData.getBackgroundImagePath(), path);
                    MetroData.addTransaction(sbia);
                    //MetroData.setBackgroundImage(path);
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        });
    }
}
