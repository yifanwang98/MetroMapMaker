package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroMapImage;
import jTPS.jTPS_Transaction;
import javafx.scene.image.ImageView;

/**
 * @author Yifan Wang
 */
public class MoveImageAction implements jTPS_Transaction {

    private final ImageView image;

    private final double oldX;
    private final double oldY;

    private double newX;
    private double newY;

    public MoveImageAction(MetroMapImage mi) {
        this.image = mi.getImageView();
        this.oldX = image.getX();
        this.oldY = image.getY();
    }

    public void setNewValues(double a, double b) {
        this.newX = a;
        this.newY = b;
    }

    @Override
    public void doTransaction() {
        this.image.setX(newX);
        this.image.setY(newY);

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.image.setX(oldX);
        this.image.setY(oldY);

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.image.toString()).append("\n");
        sb.append("Old: ").append(oldX).append("\t").append(oldY).append("\n");
        sb.append("New: ").append(newX).append("\t").append(newY).append("\n");
        return sb.toString();
    }

}
