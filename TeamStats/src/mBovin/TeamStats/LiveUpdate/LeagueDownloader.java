package mBovin.TeamStats.LiveUpdate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.os.AsyncTask;

public class LeagueDownloader extends AsyncTask<Object, Object, String> {
	private final static String cLeagueBaseURL = "http://www.aragon.ws/soccerdb/util/ppccreate.php";
	
	
	
	@Override
	protected String doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	private String getFilename(HttpResponse response) {
		String filename = null;
		String header = response.getEntity().getContentType().toString();
		if (header.startsWith("application/x-teamstats")) {
			CharSequence arrFileName = header.subSequence(header.indexOf("filename=") + 9, header.indexOf(".ts")+4);
			filename = arrFileName.toString();
		}
		return filename;
	}
}
