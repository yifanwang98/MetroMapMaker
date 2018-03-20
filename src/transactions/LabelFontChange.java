package transactions;

import app.MetroWorkspace;
import data.MetroFile;
import data.MetroMapLabel;
import jTPS.jTPS_Transaction;

/**
 * @author Yifan Wang
 */
public class LabelFontChange implements jTPS_Transaction {

    private final int instruction;

    private final MetroMapLabel label;
    private final String oldFF;
    private final String newFF;
    private final boolean bold;
    private final boolean italic;
    private final double oldSize;
    private final double newSize;

    public LabelFontChange(MetroMapLabel label, String fontFamily) {
        this.label = label;
        instruction = 0;

        this.oldFF = label.getFontFamily();
        this.newFF = fontFamily;
        this.bold = false;
        this.italic = false;
        this.oldSize = 20.0;
        this.newSize = 20.0;
    }

    public LabelFontChange(MetroMapLabel label, boolean bold, int i) {
        this.label = label;
        instruction = 1;

        this.oldFF = null;
        this.newFF = null;
        this.bold = bold;
        this.italic = false;
        this.oldSize = 20.0;
        this.newSize = 20.0;
    }

    public LabelFontChange(MetroMapLabel label, boolean italic) {
        this.label = label;
        instruction = 2;

        this.oldFF = null;
        this.newFF = null;
        this.bold = false;
        this.italic = italic;
        this.oldSize = 20.0;
        this.newSize = 20.0;
    }

    public LabelFontChange(MetroMapLabel label, double size) {
        this.label = label;
        instruction = 3;

        this.oldFF = null;
        this.newFF = null;
        this.bold = false;
        this.italic = false;
        this.oldSize = label.getSize();
        this.newSize = size;
    }

    @Override
    public void doTransaction() {
        switch (this.instruction) {
            case 0: {
                this.label.setFontFamily(newFF);
                break;
            }
            case 1: {
                this.label.setBold(bold);
                break;
            }
            case 2: {
                this.label.setItalic(italic);
                break;
            }
            case 3: {
                this.label.setSize(newSize);
                break;
            }
        }
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }

    @Override
    public void undoTransaction() {
        switch (this.instruction) {
            case 0: {
                this.label.setFontFamily(oldFF);
                break;
            }
            case 1: {
                this.label.setBold(!bold);
                break;
            }
            case 2: {
                this.label.setItalic(!italic);
                break;
            }
            case 3: {
                this.label.setSize(oldSize);
                break;
            }
        }
        MetroFile.markModified();
        MetroWorkspace.refreshTopToolbar();
    }
}
