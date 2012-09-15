package mBovin.TeamStats.LiveUpdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import mBovin.TeamStats.DefaultActivity;
import mBovin.TeamStats.Core.League;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class LeagueDownloader extends AsyncTask<Object, Object, String> {
	private static final String TAG = "LeagueDownloader.java";

	private final static String cLeagueBaseURL = "http://www.aragon.ws/soccerdb/util/ppccreate.php";
	private List<DownloadableLeague> mLeagueList;
	private League mleague;
	private LeagueDownloadListener mleagueListener;
	private Context context;
	private Resources resources;

	
	private String mfilename;
	
	public interface LeagueDownloadListener {
		public void onLeagueDownloaded(String league);
	}
	
	public LeagueDownloader(List<DownloadableLeague> mLeagueList, Context context, LeagueDownloadListener listener) {
		this.mLeagueList = mLeagueList;
		this.mleague = null;
		this.mleagueListener = listener;
		this.context = context;
		this.resources = context.getResources();
	}
	
	public LeagueDownloader(League mleague, Context context, LeagueDownloadListener listener) {
		this.mleague = mleague;
		this.mLeagueList = null;
		this.mleagueListener = listener;
		this.context = context;
		this.resources = context.getResources();
	}

	@Override
	protected String doInBackground(Object... arg0) {
		if (mleague == null) {
			downloadLeagueList();
		} else {
			downloadSingleLeague();
		}
		
		return null;
	}

	private void downloadLeagueList() {
		for (DownloadableLeague league : mLeagueList) {
			String url = cLeagueBaseURL + "?id=" + league.id;
			
			try {
				String filename = downloadLeaguefile(url);
				Log.e(TAG, filename + "...OK" );
			} catch (IOException e) {
				Log.e(TAG, url + "...failed");
			} catch (URISyntaxException e) {
				Log.e(TAG, url + "...invalid URL");
			}
		}
	}

	private void downloadSingleLeague() {
		String url = cLeagueBaseURL + "?name=" + mleague.getmName() + "&year=" + mleague.getmYear();
		try {
			String filename = downloadLeaguefile(url);
			Log.e(TAG, filename + "...OK" );
		} catch (IOException e) {
			Log.e(TAG, url + "...failed");
		} catch (Exception e) {
			Log.e(TAG, url + "...failed");
		}
	}
	
	private String downloadLeaguefile(String urlString) throws IOException, URISyntaxException {
		URI uri = new URI(null, urlString, getQueryString());
		HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(true);
		connection.setConnectTimeout(30000);
		
		connection.connect();
		
		int status = connection.getResponseCode();
		if (status == 200) {
			mfilename = getFilename(connection.getHeaderFields());
			File file = new File(mfilename);
			File path = context.getFilesDir();
			InputStream input = connection.getInputStream();
			byte[] buffer = new byte[4096];
			int n = -1;
			OutputStream output = new FileOutputStream(path + File.separator +  file);
			while ((n = input.read(buffer)) != -1) {
				if (n > 0) {
					output.write(buffer, 0, n) ;
				}
			}
			output.close();
		} else if (status == 500) {
			InputStream error = connection.getErrorStream();
			Log.e(TAG, error.toString());
		}
		connection.disconnect();
		return null;
	}

	private String getFilename(Map<String, List<String>> response) {
		String filename = null;
		List<String> contentType = response.get("Content-Type");
		for (String data : contentType) {
			if (data.startsWith("application/x-teamstats")) {
				filename = data.substring(31, data.length()-1);
			}
		}
		
		return filename;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (mfilename != null) {
			mleagueListener.onLeagueDownloaded(mfilename);
		} else {
			Toast errorToast = Toast.makeText(context, resources.getString(mBovin.TeamStats.R.string.noconnection2), Toast.LENGTH_LONG);
			errorToast.setGravity(Gravity.CENTER, 0, 0);
			errorToast.show();
		}
	}
	
	private String getQueryString() {
		String android_id = Secure.ANDROID_ID;
		String buildVersion = Build.VERSION.RELEASE;
		String queryString = "&tsVersion=" + DefaultActivity.cLiveUpdateVersion + "&deviceId=" + android_id + "&osVersion=" + buildVersion;
		return queryString;
	}
	
	
}
