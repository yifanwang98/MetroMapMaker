package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroMapImage;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class ImageOpacityChange implements jTPS_Transaction {

    private final MetroMapImage mmi;
    private final double oldOpacity;
    private double newOpacity;

    public ImageOpacityChange(MetroMapImage img, double oldO) {
        this.mmi = img;
        this.oldOpacity = oldO;
    }
    
    public void setNewOpacity(double newO){
        this.newOpacity = newO;
    }

    @Override
    public void doTransaction() {
        this.mmi.getImageView().setOpacity(this.newOpacity);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.mmi.getImageView().setOpacity(this.oldOpacity);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
