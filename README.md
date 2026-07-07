# stub-server-kit

ローカル開発を快適にする2つのツールのセット。

```
stub-server-kit/
├── env/    # ①env ツール  — 本体アプリが読む環境変数を一時的にシェルへ流し込む
└── stub/   # ②stub サーバー — 本体アプリが接続する外部API・バックエンドのスタブ
```

---

## ① env ツール

本体アプリが参照する環境変数（API_URL・DBパスワード等）を、  
現在のシェルセッションに一時的に設定します。  
セッションを閉じると消えます（永続変更なし）。

### 使い方

```powershell
# 1. .local.env.example をコピー
cp stub-server-kit/env/.local.env.example .local.env

# 2. .local.env を編集して…

# 3. 読み込む（PowerShell）
. .\stub-server-kit\env\load-env.ps1

# bash / zsh
source ./stub-server-kit/env/load-env.sh

# cmd
CALL stub-server-kit\env\load-env.bat
```

読み込み後に `yarn start` や `mvn spring-boot:run` を実行すると、  
本体アプリから設定した環境変数が参照されます。

### .local.env のフォーマット

```
# コメント行
API_URL=http://localhost:3132
DB_PASSWORD="my password"
```

`.local.env` は `.gitignore` に追加することを推奨します。

---

## ② stub サーバー（Java / Maven / Spring Boot）

本体アプリが接続する外部API・バックエンドの代役として動作します。  
YAMLにエンドポイントと返却値を書くだけ、Java不要。

### セットアップ

```bash
cd stub-server-kit/stub
mvn spring-boot:run
```

http://localhost:3132 で起動します。

### APIスタブの定義 — `config/routes.yaml`

```yaml
routes:
  - method: GET
    path: /api/users
    response:
      status: 200
      body:
        - id: 1
          name: Stub User

  - method: POST
    path: /api/items
    delay: 300        # ms（省略可）
    response:
      status: 201
      body:
        message: created
```

追加するだけでAPIが増えます。変更後は `GET /stub/reload` で再起動不要で反映。

### サーバー設定 — `config/application.yml`

```yaml
server:
  port: 3132

stub:
  auth:
    default-token: stub-token-123
    token-param: token
```

### 疑似ログイン（認証スタブ）

本体アプリの認証リダイレクト先として動作します。

```
GET /login?redirect_uri=http://yourapp/callback&state=xyz
```

疑似ログインページが表示 → 「ログイン」ボタン → `redirect_uri?token=stub-token-123&state=xyz` へリダイレクト。

#### カスタマイズ

`src/main/java/stub/AuthHandler.java` の `buildRedirectParams()` を編集するだけ。

```java
public Map<String, String> buildRedirectParams(String redirectUri, String state) {
    Map<String, String> params = new LinkedHashMap<>();
    // 認可コードフローに変える例
    params.put("code", "stub-auth-code-xyz");
    if (state != null) params.put("state", state);
    return params;
}
```

### ルート一覧

| パス | 用途 |
|------|------|
| `GET /login` | 疑似ログインページ |
| `POST /login/submit` | ログイン処理 → redirect_uri へリダイレクト |
| `GET /stub/reload` | routes.yaml を再読み込み（再起動不要） |
| その他すべて | routes.yaml の定義に従って返却 |
