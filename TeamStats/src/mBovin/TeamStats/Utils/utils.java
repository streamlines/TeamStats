package mBovin.TeamStats.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.util.Log;

// Creates static helper functions.
public class utils {
	private static final String TAG = "Utils.java";

	public static Document DownloadXMLDocument(String urlstring) {
		HttpGet uri = new HttpGet(urlstring);
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp = null;
		try {
			resp = client.execute(uri);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StatusLine status = resp.getStatusLine();
		if (status.getStatusCode() != 200) {
			Log.d("UTIL", "HTTP error, invalid server status code: " + resp.getStatusLine());
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(resp.getEntity().getContent());
		} catch (ParserConfigurationException e) {
			Log.e(TAG, e.toString());
		} catch (IllegalStateException e) {
			Log.e(TAG, e.toString());
		} catch (SAXException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return doc;
	}
		
	public static File DownloadFile(String urlString) {	
		
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
		
			File file = new File("temp.xml");
		
			FileOutputStream fileOutput = new FileOutputStream(file);
			InputStream inputStream = urlConnection.getInputStream();
		
			int totalSize = urlConnection.getContentLength();
		
			int downloadedSize = 0;
		
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
		
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
				int progress=(int)(downloadedSize*100/totalSize);
				// if way to report progress.
			}
		
			fileOutput.close();
			return file;
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void CopyFile(InputStream in, OutputStream out) {
		try {
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
