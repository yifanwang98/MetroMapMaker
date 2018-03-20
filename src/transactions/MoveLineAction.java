package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroLine;
import jTPS.jTPS_Transaction;
import javafx.scene.shape.Line;

/**
 * @author Yifan Wang
 */
public class MoveLineAction implements jTPS_Transaction {

    private final MetroLine metroLine;
    private final Line line;

    private final double oldStartX;
    private final double oldStartY;
    private final double oldEndX;
    private final double oldEndY;

    private double newStartX;
    private double newStartY;
    private double newEndX;
    private double newEndY;

    public MoveLineAction(MetroLine ml, Line line) {
        this.metroLine = ml;
        this.line = line;
        this.oldStartX = line.getStartX();
        this.oldStartY = line.getStartY();
        this.oldEndX = line.getEndX();
        this.oldEndY = line.getEndY();
    }

    public void setNewValues(double a, double b, double c, double d) {
        this.newStartX = a;
        this.newStartY = b;
        this.newEndX = c;
        this.newEndY = d;
    }

    @Override
    public void doTransaction() {
        this.line.setStartX(this.newStartX);
        this.line.setStartY(this.newStartY);
        this.line.setEndX(this.newEndX);
        this.line.setEndY(this.newEndY);

        this.metroLine.relocateStations();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.line.setStartX(this.oldStartX);
        this.line.setStartY(this.oldStartY);
        this.line.setEndX(this.oldEndX);
        this.line.setEndY(this.oldEndY);

        this.metroLine.relocateStations();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
