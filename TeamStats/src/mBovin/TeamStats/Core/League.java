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
package mBovin.TeamStats.Core;

import java.util.ArrayList;
import java.util.Collections;

public class League {
	private ILeagueStore mLeagueStore;
	
	private String	mName;
	private Integer		mYear;
	private boolean	mIsSplit;
	private int		mTeamCount;
	private int 	mRoundCount;
	private int		mMatchCount;
	private int		mPlayedMatchCount;
	private int		mWinPoints;
	private int		mDrawPoints;
	private int		mLossPoints;
	private long	mTableLines;
	private TableSortMethod mTableSort;
	
	private ArrayList<Team> mTeamList;
	
	public League() {
		mRoundCount = 1;
	}
	
	public League(ILeagueStore leagueStore) {
		mLeagueStore = leagueStore;
		mLeagueStore.LoadHeader(this);
	}

	public ILeagueStore getmLeagueStore() {
		return mLeagueStore;
	}

	public void setmLeagueStore(ILeagueStore mLeagueStore) {
		this.mLeagueStore = mLeagueStore;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public int getmYear() {
		return mYear;
	}

	public void setmYear(int mYear) {
		this.mYear = mYear;
	}

	public boolean ismIsSplit() {
		return mIsSplit;
	}

	public void setmIsSplit(boolean mIsSplit) {
		this.mIsSplit = mIsSplit;
	}

	public int getmTeamCount() {
		return mTeamCount;
	}

	public void setmTeamCount(int mTeamCount) {
		this.mTeamCount = mTeamCount;
	}

	public int getmRoundCount() {
		return mRoundCount;
	}

	public void setmRoundCount(int mRoundCount) {
		this.mRoundCount = mRoundCount;
	}

	public int getmMatchCount() {
		return mMatchCount;
	}

	public void setmMatchCount(int mMatchCount) {
		this.mMatchCount = mMatchCount;
	}

	public int getmPlayedMatchCount() {
		return mPlayedMatchCount;
	}

	public void setmPlayedMatchCount(int mPlayedMatchCount) {
		this.mPlayedMatchCount = mPlayedMatchCount;
	}

	public int getmWinPoints() {
		return mWinPoints;
	}

	public void setmWinPoints(int mWinPoints) {
		this.mWinPoints = mWinPoints;
	}

	public int getmDrawPoints() {
		return mDrawPoints;
	}

	public void setmDrawPoints(int mDrawPoints) {
		this.mDrawPoints = mDrawPoints;
	}

	public int getmLossPoints() {
		return mLossPoints;
	}

	public void setmLossPoints(int mLossPoints) {
		this.mLossPoints = mLossPoints;
	}

	public long getmTableLines() {
		return mTableLines;
	}

	public void setmTableLines(long l) {
		this.mTableLines = l;
	}

	public TableSortMethod getmTableSort() {
		return mTableSort;
	}

	public void setmTableSort(TableSortMethod mTableSort) {
		this.mTableSort = mTableSort;
	}
	
	public String Season() {
		return (mIsSplit) ?
				mYear.toString() + "-" + (((mYear + 1) % 100)):
				mYear.toString();
	}
	
	public String FullName() {
		return this.mName + " " + this.Season();
	}
	
	public Team GetTeam(int teamIndex) {
		if (mTeamList == null) {
			GetTeams();
		}
		return mTeamList.get(teamIndex);
	}

	public ArrayList<Team> GetTeams() {
		if (mTeamList == null) {
			mTeamList = mLeagueStore.LoadTeams(this);
		}
		return mTeamList;
	}
	
	public ArrayList<Integer> GetTableLines() {
		ArrayList<Integer> linesList = new ArrayList<Integer> (mTeamCount);
		
		for (int i = 0; i < mTeamCount; i++) {
			if (IsTableLine(i + 1)) {
				linesList.add(i + 1);
			}
		}
		
		return linesList;
	}
	
	public boolean IsTableLine(int position) {
		Double base = (double) 2;
		
		return ((long)Math.pow(base, (double)(position - 1)) & mTableLines) > 0;
		
	}
	
	public int GetLastPlayedRound() {
		int		lastPlayedRound = 1;
		Match 	match;
		ArrayList<Match> matchList = mLeagueStore.LoadMatches(this);
		
		for (int i = matchList.size()-1; i >=0; i--) {
			match = matchList.get(i);
			if (match.IsPlayed()) {
				lastPlayedRound = match.getmRound();
				break;
			}
		}
		
		return lastPlayedRound;
	}
	
	public ArrayList<Match> GetMatches(boolean onlyPlayed) {
		return mLeagueStore.LoadMatches(this, new MatchFilterPlayed(), onlyPlayed);
	}
	
	public ArrayList<Match> GetMatchesForRound(int round) {
		return mLeagueStore.LoadMatches(this, new MatchFilterRound(), round);
	}
	
	public ArrayList<Match> GetMatchesforTeam(int teamIndex, boolean includeHome, boolean includeAway) {
		return mLeagueStore.LoadMatches(this, new MatchFilterTeam(), teamIndex, includeHome, includeAway);
	}
	
    /// Creates the specified number of teams. The teams are 
    /// added to mTableTeamList and saved.
	public void AddTeams(int teamCount) {
		mTeamCount = teamCount;
		Team[] teamList = {};
		for (int i = 0; i < mTeamCount; i++) {
			teamList[i] = new Team(this, i, "Team" + (i + 1));
		}
		
		mLeagueStore.SaveHeader(this);
		mLeagueStore.SaveTeams(this, teamList);
	}
	
	/// Adds a match to the league.
	public void AddMatch(Match match) {
		ArrayList<Match> matchlist = mLeagueStore.LoadMatches(this);
		
		matchlist.add(match);
		mMatchCount++;
		if (match.IsPlayed()) {
			mPlayedMatchCount++;
		}
		
		Save();
		
		Collections.sort(matchlist);
		mLeagueStore.SaveMatches(this, matchlist);
	}
	
    /// Loads all the matches of the league, sorts them (on the matchdate)
    /// and then saves them again. 
    /// Use this method when match dates have changed.
	public void SortMatches() {
		ArrayList<Match> matchlist = mLeagueStore.LoadMatches(this);
		Collections.sort(matchlist);
		mLeagueStore.SaveMatches(this, matchlist);
	}

    /// Increases the number of rounds for this league.
	public void AddRound() {
		mRoundCount++;
		Save();
	}
	
    /// Decreases the number of rounds for this league. An exception is 
    /// thrown if the last round contains any matches.
	public void DeleteLastRound() throws Exception {
		ArrayList<Match> matches = GetMatchesForRound(mRoundCount);
		if (matches.size() > 0) {
			throw new Exception("Last Round is not empty");
		}
		mRoundCount--;
		Save();
	}
	
	
	
	
	public void Save() {
		mLeagueStore.SaveHeader(this);
	}
	
	
	
	
	
	
}
