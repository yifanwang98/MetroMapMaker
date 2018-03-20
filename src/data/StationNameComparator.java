package data;

import java.util.Comparator;

/**
 * @author Yifan Wang
 */
public class StationNameComparator implements Comparator<Station> {

    @Override
    public int compare(Station o1, Station o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
