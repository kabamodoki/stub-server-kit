package stub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 疑似ログイン後のリダイレクトパラメータを生成します。
 *
 * ---------------------------------------------------------------
 * ここを編集してアプリが期待するパラメータを返してください。
 *
 * 例1: access_token + token_type を返す
 *   params.put("access_token", "my-token");
 *   params.put("token_type", "Bearer");
 *
 * 例2: 認可コードフロー（code を返す）
 *   params.put("code", "stub-auth-code-xyz");
 *
 * state は OAuth 2.0 要件のため自動で含まれます。
 * ---------------------------------------------------------------
 */
@Component
public class AuthHandler {

    @Value("${stub.auth.default-token:stub-token-123}")
    private String defaultToken;

    @Value("${stub.auth.token-param:token}")
    private String tokenParam;

    public Map<String, String> buildRedirectParams(String redirectUri, String state) {
        Map<String, String> params = new LinkedHashMap<>();

        // デフォルト: トークンをそのまま返す
        params.put(tokenParam, defaultToken);

        // state は必ず返す（OAuth 2.0 要件）
        if (state != null && !state.isBlank()) {
            params.put("state", state);
        }

        return params;
    }
}
