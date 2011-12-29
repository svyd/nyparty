package com.masterofcode.android.hackathon.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class RestClient {
	
	private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	public static JSONObject connect(String url)
    {
		String result;
		JSONObject json = null;
 
        HttpClient httpclient = new DefaultHttpClient();
 
        // Prepare a request object
        HttpGet httpget = new HttpGet(url); 
 
        // Execute the request
        HttpResponse response;
        try {
        	if (Constants.ISDEBUG)
            	Log.d(Constants.LOGTAG, url);
            response = httpclient.execute(httpget);
            // Examine the response status
            if (Constants.ISDEBUG)
            	Log.d(Constants.LOGTAG, response.getStatusLine().toString());
            
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
 
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result= convertStreamToString(instream);
                if (Constants.ISDEBUG)
                	Log.d(Constants.LOGTAG, result);
 
                // A Simple JSONObject Creation
                if (result.equals("null\n") || TextUtils.isEmpty(result)){
                	json = null;
                } else {
                	json=new JSONObject(result);
                }
                	
                 
                // Closing the input stream will trigger connection release
                instream.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return json;
    }	
	
	public static String	sendPut(String url, String id, String author){

		String result = null;
		try{
			
			HttpClient httpclient = new DefaultHttpClient();
			 
	        // Prepare a request object
	        HttpPut httpput = new HttpPut(url);
	        MultipartEntity reqEntity = new MultipartEntity();
		    reqEntity.addPart("author", new StringBody(author));
		    httpput.setEntity(reqEntity);
	        // Execute the request
	        HttpResponse response;
	        try {
	        	if (Constants.ISDEBUG)
	            	Log.d(Constants.LOGTAG, url);
	            response = httpclient.execute(httpput);
	            // Examine the response status
	            if (Constants.ISDEBUG)
	            	Log.d(Constants.LOGTAG, response.getStatusLine().toString());
	            result = String.valueOf(response.getStatusLine().getStatusCode());
	 
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	public static void doFileUpload(String filePath1, String url){
		File file1 = new File(filePath1);
		String urlString = url;
		if(!TextUtils.isEmpty(urlString))
			try {
				HttpClient client = new DefaultHttpClient();
			    HttpPut post = new HttpPut(urlString);
			    FileBody bin1 = new FileBody(file1);
			    MultipartEntity reqEntity = new MultipartEntity();
			    reqEntity.addPart("phrase[photo]", bin1);
			    reqEntity.addPart("_method", new StringBody("put"));
			    post.setEntity(reqEntity);
			    HttpResponse response = client.execute(post);
			    HttpEntity resEntity = response.getEntity();
			    final String response_str = EntityUtils.toString(resEntity);
			    if (resEntity != null) {
			    	if (Constants.ISDEBUG)
			    		Log.d(Constants.LOGTAG, "Response sending: " + response_str);
			    }
			} catch (Exception ex){
				ex.printStackTrace();
			}
	}
}
