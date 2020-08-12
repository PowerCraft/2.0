package powercraft.launcher.manager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import powercraft.launcher.PC_Logger;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLInfoTag;

public class PC_ThreadCheckUpdates extends Thread {

	private static final String url = "https://raw.githubusercontent.com/PowerCraft/Maven/master/1.7.10/Version.xml";
	private XMLInfoTag updateInfo;

	public PC_ThreadCheckUpdates() {
		start();
	}

	public XMLInfoTag getUpdateInfo() {
		while (getState() != State.TERMINATED) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		return updateInfo;
	}

	private void onUpdateInfoDownloaded(String page) {
		PC_Logger.fine("\n\nUpdate information received from server.");

		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new ByteArrayInputStream(page.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();
			NodeList node = doc.getElementsByTagName("Info");

			if (node.getLength() != 1) {
				PC_Logger.severe("No Info node found");
				return;
			}

			updateInfo = new XMLInfoTag(node.item(0)).read();

		} catch (SAXParseException err) {
			PC_Logger.severe("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			PC_Logger.severe(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			PC_Logger.throwing("PC_ThreadCheckUpdates", "onUpdateInfoDownloaded()", t);
			t.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			URL url = new URL(this.url);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String page = "";
			String line;

			while ((line = reader.readLine()) != null) {
				page += line + "\n";
			}

			reader.close();
			onUpdateInfoDownloaded(page);
		} catch (Exception e) {
			PC_Logger.warning("Error while downloading update info");
		}
	}

}
