package mBovin.TeamStats.LiveUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mBovin.TeamStats.Utils.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class LeagueListManager {
	private static final String cActiveListURL = "http://www.aragon.ws/soccerdb/export/liveupdateleagues.php";
	private static final String cArchivelistURL = "http://www.aragon.ws/soccerdb/export/completegrouplist.php";
	public static final String cActiveListFilename = "activeleagues.xml";
	public static final String cArchiveListFilename = "archiveleagues.xml";
	private static final String cLastSyncActiveListFilename = "lastsyncactiveleagues.xml";
	
	private List<DownloadableLeague> mAddedLeagueList;
	
	public LeagueListManager() {
		
	}
	
	public List<DownloadableLeague> getmAddedLeagueList() {
		return mAddedLeagueList;
	}
	
	private void checkForNewLeagues() {
		String filename = cLastSyncActiveListFilename;
		
		List<DownloadableLeague> localLeagueList = ParseActiveXML(GetLocalXML(filename));
		List<DownloadableLeague> removeLeagueList = ParseActiveXML(GetRemoteXML(cActiveListURL, filename ));
		
		mAddedLeagueList = CreateAddedLeagueList(localLeagueList, removeLeagueList);
		
		if (mAddedLeagueList.size() > 0) {
			// Log 
		}
		
	}

	private List<DownloadableLeague> CreateAddedLeagueList(List<DownloadableLeague> oldList,
			List<DownloadableLeague> newList) {
		List<DownloadableLeague> newLeagueList = new ArrayList<DownloadableLeague>();
		if ((oldList != null) && (oldList.size() > 0) && (newList != null)) {
			for (DownloadableLeague newLeague : newList) {
				if (!oldList.contains(newLeague)) {
					newLeagueList.add(newLeague);
				}
			}
		}
		return newLeagueList;
	}

	private Document GetLocalXML(String filename) {
		File file = new File(filename);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		FileInputStream fileIS;
		try {
			fileIS = new FileInputStream(file);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(fileIS);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<DownloadableLeague> ParseActiveXML(Document xmlDoc) {
		List<DownloadableLeague> leagueList = new ArrayList<DownloadableLeague>();
		try {
			Element root = xmlDoc.getDocumentElement();
			NodeList items = root.getElementsByTagName("league");
			for (int i=0; i<items.getLength();i++) {
				DownloadableLeague league = new DownloadableLeague();
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
			
				for (int j=0; j<properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("name")) {
						league.Name = property.getNodeValue();
					}
					if (name.equalsIgnoreCase("id")) {
						league.id = Integer.parseInt(property.getNodeValue());
					}
					if (name.equalsIgnoreCase("country")) {
						league.Country = property.getNodeValue();
					}
				}
				leagueList.add(league);
			}
			
		} catch (Exception e) {
			Log.e("XML READ", "PARSING FAILED");
		}
		return leagueList;
	}

	private Document GetRemoteXML(String url, String filename) {
		Document xmlDoc = utils.DownloadXMLDocument(url);
		if (filename != null) {
			try {
				TransformerFactory transfac = TransformerFactory.newInstance();
				Transformer trans = transfac.newTransformer();
				trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				trans.setOutputProperty(OutputKeys.INDENT, "yes");
			
				StreamResult result = new StreamResult(new File(filename));
				DOMSource source = new DOMSource(xmlDoc);
				trans.transform(source, result);
			} catch (Exception e) {
				Log.e("", "We had a problem");
			}
		}
		return xmlDoc;
	}

}
