
package app;

import data.MetroFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * @author Yifan Wang
 */
public class MetroMapMaker extends Application {

    private static Stage stage;
    private static Scene scene;
    private static final BorderPane appPane = new BorderPane();

    public static boolean isStartPage;

    private final SplitPane welcomeSplitPane = new SplitPane();
    private final Pane leftPane = new Pane();
    private final Pane rightPane = new Pane();

    protected static MetroWorkspace workspace;

    public static final String APP_FONT_FAMILY = "Avenir";

    public static void main(String[] args) {
        isStartPage = true;
        Locale.setDefault(Locale.US);
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        appPane.setMinSize(800, 600);

        initializeLeftPane();
        initializeRightPane();

        // Set up the AppPane
        this.welcomeSplitPane.getItems().add(leftPane);
        this.welcomeSplitPane.getItems().add(rightPane);
        this.welcomeSplitPane.setDividerPositions(0.381966);
        this.welcomeSplitPane.prefWidthProperty().bind(stage.widthProperty());
        appPane.prefWidthProperty().bind(stage.widthProperty());
        // Set up the Scene
        appPane.setCenter(welcomeSplitPane);
        scene = new Scene(appPane);

        // Set up the Stage
        stage.setScene(scene);
        stage.setTitle("Welcome to Metro Map Maker");
        stage.setMaximized(true);
        stage.show();
        stage.setMinHeight(stage.getHeight());

        scene.getWindow().setOnCloseRequest(e -> {
            if (isStartPage) {
                isStartPage = false;
                workspace = new MetroWorkspace(this);
                stage.setTitle("Metro Map Maker - Untitiled");
                e.consume();
            } else {
                if (MetroFile.isSaved) {
                    stop();
                } else {
                    SaveOrDiscardDialog.show();
                    if (SaveOrDiscardDialog.canExit) {
                        stop();
                    } else {
                        e.consume();
                    }
                }
            }
        });

    }

    @Override
    public void stop() {

    }

    private void initializeRightPane() {
        // Set up the left of AppPane
        rightPane.setPrefSize(stage.getWidth() * 0.75, stage.getHeight());
        rightPane.setStyle("-fx-background-color-: #FFF");

        VBox vbox = new VBox();
        vbox.prefWidthProperty().bind(rightPane.widthProperty());
        vbox.prefHeightProperty().bind(rightPane.heightProperty());
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20.0);

        // Row 1 -- Logo
        Image img = new Image("file:./icons/Logo.png");
        ImageView logo = new ImageView(img);
        logo.setFitWidth(200.0);
        logo.setFitHeight(200.0);
        vbox.getChildren().add(logo);

        // Row 2 -- Welcome
        Label welcome = new Label("Welcome to Metro Map Maker");
        welcome.setFont(Font.font(APP_FONT_FAMILY, FontWeight.BOLD, FontPosture.REGULAR, 40));
        vbox.getChildren().add(welcome);

        // Row 3 -- Create A New Map
        Label createNew = new Label("Create A New Map");
        createNew.setFont(Font.font(APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 20));
        createNew.setUnderline(true);
        createNew.setOnMouseClicked(e -> {
            // DO SOMETHING HERE
            isStartPage = false;
            workspace = new MetroWorkspace(this);
            stage.setTitle("Metro Map Maker - Untitiled");
        });
        vbox.getChildren().add(createNew);

        // Row 3 -- Close 
        Label close = new Label("Close Welcome Dialog");
        close.setFont(Font.font(APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 20));
        close.setUnderline(true);
        close.setOnMouseClicked(e -> {
            // DO SOMETHING HERE
            isStartPage = false;
            workspace = new MetroWorkspace(this);
            stage.setTitle("Metro Map Maker - Untitiled");
        });
        vbox.getChildren().add(close);

        // Row 4 -- Exit 
        Label exit = new Label("Exit This Application");
        exit.setFont(Font.font(APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 20));
        exit.setUnderline(true);
        exit.setOnMouseClicked(e -> {
            isStartPage = false;
            System.exit(0);
        });
        vbox.getChildren().add(exit);

        rightPane.getChildren().add(vbox);
    }

    /**
     * Initialize the left pane
     */
    private void initializeLeftPane() {
        // Set up the left of AppPane
        leftPane.setPrefSize(stage.getWidth() * 0.25, stage.getHeight());
        leftPane.setStyle("-fx-background-color: #c7c7c7");

        VBox vbox = new VBox();
        vbox.prefWidthProperty().bind(leftPane.widthProperty());
        vbox.prefHeightProperty().bind(leftPane.heightProperty());
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20.0);

        // Row 1 --  Label Recent Works
        Label recentWorks = new Label("Recent Works");
        recentWorks.setFont(Font.font(APP_FONT_FAMILY, FontWeight.BOLD, FontPosture.REGULAR, 40));
        vbox.getChildren().add(recentWorks);

        // Load Recent Work List
        ArrayList<Label> list = getRecentWorksByFileIO();
        for (Label l : list) {
            vbox.getChildren().add(l);
        }

        leftPane.getChildren().add(vbox);
    }

    private ArrayList<Label> getRecentWorksByFileIO() {
        ArrayList<Label> list = new ArrayList<>();
        ArrayList<File> fileList = new ArrayList<>();

        File file = new File("./Data/recentWorks.txt");
        try {
            Scanner input = new Scanner(file);

            while (input.hasNext()) {
                File f = new File(input.nextLine());
                if (f.exists()) {
                    fileList.add(f);
                }
            }

        } catch (FileNotFoundException ex) {

        }

        if (fileList.isEmpty()) {
            Label empty = new Label("No Recent Work, It's Time to Create One!");
            empty.setFont(Font.font(APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 20));
            empty.setUnderline(true);
            list.add(empty);
            return list;
        }

        for (File f : fileList) {
            if (list.size() == 5) {
                break;
            }
            if (f.isFile()) {
                String fileName = f.getName();
                Label fileLabel = new Label(fileName);
                fileLabel.setFont(Font.font(APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 20));
                fileLabel.setUnderline(true);
                list.add(fileLabel);

                // If the label is clicked then load the file
                fileLabel.setOnMouseClicked(e -> {
                    workspace = new MetroWorkspace(this);
                    MetroFile.filePath = f.getAbsolutePath();
                    try {
                        MetroFile.load(MetroFile.filePath);
                    } catch (IOException ex) {

                    }
                    MetroFile.hasLoadRequest = true;
                    MetroFile.fileName = fileName;
                    //MetroFile.mapName = fileName.substring(0, fileName.lastIndexOf(".json"));
                    MetroFile.markSaved();
                    MetroEditor.refresh();
                    MetroWorkspace.refreshTopToolbar();
                    isStartPage = false;
                    stage.setTitle("Metro Map Maker - " + fileName);
                });
            }
        }
        return list;
    }

    /**
     *
     * @return A list of Label with recent work file name
     */
    private ArrayList<Label> getRecentWorks() {
        ArrayList<Label> list = new ArrayList<>();

        File file = new File("./UserData/work/");
        File[] fileList = file.listFiles();

        // If no recent work
        if (fileList.length == 0) {
            Label empty = new Label("No Recent Work, It's Time to Create One!");
            empty.setFont(Font.font(APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 20));
            empty.setUnderline(true);
            list.add(empty);
            return list;
        }

        for (File f : fileList) {
            if (list.size() == 5) {
                break;
            }
            if (f.isFile()) {
                String fileName = f.getName();
                Label fileLabel = new Label(fileName);
                fileLabel.setFont(Font.font(APP_FONT_FAMILY, FontWeight.NORMAL, FontPosture.ITALIC, 20));
                fileLabel.setUnderline(true);
                list.add(fileLabel);

                // If the label is clicked then load the file
                fileLabel.setOnMouseClicked(e -> {
                    // LOAD FILE
                    //System.out.println("Load File: " + fileName);
                });
            }
        }

        return list;
    }

    /**
     *
     * @return the stage of the app
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     *
     * @return get the Scene of the app
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Set a new Scene for the app.
     *
     * @param newScene non-null new Scene to be assigned.
     */
    public static void setScene(Scene newScene) {
        if (newScene != null) {
            scene = newScene;
            stage.setScene(scene);
        }
    }

    public static BorderPane getAppPane() {
        return appPane;
    }

    public static MetroWorkspace getWorkspace() {
        return workspace;
    }

    public static void setWorkspace(MetroWorkspace workspace) {
        MetroMapMaker.workspace = workspace;
    }
}
