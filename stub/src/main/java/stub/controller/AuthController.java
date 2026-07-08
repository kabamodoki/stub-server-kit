package stub.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import stub.AuthHandler;
import stub.service.RouteLoader;

import java.io.IOException;
import java.util.Map;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthHandler authHandler;

    @Autowired
    private RouteLoader routeLoader;

    /** 疑似ログインページを表示します */
    @GetMapping("${stub.auth.login-path:/login}")
    public String loginPage() {
        return "forward:/login.html";
    }

    /** ログインボタン押下 → redirect_uri へリダイレクト */
    @PostMapping("${stub.auth.login-path:/login}/submit")
    public void loginSubmit(
            @RequestParam(required = false) String redirect_uri,
            @RequestParam(required = false) String state,
            HttpServletResponse response) throws IOException {

        if (redirect_uri == null || redirect_uri.isBlank()) {
            response.sendError(400, "redirect_uri is required");
            return;
        }

        Map<String, String> params = authHandler.buildRedirectParams(redirect_uri, state);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirect_uri);
        params.forEach(builder::queryParam);
        String redirectUrl = builder.build().toUriString();

        log.info("[AUTH] Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    /** routes.yaml を再読み込みします（再起動不要） */
    @GetMapping("/stub/reload")
    public ResponseEntity<String> reload() throws IOException {
        routeLoader.reload();
        return ResponseEntity.ok("Routes reloaded");
    }
}
