package app.dodi.com.prodigykms.object;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 07/01/2018.
 */

public class DownloadObject {

    private Map<String, String> param = new HashMap<>();
    private JSONObject response;
    private final String tag, url;

    public DownloadObject(Map<String, String> param, String tag, String url) {
        this.tag = tag;
        this.url = url;
        if (param!=null)
            this.param = param;

    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public JSONObject getResponse() {
        return response;
    }

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }
}
