package data;

import app.MetroEditor;
import app.MetroMapMaker;
import framework.MetroComponent;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
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
import transactions.MoveLabelAction;
import transactions.RemoveLabelAction;
import transactions.TextChangeAction;

/**
 * @author Yifan Wang
 */
public class MetroMapLabel implements MetroComponent {

    private final Label label;
    private String text;
    private String fontFamily;
    private boolean isBold;
    private boolean isItalic;
    private double size;
    private Color color;

    private double dragA;
    private double dragB;
    private double dragX;
    private double dragY;

    private final Effect highlightedEffect;

    public MetroMapLabel() {
        this.label = new Label();
        this.fontFamily = "Avenir";
        this.isBold = false;
        this.isItalic = false;
        this.size = 24.0;
        this.label.setFont(Font.font(this.fontFamily, this.size));
        this.label.setLayoutX(DesignConstants.DEFAULT_CANVAS_SIZE / 2.0);
        this.label.setLayoutY(DesignConstants.DEFAULT_CANVAS_SIZE / 2.0);
        this.color = Color.BLACK;

        DropShadow dropShadowEffect = new DropShadow();
        dropShadowEffect.setOffsetX(0.0f);
        dropShadowEffect.setOffsetY(0.0f);
        dropShadowEffect.setSpread(1.0);
        dropShadowEffect.setColor(Color.YELLOW);
        dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
        dropShadowEffect.setRadius(15);
        highlightedEffect = dropShadowEffect;

        initializeController();
    }

    public MetroMapLabel(String text) {
        this();
        this.text = text;
        this.label.setText(this.text);
    }

    public Label getLabel() {
        return this.label;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String newText) {
        this.text = newText;
        this.label.setText(text);
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public void setFontFamily(String newFF) {
        this.fontFamily = newFF;
        updateFont();
    }

    public boolean isBold() {
        return this.isBold;
    }

    public void setBold(boolean value) {
        this.isBold = value;
        updateFont();
    }

    public boolean isItalic() {
        return this.isItalic;
    }

    public void setItalic(boolean value) {
        this.isItalic = value;
        updateFont();
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double newSize) {
        this.size = newSize;
        updateFont();
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color value) {
        this.color = value;
        this.label.setTextFill(color);
    }

    private void updateFont() {
        if (this.isBold) {
            if (this.isItalic) {
                this.label.setFont(Font.font(this.fontFamily, FontWeight.BOLD, FontPosture.ITALIC, this.size));
            } else {
                this.label.setFont(Font.font(this.fontFamily, FontWeight.BOLD, FontPosture.REGULAR, this.size));
            }
        } else {
            if (this.isItalic) {
                this.label.setFont(Font.font(this.fontFamily, FontWeight.NORMAL, FontPosture.ITALIC, this.size));
            } else {
                this.label.setFont(Font.font(this.fontFamily, FontWeight.NORMAL, FontPosture.REGULAR, this.size));
            }
        }
    }

    public void setHighlighted(boolean value) {
        if (value) {
            this.label.setEffect(highlightedEffect);
        } else {
            this.label.setEffect(null);
        }
    }

    public void remove() {
        final RemoveLabelAction rla = new RemoveLabelAction(this);
        MetroData.addTransaction(rla);
    }

    private MoveLabelAction mla;

    private void initializeController() {
        this.label.setOnMouseEntered(e -> {
            if (MetroData.mode == Mode.REMOVE_ELEMENT) {
                this.label.setCursor(Cursor.HAND);
            } else {
                this.label.setCursor(Cursor.MOVE);
            }
        });

        this.label.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                if (MetroData.mode == Mode.REMOVE_ELEMENT) {
                    this.remove();
                    MetroData.clickOnElement = true;
                } else {
                    if (MetroData.selectedLabel != null) {
                        MetroData.selectedLabel.setHighlighted(false);
                    }
                    setHighlighted(true);
                    MetroData.clickOnLabel = true;
                    MetroData.selectedLabel = this;
                    MetroData.mode = Mode.EDIT_LABEL;
                    MetroEditor.refresh();
                }
            } else if (e.getClickCount() == 2) {
                this.showEditDialog();
            }
        });

        this.label.setOnMousePressed(e -> {
            this.dragA = e.getSceneX();
            this.dragB = e.getSceneY();

            this.dragX = this.label.getLayoutX();
            this.dragY = this.label.getLayoutY();

            mla = new MoveLabelAction(this);
        });

        this.label.setOnMouseDragged(e -> {
            double x = e.getSceneX() - dragA + dragX;
            double y = e.getSceneY() - dragB + dragY;
            this.label.setLayoutX(x);
            this.label.setLayoutY(y);
        });

        this.label.setOnMouseReleased(e -> {
            mla.setNewValues(label.getLayoutX(), label.getLayoutY());
            MetroData.addTransaction(mla);
            /*MetroFile.markModified();
            MetroWorkspace.refreshTopToolbar();*/
        });
    }

    private static final Font TITLE_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 17.0);
    private static final Font TEXT_FONT
            = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 15.0);

    private void showEditDialog() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinWidth(210.0);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(15.0);

        // title
        final Label title = new Label("Edit Label");
        title.setFont(TITLE_FONT);
        vbox.getChildren().add(title);

        // Current
        HBox textHbox = new HBox();
        textHbox.setAlignment(Pos.CENTER);
        textHbox.setSpacing(3.0);
        final Label current = new Label("Text: ");
        current.setFont(TEXT_FONT);
        final TextField currentText = new TextField(this.text);
        currentText.setMinWidth(150.0);
        currentText.setMaxWidth(150.0);
        currentText.setAlignment(Pos.CENTER_LEFT);
        currentText.setFont(TEXT_FONT);
        textHbox.getChildren().add(current);
        textHbox.getChildren().add(currentText);
        vbox.getChildren().add(textHbox);

        // Done
        final Button done = new Button("Done");
        done.setMinWidth(200.0);

        // Close
        final Button close = new Button("Close");
        close.setMinWidth(200.0);

        VBox btnVbox = new VBox();
        btnVbox.setAlignment(Pos.CENTER);
        btnVbox.setSpacing(5.0);
        btnVbox.getChildren().add(done);
        btnVbox.getChildren().add(close);
        vbox.getChildren().add(btnVbox);

        // finally
        BorderPane pane = new BorderPane();
        pane.setCenter(vbox);
        pane.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);

        Scene scene = new Scene(pane, 230.0, 200.0);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UNDECORATED);
        MetroMapMaker.getAppPane().setDisable(true);
        stage.show();

        done.setOnAction(e -> {
            if (currentText.getText().length() > 0) {
                //this.setText(currentText.getText());
                //MetroFile.markModified();
                final TextChangeAction tca = new TextChangeAction(this, currentText.getText());
                MetroData.addTransaction(tca);
            }
            stage.close();
            MetroMapMaker.getAppPane().setDisable(false);
        });

        close.setOnAction(e -> {
            stage.close();
            MetroMapMaker.getAppPane().setDisable(false);
        });
    }

    static final String JSON_LABEL_TEXT = "label_text";
    static final String JSON_LABEL_FONTFAMILY = "label_fontfamily";
    static final String JSON_LABEL_BOLD = "label_bold";
    static final String JSON_LABEL_ITALIC = "label_italic";
    static final String JSON_LABEL_SIZE = "label_size";
    static final String JSON_LABEL_COLOR = "label_color";
    static final String JSON_X = "x";
    static final String JSON_Y = "y";

    @Override
    public void load(JsonObject jsonObject) {
        this.text = jsonObject.getString(JSON_LABEL_TEXT);
        this.fontFamily = jsonObject.getString(JSON_LABEL_FONTFAMILY);
        this.isBold = jsonObject.getBoolean(JSON_LABEL_BOLD);
        this.isItalic = jsonObject.getBoolean(JSON_LABEL_ITALIC);
        this.size = Double.parseDouble(jsonObject.getString(JSON_LABEL_SIZE));
        this.color = Color.web(jsonObject.getString(JSON_LABEL_COLOR));
        this.label.setLayoutX(Double.parseDouble(jsonObject.getString(JSON_X)));
        this.label.setLayoutY(Double.parseDouble(jsonObject.getString(JSON_Y)));

        this.updateFont();
        this.setText(text);
        this.setColor(color);
    }

    @Override
    public JsonObject save() {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add(JSON_LABEL_TEXT, this.text)
                .add(JSON_LABEL_FONTFAMILY, this.fontFamily)
                .add(JSON_LABEL_BOLD, this.isBold)
                .add(JSON_LABEL_ITALIC, this.isItalic)
                .add(JSON_LABEL_SIZE, "" + this.size)
                .add(JSON_LABEL_COLOR, this.color.toString())
                .add(JSON_X, "" + this.label.getLayoutX())
                .add(JSON_Y, "" + this.label.getLayoutY())
                .build();
        return jsonObject;
    }

    @Override
    public JsonObject export() {
        return null;
    }

}
