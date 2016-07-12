package edu.ncu.safe.engine;

import android.app.ProgressDialog;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("unused")
public class DownLoadFile {

	public static File downloadFile(String downloadUrl, String path,
			ProgressDialog progressDialog) throws IOException {

		URL url = new URL(downloadUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");

		if (conn.getResponseCode() == 200) {
			Log.i("tag", "下载新版本连接成功");
			InputStream is = conn.getInputStream();

			int totalLength = conn.getContentLength();
			progressDialog.setMax(totalLength);

			int progress = 0;

			int len;
			byte[] buff = new byte[1024];
			File file = new File(path);
			FileOutputStream fos = new FileOutputStream(file);

			while ((len = is.read(buff)) != -1) {
				fos.write(buff, 0, len);
				progress += len;
				progressDialog.setProgress(progress);
			}
			fos.flush();
			fos.close();
			is.close();
			return file;
		}
		return null;
	}
}
