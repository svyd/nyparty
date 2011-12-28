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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
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
	
	public static Boolean sendMedia(String serverUrl, String mediaPath) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;

		String urlServer = serverUrl;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;
		
		try {
			File file = new File(mediaPath);
			FileInputStream fileInputStream = new FileInputStream(file);
			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
			
			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			
			// Enable POST method
			connection.setRequestMethod("POST");

			String boundary = "---------------------------AAAA" + mediaPath + "AAAA";

			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\"" + mediaPath + "\"" + lineEnd);
			outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
			outputStream.writeBytes(lineEnd);
			
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();

			if ( (serverResponseCode == 200) && (serverResponseMessage.equals("OK")) ) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}
}
