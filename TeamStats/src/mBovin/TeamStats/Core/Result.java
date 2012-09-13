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

public class Result implements Comparable<Object> {
	
	private int mHomeGoals;
	private int mAwayGoals;
	private int mCount;
	
	public Result(int homeGoals, int awayGoals) {
		mHomeGoals = homeGoals;
		mAwayGoals = awayGoals;
		mCount = 1;
	}
	

	public Integer getmCount() {
		return mCount;
	}


	public void setmCount(int mCount) {
		this.mCount = mCount;
	}


	public int getmHomeGoals() {
		return mHomeGoals;
	}


	public int getmAwayGoals() {
		return mAwayGoals;
	}

    /// Returns true if this instance represents the specified result.
	public boolean IsResult(int homeGoals, int awayGoals) {
		return ((homeGoals == mHomeGoals) && (awayGoals == mAwayGoals));
	}
	
	
	
	
    /// Compares the count of this result to another result.
	@Override
	public int compareTo(Object another) {
		Result other = (Result) another;
		
		if (this.mCount > other.mCount) { 
			return -1;
		} else {
			if (this.mCount < other.mCount ) {
				return 1;
		} else {
			return 0;
			}
		}
	}


	@Override
	public String toString() {
		return  mHomeGoals + "-" + mAwayGoals;
	}
	
	

}
