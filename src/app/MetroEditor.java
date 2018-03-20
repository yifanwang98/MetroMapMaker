package app;

import control.EditController;
import data.DesignConstants;
import data.MetroData;
import data.MetroFile;
import data.MetroLine;
import data.MetroMapLabel;
import data.Station;
import data.StationNameComparator;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import transactions.LabelFontChange;
import transactions.LineThicknessChange;
import transactions.StationColorChange;
import transactions.StationRadiusChange;

/**
 * @author Yifan Wang
 */
public class MetroEditor {

    private static MetroMapMaker app;
    private static MetroWorkspace workspace;
    private static EditController editController;

    private static final Font FONT = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.BOLD, 15);
    private static final Font FONT1 = Font.font(MetroMapMaker.APP_FONT_FAMILY, FontWeight.NORMAL, 15);

    public static final String ICON_DIRC = "file:./icons/";

    private static final double ROW_SPACING = 10.0;

    private static final VBox VBOX = new VBox();

    private static final VBox ROW1 = new VBox();
    private static ChoiceBox lineCb = new ChoiceBox();
    private static ColorPicker lineColorPicker = new ColorPicker();
    private static Button addLineBTN = new Button();
    private static Button removeLineBTN = new Button();
    private static Button addStationBTN = new Button("Add Station");
    private static Button removeStationBTN = new Button("Remove Station");
    private static Button listStationBTN = new Button();
    private static Slider slider1 = new Slider(0, 1, 0.5);// Thickness

    private static final VBox ROW2 = new VBox();
    private static ChoiceBox stationCb = new ChoiceBox();
    private static ColorPicker stationColorPicker = new ColorPicker();
    private static Button minusStationBTN = new Button();
    private static Button plusStationBTN = new Button();
    private static Button snapBTN = new Button("Snap");
    private static Button moveLabelBTN = new Button("Move Label");
    private static Button rotateBTN = new Button();
    private static Slider slider2 = new Slider(0, 1, 0.5);// Radius

    private static final HBox ROW3 = new HBox();
    private static ChoiceBox fromCb = new ChoiceBox();
    private static ChoiceBox toCb = new ChoiceBox();
    private static Button findRouteBTN = new Button();

    private static final VBox ROW4 = new VBox();
    private static ColorPicker backgroundColorPicker = new ColorPicker();
    private static Button setBgImgBTN = new Button("Set Image Background");
    private static Button addImgBTN = new Button("Add Image");
    private static Button addLabelBTN = new Button("Add Label");
    private static Button removeElementBTN = new Button("Remove Element");

    private static final VBox ROW5 = new VBox();
    private static ColorPicker textColorPicker = new ColorPicker();
    private static Button boldBTN = new Button("B");
    private static Button italicBTN = new Button("I");
    private static ComboBox sizeCb = new ComboBox();
    private static ComboBox fontCb = new ComboBox();

    private static final VBox ROW6 = new VBox();
    private static CheckBox showGridCb = new CheckBox();
    private static Button increaseSizeBTN = new Button();
    private static Button decreaseSizeBTN = new Button();
    private static Button zoomInBTN = new Button();
    private static Button zoomOutBTN = new Button();

    public MetroEditor(MetroMapMaker app, MetroWorkspace workspace) {
        MetroEditor.app = app;

        VBOX.setSpacing(3.0);
        VBOX.setAlignment(Pos.TOP_CENTER);
        VBOX.setStyle("-fx-background-color:#aeaeae;"
                + "-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;");

        initializeAll();
    }

    public static void refresh() {
        ROW1.getChildren().clear();
        ROW2.getChildren().clear();
        ROW3.getChildren().clear();
        ROW4.getChildren().clear();
        ROW5.getChildren().clear();
        ROW6.getChildren().clear();
        VBOX.getChildren().clear();

        editLabel(null);

        initializeAll();

        if (MetroData.selectedLabel != null) {
            disableRow1(true);
            disableRow2(true);
            disableRow3(true);
            disableRow4(true);
            disableRow5(false);

            textColorPicker.setValue(MetroData.selectedLabel.getColor());
            boldBTN.setOpacity(MetroData.selectedLabel.isBold() ? 1.0 : 0.5);
            italicBTN.setOpacity(MetroData.selectedLabel.isItalic() ? 1.0 : 0.5);
            sizeCb.setValue((int) MetroData.selectedLabel.getSize());
            fontCb.setValue(MetroData.selectedLabel.getFontFamily());
        }
    }

    private static void initializeAll() {
        initializeEditor();
        initializeControllers1();
        initializeControllers2();
        initializeControllers3();
        initializeControllers4();
        initializeControllers5();
        initializeControllers6();
    }

    public static void initializeEditor() {
        // Row 1
        block1();
        block2();
        block3();
        block4();
        block5();
        block6();
        // Finally
        VBOX.getChildren().add(ROW1);
        VBOX.getChildren().add(ROW2);
        VBOX.getChildren().add(ROW3);
        VBOX.getChildren().add(ROW4);
        VBOX.getChildren().add(ROW5);
        VBOX.getChildren().add(ROW6);

        disableRow2(MetroData.isEmpty());
        disableRow5(true);
    }

    public static VBox getVbox() {
        return VBOX;
    }

    private static void block1() {
        HBox hbox1 = new HBox();
        Label metroLine = new Label("Metro Lines");
        metroLine.setFont(FONT);
        hbox1.getChildren().add(metroLine);
        lineCb = new ChoiceBox();
        lineCb.setMinWidth(150.0);
        lineCb.setItems(MetroData.getLineNames());
        if (MetroData.getSelectedLine() != null) {
            lineCb.setValue(MetroData.getSelectedLine());
        }
        hbox1.getChildren().add(lineCb);
        lineColorPicker = new ColorPicker();
        lineColorPicker.setPrefWidth(113.0);
        if (MetroData.getSelectedLine() != null) {
            lineColorPicker.setValue(MetroData.getSelectedLine().getColor());
        }
        hbox1.getChildren().add(lineColorPicker);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(13.0);
        hbox1.setAlignment(Pos.CENTER);
        ROW1.getChildren().add(hbox1);

        HBox hbox2 = new HBox();
        Image addImg = new Image(ICON_DIRC + "add.png");
        addLineBTN = new Button();
        addLineBTN.setGraphic(new ImageView(addImg));
        addLineBTN.setPrefSize(30, 60);
        addLineBTN.setTooltip(new Tooltip("Add Line"));
        hbox2.getChildren().add(addLineBTN);

        Image removeImg = new Image(ICON_DIRC + "remove.png");
        removeLineBTN = new Button();
        removeLineBTN.setGraphic(new ImageView(removeImg));
        removeLineBTN.setPrefSize(30, 60);
        removeLineBTN.setTooltip(new Tooltip("Remove Line"));
        removeLineBTN.setDisable(MetroData.isEmpty());
        hbox2.getChildren().add(removeLineBTN);

        addStationBTN = new Button("Add Station");
        addStationBTN.setWrapText(true);
        addStationBTN.setTextAlignment(TextAlignment.CENTER);
        addStationBTN.setFont(FONT1);
        addStationBTN.setPrefSize(72.0, 60.0);
        addStationBTN.setDisable(MetroData.isEmpty());
        hbox2.getChildren().add(addStationBTN);

        removeStationBTN = new Button("Remove Station");
        removeStationBTN.setWrapText(true);
        removeStationBTN.setTextAlignment(TextAlignment.CENTER);
        removeStationBTN.setFont(FONT1);
        removeStationBTN.setPrefSize(78.0, 60.0);
        removeStationBTN.setDisable(MetroData.getSelectedLine() == null ? true : MetroData.getSelectedLine().isEmpty());
        hbox2.getChildren().add(removeStationBTN);

        Image listImg = new Image(ICON_DIRC + "list.png");
        listStationBTN = new Button();
        listStationBTN.setGraphic(new ImageView(listImg));
        listStationBTN.setPrefSize(20, 60);
        listStationBTN.setTooltip(new Tooltip("Show List of All Stations"));
        listStationBTN.setDisable(MetroData.isEmpty());
        hbox2.getChildren().add(listStationBTN);
        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(10.0);
        ROW1.getChildren().add(hbox2);

        slider1 = new Slider(DesignConstants.MIN_LINE_THICKNESS, DesignConstants.MAX_LINE_THICKNESS, DesignConstants.DEFAULT_LINE_THICKNESS);
        if (MetroData.getSelectedLine() != null) {
            slider1.setValue(MetroData.getSelectedLine().getThickness());
        }
        ROW1.getChildren().add(slider1);

        ROW1.setSpacing(ROW_SPACING);
        ROW1.setAlignment(Pos.CENTER);
        ROW1.setStyle("-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;");
        ROW1.prefWidthProperty().bind(VBOX.widthProperty());
        ROW1.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
    }

    private static void block2() {
        stationCb = new ChoiceBox();
        stationColorPicker = new ColorPicker();
        minusStationBTN = new Button();
        plusStationBTN = new Button();
        snapBTN = new Button("Snap");
        moveLabelBTN = new Button("Move Label");
        rotateBTN = new Button();
        slider2 = new Slider(7, 13, 10);

        HBox hbox1 = new HBox();
        Label metroStation = new Label("Metro Stations");
        metroStation.setFont(FONT);
        hbox1.getChildren().add(metroStation);
        stationCb.setMinWidth(130.0);
        stationCb.setMaxWidth(130.0);
        MetroLine metroLine = MetroData.getSelectedLine();
        if (metroLine != null) {
            if (!metroLine.isEmpty()) {
                stationCb.setItems(MetroData.getSelectedLine().getStationList());
                if (MetroData.getSelectedStation() == null) {
                    MetroData.setSelectedStation(MetroData.getSelectedLine().getFirstStation());
                }
                stationCb.setValue(MetroData.getSelectedStation());
            }
        }
        hbox1.getChildren().add(stationCb);
        stationColorPicker.setPrefWidth(113.0);
        if (stationCb.getValue() != null) {
            stationColorPicker.setValue(((Station) stationCb.getValue()).getColor());
        }
        hbox1.getChildren().add(stationColorPicker);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(11.0);
        hbox1.setAlignment(Pos.CENTER);
        ROW2.getChildren().add(hbox1);

        ////////////////////////////////////////////////////////
        HBox hbox2 = new HBox();
        Image addImg = new Image(ICON_DIRC + "add.png");
        plusStationBTN.setGraphic(new ImageView(addImg));
        plusStationBTN.setPrefSize(30, 60);
        plusStationBTN.setTooltip(new Tooltip("Add Station"));
        hbox2.getChildren().add(plusStationBTN);

        Image removeImg = new Image(ICON_DIRC + "remove.png");
        minusStationBTN.setGraphic(new ImageView(removeImg));
        minusStationBTN.setPrefSize(30, 60);
        minusStationBTN.setDisable(MetroData.getSelectedLine() == null ? true : MetroData.getSelectedLine().isEmpty());
        minusStationBTN.setTooltip(new Tooltip("Remove Station"));
        hbox2.getChildren().add(minusStationBTN);

        snapBTN.setWrapText(true);
        snapBTN.setTextAlignment(TextAlignment.CENTER);
        snapBTN.setFont(FONT1);
        snapBTN.setPrefSize(72.0, 60.0);
        if (MetroData.getSelectedStation() == null) {
            snapBTN.setDisable(true);
        } else {
            snapBTN.setDisable(!MetroData.getSelectedStation().snapToGridable());
        }
        hbox2.getChildren().add(snapBTN);

        moveLabelBTN.setWrapText(true);
        moveLabelBTN.setTextAlignment(TextAlignment.CENTER);
        moveLabelBTN.setFont(FONT1);
        moveLabelBTN.setPrefSize(72.0, 60.0);
        hbox2.getChildren().add(moveLabelBTN);

        Image rotateImg = new Image(ICON_DIRC + "rotate.png");
        rotateBTN.setGraphic(new ImageView(rotateImg));
        rotateBTN.setPrefSize(27.0, 60.0);
        rotateBTN.setTooltip(new Tooltip("Rotate the station label"));
        hbox2.getChildren().add(rotateBTN);
        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(5.0);
        ROW2.getChildren().add(hbox2);

        ////////////////////////////////////////////////////////
        if (stationCb.getValue() != null) {
            slider2.setValue(((Station) stationCb.getValue()).getRadius());
        }
        ROW2.getChildren().add(slider2);

        ////////////////////////////////////////////////////////
        ROW2.setSpacing(ROW_SPACING);
        ROW2.setAlignment(Pos.CENTER);
        ROW2.setStyle("-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;");
        ROW2.prefWidthProperty().bind(VBOX.widthProperty());
        ROW2.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
    }

    private static void block3() {
        fromCb = new ChoiceBox();
        toCb = new ChoiceBox();
        findRouteBTN = new Button();

        VBox col1 = new VBox();
        col1.setSpacing(7);
        col1.getChildren().add(fromCb);
        col1.getChildren().add(toCb);
        fromCb.setPrefWidth(200);
        fromCb.setTooltip(new Tooltip("Choose From Station"));
        final ObservableList<Station> stationList = FXCollections.observableList(MetroData.stations);
        stationList.sort(new StationNameComparator());
        fromCb.setItems(stationList);
        toCb.setPrefWidth(200);
        toCb.setTooltip(new Tooltip("Choose To Station"));
        toCb.setItems(stationList);
        if (!stationList.isEmpty()) {
            fromCb.setValue(stationList.get(0));
            toCb.setValue(stationList.get(0));
        }
        ROW3.getChildren().add(col1);

        Image findImg = new Image(ICON_DIRC + "search.png");
        findRouteBTN.setGraphic(new ImageView(findImg));
        findRouteBTN.setPrefSize(62.0, 62.0);
        findRouteBTN.setTooltip(new Tooltip("Find Route"));
        if (stationList.isEmpty()) {
            findRouteBTN.setDisable(true);
        }
        ROW3.getChildren().add(findRouteBTN);

        ROW3.setSpacing(ROW_SPACING * 2);
        ROW3.setAlignment(Pos.CENTER);
        ROW3.setStyle("-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;");
        ROW3.prefWidthProperty().bind(VBOX.widthProperty());
        ROW3.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
    }

    private static void block4() {
        backgroundColorPicker = new ColorPicker();
        setBgImgBTN = new Button("Set Image Background");
        addImgBTN = new Button("Add Image");
        addLabelBTN = new Button("Add Label");
        removeElementBTN = new Button("Remove Element");

        HBox hbox1 = new HBox();
        Label decor = new Label("Decor");
        decor.setFont(FONT);
        hbox1.getChildren().add(decor);
        backgroundColorPicker.setPrefWidth(113.0);
        backgroundColorPicker.setTooltip(new Tooltip("Choose Background Color"));
        String s = MetroData.getBgStyle();
        MetroWorkspace.getCanvas().setStyle(s);
        s = s.substring(s.indexOf("#"));
        backgroundColorPicker.setValue(Color.web(s));
        hbox1.getChildren().add(backgroundColorPicker);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(206.0);
        ROW4.getChildren().add(hbox1);

        HBox hbox2 = new HBox();
        setBgImgBTN.setWrapText(true);
        setBgImgBTN.setTextAlignment(TextAlignment.CENTER);
        setBgImgBTN.setFont(FONT1);
        setBgImgBTN.setPrefSize(103.0, 60.0);
        hbox2.getChildren().add(setBgImgBTN);

        addImgBTN.setWrapText(true);
        addImgBTN.setTextAlignment(TextAlignment.CENTER);
        addImgBTN.setFont(FONT1);
        addImgBTN.setPrefSize(82.0, 60.0);
        hbox2.getChildren().add(addImgBTN);

        addLabelBTN.setWrapText(true);
        addLabelBTN.setTextAlignment(TextAlignment.CENTER);
        addLabelBTN.setFont(FONT1);
        addLabelBTN.setPrefSize(82.0, 60.0);
        hbox2.getChildren().add(addLabelBTN);

        removeElementBTN.setWrapText(true);
        removeElementBTN.setTextAlignment(TextAlignment.CENTER);
        removeElementBTN.setFont(FONT1);
        removeElementBTN.setPrefSize(82.0, 60.0);
        hbox2.getChildren().add(removeElementBTN);
        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(5.0);
        ROW4.getChildren().add(hbox2);

        ROW4.setSpacing(ROW_SPACING);
        ROW4.setAlignment(Pos.CENTER);
        ROW4.setStyle("-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;");
        ROW4.prefWidthProperty().bind(VBOX.widthProperty());
        ROW4.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
    }

    private static void block5() {
        textColorPicker = new ColorPicker();
        boldBTN = new Button("B");
        italicBTN = new Button("I");
        sizeCb = new ComboBox();
        fontCb = new ComboBox();

        HBox hbox1 = new HBox();
        Label decor = new Label("Font");
        decor.setFont(FONT);
        hbox1.getChildren().add(decor);
        textColorPicker.setPrefWidth(113.0);
        textColorPicker.setTooltip(new Tooltip("Set Label Color"));
        hbox1.getChildren().add(textColorPicker);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(204.0);
        ROW5.getChildren().add(hbox1);

        HBox hbox2 = new HBox();
        boldBTN.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        boldBTN.setPrefSize(50.0, 50.0);
        boldBTN.setTooltip(new Tooltip("Bold"));
        hbox2.getChildren().add(boldBTN);

        italicBTN.setFont(Font.font("Georgia", FontWeight.BOLD, FontPosture.ITALIC, 26));
        italicBTN.setPrefSize(50.0, 50.0);
        italicBTN.setTooltip(new Tooltip("Italic"));
        hbox2.getChildren().add(italicBTN);

        sizeCb.setPrefSize(60.0, 50.0);
        sizeCb.setEditable(false);
        initializeSizeComboBox();
        hbox2.getChildren().add(sizeCb);

        fontCb.setPrefSize(140.0, 50.0);
        initializeFontComboBox();
        hbox2.getChildren().add(fontCb);

        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(15.0);
        ROW5.getChildren().add(hbox2);

        ROW5.setSpacing(ROW_SPACING);
        ROW5.setAlignment(Pos.CENTER);
        ROW5.setStyle("-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;");
        ROW5.prefWidthProperty().bind(VBOX.widthProperty());
        ROW5.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
    }

    private static void block6() {
        showGridCb = new CheckBox();
        increaseSizeBTN = new Button();
        decreaseSizeBTN = new Button();
        zoomInBTN = new Button();
        zoomOutBTN = new Button();

        HBox hbox1 = new HBox();
        Label nav = new Label("Navigation");
        nav.setFont(FONT);
        hbox1.getChildren().add(nav);

        HBox hbox0 = new HBox();
        showGridCb.setSelected(MetroWorkspace.isGridVisible);
        Label showGrid = new Label("Show Grid");
        showGrid.setFont(FONT);
        hbox0.getChildren().add(showGridCb);
        hbox0.getChildren().add(showGrid);

        hbox1.getChildren().add(hbox0);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(204.0);
        ROW6.getChildren().add(hbox1);

        HBox hbox2 = new HBox();
        Image incSize = new Image(ICON_DIRC + "increase.png");
        increaseSizeBTN.setGraphic(new ImageView(incSize));
        increaseSizeBTN.setPrefSize(50.0, 50.0);
        increaseSizeBTN.setTooltip(new Tooltip("Increase Map Size"));
        if (EditController.increaseCounter == 5) {
            increaseSizeBTN.setDisable(true);
        }
        hbox2.getChildren().add(increaseSizeBTN);

        Image decSize = new Image(ICON_DIRC + "decrease.png");
        decreaseSizeBTN.setGraphic(new ImageView(decSize));
        decreaseSizeBTN.setPrefSize(50.0, 50.0);
        decreaseSizeBTN.setTooltip(new Tooltip("Decrease Map Size"));
        if (EditController.increaseCounter == 0) {
            decreaseSizeBTN.setDisable(true);
        }
        hbox2.getChildren().add(decreaseSizeBTN);

        Image zoomInImg = new Image(ICON_DIRC + "zoom-in.png");
        zoomInBTN.setGraphic(new ImageView(zoomInImg));
        zoomInBTN.setPrefSize(50.0, 50.0);
        zoomInBTN.setTooltip(new Tooltip("Zoom In"));
        hbox2.getChildren().add(zoomInBTN);

        Image zoomOutImg = new Image(ICON_DIRC + "zoom-out.png");
        zoomOutBTN.setGraphic(new ImageView(zoomOutImg));
        zoomOutBTN.setPrefSize(50.0, 50.0);
        zoomOutBTN.setTooltip(new Tooltip("Zoom Out"));
        hbox2.getChildren().add(zoomOutBTN);

        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(15.0);
        ROW6.getChildren().add(hbox2);

        ROW6.setSpacing(ROW_SPACING);
        ROW6.setAlignment(Pos.CENTER);
        ROW6.setStyle("-fx-border-style: solid outside;"
                + "-fx-border-width: 2;"
                + "-fx-border-color: black;");
        ROW6.prefWidthProperty().bind(VBOX.widthProperty());
        ROW6.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
    }

    private static void initializeSizeComboBox() {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 10; i < 24; i += 2) {
            list.add(i);
        }

        for (int i = 24; i <= 100; i += 6) {
            list.add(i);
        }

        ObservableList<Integer> list1 = FXCollections.observableArrayList(list);
        sizeCb.setItems(list1);
        sizeCb.setEditable(true);
        sizeCb.setValue(12);
        sizeCb.setTooltip(new Tooltip("Choose or Enter Text Size"));
    }

    private static void initializeFontComboBox() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Arial");
        list.add("American Typewriter");
        list.add("Avenir");
        list.add("Cochin");
        list.add("Courier");
        list.add("Didot");
        list.add("Georgia");
        list.add("Helvetica");
        list.add("Noteworthy");
        list.add("Times New Roman");

        ObservableList<String> list1 = FXCollections.observableArrayList(list);
        fontCb.setItems(list1);
        fontCb.setEditable(true);
        fontCb.setValue("Avenir");
        fontCb.setTooltip(new Tooltip("Choose or Enter Text Font"));
    }

    private static LineThicknessChange ltc;

    private static void initializeControllers1() {
        lineCb.setOnAction(e -> {
            if (lineCb.getValue() != null) {
                EditController.chooseLineHandler((MetroLine) lineCb.getValue());
            }
        });

        lineColorPicker.setOnAction(e -> {
            if (lineCb.getValue() != null) {
                EditController.processLineColorChange(lineColorPicker.getValue());
            }
        });

        addLineBTN.setOnAction(e -> {
            EditController.processAddLine();
        });

        removeLineBTN.setOnAction(e -> {
            EditController.processRemoveLine();
        });

        addStationBTN.setOnAction(e -> {
            EditController.processAddEmptyStationToLine();
        });

        removeStationBTN.setOnAction(e -> {
            if (lineCb.getValue() != null) {
                EditController.processRemoveEmptyStaionFromLine();
            }
        });

        listStationBTN.setOnAction(e -> {
            EditController.listStationsOfLine();
        });

        slider1.setOnMousePressed(e -> {
            if (MetroData.getSelectedLine() != null) {
                ltc = new LineThicknessChange(MetroData.getSelectedLine());
            }
        });

        slider1.setOnMouseDragged(e -> {
            EditController.processLineThicknessChange(slider1.getValue());
        });

        slider1.setOnMouseReleased(e -> {
            if (MetroData.getSelectedLine() != null) {
                ltc.setNewScale(slider1.getValue());
                MetroData.addTransaction(ltc);
            }
        });
    }

    private static StationRadiusChange src;

    private static void initializeControllers2() {
        stationCb.setOnAction(e -> {
            Station s = (Station) stationCb.getValue();
            MetroData.setSelectedStation(s);
            stationColorPicker.setValue(s.getColor());
            slider2.setValue(s.getRadius());
            //EditController.chooseStationHandler();
        });

        stationColorPicker.setOnAction(e -> {
            Station s = ((Station) stationCb.getValue());
            if (s != null) {
                final StationColorChange scc = new StationColorChange(s, stationColorPicker.getValue());
                MetroData.addTransaction(scc);
            }
            //EditController.processStationColorChange();
        });

        minusStationBTN.setOnAction(e -> {
            EditController.processRemoveStation();
        });

        plusStationBTN.setOnAction(e -> {
            EditController.processAddStation();
        });

        snapBTN.setOnAction(e -> {
            EditController.processSnap();
        });

        moveLabelBTN.setOnAction(e -> {
            EditController.processMoveLabel();
        });

        rotateBTN.setOnAction(e -> {
            EditController.processRatation();
        });

        slider2.setOnMousePressed(e -> {
            Station s = (Station) stationCb.getValue();
            if (s != null) {
                src = new StationRadiusChange(s);
            }
        });

        slider2.setOnMouseDragged(e -> {
            Station s = (Station) stationCb.getValue();
            if (s != null) {
                s.setRadius(slider2.getValue());
                MetroFile.markModified();
                MetroWorkspace.refreshTopToolbar();
            }
        });

        slider2.setOnMouseReleased(e -> {
            Station s = (Station) stationCb.getValue();
            if (s != null) {
                src.setNewRadius(slider2.getValue());
                MetroData.addTransaction(src);
            }
        });
    }

    private static void initializeControllers3() {
        findRouteBTN.setOnAction(e -> {
            EditController.processFindRoute((Station) fromCb.getValue(), (Station) toCb.getValue());
        });
    }

    private static void initializeControllers4() {
        backgroundColorPicker.setOnAction(e -> {
            Color color = backgroundColorPicker.getValue();
            EditController.processBackgroundColorChange(color);
        });

        setBgImgBTN.setOnAction(e -> {
            EditController.processSetBackgroundImage();
        });

        addImgBTN.setOnAction(e -> {
            EditController.processAddImage();
        });

        addLabelBTN.setOnAction(e -> {
            EditController.processAddLabel();
        });

        removeElementBTN.setOnAction(e -> {
            EditController.processRemoveElement();
        });
    }

    private static void initializeControllers5() {
        textColorPicker.setOnAction(e -> {
            Color color = textColorPicker.getValue();
            MetroData.selectedLabel.setColor(color);
            EditController.processTextColorChange();
        });

        boldBTN.setOnAction(e -> {
            boolean wasBold = MetroData.selectedLabel.isBold();
            //MetroData.selectedLabel.setBold(!wasBold);
            final LabelFontChange lfc = new LabelFontChange(MetroData.selectedLabel, !wasBold, 1);
            MetroData.addTransaction(lfc);
            boldBTN.setOpacity(wasBold ? 0.5 : 1.0);
            EditController.processBoldText();
        });

        italicBTN.setOnAction(e -> {
            boolean wasItalic = MetroData.selectedLabel.isItalic();
            //MetroData.selectedLabel.setItalic(!wasItalic);
            final LabelFontChange lfc = new LabelFontChange(MetroData.selectedLabel, !wasItalic);
            MetroData.addTransaction(lfc);
            italicBTN.setOpacity(wasItalic ? 0.5 : 1.0);
            EditController.processItalicText();
        });

        sizeCb.setOnAction(e -> {
            try {
                final double newSize = (Integer) sizeCb.getValue();
                final LabelFontChange lfc = new LabelFontChange(MetroData.selectedLabel, newSize);
                MetroData.addTransaction(lfc);
                //MetroData.selectedLabel.setSize((Integer) sizeCb.getValue());
            } catch (Exception ex) {

            }
            //EditController.changeTextSize();
        });

        fontCb.setOnAction(e -> {
            try {
                final String ff = (String) fontCb.getValue();
                final LabelFontChange lfc = new LabelFontChange(MetroData.selectedLabel, ff);
                MetroData.addTransaction(lfc);
            } catch (Exception ex) {

            }
            //MetroData.selectedLabel.setFontFamily((String) fontCb.getValue());
            //EditController.changeTextFont();
        });
    }

    private static void initializeControllers6() {
        showGridCb.setOnAction(e -> {
            MetroWorkspace.showGrid(showGridCb.isSelected());
            //EditController.showGrid();
        });

        increaseSizeBTN.setOnAction(e -> {
            EditController.increaseMapSize();
            if (EditController.increaseCounter == 5) {
                increaseSizeBTN.setDisable(true);
            }
            decreaseSizeBTN.setDisable(false);
        });

        decreaseSizeBTN.setOnAction(e -> {
            EditController.decreaseMapSize();
            if (EditController.increaseCounter == 0) {
                decreaseSizeBTN.setDisable(true);
            }
            increaseSizeBTN.setDisable(false);
        });

        zoomInBTN.setOnAction(e -> {
            zoomOutBTN.setDisable(false);
            EditController.processZoomIn();
            if (EditController.zoomCounter >= 10) {
                zoomInBTN.setDisable(true);
            }
        });

        zoomOutBTN.setOnAction(e -> {
            zoomInBTN.setDisable(false);
            EditController.processZoomOut();
            if (EditController.zoomCounter <= 0) {
                zoomOutBTN.setDisable(true);
            }
        });
    }

    public static void disableRow1(boolean value) {
        ROW1.setDisable(value);
    }

    public static void disableRow2(boolean value) {
        ROW2.setDisable(value);
    }

    public static void disableRow3(boolean value) {
        ROW3.setDisable(value);
    }

    public static void disableRow4(boolean value) {
        ROW4.setDisable(value);
    }

    public static void disableRow5(boolean value) {
        ROW5.setDisable(value);
    }

    public static void editLabel(MetroMapLabel mml) {
        if (mml == null) {
            disableRow1(false);
            disableRow2(false);
            disableRow3(false);
            disableRow4(false);
            disableRow5(true);
            return;
        }
        disableRow1(true);
        disableRow2(true);
        disableRow3(true);
        disableRow4(true);
        disableRow5(false);

        textColorPicker.setValue(mml.getColor());
        boldBTN.setOpacity(mml.isBold() ? 1.0 : 0.5);
        italicBTN.setOpacity(mml.isItalic() ? 1.0 : 0.5);
        sizeCb.setValue((int) mml.getSize());
        fontCb.setValue(mml.getFontFamily());
    }

    public static double getHeight() {
        return VBOX.getPrefHeight();
    }

}
