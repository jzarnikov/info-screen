package pi.home.screen.webcam;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pi.home.screen.config.ConfigController;
import pi.home.screen.config.data.WebcamConfig;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController()
public class WebcamController {

    private final ConfigController configController;
    private final HttpClient httpClient;

    @Autowired
    public WebcamController(ConfigController configController) {
        this.configController = configController;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @RequestMapping("webcam/status")
    public String isEnabled() throws Exception {
        Process process = new ProcessBuilder("/usr/bin/ssh", getWebcamHost(), "ls",  "-ls",  "/home/pi/motion/disabled")
                .inheritIO()
                .start();
        boolean success = process.waitFor(3, TimeUnit.SECONDS);
        if (success) {
            int result = process.exitValue();
            return result == 0 ? "{\"status\": \"disabled\"}" : "{\"status\": \"enabled\"}";
        } else {
            return "{\"status\": \"error\"}";
        }
    }

    @RequestMapping("webcam/disable")
    public void disable() throws Exception {
        Process process = new ProcessBuilder("/usr/bin/ssh", getWebcamHost(), "touch",  "/home/pi/motion/disabled")
                .inheritIO()
                .start();
        process.waitFor(3, TimeUnit.SECONDS);
    }

    @RequestMapping("webcam/enable")
    public void enable() throws Exception {
        Process process = new ProcessBuilder("/usr/bin/ssh", getWebcamHost(), "rm",  "/home/pi/motion/disabled")
                .inheritIO()
                .start();
        process.waitFor(3, TimeUnit.SECONDS);
    }

    @RequestMapping("webcam/preview")
    public void preview(HttpServletResponse response) throws Exception {
        response.setContentType("image/jpeg");
        String timestamp = Long.toString(new Date().getTime());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        WebcamConfig webcamConfig = configController.getConfig().webcam;
        HttpRequest request = HttpRequest.newBuilder(new URI(webcamConfig.previewUrl + "?" + timestamp))
                .setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((webcamConfig.user + ":" + webcamConfig.password).getBytes()))
                .build();
        HttpResponse<InputStream> previewResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        IOUtils.copy(previewResponse.body(), response.getOutputStream());
    }


    private String getWebcamHost() {
        return configController.getConfig().webcam.host;
    }

}
