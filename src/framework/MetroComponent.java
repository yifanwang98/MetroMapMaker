package framework;

import javax.json.JsonObject;

/**
 * @author Yifan Wang
 */
public interface MetroComponent {

    public void load(JsonObject jsonObject);

    public JsonObject save();

    public JsonObject export();
    
}
