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
        System.out.println("1등 = " + topCityMap.get("topCity_1") + " : " + topCityMap.get("topView_1"));
        System.out.println("2등 = " + topCityMap.get("topCity_2") + " : " + topCityMap.get("topView_2"));
        System.out.println("3등 = " + topCityMap.get("topCity_3") + " : " + topCityMap.get("topView_3"));

        return "contents/best-city";
    }

}
