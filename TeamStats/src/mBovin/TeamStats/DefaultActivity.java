/*
 * TeamStats by Mats Bovin (tsppc@mbovin.com)
 * v1.3.0 February 2007
 * Copyright (c) 2004-07 Mats Bovin
 * 
 * Android / Java port by Hayden Pronto-Hussey 
 * v0.1 August 2012
 *
 * This file is part of TeamStats.
 *
 * TeamStats is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TeamStats is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TeamStats; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package mBovin.TeamStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import mBovin.TeamStats.Core.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import mBovin.TeamStats.R;

@SuppressWarnings("deprecation")
public class DefaultActivity extends TabActivity {
	
	private TabHost mTabHost;
	private Spinner mSpinnerLeague;
	private Spinner mSpinnerSeason;
	
	private Map<String, HashMap<String, String>> mLeagues;
	private AppState appstate;
	private ArrayList<String> LeagueNames = new ArrayList<String>();
	private ArrayList<String> SeasonNames= new ArrayList<String>();
	private SharedPreferences savedData;
	
	private UIMode mode;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		savedData = getSharedPreferences("TeamStats", MODE_PRIVATE);
		appstate.Load(savedData);
		
		mLeagues = new HashMap<String, HashMap<String, String>>();
		
		
		mTabHost = getTabHost();
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getString(R.string.tablepage)).setContent(R.id.tabTable));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getString(R.string.matchpage)).setContent(R.id.tabMatches));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator(getString(R.string.teampage)).setContent(R.id.tabteams));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test4").setIndicator(getString(R.string.leaguepage)).setContent(R.id.tableague));
		
		mTabHost.setCurrentTab(0);

		InitLeagues(appstate.getmCurrentLeaguename(), appstate.getmCurrentSeason());
		

		mSpinnerLeague = (Spinner) findViewById(R.id.leagueSpinner);
		mSpinnerSeason = (Spinner) findViewById(R.id.seasonSpinner);
		
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.TabActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		appstate.Save(savedData);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mnu_main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.mnuFile:
			break;
			
		case R.id.mnuNewLeague:
			NewLeague();
			break;
			
		
		case R.id.mnuUpdate:
			break;
			
		case R.id.mnuSelectLeagues:
			SelectLeaguesForDownload();
			break;
			
		case R.id.mnuDownloadSelected:
			DownloadSelectedLeagues();
			break;
		
		default:
			Toast.makeText(this, "This has not been implemented yet",Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private void InitLeagues() {
		InitLeagues(null, null);
	}
	
	private void InitLeagues(String leagueName, String season) {
		//Search for league files
		LookForLeagues();
		
		//If no leagues found -> display empty screen
		if (mLeagues.size() == 0) {
			SetUIMode(UIMode.NoLeagues);
		} else {
			SetUIMode(UIMode.Normal);
			FillLeagueSpinner();
			//No league specified -> show first league
			if (leagueName == null) {
				mSpinnerLeague.setSelection(0);
			} else {
				//set spinners to correct values
				mSpinnerLeague.setSelection(LeagueNames.indexOf(leagueName));
				mSpinnerSeason.setSelection(SeasonNames.indexOf(season));
			}
		}
	}


	private void SetUIMode(UIMode mode) {
		// TODO Auto-generated method stub
		
		if (mode == UIMode.Normal) {
			// Create the normal mode
			mSpinnerLeague.setEnabled(true);
			mSpinnerSeason.setEnabled(true);
			
		} else {
			//create the no league screen
			mSpinnerLeague.setEnabled(false);
			mSpinnerSeason.setEnabled(false);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.noleaguesfound);
			builder.setPositiveButton(R.string.menuleaguenew, newLeagueListener);
			builder.setNeutralButton(R.string.menuliveupdateselect, selectLeagueListener);
			builder.setNegativeButton(R.string.menuliveupdateupdateselected, updateSelectedListener);
			AlertDialog noLeagueDialog = builder.create();
			noLeagueDialog.show();
		}	
	}
	
	DialogInterface.OnClickListener newLeagueListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			NewLeague();
		}
	};
	
	DialogInterface.OnClickListener selectLeagueListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			SelectLeaguesForDownload();
		}
	};
	
	DialogInterface.OnClickListener updateSelectedListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			DownloadSelectedLeagues();
		}
	};
	
	
	private void NewLeague() {
		// CALL TO NEW LEAGUE Activity
		
	}
	
	private void DownloadSelectedLeagues() {
		if (appstate.getmDownloadLeagueList().size() > 0) {
			showDialog(PROGRESS_DIALOG);	
		} else {
			Toast.makeText(this, R.string.noleagues,Toast.LENGTH_LONG).show();
		}
	}
	
	
	static final int PROGRESS_DIALOG = 0;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(DefaultActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage(getResources().getString(R.string.downloading));
			return progressDialog;
		default:
			return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id) {
		case PROGRESS_DIALOG:
			progressDialog.setProgress(0);
			progressThread = new ProgressThread(handler);
			progressThread.start();
		}
	}
	
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.arg1;
			progressDialog.setProgress(total);
			if (total >= 100) {
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);
			}
		}
	};
	

	// Scans the application file directory and loads all league files it finds.
	private void LookForLeagues() {
		// TODO Auto-generated method stub
		mLeagues = new HashMap<String,HashMap<String, String>>();
		
		String[] fileNames = fileList();
		for (String fileName : fileNames) {
			if (fileName.contains(".ts")) {
				AddLeague(fileName);
			}
		}
	}
	
	private void AddLeague(String fileName) {
		LeagueBinaryFile leaguefile;
		League league;
		leaguefile = new LeagueBinaryFile(fileName);
		league = new League(leaguefile);
		
		HashMap<String,String> seasons = mLeagues.get(league.getmName());
		if (seasons == null) {
			seasons = new HashMap<String,String> ();
			seasons.put(league.Season(), fileName);
			mLeagues.put(league.getmName(), seasons);
		}
		
		if (!seasons.containsKey(league.Season())) {
			seasons.put(league.Season(), fileName);
			mLeagues.put(league.getmName(), seasons);
		}
		
	}
	
	private void DeleteCurrentLeague() {
		League league = appstate.getCurrentLeague();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.deleteleague);
		builder.setMessage(getResources().getString(R.string.reallydelete) + " " + 
				league.FullName() + "?");
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteFile(appstate.getmCurrentFilename());
				InitLeagues();	
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		AlertDialog deleteDialog = builder.create();
		deleteDialog.show();
	}
	
	
	private void FillLeagueSpinner() {
		
		String[] leagueNames = mLeagues.keySet().toArray(new String[0]);
		LeagueNames.clear();
		for (String name : leagueNames) {
			LeagueNames.add(name);
		}
		
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,LeagueNames);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerLeague.setAdapter(adapter1);
		mSpinnerLeague.setOnItemSelectedListener(LeagueSpinnerListener);
		
	}
	
	private AdapterView.OnItemSelectedListener LeagueSpinnerListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			FillSeasonSpinner();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			mSpinnerSeason.setAdapter(null);
		}
		
	};
	
	private void FillSeasonSpinner() {
		String leagueName = mSpinnerLeague.getSelectedItem().toString();
		
		String[] Seasons = mLeagues.get(leagueName).keySet().toArray(new String[0]);
		Arrays.sort(Seasons);
		SeasonNames.clear();
		for (String name : Seasons) {
			SeasonNames.add(name);
		}

	
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,SeasonNames);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerSeason.setAdapter(adapter1);
		mSpinnerSeason.setOnItemSelectedListener(SeasonSpinnerListener);
	}
	
	private AdapterView.OnItemSelectedListener SeasonSpinnerListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			ShowLeague();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	protected void ShowLeague() {
		// TODO Auto-generated method stub
		appstate.setmCurrentLeaguename(mSpinnerLeague.getSelectedItem().toString());
		appstate.setmCurrentSeason(mSpinnerSeason.getSelectedItem().toString());
		String mCurrentFilename = mLeagues.get(appstate.getmCurrentLeaguename()).get(appstate.getmCurrentSeason());
		
		appstate.setmCurrentFilename(mCurrentFilename);
		
		
	}
	
	
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		int total;
		
		ProgressThread(Handler h) {
			mHandler = h;
		}
		
		public void run() {
			mState = STATE_RUNNING;
			total = 0;
			// CODE FOR DOWNLOAD FILES USE TOTAL and sendMessage to update the progress bar.
			try {
				
			} catch (Exception e) {
				
			}
			Message msg = mHandler.obtainMessage();
			msg.arg1 = total;
			mHandler.sendMessage(msg);
			total++;
			
		}
		
		public void setState(int state) {
			mState = state;
		}
		
	}
}
