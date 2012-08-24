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

public class Team implements Comparable<Object>{

	private League 	mLeague;
	private int		mIndex;
	private String	mName;
	private int		mBonusPoints;
	private int		mHiddenPoints;
	
	// Constructor
	public Team(League league, int index) {
		mLeague = league;
		mIndex = index;
	}
	
	// Constructor
	public Team(League league, int index, String name) {
		mLeague = league;
		mIndex = index;
		mName = name;
	}
		
	// Properties
	
	public League getmLeague() {
		return mLeague;
	}

	public void setmLeague(League mLeague) {
		this.mLeague = mLeague;
	}

	public int getmIndex() {
		return mIndex;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public int getmBonusPoints() {
		return mBonusPoints;
	}

	public void setmBonusPoints(int mBonusPoints) {
		this.mBonusPoints = mBonusPoints;
	}

	public int getmHiddenPoints() {
		return mHiddenPoints;
	}

	public void setmHiddenPoints(int mHiddenPoints) {
		this.mHiddenPoints = mHiddenPoints;
	}
	
	// Save the team data
	public void Save() {
		mLeague.getmLeagueStore().SaveTeam(mLeague, this);
	}

	@Override
	public int compareTo(Object arg0) {
		return mName.compareTo(((Team)arg0).getmName());
	}

	public String ToString() {
		return this.mName;
	}
}
