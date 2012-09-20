package mBovin.TeamStats.LiveUpdate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mBovin.TeamStats.LiveUpdateSelectActivity;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LeagueListManager extends AsyncTask<String, Object, Object>{
	private static final String TAG = "LeagueListManager.java";
	public static final String NEW_LEAGUES = "new_Leagues";
	public static final String ALL_LEAGUES = "all_leagues";
	public static final String ARCHIVE = "archive";
	public static final String DOWNLOAD = "Download Selected";
	public static final String DOWNLOAD_ARCHIVE = "Dowload Selected Archive";
	
	
	private static final String cActiveListURL = "http://www.aragon.ws/soccerdb/export/liveupdateleagues.php";
	private static final String cArchivelistURL = "http://www.aragon.ws/soccerdb/export/completegrouplist.php";
	public static final String cActiveListFilename = "activeleagues.xml";
	public static final String cArchiveListFilename = "archiveleagues.xml";
	private static final String cLastSyncActiveListFilename = "lastsyncactiveleagues.xml";
	private newLeagueListener leagueListener;
	private LeagueListDatabaseConnector mLeagueListDatabaseConnector; 
	
	private List<DownloadableLeague> mAddedLeagueList;
	private Context mContext;
	
	public LeagueListManager(LeagueListDatabaseConnector db, newLeagueListener listener, Context context) {
		mLeagueListDatabaseConnector = db;
		leagueListener = listener;
		mContext = context;
	}
	
	public List<DownloadableLeague> getmAddedLeagueList() {
		return mAddedLeagueList;
	}
	
	public interface newLeagueListener {
		public void onLeagueListUpdated(List<LeagueByCountry> newleagues);		
	}
	
		@Override
		protected Object doInBackground(String... arg0) {
			if (arg0[0].equals(NEW_LEAGUES)) {
				checkForNewLeagues();
			} else if (arg0[0].equals(ALL_LEAGUES)) {
				getAllLeagues();
			} else if (arg0[0].equals(ARCHIVE)) {
					//getArchiveLeagues();
			} else if (arg0[0].equals(DOWNLOAD)) {
				
			} else if (arg0[0].equals(DOWNLOAD_ARCHIVE)) {
				
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			leagueListener.onLeagueListUpdated(SetList(mAddedLeagueList));
		}
	
	private void checkForNewLeagues() {
		List<DownloadableLeague> localLeagueList = mLeagueListDatabaseConnector.getCurrent();
		
		List<DownloadableLeague> removeLeagueList = ParseActiveXML(cActiveListURL);
		
		mAddedLeagueList = CreateAddedLeagueList(localLeagueList, removeLeagueList);
		
		if (mAddedLeagueList.size() > 0) {
			// Log 
			for (DownloadableLeague league : mAddedLeagueList) {
				mLeagueListDatabaseConnector.addLeague(league);
			}
		}
	}
	
	private List<DownloadableLeague> ParseActiveXML(String urlString) {
		try {
			URL url = new URL(urlString);
			
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser parser = saxParserFactory.newSAXParser();
			
			XMLReader xmlReader = parser.getXMLReader();
			DownloadableLeagueHandler myHandler = new DownloadableLeagueHandler();
			xmlReader.setContentHandler(myHandler);
			
			xmlReader.parse(new InputSource(url.openStream()));
			
			return myHandler.getParsedData();
			
		} catch (SAXException e) {
			Log.e(TAG, "Issue with Parser");
			Log.e(TAG, e.toString());
		} catch (MalformedURLException e) {
			Log.e(TAG, e.toString());
		} catch (ParserConfigurationException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return null;
	}
	
	private void getAllLeagues() {
		mAddedLeagueList = mLeagueListDatabaseConnector.getAllLeagues();
		if (mAddedLeagueList.size() == 0 ) {
			mAddedLeagueList = ParseActiveXML(cActiveListURL);
			if (mAddedLeagueList.size() != 0) {
				// We have some data
				for (DownloadableLeague league : mAddedLeagueList) {
					mLeagueListDatabaseConnector.addLeague(league);
				}
			}
			
		}
	}
	

	private List<DownloadableLeague> CreateAddedLeagueList(List<DownloadableLeague> oldList,
			List<DownloadableLeague> newList) {
		List<DownloadableLeague> newLeagueList = new ArrayList<DownloadableLeague>();
		if ((oldList != null) && (oldList.size() > 0) && (newList != null)) {
			for (DownloadableLeague newLeague : newList) {
				if (!oldList.contains(newLeague)) {
					newLeagueList.add(newLeague);
				}
			}
		}
		return newLeagueList;
	}

	protected ArrayList<LeagueByCountry> SetList(
			List<DownloadableLeague> newleagues) {
		ArrayList<LeagueByCountry> returndata = new ArrayList<LeagueByCountry>();
		List<String> countries = new ArrayList<String>();
		for (int i = 0; i < newleagues.size(); i++) {
			String item = newleagues.get(i).Country;
			if (!countries.contains(item)) {
				countries.add(item);
			}
		}
		Collections.sort(countries);
		for (int i = 0; i < countries.size(); i++) {
			ArrayList<DownloadableLeague> leagues = new ArrayList<DownloadableLeague>();
			for (int x = 0; x < newleagues.size(); x++) {
				if (countries.get(i).equals(newleagues.get(x).Country)) {
					leagues.add(newleagues.get(x));
				}
			}
			LeagueByCountry lc = new LeagueByCountry();
			lc.setCountry(countries.get(i));
			lc.setLeagues(leagues);
			lc.findFlag(mContext);
			returndata.add(lc);
		}
		return returndata;
	}

}
