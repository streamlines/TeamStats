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

import java.util.ArrayList;
import java.util.Dictionary;

import mBovin.TeamStats.Core.*;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
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
	
	private Dictionary<String,Dictionary<String,String>> mLeagues;
	private AppState appstate;
	private ArrayList<String> LeagueNames = new ArrayList<String>();
	private ArrayList<String> SeasonNames= new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mTabHost = getTabHost();
		
		mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getString(R.string.tablepage)).setContent(R.id.tabTable));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getString(R.string.matchpage)).setContent(R.id.tabMatches));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator(getString(R.string.teampage)).setContent(R.id.tabteams));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test4").setIndicator(getString(R.string.leaguepage)).setContent(R.id.tableague));
		
		mTabHost.setCurrentTab(0);

		
		//Test data for LeagueNames
//		LeagueNames.add("test 1");
//		LeagueNames.add("Test 2");
//		LeagueNames.add("Test 3");
		
		mSpinnerLeague = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,LeagueNames);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerLeague.setAdapter(adapter1);
		mSpinnerLeague.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//				String leaguename = mSpinnerLeague.getSelectedItem().toString();
//				Toast.makeText(DefaultActivity.this , leaguename, Toast.LENGTH_LONG).show();
			}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		
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
		
		case R.id.mnuUpdate:
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
			FillLeagueCombo();
			//No league specified -> show first league
			if (leagueName == null) {
				mSpinnerLeague.setSelection(0);
			} else {
				//set spinners to correct values
				
			}
			
			
		}
		
	}

	private void FillLeagueCombo() {
		// TODO Auto-generated method stub
		
	}

	private void SetUIMode(UIMode mode) {
		// TODO Auto-generated method stub
		
		if (mode == UIMode.Normal) {
			// Create the normal mode
		} else {
			//create the no league screen
			
		}
		
	}

	// Scans the application file directory and loads all league files it finds.
	private void LookForLeagues() {
		// TODO Auto-generated method stub
		
	}
	
	
}
