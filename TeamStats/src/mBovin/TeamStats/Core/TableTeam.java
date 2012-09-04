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

public class TableTeam implements Comparable<Object> {

	private Team mTeam;
	private int mPosition;
	private int mPlayed;
	private int mWon;
	private int mDrawn;
	private int mLost;
	private int mFor;
	private int mAgainst;
	private int mGoalDiff;
	private int mPoints;
	
	public TableTeam(Team team) {
		mTeam = team;
	}
	
	public String getName() {
		return mTeam.getmName();
	}
	
	public Integer getIndex() {
		return mTeam.getmIndex();
	}
	
	public Integer getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public Integer getmPlayed() {
		return mPlayed;
	}

	public Integer getmWon() {
		return mWon;
	}

	public float getWonPercent() {
		return (mPlayed > 0) ?
				((float)mWon/ (float)mPlayed) : 0;
	}

	public Integer getmDrawn() {
		return mDrawn;
	}

	public float getDrawnPercent() {
		return (mPlayed > 0) ?
				((float)mDrawn/ (float)mPlayed) : 0;
	}

	public Integer getmLost() {
		return mLost;
	}

	public float getLostPercent() {
		return (mPlayed > 0) ?
				((float)mLost/ (float)mPlayed) : 0;
	}

	public Integer getmFor() {
		return mFor;
	}

	public float getForAverage() {
		return (mPlayed > 0) ?
				((float)mFor/ (float)mPlayed) : 0;
	}

	public Integer getmAgainst() {
		return mAgainst;
	}

	public float getAgainstAverage() {
		return (mPlayed > 0) ?
				((float)mAgainst/ (float)mPlayed) : 0;
	}

	public Integer getmGoalDiff() {
		return mGoalDiff;
	}

	public Integer getmPoints() {
		return mPoints;
	}

	///Adds a game for this table team.
	public void AddMatch(Match match, int maxMatches) {
		//Exit method if maxMatches is already reached
		if (maxMatches > -1 && mPlayed >= maxMatches) {
			return;
		}
		
		int goalsFor;
		int goalsAgainst;
		if (match.getmHomeTeamId() == mTeam.getmIndex()) {
			goalsFor = match.getmHomeGoals();
			goalsAgainst = match.getmAwayGoals();
		} else {
			goalsFor = match.getmAwayGoals();			
			goalsAgainst = match.getmHomeGoals();
		}
		
		//Add result (won, draw, lost)
		mPlayed++;
		
		if (goalsFor > goalsAgainst) {
			mWon++;
		} else if (goalsAgainst > goalsFor) {
			mLost++;
		} else {
			mDrawn++;
		}
		
		//Add Goals
		mFor 	+= goalsFor;
		mAgainst += goalsAgainst;
	}
	
	//Sums up the points and goal difference for a team.
	public void SumUp(int winPoints, int drawnPoints, int lossPoints, boolean includeBonusPoints) {
		mPoints = 	(mWon	* winPoints) +
					(mDrawn	* drawnPoints) +
					(mLost	* lossPoints);
		
		if (includeBonusPoints) {
			mPoints += mTeam.getmBonusPoints();
		}
		
		mGoalDiff = mFor - mAgainst;
	}
	
	@Override
	public int compareTo(Object another) {
		TableTeam enemy = (TableTeam) another;
		int n = 0;
		int returnValue;
		
		while ((returnValue = CompareValue(enemy, n++)) == 0);
		
		return returnValue;
	}
	
    /// Compares the n:th value of this team with another team's value.
    /// 1 if the other team is better, 0 if equal and -1 
    /// if this team is better.
	private int CompareValue(TableTeam enemy, int n) {
		int teamValue = 0;
		int enemyValue = 0;
		
		switch (n) {
		case 0:
			teamValue = mPoints;
			enemyValue = enemy.mPoints;
			break;
		case 1:
			teamValue = mTeam.getmHiddenPoints();
			enemyValue = enemy.mTeam.getmHiddenPoints();
			break;
		case 2:
			teamValue = GetCompareValue(2);
			enemyValue = enemy.GetCompareValue(2);
			break;
		case 3:
			teamValue = GetCompareValue(3);
			enemyValue = enemy.GetCompareValue(3);
			break;
		case 4:
			teamValue = GetCompareValue(4);
			enemyValue = enemy.GetCompareValue(4);
			break;
		case 5:
			enemyValue = this.getName().compareTo(enemy.getName());
			break;
		case 6:
			teamValue = mTeam.getmIndex();
			enemyValue = enemy.mTeam.getmIndex();
			break;
		}
		
		if 		(teamValue < enemyValue) {
			return 1;
		} else {
			if (teamValue > enemyValue) {
				return -1;
			} else {
				return 0;
			}
		}
		
	}

    /// Returns the n:th value to use when comparing two teams. 
	private int GetCompareValue(int n) {
		int returnValue = 0;
		
		switch (mTeam.getmLeague().getmTableSort()) {
		case GoalDiff_GoalsFor :
			switch (n) {
			case 2 :
				returnValue = mGoalDiff;
				break;
			case 3 :
				returnValue = mFor;
				break;
			case 4 :
				returnValue = mWon;
				break;
			}
			break;
		case GoalDiff_Wins :
			switch (n) {
			case 2 :
				returnValue = mGoalDiff;
				break;
			case 3 :
				returnValue = mWon;
				break;
			case 4 :
				returnValue = mFor;
				break;
			}
			break;
		case Wins_GoalDiff :
			switch (n) {
			case 2 :
				returnValue = mWon;
				break;
			case 3 :
				returnValue = mGoalDiff;
				break;
			case 4 :
				returnValue = mFor;
				break;
			}
			break;
		case Wins_GoalsFor :
			switch (n) {
			case 2 :
				returnValue = mWon;
				break;
			case 3 :
				returnValue = mFor;
				break;
			case 4 :
				returnValue = mGoalDiff;
				break;
			}
			break;
		case GoalsFor_GoalDiff :
			switch (n) {
			case 2 :
				returnValue = mFor;
				break;
			case 3 :
				returnValue = mGoalDiff;
				break;
			case 4 :
				returnValue = mWon;
				break;
			}
			break;
		case GoalsFor_Wins :
			switch (n) {
			case 2 :
				returnValue = mFor;
				break;
			case 3 :
				returnValue = mWon;
				break;
			case 4 :
				returnValue = mGoalDiff;
				break;
			}
			break;

		}
		
		return returnValue;
	}
	
}
