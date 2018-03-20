package transactions;

import app.MetroWorkspace;
import data.MetroData;
import data.MetroFile;
import data.MetroMapImage;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class AddImageAction implements jTPS_Transaction {

    private final MetroMapImage mmi;

    public AddImageAction(MetroMapImage img) {
        this.mmi = img;
    }

    @Override
    public void doTransaction() {
        MetroData.addImageView(mmi);
        MetroWorkspace.getCanvas().getChildren().add(mmi.getImageView());

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        MetroData.removeImageView(mmi);
        MetroWorkspace.getCanvas().getChildren().remove(mmi.getImageView());
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
