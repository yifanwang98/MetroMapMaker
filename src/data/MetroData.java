package data;

import app.MetroEditor;
import app.MetroWorkspace;
import control.EditController;
import jTPS.jTPS;
import jTPS.jTPS_Transaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import transactions.RemoveLineAction;

/**
 * @author Yifan Wang
 */
public class MetroData {

    public static Mode mode = Mode.DEFAULT;
    public static boolean clickOnStation = false;
    public static boolean clickOnLine = false;
    public static boolean clickOnLabel = false;
    public static boolean clickOnElement = false;
    public static boolean clickOnImage = false;

    private static final ArrayList<MetroLine> lines = new ArrayList<>();
    public static final ArrayList<Station> stations = new ArrayList<>();
    private static final ArrayList<MetroMapImage> IMAGES = new ArrayList<>();
    private static final ArrayList<MetroMapLabel> LABELS = new ArrayList<>();

    private static final ArrayList<double[]> INTERSECTIONS = new ArrayList<>();

    private static jTPS jTPS;

    private static String backgroundStyle;
    private static MetroLine selectedLine;
    private static Station selectedStation;
    public static MetroMapLabel selectedLabel;
    public static MetroMapImage selectedImage;

    private static boolean hasBackgroundImg = false;
    private static String bgImgPath;
    private static Image background;
    private static ImageView backgroundImg;

    private static double centerCor;

    private static MetroWorkspace workspace;

    public MetroData(MetroWorkspace workspace) {
        jTPS = new jTPS();
        backgroundStyle = DesignConstants.DEFAULT_BACKGROUND_STYLE;
        MetroData.workspace = workspace;
    }

    public static void addLine(MetroLine line) {
        centerCor = MetroWorkspace.getCanvas().getMinWidth() / 2.0;

        if (lines.size() % 2 == 0) {
            double diffY = (50.0 * (lines.size() % 6)) * Math.pow(-1, lines.size() / 2);

            line.setStartNEnd(centerCor - 200.0, centerCor + diffY, centerCor + 200.0, centerCor + diffY);
        } else {
            double diffX = (50.0 * (lines.size() % 6)) * Math.pow(-1, lines.size() / 2);
            line.setStartNEnd(centerCor + diffX, centerCor - 200.0, centerCor + diffX, centerCor + 200.0);
        }
        Line a = line.getLine();
        lines.add(line);
        selectedLine = line;

        if (hasBackgroundImg) {
            MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, a);
        } else {
            MetroWorkspace.getCanvas().getChildren().add(DesignConstants.INDEX, a);
        }
        MetroWorkspace.getCanvas().getChildren().add(line.getLabelA());
        MetroWorkspace.getCanvas().getChildren().add(line.getLabelB());

        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
        MetroEditor.disableRow2(false);
    }

    public static void addExsistingLineToLines(MetroLine line) {
        lines.add(line);
        if (selectedLine == null) {
            selectedLine = line;
        }
    }

    public static void removeSelectedLine() {
        /*selectedLine.remove();
        lines.remove(selectedLine);
        selectedLine = lines.isEmpty() ? null : lines.get(0);*/
        final RemoveLineAction rla = new RemoveLineAction(selectedLine);
        MetroData.addTransaction(rla);
    }

    public static void removeLine(MetroLine ml) {
        final boolean same = ml == selectedLine;
        lines.remove(ml);
        if (same) {
            selectedLine = lines.isEmpty() ? null : lines.get(0);
        }
    }

    public static void removeSelectedStation() {
        selectedLine.removeStation(selectedStation);
        selectedStation.remove();
        selectedStation = selectedLine.getFirstStation();
    }

    public static void setBgStyle(String style) {
        MetroData.backgroundStyle = style;
    }

    public static String getBgStyle() {
        return MetroData.backgroundStyle;
    }

    public static boolean hasUndo() {
        return jTPS.hasUndo();
    }

    public static boolean hasRedo() {
        return jTPS.hasRedo();
    }

    public static boolean isEmpty() {
        return lines.isEmpty();
    }

    public static void addImageView(MetroMapImage img) {
        if (img != null) {
            IMAGES.add(img);
        }
    }

    public static void removeImageView(MetroMapImage img) {
        if (img != null) {
            IMAGES.remove(img);
        }
    }

    public static void addLabel(MetroMapLabel mml) {
        if (mml != null) {
            LABELS.add(mml);
        }
    }

    public static void removeLabel(MetroMapLabel mml) {
        if (mml != null) {
            LABELS.remove(mml);
        }
    }

    public static ObservableList<MetroLine> getLineNames() {
        final ObservableList<MetroLine> nameList = FXCollections.observableList(lines);
        nameList.sort(new LineNameComparator());
        return nameList;
    }

    public static boolean isValidLineName(String name) {
        for (MetroLine l : lines) {
            if (name.equals(l.getName())) {
                return false;
            }
        }
        return true;
    }

    public static int isValidStationName(String name) {
        int i = 0;
        for (MetroLine l : lines) {
            i += l.isValidStationName(name);
        }
        return i;
    }

    public static void resetData() {
        jTPS = new jTPS();
        backgroundStyle = DesignConstants.DEFAULT_BACKGROUND_STYLE;
        lines.clear();
        stations.clear();
        selectedLine = null;
        selectedStation = null;
        mode = Mode.DEFAULT;
        clickOnStation = false;
        EditController.increaseCounter = 0;
        EditController.zoomCounter = 5;
        MetroWorkspace.getCanvas().getChildren().clear();
        MetroWorkspace.installGrid();

        DesignConstants.INDEX = 1;
    }

    public static void resetJTPS() {
        jTPS = new jTPS();
        MetroWorkspace.refreshTopToolbar();
    }

    public static void addTransaction(jTPS_Transaction trans) {
        jTPS.addTransaction(trans);
        //jTPS.doTransaction();
    }

    public static void performDoTransaction() {
        jTPS.doTransaction();
    }

    public static void performUndoTransaction() {
        jTPS.undoTransaction();
    }

    public static void refreshUndoNRedo() {

    }

    public static MetroLine getSelectedLine() {
        return selectedLine;
    }

    public static void setSelectedLine(String name) {
        for (MetroLine l : lines) {
            if (l.getName().equals(name)) {
                selectedLine = l;
                MetroEditor.refresh();
            }
        }
    }

    public static void setSelectedLine(MetroLine line) {
        selectedLine = line;
        MetroEditor.refresh();
    }

    public static Station getSelectedStation() {
        return selectedStation;
    }

    public static void setSelectedStation(Station station) {
        selectedStation = station;
    }

    public static void updateSelectedStation() {
        selectedStation = selectedLine.getFirstStation();
    }

    public static MetroLine assignParent(Station station, String name) {
        for (MetroLine ml : lines) {
            if (ml.getName().equals(name)) {
                ml.addExsistingStation(station);
                return ml;
            }
        }
        return null;
    }

    public static ArrayList<MetroLine> isAnIntersection(double x, double y) {
        final ArrayList<MetroLine> intersectioningLine = new ArrayList<>();
        for (MetroLine metroLine : lines) {
            if (metroLine.isIntersecting(x, y)) {
                intersectioningLine.add(metroLine);
            }
        }
        return intersectioningLine;
    }

    public static void setBackgroundImage(String path) {
        if (path != null) {
            final Image img = new Image("file:" + path);
            bgImgPath = path;
            background = img;
            backgroundImg = new ImageView(background);

            if (hasBackgroundImg) {
                MetroWorkspace.getCanvas().getChildren().remove(0);
            }
            MetroWorkspace.getCanvas().getChildren().add(0, backgroundImg);
            hasBackgroundImg = true;
            DesignConstants.INDEX += 1;

            // Locate Background Image
            backgroundImg.setX((DesignConstants.DEFAULT_CANVAS_SIZE
                    - background.getWidth()) / 2);
            backgroundImg.setY((DesignConstants.DEFAULT_CANVAS_SIZE
                    - background.getHeight()) / 2);

            MetroFile.markModified();
            MetroWorkspace.refreshTopToolbar();
        }
    }

    public static void removeBackgroundImage() {
        if (hasBackgroundImg) {
            bgImgPath = null;
            background = null;
            backgroundImg = null;
            MetroWorkspace.getCanvas().getChildren().remove(0);
            DesignConstants.INDEX -= 1;
            hasBackgroundImg = false;
            MetroFile.markModified();
            MetroWorkspace.refreshTopToolbar();
        }
    }

    public static boolean hasBackgroundImage() {
        return hasBackgroundImg;
    }

    public static String getBackgroundImagePath() {
        return bgImgPath;
    }

    public static double getBackgroundOpacity() {
        return backgroundImg.getOpacity();
    }

    public static void setBackgroundOpacity(double value) {
        backgroundImg.setOpacity(value);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    public static String findRoute(Station from, Station to) {
        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(from.getName()).append("\n");
        sb.append("To: ").append(to.getName()).append("\n\n");
        if (from.equals(to)) {
            sb.append("1.\t ").append(from.getName());
            return sb.toString();
        }

        // Clear Neighbours
        for (Station s : stations) {
            s.resetNeighbours();
        }

        // Assign Neighbours
        for (MetroLine l : lines) {
            l.assignNeighbours();
        }
        try {
            // BFS
            final Queue<Station> queue = new LinkedList<>();
            queue.offer(from); // Enqueue a station
            from.setVisited();

            while (!queue.isEmpty()) {
                final Station a = queue.poll(); // Dequeue station
                for (Station e : a.getNeighbours()) {
                    if (!e.isVisited()) {
                        queue.offer(e);
                        e.setVisited();
                        e.getPath().add(a);
                    }
                }
            }

            // Trace Back
            int i = 0;
            final ArrayList<Station> trace = new ArrayList<>();
            trace.add(to);
            while (!to.getPath().isEmpty()) {
                Station n = to.getPath().getFirst();
                trace.add(0, n);
                to = to.getNeighbour(n.getName());
                i++;
            }
            if (i != 0) {
                sb.append("Path:\n");
                final ArrayList<ArrayList<String>> lineNames = new ArrayList<>();
                final String[] lineNamess = new String[trace.size()];
                Arrays.fill(lineNamess, null);
                int first = -1;
                for (int j = 0; j < trace.size(); j++) {
                    final ArrayList<String> p = trace.get(j).getParentsName();
                    lineNames.add(p);
                    if (p.size() == 1) {
                        lineNamess[j] = p.get(0);
                        if (first == -1) {
                            first = j;
                        }
                    }
                }

                for (int j = 0; j < lineNames.size() - 1; j++) {
                    ArrayList<String> a = lineNames.get(j);
                    ArrayList<String> b = lineNames.get(j + 1);
                    if (lineNamess[j] == null) {
                        for (String n : a) {
                            if (b.contains(n)) {
                                lineNamess[j] = n;
                                lineNamess[j + 1] = n;
                                break;
                            }
                        }
                        lineNamess[j] = a.get(0);

                    } else if (lineNamess[j] != null && lineNamess[j + 1] == null) {
                        if (b.contains(lineNamess[j])) {
                            lineNamess[j + 1] = lineNamess[j];
                        }
                    }
                }
                if (lineNamess[lineNames.size() - 1] == null) {
                    lineNamess[lineNames.size() - 1] = lineNames.get(lineNames.size() - 1).get(0);
                }

                String current = lineNamess[0];
                for (int j = 0; j < trace.size(); j++) {
                    if (j == 0) {
                        sb.append("\t(Take Line: ").append(current).append(")\n");
                        sb.append(j).append(".\t").append(trace.get(j).getName());
                    } else if (!lineNamess[j].equals(current)) {
                        current = lineNamess[j];
                        sb.append("\t(Transfer to Line - ").append(current).append(")\n");
                        sb.append(j).append(".\t").append(trace.get(j).getName());
                    } else {
                        sb.append(j).append(".\t").append(trace.get(j).getName());
                    }
                    
                    sb.append("\n");
                }

                sb.append("\nEstimated Time: ").append(trace.size() * 2).append(" min(s)");

            } else {
                sb.append("No Path Found.\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sb.append("An Error Occurred.\n");
        }

        // Return
        return sb.toString();
    }

    static final String JSON_BG_STYLE = "background_style";
    static final String JSON_BG_IMAGE = "background_image";
    static final String JSON_BG_IMAGE_OPA = "background_image_opacity";
    static final String JSON_NAME = "name";
    static final String JSON_LINE = "lines";
    static final String JSON_STATIONS = "stations";
    static final String JSON_IMAGES = "images";
    static final String JSON_LABELS = "labels";

    public static JsonObject save() {
        // Line
        JsonArrayBuilder lineArr = Json.createArrayBuilder();
        for (MetroLine ml : lines) {
            lineArr.add(ml.save());
        }
        JsonArray lineArray = lineArr.build();

        // Station
        JsonArrayBuilder stationArr = Json.createArrayBuilder();
        for (Station s : stations) {
            stationArr.add(s.save());
        }
        JsonArray stationArray = stationArr.build();

        // Images
        JsonArrayBuilder imageArr = Json.createArrayBuilder();
        for (MetroMapImage mmi : IMAGES) {
            imageArr.add(mmi.save());
        }
        JsonArray imageArray = imageArr.build();

        // Images
        JsonArrayBuilder labelArr = Json.createArrayBuilder();
        for (MetroMapLabel mml : LABELS) {
            labelArr.add(mml.save());
        }
        JsonArray labelArray = labelArr.build();

        // Background
        double opacity = 1.0;
        if (hasBackgroundImg) {
            opacity = backgroundImg.getOpacity();
        }
        JsonObject data = Json.createObjectBuilder()
                .add(JSON_BG_STYLE, backgroundStyle)
                //.add(JSON_NAME, mapName)
                .add(JSON_LINE, lineArray)
                .add(JSON_STATIONS, stationArray)
                .add(JSON_IMAGES, imageArray)
                .add(JSON_LABELS, labelArray)
                .add(JSON_BG_IMAGE, "" + bgImgPath)
                .add(JSON_BG_IMAGE_OPA, "" + opacity)
                .build();

        return data;
    }

    public static void load(JsonObject jsonObject) {
        // Background Color
        String bgColor = jsonObject.getString(JSON_BG_STYLE);
        MetroData.setBgStyle(bgColor);

        // Lines
        JsonArray lineArray = jsonObject.getJsonArray(JSON_LINE);
        for (int i = 0; i < lineArray.size(); i++) {
            JsonObject lineJson = lineArray.getJsonObject(i);
            final MetroLine metroLine = new MetroLine("", Color.ALICEBLUE);
            metroLine.load(lineJson);
            lines.add(metroLine);
        }
        if (!lines.isEmpty()) {
            selectedLine = lines.get(0);
        }

        // Stations
        JsonArray stationArray = jsonObject.getJsonArray(JSON_STATIONS);
        for (int i = 0; i < stationArray.size(); i++) {
            JsonObject stationJson = stationArray.getJsonObject(i);
            final Station station = new Station();
            station.load(stationJson);
            stations.add(station);
            if (station.isIntersection()) {
                station.checkIntersection();
            }
        }
        if (selectedLine != null) {
            selectedStation = selectedLine.getFirstStation();
        }

        StationID.loaded();

        // Images
        JsonArray imageArray = jsonObject.getJsonArray(JSON_IMAGES);
        for (int i = 0; i < imageArray.size(); i++) {
            JsonObject imageJson = imageArray.getJsonObject(i);
            final MetroMapImage mmi = new MetroMapImage();
            mmi.load(imageJson);
            IMAGES.add(mmi);
            MetroWorkspace.getCanvas().getChildren().add(mmi.getImageView());
        }

        // Labels
        JsonArray labelArray = jsonObject.getJsonArray(JSON_LABELS);
        for (int i = 0; i < labelArray.size(); i++) {
            JsonObject labelJson = labelArray.getJsonObject(i);
            final MetroMapLabel mml = new MetroMapLabel();
            mml.load(labelJson);
            LABELS.add(mml);
            MetroWorkspace.getCanvas().getChildren().add(mml.getLabel());
        }

        // Background Image
        try {
            String bgImage = jsonObject.getString(JSON_BG_IMAGE);
            if (bgImage == null || bgImage.length() <= 0) {
                bgImgPath = null;
                background = null;
                backgroundImg = null;
                hasBackgroundImg = false;
            } else {
                final Image img = new Image("file:" + bgImage);
                bgImgPath = bgImage;
                background = img;
                backgroundImg = new ImageView(background);
                hasBackgroundImg = true;
                MetroWorkspace.getCanvas().getChildren().add(0, backgroundImg);

                // Locate Background Image
                backgroundImg.setX((DesignConstants.DEFAULT_CANVAS_SIZE
                        - background.getWidth()) / 2);
                backgroundImg.setY((DesignConstants.DEFAULT_CANVAS_SIZE
                        - background.getHeight()) / 2);

                backgroundImg.setOpacity(Double
                        .parseDouble(jsonObject.getString(JSON_BG_IMAGE_OPA)));
            }

            for (MetroLine l : lines) {
                l.sortStations();
            }

        } catch (Exception ex) {
            bgImgPath = null;
            background = null;
            backgroundImg = null;
            hasBackgroundImg = false;
            ex.printStackTrace();
        }
    }

    public static JsonObject export() {
        JsonObject lineName = Json.createObjectBuilder()
                .add(JSON_NAME, MetroFile.mapName)
                .build();

        JsonArrayBuilder lineArr = Json.createArrayBuilder();
        for (MetroLine ml : lines) {
            lineArr.add(ml.export());
        }
        JsonArray lineArray = lineArr.build();

        JsonArrayBuilder stationArr = Json.createArrayBuilder();
        for (Station s : stations) {
            stationArr.add(s.export());
        }
        JsonArray stationArray = stationArr.build();

        JsonObject data = Json.createObjectBuilder()
                .add(JSON_NAME, lineName)
                .add(JSON_LINE, lineArray)
                .add(JSON_STATIONS, stationArray)
                .build();

        return data;
    }

}
