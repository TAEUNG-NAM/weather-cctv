package live.narcy.weather.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.Iterator;

@Controller
public class LoginController {

    /**
     * 로그인
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String loginPage(Model model) {

        // 세션 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 세션에서 사용자 email 가져오기
        String email = authentication.getName();

        // 세션에서 사용자 role 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        model.addAttribute("email", email);
        model.addAttribute("role", role);

        return "contents/login";
    }

    /**
     * 로그아웃
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    public String logoutProcess(HttpServletRequest request, HttpServletResponse response) {

        // 세션 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null) {
            // 로그아웃
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/";
    }
}
