package stub.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stub.model.ResponseDefinition;
import stub.model.RouteDefinition;
import stub.service.RouteLoader;

import java.util.Map;
import java.util.Optional;

@RestController
public class StubApiController {

    @Autowired
    private RouteLoader routeLoader;

    /**
     * routes.yaml に定義されたルートへのリクエストをすべて処理します。
     * /login など他のコントローラが持つ具体的なパスはそちらが優先されます。
     */
    @RequestMapping("/**")
    public ResponseEntity<Object> handle(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        Optional<RouteDefinition> routeOpt = routeLoader.findRoute(method, path);

        if (routeOpt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Not found", "path", path));
        }

        RouteDefinition route = routeOpt.get();
        ResponseDefinition response = route.getResponse();

        if (route.getDelay() > 0) {
            try {
                Thread.sleep(route.getDelay());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatus());

        if (response.getHeaders() != null) {
            response.getHeaders().forEach(builder::header);
        }

        return response.getBody() != null
                ? builder.body(response.getBody())
                : builder.build();
    }
}
