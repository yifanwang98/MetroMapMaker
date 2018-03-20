package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroData;
import data.MetroFile;
import data.Station;
import jTPS.jTPS_Transaction;

public class AddStationAction implements jTPS_Transaction {

    private final Station station;

    public AddStationAction(Station s) {
        this.station = s;
    }

    @Override
    public void doTransaction() {
        this.station.putBackOnCanvas();
        this.station.addToParent();
        MetroData.updateSelectedStation();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        this.station.remove();
        this.station.removeFromParent();
        MetroData.updateSelectedStation();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
