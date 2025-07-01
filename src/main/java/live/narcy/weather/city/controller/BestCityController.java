package live.narcy.weather.city.controller;

import live.narcy.weather.member.service.JoinService;
import live.narcy.weather.views.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BestCityController {

    private final ViewService viewService;

    @GetMapping("/best-city")
    public String bestCityPage(Model model) {

        Map<String, String> topCityMap =  viewService.getTopCityViews();

        model.addAttribute("topCityMap", topCityMap);

        return "contents/best-city";
    }

}
