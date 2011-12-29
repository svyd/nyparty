package com.masterofcode.android.hackathon.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class ServerCommunication {
	
	private static ServerCommunication		instance = null;

	HttpURLConnection connection 	= null;
	DataOutputStream outputStream 	= null;
	DataInputStream inputStream 	= null;

	String lineEnd 		= "\r\n";
	String twoHyphens 	= "--";
	String boundary 	= "*****";

	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 1*1024*1024;
	
	public ServerCommunication(){
		super();
	}
	
	static public ServerCommunication getInstance(){
		
		if (instance == null)
			instance = new ServerCommunication();
		return instance;
	}

	public void doFileUpload(String filePath1, String url){
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
