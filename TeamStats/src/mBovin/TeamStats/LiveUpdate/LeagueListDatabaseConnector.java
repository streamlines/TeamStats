package mBovin.TeamStats.LiveUpdate;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LeagueListDatabaseConnector {
	
	private static final String DATABASE_NAME = "LeagueList";
	private static final int DATABASE_VERSION = 0;
	private SQLiteDatabase database;
	private DatabaseOpenHelper dbOpenHelper;
	
	// Table 1 - Current Leagues
	private static final String CURRENT_TABLE = "LeagueTable";
	private static final String ROWID = "_id";
	private static final String ID = "ID";
	private static final String COUNTRY = "country";
	private static final String LEAGUE = "league";
	private static final String SELECTED = "selected";
	
	//Table 2 - Archive Leagues
	private static final String ARCHIVE_TABLE = "ArchiveTable";
	private static final String SEASON = "Season";
	
	public LeagueListDatabaseConnector(Context context) {
		dbOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void open() throws SQLException {
		database = dbOpenHelper.getWritableDatabase();
	}
	
	public void close() {
		if (database != null) {
			database.close();
		}
	}
	
	public void addLeague(DownloadableLeague dleague) {
		ContentValues newLeague = new ContentValues();
		newLeague.put(LEAGUE, dleague.Name);
		newLeague.put(COUNTRY, dleague.Country);
		newLeague.put(ID, dleague.id);
		
		open();
		database.insert(CURRENT_TABLE, null, newLeague);
		close();
	}
	
	public void selectLeague(int id, boolean selected) {
		ContentValues updateLeague = new ContentValues();
		updateLeague.put(SELECTED, selected);
		open();
		database.update(CURRENT_TABLE, updateLeague, ID + "=" + id, null);
		close();
	}
	
	public Cursor getAllCountries() {
		return database.query(true, CURRENT_TABLE, new String[] {COUNTRY}, null, null, null, null, COUNTRY, null);
	}
	
	public Cursor getLeagues(String country) {
		return database.query(CURRENT_TABLE, new String[] {ID, COUNTRY, LEAGUE, SELECTED}, COUNTRY + "=" + country, null, null, null, LEAGUE,null);
	}
	
	List<DownloadableLeague> getCurrent() {
		Cursor cursor = database.query(CURRENT_TABLE,new String[] {ID, COUNTRY, LEAGUE}	 , null, null, null, null, null);
		List<DownloadableLeague> leagues = new ArrayList<DownloadableLeague>();
		for (int i = 0; i < cursor.getCount(); i++) {
			DownloadableLeague league = new DownloadableLeague();
			cursor.moveToPosition(i);
			league.Name = cursor.getString(cursor.getColumnIndex(LEAGUE));
			league.Country = cursor.getString(cursor.getColumnIndex(COUNTRY));
			league.id = cursor.getInt(cursor.getColumnIndex(ID));
			leagues.add(league);
		}
		return leagues;
	}
	
	private class DatabaseOpenHelper extends SQLiteOpenHelper{

		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			String createQuery = "CREATE TABLE " + CURRENT_TABLE + 
					"(" + ROWID + " integer primary key autoincrement," + ID + " integer, " + 
					COUNTRY + " TEXT, " + LEAGUE + " TEXT, " + SELECTED + " BOOLEAN;";
			db.execSQL(createQuery);
			createQuery = "CREATE TABLE " + ARCHIVE_TABLE + 
					"(" + ROWID + " integer primary key autoincrement," + ID + " integer, " + 
					COUNTRY + " TEXT, " + LEAGUE + " TEXT, " + SEASON + " TEXT;";
			db.execSQL(createQuery);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
