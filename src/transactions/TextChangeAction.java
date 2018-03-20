package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroMapLabel;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class TextChangeAction implements jTPS_Transaction {

    private final MetroMapLabel label;
    private final String oldText;
    private final String newText;

    public TextChangeAction(MetroMapLabel label, String newText) {
        this.label = label;
        this.oldText = label.getText();
        this.newText = newText;
    }

    @Override
    public void doTransaction() {
        this.label.setText(newText);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.label.setText(oldText);
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
