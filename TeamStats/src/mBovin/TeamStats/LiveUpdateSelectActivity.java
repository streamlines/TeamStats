package mBovin.TeamStats;

import java.util.ArrayList;
import java.util.List;

import mBovin.TeamStats.LiveUpdate.LeagueByCountry;
import mBovin.TeamStats.LiveUpdate.LeagueListDatabaseConnector;
import mBovin.TeamStats.LiveUpdate.LeagueListManager;
import mBovin.TeamStats.LiveUpdate.LeagueListManager.newLeagueListener;
import mBovin.TeamStats.UI.LeagueListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class LiveUpdateSelectActivity extends Activity {
	
	public static final String UPDATE_MODE = "MODE";
	public static final int MODE_NEW_LEAGUES = 0;
	public static final int MODE_ALL_LEAGUES = 1;
	public static final int MODE_ARCHIVE_LEAGUES = 2;
	private int mode;
	
	private LeagueListManager leagueManager;
	private LeagueListDatabaseConnector dbConnector;
	private LeagueListAdapter listAdapter;
	
	private ExpandableListView mExpandList;
	private AlertDialog DownloadDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_league_list);
		Bundle extras = getIntent().getExtras();
		mode = 1;
		if (extras != null) {
			mode = extras.getInt(UPDATE_MODE);
		}
		dbConnector = new LeagueListDatabaseConnector(this);
		leagueManager = new LeagueListManager(dbConnector, leagueListener, LiveUpdateSelectActivity.this);
		mExpandList = (ExpandableListView) findViewById(R.id.leagueExpandListView);
		Button doneButton = (Button) findViewById(R.id.returnButton);
		doneButton.setOnClickListener(doneButtonListener);
		
		startDialog();
		getLeagueList();
	}
	
	private void startDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.dialog_waiting, null);
		TextView dialogTextView = (TextView) dialogView.findViewById(R.id.MessageTextView);
		dialogTextView.setText(R.string.downloadinglist);
		AlertDialog.Builder DownloadDialogBuilder = new AlertDialog.Builder(this);
		DownloadDialogBuilder.setView(dialogView);
		
		DownloadDialog = DownloadDialogBuilder.create();
		DownloadDialog.show();
	}
	
	private void getLeagueList() {
		switch (mode) {
		case MODE_NEW_LEAGUES:
			leagueManager.execute(LeagueListManager.NEW_LEAGUES);
			break;
		case MODE_ALL_LEAGUES:
			leagueManager.execute(LeagueListManager.ALL_LEAGUES);
			break;
		}
	}
	
	private newLeagueListener leagueListener = new newLeagueListener() {

		@Override
		public void onLeagueListUpdated(List<LeagueByCountry> newleagues) {
			ArrayList<LeagueByCountry> leagueList = (ArrayList<LeagueByCountry>) newleagues;
			listAdapter = new LeagueListAdapter(LiveUpdateSelectActivity.this, leagueList, checkBoxListener);
			mExpandList.setAdapter(listAdapter);
			DownloadDialog.dismiss();
		}
	};
	
	private OnClickListener checkBoxListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dbConnector.selectLeague((int) Integer.parseInt(v.getTag().toString()), !v.isSelected()); 
			
		}
	};
	
	private OnClickListener doneButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
		
	};

}
