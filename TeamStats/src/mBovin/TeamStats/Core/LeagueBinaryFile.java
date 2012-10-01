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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;

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
	public void LoadHeader(League league)  {
		File file = new File(mFilename); 
		DataInputStream ds = null;
		try {
			ds = new DataInputStream(new FileInputStream(file));
			byte[] namebuffer = new byte[4];
			for (int z=1; z<5; z++) {
				namebuffer[z-1] = ds.readByte();
			}
			String idStr = new String(namebuffer);
			if (!(idStr.equals("mbTS"))) {
				ds.close();
				throw new IOException("Not a TeamStats data file");
			}
			
			int fileVersion = ds.readByte();
			namebuffer = new byte[cLeagueNameChars*4];
			ds.read(namebuffer);
			String leaguename = new String(namebuffer);
			league.setmName(leaguename.trim());
		
			// YEAR IS NOT WORKING
			byte[] shortBuffer = new byte[2];
			ds.read(shortBuffer);
			
			//year is stored little-Endian so need to swap the bytes and compare for negative
			int year = (shortBuffer[0] & 0xFF) | (shortBuffer[1] & 0xFF) << 8;
			if (year > 10000) {
				year = year - 65536;
			}
			
			league.setmIsSplit(year < 0);
			league.setmYear(Math.abs((int) year));
			
			league.setmTeamCount((int) ds.readByte());
			league.setmRoundCount((int) ds.readByte());
			
			ds.read(shortBuffer);
			league.setmMatchCount((shortBuffer[0] & 0xFF) | (shortBuffer[1] & 0xFF) << 8);
			ds.read(shortBuffer);
			league.setmPlayedMatchCount((shortBuffer[0] & 0xFF) | (shortBuffer[1] & 0xFF) << 8);
			
			league.setmWinPoints((int) ds.readByte());
			league.setmDrawPoints((int) ds.readByte());
			league.setmLossPoints((int) ds.readByte());
			
			int TableSort = ds.readUnsignedByte();
			TableSortMethod selected = null;
			switch (TableSort) {
			case 0:
				selected = TableSortMethod.GoalDiff_GoalsFor;
				break;
			case 1:
				selected = TableSortMethod.GoalDiff_Wins;
				break;
			case 2:
				selected = TableSortMethod.Wins_GoalDiff;
				break;
			case 3:
				selected = TableSortMethod.Wins_GoalsFor;
				break;
			case 4:
				selected = TableSortMethod.GoalsFor_GoalDiff;
				break;
			case 5:
				selected = TableSortMethod.GoalsFor_Wins;
				break;				
			}
			league.setmTableSort(selected);
			byte[] longBuffer = new byte[4];
			ds.read(longBuffer);
			league.setmTableLines((longBuffer[0] & 0xFF) | (longBuffer[1] & 0xFF) << 8 | (longBuffer[2] & 0xFF) << 16 | (longBuffer[3] & 0xFF) << 24);
			ds.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

		
	}

	@Override
	public ArrayList<Team> LoadTeams(League league) {
		ArrayList<Team> listTeams = null;
		File file = new File(mFilename); 
		DataInputStream ds = null;
		try {
			ds = new DataInputStream(new FileInputStream(file));
			// Skip the header
			byte[] buffer= new byte[cHeaderSize];
			ds.read(buffer);
		
			listTeams = new ArrayList<Team>(league.getmTeamCount());
			for (int i = 0; i < league.getmTeamCount(); i++) {
				listTeams.add(LoadTeam(league,i,ds));
			}
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listTeams;
		
	}
	
	private Team LoadTeam(League league, int index, DataInputStream reader) {
		Team team = new Team(league, index);
		
		byte[] buffer = new byte[cTeamNameChars*4];
		try {
			reader.read(buffer);
			String teamName = new String(buffer);
			team.setmName(teamName.trim());
			team.setmBonusPoints(reader.read()); //Read SByte
			team.setmHiddenPoints(reader.read()); //Read SByte
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return team;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league) {
		ArrayList<Match> matches = null;
		File file = new File(mFilename); 
		DataInputStream ds = null;
		try {
			ds = new DataInputStream(new FileInputStream(file));
			// Skip the header
			byte[] buffer= new byte[cHeaderSize + (league.getmTeamCount() * cTeamSize)];
			ds.read(buffer);
			buffer = null;
			matches = new ArrayList<Match>(league.getmMatchCount());
			for (int i = 0; i < league.getmMatchCount(); i++) {
				matches.add(LoadMatch(league,i,ds));
			}
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matches;
		
	}

	private Match LoadMatch(League league, int index, DataInputStream ds) {
		Match match= new Match(league, index);
		
		byte[] shortBuffer = new byte[2];
		try {
			match.setmRound(ds.read());
			ds.read(shortBuffer);
			int year = ((shortBuffer[0] & 0xFF) | (shortBuffer[1] & 0xFF) << 8);
			int month = ds.read() - 1;
			int day = ds.read();
			match.setmHomeTeamId(ds.read());
			match.setmAwayTeamId(ds.read());
			match.setmHomeGoals(ds.readByte());
			match.setmAwayGoals(ds.readByte());
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day);
			match.setmDate(cal);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return match;
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
		ArrayList<Match> matches = null;
		File file = new File(mFilename); 
		DataInputStream ds = null;
		try {
			ds = new DataInputStream(new FileInputStream(file));
			// Skip the header
			byte[] buffer= new byte[cHeaderSize + (league.getmTeamCount() * cTeamSize)];
			ds.read(buffer);
			buffer = null;
			matches = new ArrayList<Match>(league.getmMatchCount());
			for (int i = 0; i < league.getmMatchCount(); i++) {
				Match match = LoadMatch(league,i,ds);
				if (match.IsPlayed() == onlyPlayed) {
					matches.add(match);
				}
			}
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matches;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league, MatchFilterRound filter,
			int round) {
		ArrayList<Match> matches = null;
		File file = new File(mFilename); 
		DataInputStream ds = null;
		try {
			ds = new DataInputStream(new FileInputStream(file));
			// Skip the header
			byte[] buffer= new byte[cHeaderSize + (league.getmTeamCount() * cTeamSize)];
			ds.read(buffer);
			buffer = null;
			matches = new ArrayList<Match>(league.getmMatchCount());
			for (int i = 0; i < league.getmMatchCount(); i++) {
				Match match = LoadMatch(league,i,ds);
				if (match.getmRound() == round) {
					matches.add(match);
				}
			}
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matches;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league,
			MatchFilterTeam matchFilterTeam, int teamIndex,
			boolean includeHome, boolean includeAway) {
		ArrayList<Match> matches = null;
		File file = new File(mFilename); 
		DataInputStream ds = null;
		try {
			ds = new DataInputStream(new FileInputStream(file));
			// Skip the header
			byte[] buffer= new byte[cHeaderSize + (league.getmTeamCount() * cTeamSize)];
			ds.read(buffer);
			buffer = null;
			matches = new ArrayList<Match>(league.getmMatchCount());
			for (int i = 0; i < league.getmMatchCount(); i++) {
				Match match = LoadMatch(league,i,ds);
				if ((includeHome && match.getmHomeTeamId() == teamIndex) || (includeAway && match.getmAwayTeamId() == teamIndex)) {
					matches.add(match);
				}
			}
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matches;
	}

}
