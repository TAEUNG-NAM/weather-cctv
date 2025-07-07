package live.narcy.weather.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

/* 접근 예외 처리 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * 404(존재하지 않는 URL) 접근 시 메인 페이지로 리다이렉트
     * @param request
     * @return
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        if (statusCode != null && statusCode == HttpStatus.NOT_FOUND.value()) {
            // 404 에러일 때 메인 페이지로 리다이렉트
            return "redirect:/";
        }
        // 기타 에러일 때 메인 페이지로
        return "redirect:/";
    }
}
