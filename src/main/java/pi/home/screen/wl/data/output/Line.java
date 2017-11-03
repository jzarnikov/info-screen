package pi.home.screen.wl.data.output;

import java.util.ArrayList;
import java.util.List;

public class Line {

    public String name;
    public String destination;
    public boolean realTime;
    public boolean trafficJam;
    public List<Integer> departures = new ArrayList<>();
}
