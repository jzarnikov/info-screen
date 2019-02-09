package pi.home.screen.config.data;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public WienerLinienConfig wl = new WienerLinienConfig();

    public List<String> oebb = new ArrayList<>();

    public String weather = "";

    public List<Integer> cbw = new ArrayList<>();

    public WebcamConfig webcam = new WebcamConfig();
}
