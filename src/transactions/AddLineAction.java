package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroData;
import data.MetroFile;
import data.MetroLine;
import jTPS.jTPS_Transaction;

public class AddLineAction implements jTPS_Transaction {

    private final MetroLine line;

    public AddLineAction(MetroLine ml) {
        this.line = ml;
    }

    @Override
    public void doTransaction() {
        MetroData.addLine(line);

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

    @Override
    public void undoTransaction() {
        line.remove();
        MetroData.removeLine(line);

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
        MetroEditor.refresh();
    }

}
