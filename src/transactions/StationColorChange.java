package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroFile;
import data.MetroLine;
import data.Station;
import jTPS.jTPS_Transaction;
import javafx.scene.paint.Color;

/**
 * @author Yifan Wang
 */
public class StationColorChange implements jTPS_Transaction {
    
    private final Station station;
    private final Color oldColor;
    private final Color newColor;

    public StationColorChange(Station s, Color newColor) {
        this.station = s;
        this.oldColor = s.getColor();
        this.newColor = newColor;
    }

    @Override
    public void doTransaction() {
        station.setColor(this.newColor);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        station.setColor(this.oldColor);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
