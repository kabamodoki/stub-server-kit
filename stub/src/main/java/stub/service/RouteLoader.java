package stub.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import stub.config.StubProperties;
import stub.model.RouteDefinition;
import stub.model.RoutesConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteLoader {

    private static final Logger log = LoggerFactory.getLogger(RouteLoader.class);

    private final StubProperties stubProperties;
    private List<RouteDefinition> routes = new ArrayList<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RouteLoader(StubProperties stubProperties) {
        this.stubProperties = stubProperties;
    }

    @PostConstruct
    public void load() throws IOException {
        File file = new File(stubProperties.getRoutesFile());
        if (!file.exists()) {
            log.warn("Routes file not found: {}", file.getAbsolutePath());
            return;
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RoutesConfig config = mapper.readValue(file, RoutesConfig.class);
        this.routes = config.getRoutes();

        log.info("Loaded {} route(s) from {}", routes.size(), routesFile);
        routes.forEach(r ->
                log.info("  [STUB] {} {}", r.getMethod().toUpperCase(), r.getPath()));
    }

    public Optional<RouteDefinition> findRoute(String method, String path) {
        if (path == null) return Optional.empty();
        return routes.stream()
                .filter(r -> r.getMethod().equalsIgnoreCase(method))
                .filter(r -> r.getPath() != null)
                .filter(r -> pathMatcher.match(toAntPattern(r.getPath()), path))
                .findFirst();
    }

    /** Express の :param 記法を Ant の * ワイルドカードに変換 */
    private String toAntPattern(String path) {
        return path.replaceAll(":([a-zA-Z_][a-zA-Z0-9_]*)", "*");
    }

    /** ルート定義を再読み込みします（サーバー再起動なしで反映） */
    public void reload() throws IOException {
        load();
    }
}
