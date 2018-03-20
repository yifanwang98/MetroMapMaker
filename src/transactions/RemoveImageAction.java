package transactions;

import app.MetroWorkspace;
import data.MetroData;
import data.MetroFile;
import data.MetroMapImage;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class RemoveImageAction implements jTPS_Transaction {

    private final MetroMapImage mmi;
    
    public RemoveImageAction(MetroMapImage img){
        mmi = img;
    }
    
    
    @Override
    public void doTransaction() {
        MetroData.removeImageView(mmi);
        MetroWorkspace.getCanvas().getChildren().remove(mmi.getImageView());
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        MetroData.addImageView(mmi);
        MetroWorkspace.getCanvas().getChildren().add(mmi.getImageView());
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }
    
}
