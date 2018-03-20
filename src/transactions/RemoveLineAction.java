package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroData;
import data.MetroFile;
import data.MetroLine;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class RemoveLineAction implements jTPS_Transaction {
    
    private final MetroLine line;

    public RemoveLineAction(MetroLine ml) {
        this.line = ml;
    }
    
    @Override
    public void doTransaction() {
        this.line.remove();
        MetroData.removeLine(line);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        this.line.putBackOnCanvas();
        MetroData.addExsistingLineToLines(line);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
