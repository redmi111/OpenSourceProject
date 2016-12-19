package com.music.musicplayer135.ads;


import android.os.AsyncTask;
import android.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mac on 11/28/16.
 */
public class GetConfig {
//        static Accounts mainActivity;
    public  static String AppNextId = "";
    public static String InstalId = "";
    public static String StartAppId = "";
        String PackageName = "";

        public GetConfig( String packageName) {
            this.PackageName = packageName;
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
                    yourJsonStringUrl = new String(Base64.decode(yourJsonStringUrl, Base64.DEFAULT));
                    // instantiate our json parser
                    JsonParser jParser = new JsonParser();

                    // get json string from url
                    JSONArray json = jParser.getJSONFromUrl(yourJsonStringUrl);

                    jsonArrayDataServer = json;

                    if(jsonArrayDataServer!=null) {
                        for (int i = 0; i < jsonArrayDataServer.length(); i++) {
                            JSONObject obj = (JSONObject) jsonArrayDataServer.get(i);
                            if (obj.getString("app").equals(PackageName)) {
                                if (obj.getBoolean("show_appnext")) {
                                    AppNextId = obj.getString("appnext_id");
                                }
                                if (obj.getBoolean("show_startapp")) {
                                    StartAppId = obj.getString("startapp_id");
                                }
                                if (obj.getBoolean("show_instal")) {
                                    InstalId = obj.getString("instal_id");
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
