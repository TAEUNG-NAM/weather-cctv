package live.narcy.weather.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AreaAdminController {

    /**
     * 구역관리 페이지
     * @param model
     * @return
     */
    @GetMapping("/admin/chart")
    public String areaAdmin(Model model) {

        LocalDate now = LocalDate.now();
        int thisYear = now.getYear();
        List<Integer> years = new ArrayList<>();

        for(int i = thisYear; i > thisYear-10; i--) {
            years.add(i);
        }

        model.addAttribute("years", years);
        return "contents/areaManagement";
    }
}
