package transactions;

import app.MetroEditor;
import app.MetroWorkspace;
import data.MetroData;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class BackgroundStyleAction implements jTPS_Transaction {

    private final String oldBG;
    private final String newBG;

    public BackgroundStyleAction(String oldStyle, String newStyle) {
        oldBG = oldStyle;
        newBG = newStyle;
    }

    @Override
    public void doTransaction() {
        MetroWorkspace.getCanvas().setStyle(newBG);
        MetroData.setBgStyle(newBG);
        MetroEditor.refresh();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        MetroWorkspace.getCanvas().setStyle(oldBG);
        MetroData.setBgStyle(oldBG);
        MetroEditor.refresh();
        MetroWorkspace.refreshTopToolbar();
    }
}
