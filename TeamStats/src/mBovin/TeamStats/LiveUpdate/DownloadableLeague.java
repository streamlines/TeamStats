package mBovin.TeamStats.LiveUpdate;

public class DownloadableLeague {
	public int 	id;
	public String Name;
	public String Country;
	
	/**
	 * @param object
	 * @return
	 * @see java.lang.String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return (id == ((DownloadableLeague)object).id);
	}

}
