package mBovin.TeamStats;

import mBovin.TeamStats.LiveUpdate.LeagueListManager;
import android.app.ListActivity;
import android.os.Bundle;

public class LiveUpdateSelectActivity extends ListActivity {
	
	public static String UPDATE_MODE = "MODE";
	public static int MODE_NEW_LEAGUES = 0;
	public static int MODE_ALL_LEAGUES = 1;
	public static int MODE_ARCHIVE_LEAGUES = 2;
	
	private LeagueListManager leagueManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_league_list);
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			int mode = extras.getInt(UPDATE_MODE);
			
		}
		
	}
	
	

}
