package data;

import app.MetroMapMaker;
import framework.MetroComponent;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.json.Json;
import javax.json.JsonObject;
import transactions.ImageOpacityChange;
import transactions.ImageScaleChange;
import transactions.MoveImageAction;
import transactions.RemoveImageAction;

/**
 * @author Yifan Wang
 */
public class MetroMapImage implements MetroComponent {

    private String path;
    private Image image;
    private final ImageView imgView;
    private double scale;
    private double opacity;

    private double dragA;
    private double dragB;
    private double dragX;
    private double dragY;

    private final Effect highlightedEffect;

    public MetroMapImage() {
        imgView = new ImageView();

        DropShadow dropShadowEffect = new DropShadow();
        dropShadowEffect.setOffsetX(0.0f);
        dropShadowEffect.setOffsetY(0.0f);
        dropShadowEffect.setSpread(1.0);
        dropShadowEffect.setColor(Color.YELLOW);
        dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
        dropShadowEffect.setRadius(15);
        highlightedEffect = dropShadowEffect;
    }

    public MetroMapImage(String path) {
        this.path = path;
        this.image = new Image("file:" + path);
        this.imgView = new ImageView(this.image);
        this.imgView.setX((DesignConstants.DEFAULT_CANVAS_SIZE
                - this.image.getWidth()) / 2);
        this.imgView.setY((DesignConstants.DEFAULT_CANVAS_SIZE
                - this.image.getHeight()) / 2);
        this.scale = 1.0;
        this.opacity = 1.0;
        initializeController();

        DropShadow dropShadowEffect = new DropShadow();
        dropShadowEffect.setOffsetX(0.0f);
        dropShadowEffect.setOffsetY(0.0f);
        dropShadowEffect.setSpread(1.0);
        dropShadowEffect.setColor(Color.YELLOW);
        dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
        dropShadowEffect.setRadius(15);
        highlightedEffect = dropShadowEffect;
    }

    public String getPath() {
        return this.path;
    }

    public Image getImage() {
        return this.image;
    }

    public ImageView getImageView() {
        return this.imgView;
    }

    public double getOldScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        this.imgView.setFitWidth(image.getWidth() * scale);
        this.imgView.setFitHeight(image.getHeight() * scale);
    }

    public void performSetScale(ImageScaleChange action) {
        final ImageScaleChange isc = action;
        MetroData.addTransaction(isc);
    }

    public void setOpacity(double value) {
        this.opacity = value;
        this.imgView.setOpacity(value);
    }

    public void performSetOpacity(ImageOpacityChange action) {
        final ImageOpacityChange ioc = action;
        MetroData.addTransaction(ioc);
    }

    public void remove() {
        final RemoveImageAction ria = new RemoveImageAction(this);
        MetroData.addTransaction(ria);
    }

    public void setHighlighted(boolean value) {
        if (value) {
            this.imgView.setEffect(highlightedEffect);
        } else {
            this.imgView.setEffect(null);
        }
    }

    private MoveImageAction mia;

    private void initializeController() {
        this.imgView.setOnMouseEntered(e -> {
            if (MetroData.mode == Mode.REMOVE_ELEMENT) {
                this.imgView.setCursor(Cursor.HAND);
            } else {
                this.imgView.setCursor(Cursor.MOVE);
            }
        });

        this.imgView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                if (MetroData.mode == Mode.REMOVE_ELEMENT) {
                    this.remove();
                    MetroData.clickOnElement = true;
                } else {
                    this.setHighlighted(true);
                    MetroData.selectedImage = this;
                    MetroData.clickOnImage = true;
                }
            } else if (e.getClickCount() == 2) {
                this.showEditDialog();
            }
        });

        this.imgView.setOnMousePressed(e -> {
            this.setHighlighted(false);
            MetroData.selectedImage = this;
            MetroData.clickOnImage = true;

            this.dragA = e.getSceneX();
            this.dragB = e.getSceneY();

            this.dragX = this.imgView.getX();
            this.dragY = this.imgView.getY();

            this.mia = new MoveImageAction(this);
        });

        this.imgView.setOnMouseDragged(e -> {
            double x = e.getSceneX() - dragA + dragX;
            double y = e.getSceneY() - dragB + dragY;
            this.imgView.setX(x);
            this.imgView.setY(y);
        });

        this.imgView.setOnMouseReleased(e -> {
            this.setHighlighted(true);
            MetroData.selectedImage = this;
            MetroData.clickOnImage = true;
            this.mia.setNewValues(this.imgView.getX(), this.imgView.getY());
            MetroData.addTransaction(mia);
        });
    }

    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private void showEditDialog() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(300.0);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(25.0);

        // title
        final Label title = new Label("Edit Image");
        title.setFont(TITLE_FONT);
        vbox.getChildren().add(title);

        // Current
        HBox curHbox = new HBox();
        curHbox.setAlignment(Pos.CENTER);
        curHbox.setSpacing(3.0);
        final Label current = new Label("Current: ");
        current.setFont(TEXT_FONT);
        final Label currentPath = new Label(path);
        currentPath.setMinWidth(230.0);
        currentPath.setMaxWidth(230.0);
        currentPath.setAlignment(Pos.CENTER_LEFT);
        currentPath.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        currentPath.setFont(TEXT_FONT);
        curHbox.getChildren().add(current);
        curHbox.getChildren().add(currentPath);
        vbox.getChildren().add(curHbox);

        // Scale
        HBox scaHbox = new HBox();
        scaHbox.setSpacing(3.0);
        scaHbox.setAlignment(Pos.CENTER);
        final Label scaleL = new Label("Scale: ");
        scaleL.setFont(TEXT_FONT);
        final Slider scaS = new Slider(0.1, 2.0, this.scale);
        //this.oldScale = this.scale;
        final ImageScaleChange isc = new ImageScaleChange(this, this.scale);
        scaS.setOnMouseDragged(e -> {
            this.setScale(scaS.getValue());
        });
        scaS.setOnMouseReleased(e -> {
            isc.setNewScale(scale);
            this.performSetScale(isc);
        });
        scaHbox.getChildren().add(scaleL);
        scaHbox.getChildren().add(scaS);
        vbox.getChildren().add(scaHbox);

        // Opacity
        HBox opaHbox = new HBox();
        opaHbox.setSpacing(3.0);
        opaHbox.setAlignment(Pos.CENTER);
        final Label opacityL = new Label("Opacity: ");
        opacityL.setFont(TEXT_FONT);
        final Slider opaS = new Slider(0.0, 1.0, this.opacity);
        final ImageOpacityChange ioc = new ImageOpacityChange(this, this.opacity);
        opaS.setOnMouseDragged(e -> {
            this.setOpacity(opaS.getValue());
        });
        opaS.setOnMouseReleased(e -> {
            ioc.setNewOpacity(this.opacity);
            this.performSetOpacity(ioc);
        });
        opaHbox.getChildren().add(opacityL);
        opaHbox.getChildren().add(opaS);
        vbox.getChildren().add(opaHbox);

        // Close
        final Button close = new Button("Close");
        close.setMinWidth(200.0);
        vbox.getChildren().add(close);

        // finally
        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 360.0, 260.0);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UNDECORATED);
        MetroMapMaker.getAppPane().setDisable(true);
        stage.show();

        close.setOnAction(e -> {
            stage.close();
            MetroMapMaker.getAppPane().setDisable(false);
        });
    }

    static final String JSON_IMAGE_OPA = "image_opacity";
    static final String JSON_IMAGE_PATH = "image_path";
    static final String JSON_IMAGE_SCALE = "image_scale";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";

    @Override
    public void load(JsonObject jsonObject) {
        this.opacity = Double.parseDouble(jsonObject.getString(JSON_IMAGE_OPA));
        this.path = jsonObject.getString(JSON_IMAGE_PATH);
        this.scale = Double.parseDouble(jsonObject.getString(JSON_IMAGE_SCALE));
        double x = Double.parseDouble(jsonObject.getString(JSON_X));
        double y = Double.parseDouble(jsonObject.getString(JSON_Y));

        this.image = new Image("file:" + this.path);
        this.imgView.setImage(this.image);
        this.imgView.setX(x);
        this.imgView.setY(y);
        this.imgView.setOpacity(opacity);
        this.setScale(scale);

        initializeController();
    }

    @Override
    public JsonObject save() {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add(JSON_IMAGE_OPA, "" + this.opacity)
                .add(JSON_IMAGE_PATH, this.path)
                .add(JSON_IMAGE_SCALE, "" + this.scale)
                .add(JSON_X, "" + this.imgView.getX())
                .add(JSON_Y, "" + this.imgView.getY())
                .build();
        return jsonObject;
    }

    @Override
    public JsonObject export() {
        return null;
    }

}
