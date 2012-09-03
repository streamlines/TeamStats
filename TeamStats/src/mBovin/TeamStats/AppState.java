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

import mBovin.TeamStats.Core.League;
import mBovin.TeamStats.Core.LeagueBinaryFile;

import android.content.SharedPreferences;

public class AppState {

	private String 		mCurrentFilename;
	private String		mCurrentLeaguename;
	private String		mCurrentSeason;
	private int 		mleagueNameCount;
	private String[]	mLeagueNames 		= new String[0];
	private int			mdownloadLeagueCount;
	private ArrayList<Integer>		mDownloadLeagueList = new ArrayList<Integer>();
	private String		mLiveUpdateLog;
	
	private LeagueViewMode mMode = LeagueViewMode.Table;
	
	public AppState() {}

	public League getCurrentLeague() {
		return new League(new LeagueBinaryFile(mCurrentFilename));
	}
	
	
	/**
	 * @return the mCurrentFilename
	 */
	public String getmCurrentFilename() {
		return mCurrentFilename;
	}

	/**
	 * @param mCurrentFilename the mCurrentFilename to set
	 */
	public void setmCurrentFilename(String mCurrentFilename) {
		this.mCurrentFilename = mCurrentFilename;
	}

	/**
	 * @return the mCurrentLeaguename
	 */
	public String getmCurrentLeaguename() {
		return mCurrentLeaguename;
	}

	/**
	 * @param mCurrentLeaguename the mCurrentLeaguename to set
	 */
	public void setmCurrentLeaguename(String mCurrentLeaguename) {
		this.mCurrentLeaguename = mCurrentLeaguename;
	}

	/**
	 * @return the mCurrentSeason
	 */
	public String getmCurrentSeason() {
		return mCurrentSeason;
	}

	/**
	 * @param mCurrentSeason the mCurrentSeason to set
	 */
	public void setmCurrentSeason(String mCurrentSeason) {
		this.mCurrentSeason = mCurrentSeason;
	}

	/**
	 * @return the mLeagueNames
	 */
	public String[] getmLeagueNames() {
		return mLeagueNames;
	}

	/**
	 * @param mLeagueNames the mLeagueNames to set
	 */
	public void setmLeagueNames(String[] mLeagueNames) {
		this.mLeagueNames = mLeagueNames;
	}

	/**
	 * @return the mLiveUpdateLog
	 */
	public String getmLiveUpdateLog() {
		return mLiveUpdateLog;
	}

	/**
	 * @param mLiveUpdateLog the mLiveUpdateLog to set
	 */
	public void setmLiveUpdateLog(String mLiveUpdateLog) {
		this.mLiveUpdateLog = mLiveUpdateLog;
	}

	/**
	 * @return the mMode
	 */
	public LeagueViewMode getmMode() {
		return mMode;
	}

	/**
	 * @param mMode the mMode to set
	 */
	public void setmMode(LeagueViewMode mMode) {
		this.mMode = mMode;
	}

	/**
	 * @return the mDownloadLeagueList
	 */
	public ArrayList<Integer> getmDownloadLeagueList() {
		return mDownloadLeagueList;
	}

	public void Load(SharedPreferences savedData) {
		mCurrentLeaguename = savedData.getString("CURRENTLEAGUENAME", null);
		mCurrentSeason = savedData.getString("CURRENTSEASON", null);
		mleagueNameCount = savedData.getInt("LEAGUECOUNT", 0);
		mLeagueNames = new String[mleagueNameCount];
		for (int i = 0; i < mleagueNameCount; i++) {
			mLeagueNames[i] = savedData.getString("LEAGUENAME" + i, null);
		}
		mdownloadLeagueCount = savedData.getInt("DOWNLOADCOUNT", 0);
		mDownloadLeagueList.clear();
		for (int i = 0; i < mdownloadLeagueCount; i++) {
			mDownloadLeagueList.add(savedData.getInt("Download" + i, 0));
		}	
	}
	
	public void Save(SharedPreferences savedData) {
		SharedPreferences.Editor preferenceEditor = savedData.edit();
		preferenceEditor.putString("CURRENTLEAGUENAME", mCurrentLeaguename);
		preferenceEditor.putString("CURRENTSEASON", mCurrentSeason);
		preferenceEditor.putInt("LEAGUECOUNT", mleagueNameCount);
		for (int i = 0; i < mleagueNameCount; i++) {
			preferenceEditor.putString("LEAGUENAME" + i, mLeagueNames[i]);
		}
		preferenceEditor.putInt("DOWNLOADCOUNT", mdownloadLeagueCount);
		for (int i = 0; i < mdownloadLeagueCount; i++) {
			preferenceEditor.putInt("Download" + i, mDownloadLeagueList.get(i));
		}	
		preferenceEditor.apply();
	}
	
    public void LiveUpdate_Log(Object sender, String message)
    {
        this.mLiveUpdateLog += message;
    }        

	

}
