package com.ciego.sensorappnativa.parsers;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ciego on 16/09/2015.
 */
public class JSONParser {

    static InputStream is = null;
    static JSONObject jobj = null;
    static JSONArray jarray = null;
    static String json = "";
    public JSONParser(){

    }
    public JSONArray makeHttpRequest(String url){
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity httpentity = httpresponse.getEntity();
            is = httpentity.getContent();
            //is = httpresponse.getEntity().getContent();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while((line = reader.readLine())!=null){
                    sb.append(line+"\n");

                }
                is.close();
                json = sb.toString();
                try {
                    jarray = new JSONArray(json);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jarray;

    }

    public JSONArray makeHttpRequestWithParams(String url, List<NameValuePair> params){
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity httpentity = httpresponse.getEntity();
            is = httpentity.getContent();
            //is = httpresponse.getEntity().getContent();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while((line = reader.readLine())!=null){
                    sb.append(line+"\n");

                }
                is.close();
                json = sb.toString();
                try {
                    jarray = new JSONArray(json);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jarray;

    }

    public JSONArray consultWebService(String url){
        try{
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            HttpResponse httpresponse = httpclient.execute(httppost);

            String stringResponse = null;

            stringResponse = EntityUtils.toString(httpresponse.getEntity());

            JSONArray jsonArray = null;

            jsonArray = new JSONArray(stringResponse);

            return jsonArray;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*public String getEmail(){
        JSONArray array = consultWebService();

        ArrayList<Cause> listCauses = new ArrayList<Cause>();

        for(int i=0; i <array.length(); i++)
        {
            try{

                JSONObject json = array.getJSONObject(i);
                Cause cause = parseCause(json);
                listCauses.add(cause);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        return listCauses;
    }

    private Cause parseCause(JSONObject json){
        String title = "";
        int daysRemaining = 0;
        int percentage = 0;
        String urlImage = "";
        ArrayList<Integer> supporters = new ArrayList<Integer>();
        String id = "";

        try {
            title = json.getString("titile");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            daysRemaining = json.getInt("days");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            percentage = json.getInt("support");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            urlImage = json.getString("image");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            id = json.getString("id");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray = json.getJSONArray("supporters");
            for(int i = 0; i < jsonArray.length(); i++){
                supporters.add(jsonArray.getInt(i));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        Cause cause = new Cause(title, daysRemaining,percentage,urlImage,supporters, id);

        return cause;
    }*/

    public String getStringFromURL(){
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext contexto = new BasicHttpContext();
        HttpPost httpPost = new HttpPost("http://sensorsafe.com.mx/Wallet_Ferretuerk/prueba.php");
        String answer = null;

        try{
            HttpResponse response = httpClient.execute(httpPost, contexto);
            HttpEntity entity = response.getEntity();
            answer = EntityUtils.toString(entity, "UTF-8");
        }catch (Exception e){

        }
        return answer;
    }

    public ArrayList<String> getJSONData(String response){
        ArrayList<String> list = new ArrayList<String>();

        try{
            JSONArray json = new JSONArray(response);
            String text = "";
            for(int i = 0; i < json.length(); i++){
                text = json.getJSONObject(i).getString("NAME") + " - " +
                        json.getJSONObject(i).getString("EMAIL") + " - " +
                        json.getJSONObject(i).getString("PASSWORD");
                list.add(text);
            }
        }catch(Exception e){

        }
        return list;
    }

}
