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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import mBovin.TeamStats.Core.*;
import mBovin.TeamStats.LiveUpdate.LeagueDownloader;
import mBovin.TeamStats.LiveUpdate.LeagueDownloader.LeagueDownloadListener;
import mBovin.TeamStats.Utils.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import mBovin.TeamStats.R;

@SuppressWarnings("deprecation")
public class DefaultActivity extends TabActivity {
	
	private TabHost mTabHost;
	private Spinner mSpinnerLeague;
	private Spinner mSpinnerSeason;
	private TableLayout mTableTableLayout;
	private TableLayout mMatchTableLayout;
	private TextView mRoundTextView;
	private Button mNextRoundButton;
	private Button mPreviousRoundButton;
	
	private Map<String, HashMap<String, String>> mLeagues;
	private AppState appstate = new AppState();
	private ArrayList<String> LeagueNames = new ArrayList<String>();
	private ArrayList<String> SeasonNames= new ArrayList<String>();
	private SharedPreferences savedData;
	private Integer currentround;
	private boolean mEditMode;
	
	private UIMode mode;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		savedData = getSharedPreferences("TeamStats", MODE_PRIVATE);
		appstate.Load(savedData);
		
		mLeagues = new HashMap<String, HashMap<String, String>>();
		currentround = 1;
		
		mTabHost = getTabHost();
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getString(R.string.tablepage)).setContent(R.id.tabTable));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getString(R.string.matchpage)).setContent(R.id.tabMatches));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator(getString(R.string.teampage)).setContent(R.id.tabteams));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test4").setIndicator(getString(R.string.leaguepage)).setContent(R.id.tableague));
		
		mTabHost.setCurrentTab(0);
		
		mTableTableLayout = (TableLayout) findViewById(R.id.tableTableLayout);
		mMatchTableLayout = (TableLayout) findViewById(R.id.roundTableLayout);
		
		mRoundTextView = (TextView) findViewById(R.id.RoundTextView);
		mNextRoundButton = (Button) findViewById(R.id.buttonNextRound);
		mPreviousRoundButton = (Button) findViewById(R.id.buttonPreviousRound);
		ToggleButton editButton = (ToggleButton) findViewById(R.id.toggleButtonEditResults);
		editButton.setOnCheckedChangeListener(editMatchesToggleButtonListener);
		mEditMode = false;
		
		mNextRoundButton.setOnClickListener(NextRoundButtonClicked);
		mPreviousRoundButton.setOnClickListener(PreviousRoundButtonClicked);
		
		mSpinnerLeague = (Spinner) findViewById(R.id.leagueSpinner);
		mSpinnerSeason = (Spinner) findViewById(R.id.seasonSpinner);

		InitLeagues(appstate.getmCurrentLeaguename(), appstate.getmCurrentSeason());
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
			
		case R.id.mnuDeleteLeague:
			DeleteCurrentLeague();
			break;
			
		
		case R.id.mnuUpdate:
			break;
			
		case R.id.mnuSelectLeagues:
			SelectLeaguesForDownload();
			break;
			
		case R.id.mnuDownloadSelected:
			DownloadSelectedLeagues();
			break;
			
		case R.id.mnuDowloadThisLeague:
			DownloadThisLeague();
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
	
	protected void SelectLeaguesForDownload() {
		// TODO Auto-generated method stub - Go to Live Update Select Form.
		
	}

	private void DownloadSelectedLeagues() {
		if (appstate.getmDownloadLeagueList().size() > 0) {
			// Download each league in order.
			
			LookForLeagues();
		} else {
			Toast.makeText(this, R.string.noleagues,Toast.LENGTH_LONG).show();
		}
	}
	
	private void DownloadThisLeague() {
		LeagueDownloader download = new LeagueDownloader(appstate.getCurrentLeague(), DefaultActivity.this, SingleLeaguedownload );
		download.execute();
	}
	
	LeagueDownloadListener SingleLeaguedownload = new LeagueDownloadListener() {

		@Override
		public void onLeagueDownloaded(String league) {
			if (league != null) {
				ShowLeague();
			} else {
				Toast errorToast = Toast.makeText(getBaseContext(), getString(mBovin.TeamStats.R.string.noconnection2), Toast.LENGTH_LONG);
				errorToast.setGravity(Gravity.CENTER, 0, 0);
				errorToast.show();
			}
		}
		
	};
	
	
	// Scans the application file directory and loads all league files it finds.
	private void LookForLeagues() {
		mLeagues = new HashMap<String,HashMap<String, String>>();
		File path = getFilesDir();
		
		String[] fileNames = fileList();
		for (String fileName : fileNames) {
			if (fileName.contains(".ts")) {
				AddLeague(path + File.separator + fileName);
			}
		}
		
		if (fileNames.length == 0) {
			try {
				
				AssetManager assets = getAssets();
				fileNames = assets.list("leagues");
				for (String fileName : fileNames) {
					if (fileName.contains(".ts")) {
						File output = new File(fileName);
						InputStream iStream = assets.open("leagues/" + fileName);
						OutputStream oStream = new FileOutputStream(path + File.separator + output);
						utils.CopyFile(iStream, oStream);
						AddLeague(path + File.separator + output);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
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
		currentround = appstate.getCurrentLeague().GetLastPlayedRound();
		makeTable();
		makeMatches();	
		makeLeagueStats();
	}
	
	private void makeLeagueStats() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		League league = appstate.getCurrentLeague();
		LeagueStats stats = new LeagueStats(league);
		TableLayout leaguestats = (TableLayout) inflater.inflate(R.layout.league_stats, null);
		TextView matchesPlayed = (TextView) leaguestats.findViewById(R.id.matchesPlayedTextView);
		matchesPlayed.setText(stats.Played().toString());
		TextView homeWins = (TextView) leaguestats.findViewById(R.id.homeWinsTextView);
		homeWins.setText(stats.HomeWins().toString());
		TextView homeWinPercentage = (TextView) leaguestats.findViewById(R.id.homeWinsPercTextView);
		homeWinPercentage.setText(((CharSequence) new DecimalFormat("#.##").format(stats.HomeWinPercent() * 100)) + "%");
		TextView draws = (TextView) leaguestats.findViewById(R.id.drawsTextView);
		draws.setText(stats.Draws().toString());
		TextView drawsPercentage = (TextView) leaguestats.findViewById(R.id.drawsPercTextView);
		drawsPercentage.setText(((CharSequence) new DecimalFormat("#.##").format(stats.DrawPercent() * 100)) + "%");
		TextView awayWins = (TextView) leaguestats.findViewById(R.id.awayWinsTextView);
		awayWins.setText(stats.AwayWins().toString());
		TextView awayWinsPercentage = (TextView) leaguestats.findViewById(R.id.awayWinsPercTextView);
		awayWinsPercentage.setText(((CharSequence) new DecimalFormat("#.##").format(stats.AwayWinPercent() * 100)) + "%");
		TextView goalsScored = (TextView) leaguestats.findViewById(R.id.goalsTextView);
		goalsScored.setText(stats.Goals().toString());
		TextView goalsAverage = (TextView) leaguestats.findViewById(R.id.goalsPercTextView);
		goalsAverage.setText((CharSequence) new DecimalFormat("#.##").format(stats.GoalAvg()));
		TextView goalsHome = (TextView) leaguestats.findViewById(R.id.homeGoalsTextView);
		goalsHome.setText(stats.HomeGoals().toString());
		TextView goalsHomeAvg = (TextView) leaguestats.findViewById(R.id.homeGoalsPercTextView);
		goalsHomeAvg.setText((CharSequence) new DecimalFormat("#.##").format(stats.HomeGoalAvg()));
		TextView goalsAway = (TextView) leaguestats.findViewById(R.id.awayGoalsTextView);
		goalsAway.setText(stats.AwayGoals().toString());
		TextView goalsAwayAvg = (TextView) leaguestats.findViewById(R.id.awayGoalsPercTextView);
		goalsAwayAvg.setText((CharSequence) new DecimalFormat("#.##").format(stats.AwayGoalAvg()));
		
		TextView FrequentResult1 = (TextView) leaguestats.findViewById(R.id.FrequentScore1TextView);
		FrequentResult1.setText(stats.GetFrequentResult(0).toString());
		TextView Frequent1 = (TextView) leaguestats.findViewById(R.id.Frequency1TextView);
		Frequent1.setText(stats.GetFrequentResult(0).getmCount().toString() + " " + getResources().getString(R.string.times));
		
		TextView FrequentResult2 = (TextView) leaguestats.findViewById(R.id.FrequentScore2TextView);
		FrequentResult2.setText(stats.GetFrequentResult(1).toString());
		TextView Frequent2 = (TextView) leaguestats.findViewById(R.id.Frequency2TextView);
		Frequent2.setText(stats.GetFrequentResult(1).getmCount().toString() + " " + getResources().getString(R.string.times));

		TextView FrequentResult3 = (TextView) leaguestats.findViewById(R.id.FrequentScore3TextView);
		FrequentResult3.setText(stats.GetFrequentResult(2).toString());
		TextView Frequent3 = (TextView) leaguestats.findViewById(R.id.Frequency3TextView);
		Frequent3.setText(stats.GetFrequentResult(2).getmCount().toString() + " " + getResources().getString(R.string.times));

		TextView FrequentResult4 = (TextView) leaguestats.findViewById(R.id.FrequentScore4TextView);
		FrequentResult4.setText(stats.GetFrequentResult(3).toString());
		TextView Frequent4 = (TextView) leaguestats.findViewById(R.id.Frequency4TextView);
		Frequent4.setText(stats.GetFrequentResult(3).getmCount().toString() + " " + getResources().getString(R.string.times));

		TextView FrequentResult5 = (TextView) leaguestats.findViewById(R.id.FrequentScore5TextView);
		FrequentResult5.setText(stats.GetFrequentResult(4).toString());
		TextView Frequent5 = (TextView) leaguestats.findViewById(R.id.Frequency5TextView);
		Frequent5.setText(stats.GetFrequentResult(4).getmCount().toString() + " " + getResources().getString(R.string.times));
		
		LinearLayout tabLeague = (LinearLayout) findViewById(R.id.tableague);
		tabLeague.removeAllViews();
		tabLeague.addView(leaguestats);
	}

	private void makeMatches() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		League league = appstate.getCurrentLeague();;
		ArrayList<Match> matchlist = league.GetMatchesForRound(currentround);
		mRoundTextView.setText(getResources().getString(R.string.round) + " " + currentround.toString());
		mMatchTableLayout.removeAllViews();
		for (int i = 0; i < matchlist.size(); i++ ) {
			Match match = matchlist.get(i);
			if (!mEditMode) {
				View matchline = inflater.inflate(R.layout.match_row, null);
				TextView dateTextView = (TextView) matchline.findViewById(R.id.dateTextView);
				TextView homeTeamTextView = (TextView) matchline.findViewById(R.id.homeTeamTextView);
				TextView awayTeamTextView = (TextView) matchline.findViewById(R.id.awayTeamTextView);
				TextView homeScoreTextView = (TextView) matchline.findViewById(R.id.homeScoreTextView);
				TextView awayScoreTextView = (TextView) matchline.findViewById(R.id.awayScoreTextView);

				dateTextView.setText(DateFormat.format("dd/MMM", match.getmDate()));
				homeTeamTextView.setText(league.GetTeam(match.getmHomeTeamId()).getmName());
				awayTeamTextView.setText(league.GetTeam(match.getmAwayTeamId()).getmName());
				if (match.IsPlayed()) {
					homeScoreTextView.setText(match.getmHomeGoals().toString());
					awayScoreTextView.setText(match.getmAwayGoals().toString());
				} else {
					homeScoreTextView.setText(" ");
					awayScoreTextView.setText(" ");
				}
				mMatchTableLayout.addView(matchline, i);
			} else {
				View matchline = inflater.inflate(R.layout.edit_match_row, null);
				EditText dateEditText = (EditText) matchline.findViewById(R.id.dateEditText);
				TextView homeTeamTextView = (TextView) matchline.findViewById(R.id.homeTeamTextView);
				TextView awayTeamTextView = (TextView) matchline.findViewById(R.id.awayTeamTextView);
				EditText homeScoreEditText = (EditText) matchline.findViewById(R.id.homeScoreEditText);
				EditText awayScoreEditText = (EditText) matchline.findViewById(R.id.awayScoreEditText);

				dateEditText.setText(DateFormat.format("dd/MMM", match.getmDate()));
				homeTeamTextView.setText(league.GetTeam(match.getmHomeTeamId()).getmName());
				awayTeamTextView.setText(league.GetTeam(match.getmAwayTeamId()).getmName());
				if (match.IsPlayed()) {
					homeScoreEditText.setText(match.getmHomeGoals().toString());
					awayScoreEditText.setText(match.getmAwayGoals().toString());
				} else {
					homeScoreEditText.setText(" ");
					awayScoreEditText.setText(" ");
				}
				mMatchTableLayout.addView(matchline, i);

			}

		}
		
	}
	
	private void makeTable() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		League league = appstate.getCurrentLeague();;
		// Add additional functionality
		Table table = new Table(league, true, true, true, -1);
		mTableTableLayout.removeAllViews();
		View row = inflater.inflate(R.layout.new_table_row, null);
		mTableTableLayout.addView(row, 0);
		
		for (Integer i = 1; i<=league.getmTeamCount(); i++) {
			TableTeam Teamdata = table.TeamList().get(i-1);
			View teamline = inflater.inflate(R.layout.new_table_row, null);
			
			TextView positionTextView = (TextView) teamline.findViewById(R.id.positionTextView);
			TextView teamNameTextView = (TextView) teamline.findViewById(R.id.teamnameTextView);
			TextView playedTextView = (TextView) teamline.findViewById(R.id.playedTextView);
			TextView wonTextView = (TextView) teamline.findViewById(R.id.wonTextView);
			TextView drawnTextView = (TextView) teamline.findViewById(R.id.drawTextView);
			TextView lossTextView = (TextView) teamline.findViewById(R.id.lossTextView);
			TextView forTextView = (TextView) teamline.findViewById(R.id.forTextView);
			TextView againstTextView = (TextView) teamline.findViewById(R.id.againstTextView);
			TextView goalDiffTextView = (TextView) teamline.findViewById(R.id.goalDiffTextView);
			TextView pointsTextView = (TextView) teamline.findViewById(R.id.pointsTextView);
			
			positionTextView.setText(i.toString());
			teamNameTextView.setText(Teamdata.getName());
			playedTextView.setText(Teamdata.getmPlayed().toString());
			wonTextView.setText(Teamdata.getmWon().toString());
			drawnTextView.setText(Teamdata.getmDrawn().toString());
			lossTextView.setText(Teamdata.getmLost().toString());
			forTextView.setText(Teamdata.getmFor().toString());
			againstTextView.setText(Teamdata.getmAgainst().toString());
			goalDiffTextView.setText(Teamdata.getmGoalDiff().toString());
			pointsTextView.setText(Teamdata.getmPoints().toString());
			
			mTableTableLayout.addView(teamline, i);
		}
		
	}
	
	
	
	private View.OnClickListener NextRoundButtonClicked = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			League league = appstate.getCurrentLeague();
			if (currentround < league.getmRoundCount()) {
				currentround++;
				makeMatches();
			}
		}
	};
	
	private View.OnClickListener PreviousRoundButtonClicked = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (currentround > 1) {
				currentround--;
				makeMatches();
			}
			
		}
	};
	
	private OnCheckedChangeListener editMatchesToggleButtonListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (!isChecked) {
				// Save off any changes
				saveMatches();
				mEditMode = false;
				makeMatches();
			} else {
				// Go into edit Mode.
				mEditMode = true;
				makeMatches();
			}
			
		}
		
	};

	protected void saveMatches() {
		// TODO Code to save off the matches.
		
	}
}
