package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroFile;
import data.Station;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class RemoveStationAction implements jTPS_Transaction {

    private final Station station;

    public RemoveStationAction(Station s) {
        this.station = s;
    }

    @Override
    public void doTransaction() {
        this.station.remove();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        this.station.putBackOnCanvas();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
