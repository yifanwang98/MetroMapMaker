package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.Station;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class MoveStationAction implements jTPS_Transaction {

    private final Station station;

    private final double oldX;
    private final double oldY;
    private final int oldLineNum;
    private final double oldLambda;

    private double newX;
    private double newY;
    private int newLineNum;
    private double newLambda;

    public MoveStationAction(Station s) {
        this.station = s;
        this.oldX = s.getCircle().getCenterX();
        this.oldY = s.getCircle().getCenterY();
        this.oldLineNum = s.getLineNumber();
        this.oldLambda = s.lambda;
    }

    public void setNewValues(double a, double b, int lineNum, double lambda) {
        this.newX = a;
        this.newY = b;
        this.newLineNum = lineNum;
        this.newLambda = lambda;
    }

    @Override
    public void doTransaction() {
        this.station.getCircle().setCenterX(this.newX);
        this.station.getCircle().setCenterY(this.newY);
        this.station.setLineNumber(newLineNum);
        this.station.lambda = this.newLambda;
        this.station.checkIntersection();
        this.station.relocateLabel();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        this.station.getCircle().setCenterX(this.oldX);
        this.station.getCircle().setCenterY(this.oldY);
        this.station.setLineNumber(oldLineNum);
        this.station.lambda = this.oldLambda;
        this.station.checkIntersection();
        this.station.relocateLabel();

        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

}
