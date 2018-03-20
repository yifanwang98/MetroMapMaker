package transactions;

import data.MetroData;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class SetBackgroundImageAction implements jTPS_Transaction {

    private final String oldPath;
    private final String newPath;

    public SetBackgroundImageAction(String oldPath, String newPath) {
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    @Override
    public void doTransaction() {
        if (newPath == null) {
            MetroData.removeBackgroundImage();
        } else {
            MetroData.setBackgroundImage(newPath);
        }
    }

    @Override
    public void undoTransaction() {
        if (oldPath == null) {
            MetroData.removeBackgroundImage();
        } else {
            MetroData.setBackgroundImage(oldPath);
        }
    }
}
