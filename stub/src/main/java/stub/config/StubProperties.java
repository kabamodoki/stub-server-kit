package stub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stub")
public class StubProperties {

    private String routesFile = "config/routes.yaml";
    private Auth auth = new Auth();

    public String getRoutesFile() { return routesFile; }
    public void setRoutesFile(String routesFile) { this.routesFile = routesFile; }

    public Auth getAuth() { return auth; }
    public void setAuth(Auth auth) { this.auth = auth; }

    public static class Auth {
        private String loginPath = "/login";
        private String defaultToken = "stub-token-123";
        private String tokenParam = "token";

        public String getLoginPath() { return loginPath; }
        public void setLoginPath(String loginPath) { this.loginPath = loginPath; }

        public String getDefaultToken() { return defaultToken; }
        public void setDefaultToken(String defaultToken) { this.defaultToken = defaultToken; }

        public String getTokenParam() { return tokenParam; }
        public void setTokenParam(String tokenParam) { this.tokenParam = tokenParam; }
    }
}
