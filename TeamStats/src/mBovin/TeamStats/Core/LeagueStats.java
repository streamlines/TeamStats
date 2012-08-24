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

public class LeagueStats {
	private League mLeague;
	
	private int mHomeWins;
	private int mDraws;
	private int mAwayWins;
	private int mHomeGoals;
	private int mAwayGoals;
	
	private ArrayList<Result> mFrequentResultList = new ArrayList<Result>(10);
	
	public LeagueStats(League league) {
		mLeague = league;
		Build();
	}

	public int Played() {
		return mHomeWins + mDraws + mAwayWins;
	}
	
	public int HomeWins() {
		return mHomeWins;
	}
	
	public float HomeWinPercent() {
		return (this.Played() > 0) ? ((float)mHomeWins / (float)this.Played()) : 0;
	}
	
	public int Draws() {
		return mDraws;
	}
	
	public float DrawPercent() {
		return (this.Played() > 0) ? ((float)mDraws / (float)this.Played()) : 0;
	}
	
	public int AwayWins() {
		return mAwayWins;
	}
	
	public float AwayWinPercent() {
		return (this.Played() > 0) ? ((float)mAwayWins / (float)this.Played()) : 0;
	}

	public int Goals() {
		return mHomeGoals + mAwayGoals;
	}
	
	public float GoalAvg() {
		return (this.Played() > 0) ? ((float)this.Goals() / (float)this.Played()) : 0;
	}

	public int HomeGoals() {
		return mHomeGoals;
	}
	
	public float HomeGoalAvg() {
		return (this.Played() > 0) ? ((float)mHomeGoals / (float)this.Played()) : 0;
	}
	
	public int AwayGoals() {
		return mAwayGoals;
	}
	
	public float AwayGoalAvg() {
		return (this.Played() > 0) ? ((float)mAwayGoals / (float)this.Played()) : 0;
	}
	
    /// Returns the most frequent result at the specified index.
	public Result GetFrequentResult(int index) {
		return (index > -1 && index < mFrequentResultList.size()) ?
				mFrequentResultList.get(index) :
					null;
	}
	
	
	
	//Builds the Statistics
	private void Build() {
		ArrayList<Match> matchList = mLeague.GetMatches(true);
		
		for (Match match : matchList) {
			if (match.getmHomeGoals() > match.getmAwayGoals()) {
				mHomeWins++;
			} else if (match.getmAwayGoals() > match.getmHomeGoals()) {
				mAwayWins++;
			} else { mDraws++;}
			
			mHomeGoals += match.getmHomeGoals();
			mAwayGoals += match.getmAwayGoals();
			
			AddResult(match.getmHomeGoals(), match.getmAwayGoals());
		}
		
		Collections.sort(mFrequentResultList);
		
	}

    /// Adds a result to the the most frequent result list.
	private void AddResult(int homeGoals, int awayGoals) {
		boolean resultFound = false;
		
		//Loop through all results in the list
		for (Result result : mFrequentResultList) {
            // If the result is found -> bump the count for
            // that result and exit loop
			if (result.IsResult(homeGoals, awayGoals)){
				result.setmCount(result.getmCount()+1);
				resultFound = true;
				break;
			}
		}
		
		//This was a new result then add to list
		if (!resultFound) {
			mFrequentResultList.add(new Result(homeGoals, awayGoals));
		}
	}
	
	
	
	

}
