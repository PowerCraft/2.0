package powercraft.launcher.manager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import powercraft.launcher.PC_Launcher;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Logger;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLTag;

public class PC_ThreadLangDownloader extends Thread {

	private static final String url = "https://raw.githubusercontent.com/PowerCraft/Maven/master/1.7.10/Lang.xml";
	private String module;
	private ArrayList<String> modules;

	public PC_ThreadLangDownloader(String module) {
		this.module = module;
		start();
	}

	public PC_ThreadLangDownloader(ArrayList<String> modules) {
		this.modules = modules;
		start();
	}

	private void onInfoDownloaded(String page) {
		PC_Logger.fine("\n\nLang information received from server.");
		XMLLangInfoTag langInfo = null;

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

			langInfo = new XMLLangInfoTag(node.item(0)).read();

		} catch (SAXParseException err) {
			PC_Logger.severe("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			PC_Logger.severe(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			PC_Logger.throwing("PC_ThreadLangDownloader", "onInfoDownloaded()", t);
			PC_ModuleManager.errors.add("PC_ThreadLangDownloader error in onInfoDownloaded()");
			t.printStackTrace();
		}

		if (langInfo != null) {
			if (module != null) {
				List<String> codes = langInfo.getLangCodes();
				for (String code : codes) {
					XMLLangVersionTag lang = langInfo.getLang(code, module);
					download(lang);
				}
				PC_ModuleManager.updatedCount++;
			} else if (modules != null) {
				List<String> codes = langInfo.getLangCodes();
				for (String code : codes) {
					for (String mod : modules) {
						XMLLangVersionTag lang = langInfo.getLang(code, mod);
						download(lang);
					}
				}
			}
		}
	}

	private boolean download(XMLLangVersionTag langVersion) {
		boolean error = false;
		int depth = 0;
		do {
			error = false;
			try {
				File langFile = new File(PC_LauncherUtils.getPowerCraftFile(), "lang/" + langVersion.getLang());
				if (!langFile.exists())
					langFile.mkdirs();

				URL url = new URL(langVersion.download);
				BufferedInputStream bis = new BufferedInputStream(url.openStream());
				FileOutputStream fis = new FileOutputStream(langFile + "/" + langVersion.getModule() + ".lang");
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = bis.read(buffer, 0, 1024)) != -1)
					fis.write(buffer, 0, count);
				fis.close();
				bis.close();

				PC_Logger.fine(langVersion.getLang() + " lang for " + langVersion.getModule() + " installed.\n\n");
				try {
					Class c = Class.forName("powercraft.api.PC_Lang");
					c.getConstructor().newInstance();
				} catch (Exception e) {
					PC_Logger.warning("Error reload lang resources");
					PC_ModuleManager.errors.add("Error reload lang resources");
				}
				return true;

			} catch (IOException e) {
				if (depth <= 5)
					error = true;
				else {
					PC_Logger.warning("Error while downloading lang info: download()");
					PC_ModuleManager.errors.add("Error while downloading lang " + langVersion.getLang() + "/"
							+ langVersion.getModule() + " in download()");
				}
				depth++;
			}
		} while (error);
		return false;
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
			onInfoDownloaded(page);
		} catch (IOException e) {
			PC_Logger.warning("Error while downloading lang info: run()");
			PC_ModuleManager.errors.add("IOException in run");
		}
	}

	public static class XMLLangInfoTag extends XMLTag<XMLLangInfoTag> {
		private List<XMLLangVersionTag> langs = new ArrayList<XMLLangVersionTag>();
		private List<String> langCodes = new ArrayList<String>();

		public XMLLangInfoTag(Node node) {
			super(node);
		}

		public XMLLangVersionTag getLang(String usedLang, String module) {
			for (XMLLangVersionTag lang : langs) {
				if (lang.getLang().equalsIgnoreCase(usedLang) && lang.getModule().equalsIgnoreCase(module)) {
					return lang;
				}
			}
			return null;
		}

		public List<String> getLangCodes() {
			langCodes.clear();
			for (XMLLangVersionTag lang : langs)
				if (!langCodes.contains(lang.lang))
					langCodes.add(lang.lang);
			return langCodes;
		}

		public List<XMLLangVersionTag> getLangs() {
			return new ArrayList<XMLLangVersionTag>(langs);
		}

		@Override
		protected void readAttributes(Element element) {
		}

		@Override
		protected void readChild(String childName, Node childNode) {
			if (childName.equalsIgnoreCase("lang")) {
				langs.add(new XMLLangVersionTag(this, childNode).read());
			}
		}

	}

	public static class XMLLangVersionTag extends XMLTag<XMLLangVersionTag> {
		private String lang;
		private int version;
		private String download;
		private String module;

		public XMLLangVersionTag(XMLTag parent, Node node) {
			super(parent, node);
		}

		public String getLang() {
			return lang;
		}

		public String getModule() {
			return module;
		}

		public int getVersion() {
			return version;
		}

		public String getDownload() {
			return download;
		}

		@Override
		protected void readAttributes(Element element) {
			module = element.getAttribute("name");
			lang = element.getAttribute("lang");
			String sVersion = element.getAttribute("version");
			try {
				version = Integer.parseInt(sVersion);
			} catch (NumberFormatException e) {
				PC_Logger.throwing("PC_ThreadLangDownloader.XMLLangVersionTag", "readAttributes()", e);
				PC_ModuleManager.errors.add("PC_ThreadLangDownloader.XMLLangVersionTag error in readAttributes()");
				e.printStackTrace();
			}
			download = element.getTextContent().trim();
		}

		@Override
		protected void readChild(String childName, Node childNode) {

		}

	}

}
