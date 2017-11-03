package pi.home.screen.cbw;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pi.home.screen.cbw.data.input.Input;
import pi.home.screen.cbw.data.output.Output;
import pi.home.screen.cbw.data.output.Station;
import pi.home.screen.common.AbstractTransformingController;
import pi.home.screen.config.ConfigController;

@RestController
public class CityBikeWienController extends AbstractTransformingController<Input, Output> {

    @Autowired
    public CityBikeWienController(ConfigController configController) {
        super(configController, new TypeReference<Input>() {});
    }

    @RequestMapping("/cbw")
    public String cbw() {
        try {
            return getAndTranslate(new URI("https://ssl.citybikewien.at/dyncbw/stationenplan2016/citybike_xml.php?json"));
        } catch (Exception e) {
            e.printStackTrace();
            return "{ 'stations': [] }";
        }
    }

    @Override
    protected Output translate(Input input) throws IOException {
        Output output = new Output();
        getConfig().cbw.stream()
                .map(id -> getStation(id, input))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::mapStation)
                .forEach(output.stations::add);
        return output;
    }

    private Optional<pi.home.screen.cbw.data.input.Station> getStation(Integer id, Input input) {
        return input.stream()
                .filter(station -> id.equals(station.id))
                .findFirst();
    }

    private Station mapStation(pi.home.screen.cbw.data.input.Station inputStation) {
        Station outputStation = new Station();
        outputStation.active = "aktiv".equals(inputStation.status);
        outputStation.name = inputStation.name;
        outputStation.freeBikes = inputStation.free_bikes;
        outputStation.freeBoxes = inputStation.free_boxes;
        return outputStation;
    }
}
