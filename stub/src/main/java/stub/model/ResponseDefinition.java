package stub.model;

import java.util.Map;

public class ResponseDefinition {

    private Integer status;
    private Object body;
    private Map<String, String> headers;

    public int getStatus() {
        return status != null ? status : 200;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
