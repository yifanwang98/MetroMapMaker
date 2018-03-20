package data;

import app.MetroMapMaker;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * @author Yifan Wang
 */
public final class DesignConstants {
    
    // canvas.getChildren(int index)
    public static int INDEX = 1;
    
    // APP
    public static final String VERSION = "Version: 1.20.1214";

    // Line
    public static final double MAX_LINE_THICKNESS = 12.0;
    public static final double MIN_LINE_THICKNESS = 5.0;
    public static final double DEFAULT_LINE_THICKNESS = 7.0;
    public static final Font LINE_LABEL_FONT = Font.font("Georgia",
            FontWeight.BOLD, FontPosture.REGULAR, 15);
    
    // Canvas
    public static final String DEFAULT_BACKGROUND_STYLE = "-fx-background-color:#ffffff";
    public static final double DEFAULT_CANVAS_SIZE = 2000.0;
    
    // Top Toolbar
    public static final String DEFAULT_TOPTOOLBAR_STYLE = "-fx-background-color:#000000";
    public static final String DEFAULT_TOPTOOLBAR_BUTTON_STYLE = "-fx-background-color:#606060";
    public static final Font DEFAULT_TOPTOOLBAR_BUTTON_FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 13.5);
    public static final double DEFAULT_TOPTOOLBAR_BUTTON_WIDTH = 100.0;
    
    // Editor
    public static final String DEFAULT_EDITOR_STYLE = "-fx-background-color:#aeaeae;"
                + "-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;";
    
    // Dialog
    public static final String DIALOG_BACKGROUND_STYLE = "-fx-background-color:#F6FDFF";
}
