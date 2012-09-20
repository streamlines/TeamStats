package mBovin.TeamStats.LiveUpdate;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class LeagueByCountry {
	private String Country;
	private String Flag;
	ArrayList<DownloadableLeague> leagues;
	/**
	 * @return the country
	 */
	public String getCountry() {
		return Country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		Country = country;
	}
	/**
	 * @return the leagues
	 */
	public ArrayList<DownloadableLeague> getLeagues() {
		return leagues;
	}
	/**
	 * @param leagues the leagues to set
	 */
	public void setLeagues(ArrayList<DownloadableLeague> leagues) {
		this.leagues = leagues;
	}
	/**
	 * @return the flag
	 */
	public String getFlag() {
		return Flag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return Country.equals(((LeagueByCountry)o).Country);
	}
	
	public void findFlag(Context c) {
		AssetManager assets = c.getAssets();
		try {
			String[] paths = assets.list("Flags");
			for (String path : paths) {
				if (path.contains(Country)) {
					Flag = path;
				}
			}
		} catch (IOException e) {
			Log.e("LeagueByCountry.java", "Unable to find Flag for " + Country);
		}
			
		
	}
	

}
