package pi.home.screen.wl;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pi.home.screen.common.AbstractTransformingController;
import pi.home.screen.config.ConfigController;
import pi.home.screen.config.data.WienerLinienConfig;
import pi.home.screen.wl.data.input.Input;
import pi.home.screen.wl.data.output.Line;
import pi.home.screen.wl.data.output.Output;

@RestController
public class WienerLinienController extends AbstractTransformingController<Input, Output> {


    @Autowired
    public WienerLinienController(ConfigController configController) {
        super(configController, new TypeReference<Input>() {});
    }

    @RequestMapping("/wl")
    public String wl() {
        try {
            WienerLinienConfig wienerLinienConfig = getConfig().wl;
            URIBuilder uriBuilder = new URIBuilder("http://www.wienerlinien.at/ogd_realtime/monitor")
                    .setParameter("sender", wienerLinienConfig.apiKey);
            for (Integer rbl : wienerLinienConfig.rbl) {
                uriBuilder = uriBuilder.addParameter("rbl", Integer.toString(rbl));
            }
            URI uri = uriBuilder.build();
            return getAndTranslate(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return "{ 'lines': [] }";
        }
    }

    @Override
    protected Output translate(Input input) throws IOException {
        Output output = new Output();
        input.data.monitors.stream()
                .flatMap(monitor -> monitor.lines.stream())
                .map(this::mapLine)
                .forEach(output.lines::add);
        Collections.sort(output.lines, this::compareLines);
        return output;
    }

    private int compareLines(Line line1, Line line2) {
        return line1.name.compareTo(line2.name);
    }

    private Line mapLine(pi.home.screen.wl.data.input.Line inputLine) {
        Line outputLine = new Line();
        outputLine.name = inputLine.name;
        outputLine.destination = inputLine.towards;
        outputLine.realTime = inputLine.realtimeSupported;
        outputLine.trafficJam = inputLine.trafficjam;
        inputLine.departures.departure.stream()
                .map(departure -> departure.departureTime.countdown)
                .filter(Objects::nonNull)
                .filter(departure -> departure != -1)
                .limit(getConfig().wl.maxDepartures)
                .forEach(outputLine.departures::add);
        return outputLine;
    }
}
