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


public interface ILeagueStore {
	void LoadHeader(League league);
	ArrayList<Team> LoadTeams(League league);
	ArrayList<Match> LoadMatches(League league);
	
    void SaveHeader(League league);
    void SaveTeams(League league, Team[] listTeams);
    void SaveMatches(League league, ArrayList<Match> matchlist);

    void SaveMatch(League league, Match match);
    void SaveTeam(League league, Team team);
	ArrayList<Match> LoadMatches(League league, MatchFilterPlayed filter,
			boolean onlyPlayed);
	ArrayList<Match> LoadMatches(League league, MatchFilterRound filter,
			int round);
	ArrayList<Match> LoadMatches(League league,
			MatchFilterTeam matchFilterTeam, int teamIndex,
			boolean includeHome, boolean includeAway);    
 

}
