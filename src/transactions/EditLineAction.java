package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroFile;
import data.MetroLine;
import jTPS.jTPS_Transaction;
import javafx.scene.paint.Color;

/**
 * @author Yifan Wang
 */
public class EditLineAction implements jTPS_Transaction {
    
    private final MetroLine ml;
    private final Color oldColor;
    private final Color newColor;
    private final String oldName;
    private final String newName;

    public EditLineAction(MetroLine line, Color newColor, String newName) {
        this.ml = line;
        this.oldColor = ml.getColor();
        this.newColor = newColor;
        this.oldName = ml.getName();
        this.newName = newName;
    }

    @Override
    public void doTransaction() {
        ml.setColor(this.newColor);
        ml.setName(this.newName);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        ml.setColor(this.oldColor);
        ml.setName(this.oldName);
        
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
