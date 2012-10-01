package mBovin.TeamStats.LiveUpdate;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.widget.Toast;

public class DownloadableArchiveHandler extends DefaultHandler {
	
	// Tags used in XML Document
	private static final String GROUPS = "groups";
	private static final String LEAGUE = "league";
	private static final String SEASON = "season";
	private static final String STAGE = "stage";
	private static final String GROUP = "group";
	
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String COUNTRY = "country";
	
	
	// Flags for position in document
	@SuppressWarnings("unused")
	private boolean in_Groups = false;
	@SuppressWarnings("unused")
	private boolean in_Country = false;
	private boolean in_League = false;
	private boolean in_Season = false;
	private boolean in_Stage = false;
	private boolean in_Group = false;
	
	
	//Data Storage
	private List<DownloadableArchive> myDataSet = new ArrayList<DownloadableArchive>();
	private DownloadableArchive currentElement;
	private DownloadableArchiveItem currentSeason;
	private DownloadableArchiveItem currentStage;
	private DownloadableArchiveItem currentGroup;
	private String Country;
	private String League;
	private String Name;
	
	// Getter and Setter
	public List<DownloadableArchive> getParsedData() {
		return myDataSet;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		myDataSet = new ArrayList<DownloadableArchive> ();
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
		if (localName.equals(GROUPS)){
			this.in_Groups = true;
		} else if (localName.equals(COUNTRY)) {
			this.in_Country = true;
			this.currentElement = new DownloadableArchive();
			this.currentElement.country = attributes.getValue(NAME);
		} else if (localName.equals(LEAGUE)) {
			this.in_League = true;
			this.currentElement.league = attributes.getValue(NAME);
		} else if (localName.equals(SEASON)) {
			this.in_Season = true;
			this.currentSeason = new DownloadableArchiveItem();
			this.currentSeason.Name = attributes.getValue(NAME);
			String attrValue = attributes.getValue(ID);
			int i = Integer.parseInt(attrValue);
			this.currentSeason.Id = i;
		} else if (localName.equals(STAGE)) {
			this.in_Stage = true;
			this.currentStage = new DownloadableArchiveItem();
			this.currentStage.Name = attributes.getValue(NAME);
			String attrValue = attributes.getValue(ID);
			int i = Integer.parseInt(attrValue);
			this.currentStage.Id = i;
		} else if (localName.equals(GROUP)) {
			this.in_Group = true;
			this.currentGroup = new DownloadableArchiveItem();
			this.currentGroup.Name = attributes.getValue(NAME);
			String attrValue = attributes.getValue(ID);
			int i = Integer.parseInt(attrValue);
			this.currentGroup.Id = i;
			this.currentStage.SubItems.add(currentGroup);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals(GROUPS)){
			this.in_Groups = false;
		} else if (localName.equals(COUNTRY)) {
			this.myDataSet.add(currentElement);
			this.currentElement = null;
			this.in_Country = false;
		} else if (localName.equals(LEAGUE)) {
			this.in_League = false;
		} else if (localName.equals(SEASON)) {
			this.currentElement.seasons.add(currentSeason);
			this.currentSeason = null;
			this.in_Season = false;
		} else if (localName.equals(STAGE)) {
			this.currentSeason.SubItems.add(currentStage);
			this.currentStage = null;
			this.in_Stage = false;
		} else if (localName.equals(GROUP)) {
			this.in_Group = false;
		}
 	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
//		if (this.in_Country) {
//			this.Country = new String(ch, start, length);
//		} else if(this.in_Name) {
//			this.Name = new String(ch, start, length);
//		}
		Toast.makeText(null, "", Toast.LENGTH_SHORT).show();
	}
	
}
