package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroFile;
import data.Station;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class StationRadiusChange implements jTPS_Transaction {

    private final Station station;
    private final double oldValue;
    private double newValue;

    public StationRadiusChange(Station s) {
        this.station = s;
        this.oldValue = s.getRadius();
    }
    
    public void setNewRadius(double newR){
        this.newValue = newR;
    }

    @Override
    public void doTransaction() {
        this.station.setRadius(this.newValue);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        this.station.setRadius(this.oldValue);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
