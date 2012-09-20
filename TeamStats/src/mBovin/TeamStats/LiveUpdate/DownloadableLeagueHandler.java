package mBovin.TeamStats.LiveUpdate;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DownloadableLeagueHandler extends DefaultHandler {
	
	// Tags used in XML Document
	private static final String LEAGUES = "leagues";
	private static final String LEAGUE = "league";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String COUNTRY = "country";
	
	
	// Flags for position in document
	@SuppressWarnings("unused")
	private boolean in_Leagues = false;
	@SuppressWarnings("unused")
	private boolean in_League = false;
	private boolean in_Name = false;
	private boolean in_Country = false;
	
	
	//Data Storage
	private List<DownloadableLeague> myDataSet = new ArrayList<DownloadableLeague>();
	private DownloadableLeague currentElement;
	private String Country;
	private String Name;
	
	// Getter and Setter
	public List<DownloadableLeague> getParsedData() {
		return myDataSet;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		myDataSet = new ArrayList<DownloadableLeague> ();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals(LEAGUES)){
			this.in_Leagues = true;
		} else if (localName.equals(LEAGUE)) {
			this.in_League = true;
			this.currentElement = new DownloadableLeague();
			String attrValue = attributes.getValue(ID);
			int i = Integer.parseInt(attrValue);
			this.currentElement.id = i;
		} else if (localName.equals(NAME)) {
			this.in_Name = true;
		} else if (localName.equals(COUNTRY)) {
			this.in_Country = true;
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals(LEAGUES)){
			this.in_Leagues = false;
		} else if (localName.equals(LEAGUE)) {
			this.myDataSet.add(currentElement);
			this.currentElement = null;
			this.in_League = false;
		} else if (localName.equals(NAME)) {
			this.currentElement.Name = this.Name;
			this.Name = "";
			this.in_Name = false;
		} else if (localName.equals(COUNTRY)) {
			this.currentElement.Country = this.Country;
			this.Country = "";
			this.in_Country = false;
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (this.in_Country) {
			this.Country = new String(ch, start, length);
		} else if(this.in_Name) {
			this.Name = new String(ch, start, length);
		}
	}
	
}
