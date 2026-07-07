package stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class StubServerApplication {

    private static final Logger log = LoggerFactory.getLogger(StubServerApplication.class);

    @Value("${server.port:3000}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(StubServerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("  Stub Server ready  →  http://localhost:{}", port);
        log.info("  Login page         →  http://localhost:{}/login", port);
        log.info("  Reload routes      →  GET /stub/reload");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
