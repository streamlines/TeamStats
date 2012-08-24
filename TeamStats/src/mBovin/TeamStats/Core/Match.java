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

import java.util.Date;

import android.text.format.DateFormat;

public class Match implements Comparable<Object>{
	private League mleague;
	
	private int mIndex;
	private int mHomeTeamId;
	private int mAwayTeamId;
	private Integer mHomeGoals;
	private Integer mAwayGoals;
	private int mRound;
	private Date mDate;
	
	public Match(League league, int index) {
		mleague = league;
		mIndex = index;
	}
	
	public Match(League league, int round, Date date, int homeTeamId, int awayTeamId) {
		mleague = league;
		mRound = round;
		mDate = date;
		mHomeTeamId = homeTeamId;
		mAwayTeamId = awayTeamId;
		mHomeGoals = -1;
		mAwayGoals = -1;
		mIndex = -1;
	}

	public int getmIndex() {
		return mIndex;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public int getmHomeTeamId() {
		return mHomeTeamId;
	}

	public void setmHomeTeamId(int mHomeTeamId) {
		this.mHomeTeamId = mHomeTeamId;
	}

	public int getmAwayTeamId() {
		return mAwayTeamId;
	}

	public void setmAwayTeamId(int mAwayTeamId) {
		this.mAwayTeamId = mAwayTeamId;
	}

	public int getmHomeGoals() {
		return mHomeGoals;
	}

	public void setmHomeGoals(int mHomeGoals) {
		this.mHomeGoals = mHomeGoals;
	}

	public int getmAwayGoals() {
		return mAwayGoals;
	}

	public void setmAwayGoals(int mAwayGoals) {
		this.mAwayGoals = mAwayGoals;
	}

	public int getmRound() {
		return mRound;
	}

	public void setmRound(int mRound) {
		this.mRound = mRound;
	}

	public Date getmDate() {
		return mDate;
	}

	public void setmDate(Date mDate) {
		this.mDate = mDate;
	}
	
	public String HomeTeamName() {
		return mleague.GetTeam(mHomeTeamId).getmName();
	}
	
	public String AwayTeamName() {
		return mleague.GetTeam(mAwayTeamId).getmName();
	}
	
	public String HomeGoalsString() {
		return (this.IsPlayed()) ? mHomeGoals.toString() : "";
	}
	
	public String AwayGoalsString() {
		return (this.IsPlayed()) ? mAwayGoals.toString() : "";
	}
	
	public boolean IsPlayed() {
		return mHomeGoals > -1;
	}
	
	
	/// Returns the opponent of the specified team. If the team didn't
    /// play this match null is returned.
    public Team GetOpponent(Team team) {
		if (team.getmIndex() == mHomeTeamId) {
			return mleague.GetTeam(mAwayTeamId);
		} else {
			if (team.getmIndex() == mAwayTeamId) {
				return mleague.GetTeam(mHomeTeamId);
			} else {
				return null;
			}
		}
	}
	
    /// Returns the number of goals scored by the specified team. 
    /// Returns -1 if the team didn't play this match.
    public int GetGoalsFor(Team team) {
		if (team.getmIndex() == mHomeTeamId) {
			return mHomeGoals;
		} else {
			if (team.getmIndex() == mAwayTeamId) {
				return mAwayGoals;
			} else {
				return -1;
			}
		}
    }
 
    /// Returns the number of goals conceded by the specified team. 
    /// Returns -1 if the team didn't play this match.
    public int GetGoalsAgainst(Team team) {
		if (team.getmIndex() == mHomeTeamId) {
			return mAwayGoals;
		} else {
			if (team.getmIndex() == mAwayTeamId) {
				return mHomeGoals;
			} else {
				return -1;
			}
		}
    }

    /// Sets the result of this match.
    public void SetResult(int homegoals, int awaygoals) {
    	if ((homegoals < 0 ) || (awaygoals < 0)) {
    		SetNotPlayed();
    	} else {
    		mHomeGoals = homegoals;
    		mAwayGoals = awaygoals;
    	}
    }
    
    /// Sets this match to "not yet played"
    public void SetNotPlayed(){
    	mHomeGoals = -1;
    	mAwayGoals = -1;
    }
    
    /// Saves the match data
    public void Save() {
    	mleague.getmLeagueStore().SaveMatch(mleague, this);
    }

	@Override
	public String toString() {
		return DateFormat.format("dd/MMM", mDate) + " " + HomeTeamName()
				+ "-" + AwayTeamName() + " "
				+ HomeGoalsString() + "-"
				+ AwayGoalsString();
	}

	@Override
	public int compareTo(Object another) {
		Match otherMatch = (Match)another;
		
		return (this.mDate.compareTo(otherMatch.mDate));
		
	}

    
}
