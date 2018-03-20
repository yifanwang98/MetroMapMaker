package transactions;

import data.MetroData;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class BackgroundImageOpacity implements jTPS_Transaction {
    
    private final double oldOpacity;
    private final double newOpacity;

    public BackgroundImageOpacity(double oldOpa, double newOpa) {
        this.oldOpacity = oldOpa;
        this.newOpacity = newOpa;
    }

    @Override
    public void doTransaction() {
        MetroData.setBackgroundOpacity(this.newOpacity);
    }

    @Override
    public void undoTransaction() {
        MetroData.setBackgroundOpacity(this.oldOpacity);
    }

}
