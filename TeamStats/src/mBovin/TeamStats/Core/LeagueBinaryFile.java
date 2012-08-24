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

public class LeagueBinaryFile implements ILeagueStore {
	
	private static int cFileVersion 	= 1;
	private static int cHeaderSize		= 89;
	private static int cTeamSize 		= 42;
	private static int cMatchSize		= 9;
	private static int cLeagueNameChars	= 16;
	private static int cTeamNameChars	= 10;
	
	private String mFilename;
	
	public LeagueBinaryFile(String filename) {
		mFilename =  filename;
	}
	
	public String getmFilename() {
		return mFilename;
	}

	public void setmFilename(String mFilename) {
		this.mFilename = mFilename;
	}



	@Override
	public void LoadHeader(League league) {
		// TODO Auto-generated method stub
	//	AndroidFileIO file = new AndroidFileIO(context.getAssets()); 

	}

	@Override
	public ArrayList<Team> LoadTeams(League league) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void SaveHeader(League league) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SaveTeams(League league, Team[] listTeams) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SaveMatches(League league, ArrayList<Match> listMatches) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SaveMatch(League league, Match match) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SaveTeam(League league, Team team) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Match> LoadMatches(League league,
			MatchFilterPlayed filter, boolean onlyPlayed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league, MatchFilterRound filter,
			int round) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league,
			MatchFilterTeam matchFilterTeam, int teamIndex,
			boolean includeHome, boolean includeAway) {
		// TODO Auto-generated method stub
		return null;
	}

}
