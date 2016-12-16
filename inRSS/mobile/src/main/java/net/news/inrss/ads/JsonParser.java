package net.news.inrss.ads;

/**
 * Created by mac on 11/28/16.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
//import java.io.*;

public class JsonParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    org.json.JSONArray jsonArray = null;

    static String json = "";

    public org.json.JSONArray getJSONFromUrl(String url) {

        json = "";
        jObj = null;
        // make HTTP request
        try {

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(params, 11000);

            DefaultHttpClient httpClient = new DefaultHttpClient(params);


            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
//            String url1 = "https://s3.ap-south-1.amazonaws.com/opensource-emin-projects/config.txt?";
//
//            URL obj = new URL(url1);
//            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//
//            //add reuqest header
//            con.setRequestMethod("GET");
//            con.setRequestProperty("User-Agent", "Mozilla/5.0");
//            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//            // Send post request
//            con.setDoOutput(true);
////            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
////            wr.flush();
////            wr.close();
//
//            Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
//            StringBuilder sb = new StringBuilder();
//            for (int c; (c = in.read()) >= 0; )
//                sb.append((char) c);
//            in.close();
//            json = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG, "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
         jsonArray = new org.json.JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e(TAG, "Error parsing data " + e.toString());
        }
        // return JSON String
        return jsonArray;
    }

  }