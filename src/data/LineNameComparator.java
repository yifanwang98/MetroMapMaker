package data;

import java.util.Comparator;

/**
 * @author Yifan Wang
 */
public class LineNameComparator implements Comparator<MetroLine> {

    @Override
    public int compare(MetroLine o1, MetroLine o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
