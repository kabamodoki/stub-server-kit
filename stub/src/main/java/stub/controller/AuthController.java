package stub.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import stub.AuthHandler;
import stub.service.RouteLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthHandler authHandler;

    @Autowired
    private RouteLoader routeLoader;

    /** 疑似ログインページを表示します（/** に横取りされないよう直接返す） */
    @GetMapping(value = "${stub.auth.login-path:/login}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> loginPage() throws IOException {
        byte[] html = new ClassPathResource("static/login.html").getInputStream().readAllBytes();
        return ResponseEntity.ok()
                .header("Content-Type", "text/html;charset=UTF-8")
                .body(html);
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

    /**
     * リダイレクト受信確認用コールバック。
     * redirect-check.html の redirect_uri にこのURLを指定して使います。
     * http://localhost:3132/stub/callback
     */
    @GetMapping("/stub/callback")
    public ResponseEntity<byte[]> callback(HttpServletRequest request) {
        StringBuilder rows = new StringBuilder();
        request.getParameterMap().forEach((key, values) -> {
            String val = String.join(", ", values);
            rows.append("<tr><td>").append(escape(key)).append("</td>")
                .append("<td>").append(escape(val)).append("</td></tr>");
        });

        String html = """
                <!DOCTYPE html><html lang="ja"><head><meta charset="UTF-8">
                <title>Callback — stub-server-kit</title>
                <style>
                  body{font-family:system-ui,sans-serif;background:#f0f2f5;display:flex;
                       align-items:center;justify-content:center;min-height:100vh;margin:0}
                  .card{background:#fff;border-radius:12px;padding:32px;min-width:400px;
                        box-shadow:0 2px 12px rgba(0,0,0,.08)}
                  .badge{background:#22c55e;color:#fff;font-size:11px;font-weight:700;
                         letter-spacing:1px;padding:2px 8px;border-radius:4px;margin-bottom:12px;display:inline-block}
                  h1{font-size:18px;margin-bottom:20px}
                  table{width:100%;border-collapse:collapse;font-size:13px}
                  th{background:#f5f6fa;padding:8px 12px;text-align:left;border:1px solid #e5e7eb}
                  td{padding:8px 12px;border:1px solid #e5e7eb;font-family:monospace;word-break:break-all}
                  .empty{color:#aaa;font-size:13px;margin-top:12px}
                  .back{display:inline-block;margin-top:20px;font-size:13px;color:#4f46e5;text-decoration:none}
                </style></head><body>
                <div class="card">
                  <div class="badge">CALLBACK</div>
                  <h1>リダイレクトを受信しました</h1>
                """ +
                (rows.isEmpty()
                    ? "<p class='empty'>パラメータなし</p>"
                    : "<table><tr><th>パラメータ</th><th>値</th></tr>" + rows + "</table>") +
                """
                  <a class="back" href="/redirect-check.html">← 検証ページへ戻る</a>
                </div></body></html>
                """;

        return ResponseEntity.ok()
                .header("Content-Type", "text/html;charset=UTF-8")
                .body(html.getBytes(StandardCharsets.UTF_8));
    }

    /** routes.yaml を再読み込みします（再起動不要） */
    @GetMapping("/stub/reload")
    public ResponseEntity<String> reload() throws IOException {
        routeLoader.reload();
        return ResponseEntity.ok("Routes reloaded");
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
