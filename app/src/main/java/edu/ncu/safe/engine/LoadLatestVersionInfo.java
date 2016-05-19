package edu.ncu.safe.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import edu.ncu.safe.R;
import edu.ncu.safe.domain.VersionInfo;

public class LoadLatestVersionInfo {

	private Context context;
	public LoadLatestVersionInfo(Context context){
		this.context = context;
	}
	
	
	public VersionInfo getVersionInfo() throws IOException, JSONException{
		String path =context.getResources().getString(R.string.versionrul);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		
		InputStream is = conn.getInputStream();
		String json = getJson(is);
		return parseJsonToVersionInfo(json);
	}
	
	private String getJson(InputStream is) throws IOException{
		StringBuffer sb = new StringBuffer();
		String buffer;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while((buffer=br.readLine())!=null){
			sb.append(buffer);
		}
		return sb.toString();
	}
	
	private VersionInfo parseJsonToVersionInfo(String json) throws JSONException{
		JSONObject obj = new JSONObject(json);
		String version = obj.getString("version");
		String description = obj.getString("description");
		String downloadUrl = obj.getString("downloadUrl");
		
		return new VersionInfo(version,description,downloadUrl);
	}
}
