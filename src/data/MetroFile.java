package data;

import app.MetroWorkspace;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

/**
 * @author Yifan Wang
 */
public class MetroFile {

    public static final String RENCENT_PATH = "./Data/recentWorks.txt";

    public static boolean isSaved = false;
    public static boolean hasLoadRequest = false;
    public static boolean requestCanceled = false;

    public static String filePath = null;
    public static String fileName = null;
    public static String mapName = null;

    public MetroFile() {

    }

    public static void markSaved() {
        isSaved = true;
    }

    public static void markModified() {
        isSaved = false;
    }

    static final String JSON_BG_STYLE = "background_style";
    static final String JSON_STYLE = "style";

    public static void save() {
        saveAs(filePath);
        //MetroFile.markSaved();
    }

    public static void saveAs(String filepath) {
        try {
            JsonObject dataManagerJSO = MetroData.save();
            
            // AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = writerFactory.createWriter(sw);
            jsonWriter.writeObject(dataManagerJSO);
            jsonWriter.close();

            // INIT THE WRITER
            File file = new File(filepath);
            OutputStream os = new FileOutputStream(file);
            JsonWriter jsonFileWriter = Json.createWriter(os);
            jsonFileWriter.writeObject(dataManagerJSO);
            String prettyPrinted = sw.toString();
            PrintWriter pw = new PrintWriter(file);
            pw.write(prettyPrinted);
            pw.close();

            MetroFile.markSaved();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public static void load(String filePath) throws IOException {
        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(filePath);

        // LOAD THE BACKGROUND COLOR
        //JsonObject bg = json.getJsonObject(JSON_BG_STYLE);
        String bgColor = json.getString(JSON_BG_STYLE);
        MetroData.setBgStyle(bgColor);

        // Grid
        //MetroWorkspace.installGrid();
        
        // AND NOW LOAD ALL THE SHAPES
        MetroData.load(json);
        
        // Ending
        MetroFile.markSaved();
        MetroFile.hasLoadRequest = false;
        MetroData.refreshUndoNRedo();
    }

    private static JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }

    public static void export(String filepath) {
        try {
            // AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
            JsonObject data = MetroData.export();

            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = writerFactory.createWriter(sw);
            jsonWriter.writeObject(data);
            jsonWriter.close();

            // INIT THE WRITER
            File file = new File(filepath + ".json");
            OutputStream os = new FileOutputStream(file);
            JsonWriter jsonFileWriter = Json.createWriter(os);
            jsonFileWriter.writeObject(data);
            String prettyPrinted = sw.toString();
            PrintWriter pw = new PrintWriter(file);
            pw.write(prettyPrinted);
            pw.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public static void resetData() {
        isSaved = false;
        hasLoadRequest = false;
        filePath = null;
        fileName = null;
        mapName = null;
    }

}
