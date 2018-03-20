package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroMapImage;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class ImageScaleChange implements jTPS_Transaction {

    private final MetroMapImage mmi;
    private final double oldScale;
    private double newScale;

    public ImageScaleChange(MetroMapImage img, double oldS) {
        this.mmi = img;
        this.oldScale = oldS;
    }
    
    public void setNewScale(double newS){
        this.newScale = newS;
    }

    @Override
    public void doTransaction() {
        this.mmi.getImageView().setFitWidth(mmi.getImage().getWidth() * newScale);
        this.mmi.getImageView().setFitHeight(mmi.getImage().getHeight() * newScale);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.mmi.getImageView().setFitWidth(mmi.getImage().getWidth() * oldScale);
        this.mmi.getImageView().setFitHeight(mmi.getImage().getHeight() * oldScale);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
