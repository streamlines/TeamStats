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
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LeagueDatabase implements ILeagueStore {
	
	private static int cFileVersion 	= 1;
	private static final String cHeader = "HEADER";
	private static final String cLeagueName = "NAME";
	private static final String cLeagueYear = "YEAR";
	private static final String cTeamCount = "TEAMS";
	private static final String cRoundCount = "ROUNDS";
	private static final String cMatchCount = "MATCHES";
	private static final String cPlayedCount = "PLAYED";
	private static final String cWinPoints = "WIN";
	private static final String cDrawPoints = "DRAW";
	private static final String cLossPoints = "LOSS";
	private static final String cTableLines = "LINES";
	private static final String cSort = "SORT";
	
	private static final String cTeams = "TEAMS";
	private static final String cID = "ID";
	private static final String cTeamName = "NAME";
	private static final String cBonusPoints = "BPOINTS";
	private static final String cHiddenPoints = "HPOINTS";
	
	private static final String cMatches = "MATCHES";
	private static final String cRound = "ROUND";
	private static final String cYear = "YEAR";
	private static final String cMonth = "MONTH";
	private static final String cDay = "DAY";
	private static final String cHomeId = "HOME";
	private static final String cAwayId = "AWAY";
	private static final String cHomeGoals = "HOMEGOALS";
	private static final String cAwayGoals = "AWAYGOALS";
	
	private String mDatabasename;
	private String mFilename;
	private dbOpenHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;
	
	public LeagueDatabase(Context context, String Dbname, String fileName) {
		mDatabasename =  Dbname;
		mFilename = fileName;
		mDatabaseHelper = new dbOpenHelper(context, mDatabasename, null, cFileVersion);
	}
	
	private void open() {
		mDatabase = mDatabaseHelper.getWritableDatabase();
	}
	
	private void close() {
		if (mDatabase != null) {
			mDatabase.close();
		}
	}
	
	public void Refresh(League league) {
		open();
		mDatabase.delete(cHeader, null, null);
		mDatabase.delete(cTeams, null, null);
		mDatabase.delete(cMatches, null, null);
		LeagueBinaryFile binaryFile = new LeagueBinaryFile(mFilename);
		binaryFile.LoadHeader(league);
		SaveHeader(league);
		SaveTeams(league, (Team[]) binaryFile.LoadTeams(league).toArray());
		SaveMatches(league, binaryFile.LoadMatches(league));
	}

	@Override
	public void LoadHeader(League league)  {
		open();
		Cursor cursor = mDatabase.query(cHeader, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			league.setmName(cursor.getString(cursor.getColumnIndex(cLeagueName)));
			int year = cursor.getInt(cursor.getColumnIndex(cLeagueYear));
			league.setmIsSplit(year < 0);
			league.setmYear(Math.abs((int) year));
			league.setmTeamCount(cursor.getInt(cursor.getColumnIndex(cTeamCount)));
			league.setmRoundCount(cursor.getInt(cursor.getColumnIndex(cRoundCount)));
			league.setmMatchCount(cursor.getInt(cursor.getColumnIndex(cMatchCount)));
			league.setmPlayedMatchCount(cursor.getInt(cursor.getColumnIndex(cPlayedCount)));
			league.setmWinPoints(cursor.getInt(cursor.getColumnIndex(cWinPoints)));
			league.setmDrawPoints(cursor.getInt(cursor.getColumnIndex(cDrawPoints)));
			league.setmLossPoints(cursor.getInt(cursor.getColumnIndex(cLossPoints)));
			
			int TableSort = cursor.getInt(cursor.getColumnIndex(cSort));
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
			league.setmTableLines(cursor.getLong(cursor.getColumnIndex(cTableLines)));
		} else {
			// No Data load from File
			LeagueBinaryFile binaryFile = new LeagueBinaryFile(mFilename);
			binaryFile.LoadHeader(league);
			SaveHeader(league);
		}
		close();
	}

	@Override
	public ArrayList<Team> LoadTeams(League league) {
		ArrayList<Team> listTeams = null;
		Cursor cursor = mDatabase.query(cTeams, null, null, null, null, null, null);
		listTeams = new ArrayList<Team>(league.getmTeamCount());
		if (cursor.getCount() == league.getmTeamCount()) {
			for (int i = 0; i < league.getmTeamCount(); i++) {
				cursor.moveToPosition(i);
				listTeams.add(LoadTeam(league,i,cursor));
			}
		} else {
			// No Data load from File
			LeagueBinaryFile binaryFile = new LeagueBinaryFile(mFilename);
			SaveTeams(league, (Team[]) binaryFile.LoadTeams(league).toArray());
		}
		return listTeams;
		
	}
	
	private Team LoadTeam(League league, int index, Cursor reader) {
		Team team = new Team(league, index);
		
		team.setmName(reader.getString(reader.getColumnIndex(cTeamName)));
		team.setmBonusPoints(reader.getInt(reader.getColumnIndex(cBonusPoints))); 
		team.setmHiddenPoints(reader.getInt(reader.getColumnIndex(cHiddenPoints)));
		
		return team;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league) {
		ArrayList<Match> matches = null;
		Cursor cursor = mDatabase.query(cMatches, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			matches = new ArrayList<Match>(league.getmMatchCount());
			for (int i = 0; i < league.getmMatchCount(); i++) {
				cursor.moveToPosition(i);
				matches.add(LoadMatch(league,i,cursor));
			}
		} else {
			LeagueBinaryFile binaryFile = new LeagueBinaryFile(mFilename);
			SaveMatches(league, binaryFile.LoadMatches(league));
		}
		return matches;
		
	}

	private Match LoadMatch(League league, int index, Cursor cursor) {
		Match match= new Match(league, index);
		
		match.setmRound(cursor.getInt(cursor.getColumnIndex(cRound)));
		int year = cursor.getInt(cursor.getColumnIndex(cYear));
		int month = cursor.getInt(cursor.getColumnIndex(cMonth));
		int day = cursor.getInt(cursor.getColumnIndex(cDay));
		match.setmHomeTeamId(cursor.getInt(cursor.getColumnIndex(cHomeId)));
		match.setmAwayTeamId(cursor.getInt(cursor.getColumnIndex(cAwayId)));
		match.setmHomeGoals(cursor.getInt(cursor.getColumnIndex(cHomeGoals)));
		match.setmAwayGoals(cursor.getInt(cursor.getColumnIndex(cAwayGoals)));
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		match.setmDate(cal);
		
		return match;
	}

	@Override
	public void SaveHeader(League league) {
		ContentValues saveData = new ContentValues();
		saveData.put(cLeagueName, league.getmName());
		int year = league.getmYear();
		if (league.ismIsSplit()) {
			year = 0 - year;
		}
		saveData.put(cLeagueYear, year);
		saveData.put(cTeamCount, league.getmTeamCount());
		saveData.put(cRoundCount, league.getmRoundCount());
		saveData.put(cMatchCount, league.getmMatchCount());
		saveData.put(cPlayedCount, league.getmPlayedMatchCount());
		saveData.put(cWinPoints, league.getmWinPoints());
		saveData.put(cDrawPoints, league.getmDrawPoints());
		saveData.put(cLossPoints, league.getmLossPoints());
		
		TableSortMethod selected = league.getmTableSort();
		if (selected.equals(TableSortMethod.GoalDiff_GoalsFor)) {
			saveData.put(cSort, 0);
		} else if (selected.equals(TableSortMethod.GoalDiff_Wins)) {
			saveData.put(cSort, 1);
		} else if (selected.equals(TableSortMethod.Wins_GoalDiff)) {
			saveData.put(cSort, 2);
		} else if (selected.equals(TableSortMethod.Wins_GoalsFor)) {
			saveData.put(cSort, 3);
		} else if (selected.equals(TableSortMethod.GoalsFor_GoalDiff)) {
			saveData.put(cSort, 4);
		} else if (selected.equals(TableSortMethod.GoalsFor_Wins)) {
			saveData.put(cSort, 5);
		}
		saveData.put(cTableLines, league.getmTableLines());
		
		open();
		mDatabase.delete(cHeader, null, null);
		mDatabase.insert(cHeader, null, saveData);
		close();
	}

	@Override
	public void SaveTeams(League league, Team[] listTeams) {
		for (Team team : listTeams) {
			SaveTeam(league, team);
		}
	}

	@Override
	public void SaveMatches(League league, ArrayList<Match> listMatches) {
		for (Match match : listMatches) {
			SaveMatch(league, match);
		}
	}

	@Override
	public void SaveMatch(League league, Match match) {
		ContentValues saveData = new ContentValues();
		saveData.put(cID, match.getmIndex());
		saveData.put(cRound, match.getmRound());
		saveData.put(cHomeId, match.getmHomeTeamId());
		saveData.put(cAwayId, match.getmAwayTeamId());
		Calendar cal = match.getmDate();
		saveData.put(cYear, cal.get(Calendar.YEAR));
		saveData.put(cMonth, cal.get(Calendar.MONTH));
		saveData.put(cDay, cal.get(Calendar.DAY_OF_MONTH));
		saveData.put(cHomeGoals, match.getmHomeGoals());
		saveData.put(cAwayGoals, match.getmAwayGoals());
		String whereClause = cID + " = " + match.getmIndex();
		
		int retVal = mDatabase.update(cMatches, saveData, whereClause, null);
		if (retVal < 1) {
			mDatabase.insert(cMatches, null, saveData);
		}
	}

	@Override
	public void SaveTeam(League league, Team team) {
		ContentValues saveData = new ContentValues();
		saveData.put(cID, team.getmIndex());
		saveData.put(cTeamName, team.getmName());
		saveData.put(cBonusPoints, team.getmBonusPoints());
		saveData.put(cHiddenPoints, team.getmHiddenPoints());
		String whereClause = cID + " = " + team.getmIndex();
		
		int retVal = mDatabase.update(cTeams, saveData, whereClause, null);
		if (retVal < 1) {
			mDatabase.insert(cTeams, null, saveData);
		}
		
	}

	@Override
	public ArrayList<Match> LoadMatches(League league,
			MatchFilterPlayed filter, boolean onlyPlayed) {
		ArrayList<Match> matches = null;
		String query;
		if (onlyPlayed) {
			query = cHomeGoals + " > -1";
		} else {
			query = null;
		}
		Cursor cursor = mDatabase.query(cMatches, null, query, null, null, null, null);
		matches = new ArrayList<Match>(cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			matches.add(LoadMatch(league,i,cursor));
		}
		return matches;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league, MatchFilterRound filter,
			int round) {
		ArrayList<Match> matches = null;
		String query = cRound + " = " + round;
		Cursor cursor = mDatabase.query(cMatches, null, query, null, null, null, null);
		matches = new ArrayList<Match>(cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			matches.add(LoadMatch(league,i,cursor));
		}
		return matches;
	}

	@Override
	public ArrayList<Match> LoadMatches(League league,
			MatchFilterTeam matchFilterTeam, int teamIndex,
			boolean includeHome, boolean includeAway) {
		
		ArrayList<Match> matches = null;
		String query = null;
		if (includeHome) {
			query = cHomeId + " = " + teamIndex;
		} 
		if (includeAway) {
			if (query != null) {
				query = query + " AND ";
			} 
			query += cAwayId + " = " + teamIndex;
		}
		Cursor cursor = mDatabase.query(cMatches, null, query, null, null, null, null);
		matches = new ArrayList<Match>(cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			matches.add(LoadMatch(league,i,cursor));
		}
		return matches;
	}

	private class dbOpenHelper extends SQLiteOpenHelper {

		public dbOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String dbCreate = "CREATE TABLE " + cHeader + "(" + cLeagueName + " TEXT, " +
					cLeagueYear + " INTEGER, " + cTeamCount + " INTEGER, " + 
					cRoundCount + " INTEGER, " + cMatchCount + " INTEGER, " + 
					cPlayedCount + " INTEGER, " + cWinPoints + " INTEGER, " +
					cDrawPoints + " INTEGER, " + cLossPoints + " INTEGER, " + 
					cTableLines + " LONG, " + cSort + " INTEGER);";
			db.execSQL(dbCreate);
			dbCreate = "CREATE TABLE " + cTeams + "(" + cID + " INTEGER, " +
					cTeamName + " TEXT, " + cBonusPoints + " INTEGER, " +
					cHiddenPoints + " INTEGER);";
			db.execSQL(dbCreate);
			dbCreate = "CREATE TABLE " + cMatches + "(" + cID + " INTEGER, " + 
					cRound + " INTEGER, " + cYear + " INTEGER, " + cMonth + " INTEGER, " + 
					cDay + " INTEGER, " + cHomeId + " INTEGER, " + cAwayId + " INTEGER, " + 
					cHomeGoals + " INTEGER, " + cAwayGoals + " INTEGER);";
			db.execSQL(dbCreate);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	}
	
}
