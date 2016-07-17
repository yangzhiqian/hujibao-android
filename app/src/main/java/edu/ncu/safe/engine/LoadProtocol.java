package edu.ncu.safe.engine;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.ncu.safe.R;

/**
 * 用户从服务器加载app说明
 */
public class LoadProtocol {
	Context context;

	public LoadProtocol(Context context) {
		this.context = context;
	}

	public String loadProtocol() throws IOException, XmlPullParserException {
		try {
			String path = context.getResources()
					.getString(R.string.protocol);
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			InputStream is = conn.getInputStream();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();

			while (type != XmlPullParser.END_DOCUMENT) {
				if(type == XmlPullParser.START_TAG){
					if("protocol".equals(parser.getName())){
						String text = parser.nextText();
						return text;
					}
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
