package live.narcy.weather.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CityAdminController {

    /**
     * 도시관리 페이지
     * @param model
     * @return
     */
    @GetMapping("/admin/management")
    public String cityAdmin(Model model) {
        return "contents/cityManagement";
    }
}
