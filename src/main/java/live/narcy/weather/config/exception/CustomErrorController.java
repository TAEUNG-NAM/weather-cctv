package live.narcy.weather.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

/* 접근 예외 처리 */
@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    /**
     * 404(존재하지 않는 URL), 500(서버 에러) 접근 시 메인 페이지로 리다이렉트
     * @param request
     * @return
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        log.error("HttpStatus : {}", statusCode != null ? statusCode : "");
        if (statusCode != null && statusCode == HttpStatus.NOT_FOUND.value()) {
            // 404 에러일 때 메인 페이지로 리다이렉트
            return "redirect:/";
        } else if (statusCode != null && statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            // 500 에러일 때 메인 페이지로 리다이렉트
            return "redirect:/";
        }
        return null;
    }
}
