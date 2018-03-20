package app;

import control.EditController;
import data.DesignConstants;
import data.MetroData;
import data.MetroFile;
import data.Mode;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

/**
 * @author Yifan Wang
 */
public class MetroWorkspace {

    private static MetroMapMaker app;
    private static MetroEditor editor;
    private static MetroData dataManager;

    private static BorderPane appPane;

    private static ToolBar topToolBar;
    private static VBox editBar;
    private static Pane canvas;

    private static ImageView grid;

    // Toolbar Buttons
    private static Button newBTN;
    private static Button loadBTN;
    private static Button saveBTN;
    private static Button saveAsBTN;
    private static Button exportBTN;
    private static Button undoBTN;
    private static Button redoBTN;
    private static Button aboutBTN;
    private static Button exitButton;

    private static double a = 0;
    private static double b = 0;
    private static double c = 0;
    private static double d = 0;
    private static double g = 0;
    private static double h = 0;

    private static double maxXMove;
    private static double maxYMove;
    private static double canvasWidth;
    private static double canvasHeight;

    public MetroWorkspace(MetroMapMaker app) {
        MetroWorkspace.app = app;
        dataManager = new MetroData(this);
        appPane = MetroMapMaker.getAppPane();

        if (MetroFile.hasLoadRequest) {
            try {
                MetroFile.load(MetroFile.filePath);
            } catch (IOException ex) {

            }
        }

        initializeWorkspace();
        initializeController();

        final Image imgGrid = new Image("file:./icons/50a.png");
        grid = new ImageView(imgGrid);
        grid.setVisible(false);
        canvas.getChildren().add(grid);

        appPane.setCenter(canvas);
        appPane.setTop(topToolBar);
        appPane.setLeft(editBar);
        appPane.setRight(null);

        canvasWidth = appPane.getWidth() - 400.0;
        canvasHeight = MetroEditor.getHeight();
        canvas.setMinSize(DesignConstants.DEFAULT_CANVAS_SIZE, DesignConstants.DEFAULT_CANVAS_SIZE);

        canvas.setOnMouseClicked(e -> {
            if (MetroData.mode != Mode.DEFAULT) {
                if (MetroData.clickOnStation && MetroData.mode == Mode.REMOVE_STATION) {
                    MetroData.clickOnStation = false;

                } else if (MetroData.clickOnLine && MetroData.mode == Mode.ADD_STATION) {
                    MetroData.clickOnLine = false;

                } else if (MetroData.mode == Mode.EDIT_LABEL) {
                    if (MetroData.clickOnLabel) {
                        MetroData.clickOnLabel = false;
                    } else {
                        if (MetroData.selectedLabel != null) {
                            MetroData.selectedLabel.setHighlighted(false);
                        }
                        MetroData.selectedLabel = null;
                        MetroEditor.editLabel(null);
                    }

                } else if (MetroData.clickOnElement && MetroData.mode == Mode.REMOVE_ELEMENT) {
                    MetroData.clickOnElement = false;

                } else {
                    MetroData.mode = Mode.DEFAULT;
                }
            }
            if (MetroData.clickOnImage) {
                MetroData.clickOnImage = false;
            } else {
                if (MetroData.selectedImage != null) {
                    MetroData.selectedImage.setHighlighted(false);
                }
            }
        });
        /*
        canvas.setOnMousePressed(e -> {
            a = e.getSceneX();
            b = e.getSceneY();
            c = canvas.getTranslateX();
            d = canvas.getTranslateY();
        });

        canvas.setOnMouseDragged(e -> {
            if (!MetroData.elementPressed) {
                canvas.setTranslateX(e.getSceneX() - a + c);
                canvas.setTranslateY(e.getSceneY() - b + d);
            }
        });
        canvas.setOnMouseReleased(e -> {
            MetroData.elementPressed = false;
        });//*/

        canvas.setOnScroll(e -> {
            if (Math.abs(canvas.getTranslateX() + e.getDeltaX()) <= maxXMove) {
                canvas.setTranslateX(canvas.getTranslateX() + e.getDeltaX());
            }
            if (Math.abs(canvas.getTranslateY() + e.getDeltaY()) <= maxYMove) {
                canvas.setTranslateY(canvas.getTranslateY() + e.getDeltaY());
            }
        });

        MetroMapMaker.getScene().setOnKeyPressed(e -> {
            if (maxXMove != 0 || maxYMove != 0) {
                switch (e.getCode()) {
                    case A:
                        if (Math.abs(canvas.getTranslateX()) < maxXMove || canvas.getTranslateX() == maxXMove) {
                            canvas.setTranslateX(canvas.getTranslateX() - 2.5);
                        } else {
                            canvas.setTranslateX(-maxXMove);
                        }
                        break;
                    case D:
                        if (Math.abs(canvas.getTranslateX()) < maxXMove || canvas.getTranslateX() == -maxXMove) {
                            canvas.setTranslateX(canvas.getTranslateX() + 2.5);
                        } else {
                            canvas.setTranslateX(maxXMove);
                        }
                        break;
                    case W:
                        if (Math.abs(canvas.getTranslateY()) < maxYMove || canvas.getTranslateY() == maxYMove) {
                            canvas.setTranslateY(canvas.getTranslateY() - 2.5);
                        } else {
                            canvas.setTranslateY(-maxYMove);
                        }
                        break;
                    case S:
                        if (Math.abs(canvas.getTranslateY()) < maxYMove || canvas.getTranslateY() == -maxYMove) {
                            canvas.setTranslateY(canvas.getTranslateY() + 2.5);
                        } else {
                            canvas.setTranslateY(maxYMove);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public final void initializeWorkspace() {
        // Top
        topToolBar = new ToolBar();
        topToolBar.prefWidthProperty().bind(appPane.widthProperty());
        topToolBar.prefHeight(150);
        topToolBar.setPadding(new Insets(15.0, 20.0, 15.0, 20.0));
        topToolBar.setStyle(DesignConstants.DEFAULT_TOPTOOLBAR_STYLE);
        initializeToolbarButtons();

        // Right
        canvas = new Pane();

        // Left
        editor = new MetroEditor(app, this);
        editBar = MetroEditor.getVbox();
        editBar.setPrefSize(400, appPane.getHeight() - topToolBar.getHeight());
    }

    public static final void initializeToolbarButtons() {
        // 1
        newBTN = new Button("New");
        costomizeButton(newBTN);
        topToolBar.getItems().add(newBTN);

        // 2
        loadBTN = new Button("Load");
        costomizeButton(loadBTN);
        topToolBar.getItems().add(loadBTN);

        // 3
        saveBTN = new Button("Save");
        costomizeButton(saveBTN);
        topToolBar.getItems().add(saveBTN);

        // 4
        saveAsBTN = new Button("Save As");
        costomizeButton(saveAsBTN);
        topToolBar.getItems().add(saveAsBTN);

        // 5
        exportBTN = new Button("Export");
        costomizeButton(exportBTN);
        topToolBar.getItems().add(exportBTN);
        addSpacing();

        // 6
        undoBTN = new Button("Undo");
        undoBTN.setDisable(true);
        costomizeButton(undoBTN);
        topToolBar.getItems().add(undoBTN);

        // 7
        redoBTN = new Button("Redo");
        redoBTN.setDisable(true);
        costomizeButton(redoBTN);
        topToolBar.getItems().add(redoBTN);
        addSpacing();
        addSpacing();
        addSpacing();

        // 8
        aboutBTN = new Button("About");
        costomizeButton(aboutBTN);
        topToolBar.getItems().add(aboutBTN);

        // 9
        exitButton = new Button("Exit");
        costomizeButton(exitButton);
        topToolBar.getItems().add(exitButton);

        refreshTopToolbar();
    }

    private static void costomizeButton(Button btn) {
        btn.setFont(DesignConstants.DEFAULT_TOPTOOLBAR_BUTTON_FONT);
        btn.setPrefWidth(DesignConstants.DEFAULT_TOPTOOLBAR_BUTTON_WIDTH);
        btn.setTextFill(Color.WHITE);
        btn.setStyle(DesignConstants.DEFAULT_TOPTOOLBAR_BUTTON_STYLE);
    }

    private static void addSpacing() {
        Pane space = new Pane();
        space.setPrefWidth(112.0);
        topToolBar.getItems().add(space);
    }

    public final void initializeController() {
        // 1
        newBTN.setOnAction(e -> {
            if (!MetroFile.isSaved) {
                SaveOrDiscardDialog.show();
                if (SaveOrDiscardDialog.canExit == false) {
                    return;
                }
            }
            MetroFile.resetData();
            MetroData.resetData();
            MetroEditor.refresh();
            refreshTopToolbar();
        });

        // 2
        loadBTN.setOnAction(e -> {
            if (!MetroFile.isSaved) {
                SaveOrDiscardDialog.show();
                if (SaveOrDiscardDialog.canExit == false) {
                    return;
                }
            }
            try {
                final FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json"));
                File f = fc.showOpenDialog(MetroMapMaker.getStage());
                String s = f.getAbsolutePath();
                MetroFile.resetData();
                MetroFile.filePath = s;
                MetroFile.fileName = f.getName();
                MetroData.resetData();
                canvas.getChildren().clear();
                MetroFile.load(s);

            } catch (Exception ex) {
                //ex.printStackTrace();
            } finally {
                MetroFile.markSaved();
                MetroEditor.refresh();
                refreshTopToolbar();
            }
        });

        // 3
        saveBTN.setOnAction(e -> {
            try {
                // Get Destination
                if (MetroFile.filePath == null && MetroFile.fileName == null) {
                    appPane.setDisable(true);
                    MetroFile.requestCanceled = false;
                    Stage stg = new Stage();
                    Label exportLabel = new Label("Save");
                    exportLabel.setFont(Font.font("Avenir Next", FontWeight.NORMAL, 16.0));

                    HBox hbox1 = new HBox();
                    hbox1.setAlignment(Pos.CENTER);
                    Label nameL = new Label("Name: ");
                    TextField tf = new TextField();
                    hbox1.getChildren().add(nameL);
                    hbox1.getChildren().add(tf);

                    Button confirm = new Button("Confirm");
                    confirm.setPrefWidth(100.0);
                    confirm.setOnAction(eh -> {
                        MetroFile.mapName = "" + tf.getText();
                        appPane.setDisable(false);
                        stg.close();
                    });
                    Button cancel = new Button("Cancel");
                    cancel.setPrefWidth(100.0);
                    cancel.setOnAction(eh -> {
                        MetroFile.requestCanceled = true;
                        appPane.setDisable(false);
                        stg.close();
                    });
                    HBox hbox2 = new HBox();
                    hbox2.setAlignment(Pos.CENTER);
                    hbox2.setSpacing(20.0);
                    hbox2.getChildren().add(confirm);
                    hbox2.getChildren().add(cancel);

                    VBox vbox = new VBox();
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setSpacing(20.0);
                    vbox.getChildren().add(exportLabel);
                    vbox.getChildren().add(hbox1);
                    vbox.getChildren().add(hbox2);

                    BorderPane p = new BorderPane();
                    p.setCenter(vbox);
                    p.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);
                    Scene sc = new Scene(p, 270.0, 160.0);

                    stg.setScene(sc);
                    stg.setAlwaysOnTop(true);
                    stg.initStyle(StageStyle.UNDECORATED);
                    stg.setResizable(false);
                    stg.showAndWait();

                    if (MetroFile.requestCanceled) {
                        return;
                    }
                    MetroFile.filePath = "./UserData/work/" + MetroFile.mapName + " Metro";

                    // Update Recent
                    File recent = new File(MetroFile.RENCENT_PATH);
                    String originalText = "";
                    Scanner input = new Scanner(recent);
                    while (input.hasNextLine()) {
                        originalText += "\n" + input.nextLine();
                    }
                    input.close();
                    PrintWriter pw = new PrintWriter(recent);
                    pw.print(MetroFile.filePath);
                    pw.print(originalText);
                    pw.close();
                }
                MetroFile.save();

            } catch (FileNotFoundException ex) {

            } finally {
                MetroFile.save();
                refreshTopToolbar();
            }
        });

        // 4
        saveAsBTN.setOnAction(e -> {
            try {
                // Get Destination
                final FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("json", "*.json"));
                File f = fc.showSaveDialog(MetroMapMaker.getStage());
                String s = f.getAbsolutePath();
                if (!f.getName().contains(".json")) {
                    f = new File(f.getAbsolutePath() + ".json");
                    s = f.getAbsolutePath();
                }

                // Save
                MetroFile.saveAs(s);

                // Update Recent
                File recent = new File(MetroFile.RENCENT_PATH);
                String originalText = "";
                Scanner input = new Scanner(recent);
                while (input.hasNextLine()) {
                    originalText += "\n" + input.nextLine();
                }
                input.close();
                PrintWriter pw = new PrintWriter(recent);
                pw.print(s);
                pw.print(originalText);
                pw.close();
            } catch (Exception ex) {

            } finally {
                refreshTopToolbar();
            }
        });

        // 5
        exportBTN.setOnAction(e -> {
            try {
                appPane.setDisable(true);
                MetroFile.requestCanceled = false;
                //if (MetroFile.mapName == null) {
                Stage stg = new Stage();
                Label exportLabel = new Label("Export");
                exportLabel.setFont(Font.font("Avenir Next", FontWeight.NORMAL, 16.0));

                HBox hbox1 = new HBox();
                hbox1.setAlignment(Pos.CENTER);
                Label nameL = new Label("Name: ");
                TextField tf = new TextField();
                hbox1.getChildren().add(nameL);
                hbox1.getChildren().add(tf);

                Button confirm = new Button("Confirm");
                confirm.setPrefWidth(100.0);
                confirm.setOnAction(eh -> {
                    MetroFile.mapName = "" + tf.getText();
                    appPane.setDisable(false);
                    stg.close();
                });
                Button cancel = new Button("Cancel");
                cancel.setPrefWidth(100.0);
                cancel.setOnAction(eh -> {
                    MetroFile.requestCanceled = true;
                    appPane.setDisable(false);
                    stg.close();
                });
                HBox hbox2 = new HBox();
                hbox2.setAlignment(Pos.CENTER);
                hbox2.setSpacing(20.0);
                hbox2.getChildren().add(confirm);
                hbox2.getChildren().add(cancel);

                VBox vbox = new VBox();
                vbox.setAlignment(Pos.CENTER);
                vbox.setSpacing(20.0);
                vbox.getChildren().add(exportLabel);
                vbox.getChildren().add(hbox1);
                vbox.getChildren().add(hbox2);

                BorderPane p = new BorderPane();
                p.setCenter(vbox);
                p.setStyle(DesignConstants.DIALOG_BACKGROUND_STYLE);
                Scene sc = new Scene(p, 270.0, 160.0);

                stg.setScene(sc);
                stg.setAlwaysOnTop(true);
                stg.initStyle(StageStyle.UNDECORATED);
                stg.setResizable(false);
                stg.show();
                //}
                if (MetroFile.requestCanceled) {
                    return;
                }
                String fp = "./UserData/work/" + MetroFile.mapName + " Metro";
                MetroFile.export(fp);
                exportToPNG();
            } catch (IllegalArgumentException ex) {
                //ex.printStackTrace();
            } catch (Exception ex1) {
                //ex1.printStackTrace();
            } finally {
                appPane.setDisable(false);
            }
        });

        // 6
        undoBTN.setOnAction(e -> {
            MetroData.performUndoTransaction();
        });

        // 7
        redoBTN.setOnAction(e -> {
            MetroData.performDoTransaction();
        });

        // 8
        aboutBTN.setOnAction(e -> {
            AboutView.show();
        });

        // 9
        exitButton.setOnAction(e -> {
            if (MetroFile.isSaved) {
                System.exit(0);
            }
            SaveOrDiscardDialog.show();
            if (SaveOrDiscardDialog.canExit) {
                System.exit(0);
            }
            e.consume();
        });

    }

    public static void refreshTopToolbar() {
        undoBTN.setDisable(!MetroData.hasUndo());
        redoBTN.setDisable(!MetroData.hasRedo());
        saveBTN.setDisable(MetroFile.isSaved);
        saveAsBTN.setDisable(false);
    }

    public static Pane getCanvas() {
        return canvas;
    }

    public static MetroData getMetroData() {
        return dataManager;
    }

    public static void exportToPNG() {
        //PixelReader pr = new PixelReader();
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        File file = new File("./UserData/work/" + MetroFile.mapName + " Metro.png");
        try {
            BufferedImage img = SwingFXUtils.fromFXImage(image, null);

            final double x = (DesignConstants.DEFAULT_CANVAS_SIZE - canvasWidth) / 2 - maxXMove;
            final double y = (DesignConstants.DEFAULT_CANVAS_SIZE - canvasHeight) / 2 - maxYMove;
            final double width = canvasWidth + 1.0 * maxXMove;
            final double height = canvasHeight + 1.0 * maxYMove;

            BufferedImage croppedImage = img.getSubimage((int) x, (int) y, (int) width, (int) height);
            image = SwingFXUtils.toFXImage(croppedImage, null);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static boolean isGridVisible;

    public static void showGrid(boolean value) {
        grid.setVisible(value);
        isGridVisible = value;
    }

    public static void installGrid() {
        canvas.getChildren().add(grid);
        showGrid(false);
    }

    public static void increaseMapSize() {
        maxXMove += 50.0;
        maxYMove += 50.0;
    }

    public static void decreaseMapSize() {
        maxXMove -= 50.0;
        maxYMove -= 50.0;

        canvas.setTranslateX(0.0);
        canvas.setTranslateY(0.0);
    }
}
