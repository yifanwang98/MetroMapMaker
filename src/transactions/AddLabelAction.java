package transactions;

import app.MetroWorkspace;
import data.MetroData;
import data.MetroFile;
import data.MetroMapLabel;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class AddLabelAction implements jTPS_Transaction {

    private final MetroMapLabel mml;

    public AddLabelAction(MetroMapLabel label) {
        this.mml = label;
    }

    @Override
    public void doTransaction() {
        MetroData.addLabel(mml);
        MetroWorkspace.getCanvas().getChildren().add(mml.getLabel());

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        MetroData.removeLabel(mml);
        MetroWorkspace.getCanvas().getChildren().remove(mml.getLabel());

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
