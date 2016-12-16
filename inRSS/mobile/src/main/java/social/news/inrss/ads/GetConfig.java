package social.news.inrss.ads;


import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by mac on 11/28/16.
 */
public class GetConfig {

    public static String appNextId = "";
    public static String instalId = "";
    public static String startAppId = "";
    String packageName = "";

    public GetConfig(String packageName) {
        this.packageName = packageName;
        /////// load ads
        new AsyncTaskGetNews().execute();
    }

    // Khai bao các biến cần lấy giá trị từ server trả về.
    public static JSONArray jsonArrayDataServer = new JSONArray();

    class AsyncTaskGetNews extends AsyncTask<String, String, String> {

        // set your json string url here
        String yourJsonStringUrl = ""; //

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                yourJsonStringUrl = ConfigParam.ConfigUrl1 + ConfigParam.ConfigUrl2 + ConfigParam.ConfigUrl3;
//                    yourJsonStringUrl = Base64.decode(yourJsonStringUrl);
                byte[] bytes = Base64.decode(yourJsonStringUrl, Base64.DEFAULT);
                yourJsonStringUrl = new String(bytes, "UTF-8");

                // instantiate our json parser
                JsonParser jParser = new JsonParser();

                // get json string from url
                JSONArray json = jParser.getJSONFromUrl(yourJsonStringUrl);

                jsonArrayDataServer = json;

                if (jsonArrayDataServer != null) {
                    for (int i = 0; i < jsonArrayDataServer.length(); i++) {
                        JSONObject obj = (JSONObject) jsonArrayDataServer.get(i);
                        if (obj.getString("app").equals(packageName)) {
                            if (obj.getBoolean("show_appnext")) {
                                appNextId = obj.getString("appnext_id");
                            }
                            if (obj.getBoolean("show_startapp")) {
                                startAppId = obj.getString("startapp_id");
                            }
                            if (obj.getBoolean("show_instal")) {
                                instalId = obj.getString("instal_id");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // lay gia tri cac bien ra.
            }

            return null;
        }
    }

}
