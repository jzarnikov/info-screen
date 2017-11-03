package pi.home.screen.screen;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScreenController {

    @RequestMapping("/off")
    public String off() {
        try {
            new ProcessBuilder("xset", "dpms", "force", "off").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
