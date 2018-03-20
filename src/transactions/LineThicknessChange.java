package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroLine;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class LineThicknessChange implements jTPS_Transaction {

    private final MetroLine ml;
    private final double oldValue;
    private double newValue;

    public LineThicknessChange(MetroLine line) {
        this.ml = line;
        this.oldValue = line.getThickness();
    }
    
    public void setNewScale(double newS){
        this.newValue = newS;
    }

    @Override
    public void doTransaction() {
        this.ml.setThickness(newValue);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.ml.setThickness(oldValue);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
