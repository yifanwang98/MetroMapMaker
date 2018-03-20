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
public class LineColorChange implements jTPS_Transaction {
    
    private final MetroLine ml;
    private final Color oldColor;
    private final Color newColor;

    public LineColorChange(MetroLine line, Color newColor) {
        this.ml = line;
        this.oldColor = ml.getColor();
        this.newColor = newColor;
    }

    @Override
    public void doTransaction() {
        ml.setColor(this.newColor);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        ml.setColor(this.oldColor);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
