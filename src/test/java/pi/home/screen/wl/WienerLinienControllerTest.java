package pi.home.screen.wl;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import pi.home.screen.config.ConfigController;
import pi.home.screen.config.data.Config;

import static net.javacrumbs.jsonunit.JsonAssert.*;
import static org.mockito.Mockito.*;

public class WienerLinienControllerTest {

    private Config config;
    private ConfigController configController;

    @Before
    public void setUp() throws Exception {
        configController = mock(ConfigController.class);
        config = new Config();
        when(configController.getConfig()).thenReturn(config);
    }

//    @Test
//    public void translate1() throws Exception {
//        assertJsonEquals("{ 'lines': [] }", new WienerLinienController(configController).translateResponse("{}"));
//    }
//
//    @Test
//    public void translate2() throws Exception {
//        String input = IOUtils.toString(WienerLinienControllerTest.class.getResourceAsStream("/wl1.json"));
//        String expectedOutput = IOUtils.toString(WienerLinienControllerTest.class.getResourceAsStream("/wl1-result.json"));
//        String output = new WienerLinienController(configController).translateResponse(input);
//        assertJsonEquals(expectedOutput, output);
//
//    }
//    @Test
//    public void translate_maxDepartures() throws Exception {
//        config.wl.maxDepartures = 4;
//        String input = IOUtils.toString(WienerLinienControllerTest.class.getResourceAsStream("/wl2.json"));
//        String expectedOutput = IOUtils.toString(WienerLinienControllerTest.class.getResourceAsStream("/wl2-result.json"));
//        String output = new WienerLinienController(configController).translateResponse(input);
//        assertJsonEquals(expectedOutput, output);
//
//    }
}