package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroMapLabel;
import jTPS.jTPS_Transaction;
import javafx.scene.control.Label;

/**
 * @author Yifan Wang
 */
public class MoveLabelAction implements jTPS_Transaction {

    private final Label label;

    private final double oldX;
    private final double oldY;

    private double newX;
    private double newY;

    public MoveLabelAction(MetroMapLabel ml) {
        this.label = ml.getLabel();
        this.oldX = label.getLayoutX();
        this.oldY = label.getLayoutY();
    }

    public void setNewValues(double a, double b) {
        this.newX = a;
        this.newY = b;
    }

    @Override
    public void doTransaction() {
        this.label.setLayoutX(newX);
        this.label.setLayoutY(newY);

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.label.setLayoutX(oldX);
        this.label.setLayoutY(oldY);

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
