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

public class Table {
	private League 	mLeague;
	private ArrayList<TableTeam> mTeamList;
	
	private boolean mIncludeHome;
	private boolean mIncludeAway;
	private boolean mIsForward;
	private int		mMaxMatches;
	
	public Table(League league, boolean includeHome, 
			boolean includeAway, boolean isForward, int maxMatches) {
		mLeague = league;
		mIncludeHome = includeHome;
		mIncludeAway = includeAway;
		mIsForward = isForward;
		mMaxMatches = maxMatches;
		
		Build();
		
		Sort();
		
	}
	
	public ArrayList<TableTeam> TeamList() {
		return mTeamList;
	}
	
	//returns the TableTeam for the specified team.
	public TableTeam getTableTeam(Team team) {
		int pos = 1;
		
		//Loop through the table teams
		for (TableTeam tableTeam : mTeamList) {
			if (tableTeam.getIndex() == team.getmIndex()) {
				//Update the table position of this table team
				tableTeam.setmPosition(pos);
				//Table team found -> bail out
				return tableTeam;
			}
			pos++;
		}
		return null;
	}
	
	//Builds the table by looping over all the matches
	private void Build(){
		ArrayList<Team> teamList = mLeague.GetTeams();
		mTeamList = new ArrayList<TableTeam>(teamList.size());
		for (Team team : teamList) {
			mTeamList.add(new TableTeam(team));
		}
		
		//Get the matches
		ArrayList<Match> matchList = mLeague.GetMatches(true);
		
		//Check in which direction we should loop
		int start, stop, step;
		if (mIsForward) {
			start = 0;
			stop = matchList.size();
			step = 1;
		} else {
			start = matchList.size() - 1 ;
			stop = -1;
			step = -1;
		}
		
		//Loop through the matches
		for (int i = start; i != stop; i += step) {
			Match match = matchList.get(i);
			if (mIncludeHome) {
				mTeamList.get(match.getmHomeTeamId()).AddMatch(match, mMaxMatches);
//				TableTeam update = mTeamList.get(match.getmHomeTeamId());
//				update.AddMatch(match, mMaxMatches);
//				mTeamList.set(match.getmHomeTeamId(), update);
			}
			if (mIncludeAway) {
				mTeamList.get(match.getmHomeTeamId()).AddMatch(match, mMaxMatches);
//				TableTeam update = mTeamList.get(match.getmAwayTeamId());
//				update.AddMatch(match, mMaxMatches);
//				mTeamList.set(match.getmAwayTeamId(), update);
			}
			
		}
		
	}
	
	//Sorts the Table
	private void Sort() {
		
		//Only include bonus points on "normal" table (over all matches)
		boolean useBonusPoints = (mIncludeHome && mIncludeAway && (mMaxMatches == -1));
		
		//calculate points and goaldiff for each team.
		for (TableTeam team : mTeamList) {
			team.SumUp(mLeague.getmWinPoints(),
					mLeague.getmDrawPoints(),
					mLeague.getmLossPoints(), useBonusPoints);
		}
		
		//Sort the team array
		Collections.sort(mTeamList);
		
	}

}
