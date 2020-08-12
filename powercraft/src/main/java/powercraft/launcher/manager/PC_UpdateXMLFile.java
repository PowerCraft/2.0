package powercraft.launcher.manager;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Version;

public class PC_UpdateXMLFile {

	public static abstract class XMLTag<T extends XMLTag>{
		protected XMLTag document;
		protected XMLTag parent;
		protected Node node;
		
		protected XMLTag(Node node){
			this.document = this;
			this.parent = null;
			this.node = node;
		}
		
		protected XMLTag(XMLTag parent, Node node){
			this.document = parent.document;
			this.parent = parent;
			this.node = node;
		}
		
		public T read(){
			if(node instanceof Element){
				readAttributes((Element)node);
			}
			readChilds(node.getChildNodes());
			return (T)this;
		}
		
		protected abstract void readAttributes(Element element);
		
		protected void readChilds(NodeList childNods){
			for (int i = 0; i < childNods.getLength(); i++){
				Node childNode = childNods.item(i);
				String childName = childNode.getNodeName();
				readChild(childName, childNode);
			}
		}
		
		protected abstract void readChild(String childName, Node childNode);
		
	}
	
	public static class XMLInfoTag extends XMLTag<XMLInfoTag>{
		private List<XMLModuleTag> modules = new ArrayList<XMLModuleTag>();
		private List<XMLModuleTag> installedModules = new ArrayList<XMLModuleTag>();
		private List<XMLPackTag> packs = new ArrayList<XMLPackTag>();
		private PC_Version powerCraftVersion;
		private XMLMainTag main;
		
		public XMLInfoTag(Node node) {
			super(node);
		}
		
		public PC_Version getPowerCraftVersion(){
			return powerCraftVersion;
		}
		
		public XMLModuleTag getModule(String moduleName) {
			for(XMLModuleTag module:modules){
				if(moduleName.equalsIgnoreCase(module.getName())){
					return module;
				}
			}
			return null;
		}
		
		public XMLPackTag getPack(String packName) {
			for(XMLPackTag pack:packs){
				if(packName.equalsIgnoreCase(pack.getName())){
					return pack;
				}
			}
			return null;
		}
		
		public List<XMLModuleTag> getModules(){
			return new ArrayList<XMLModuleTag>(modules);
		}
		
		public List<XMLModuleTag> getInstalledModules(){
			return new ArrayList<XMLModuleTag>(installedModules);
		}
		
		public List<XMLPackTag> getPacks(){
			return new ArrayList<XMLPackTag>(packs);
		}
		
		public XMLMainTag getMain(){
			return main;
		}
		
		@Override
		protected void readAttributes(Element element) {
			if(PC_LauncherUtils.isDeveloperVersion()){
				powerCraftVersion = new PC_Version(element.getAttribute("devversion"));
			}else{
				powerCraftVersion = new PC_Version(element.getAttribute("version"));
			}
		}

		@Override
		protected void readChild(String childName, Node childNode) {
			if (childName.equalsIgnoreCase("module")){
            	modules.add(new XMLModuleTag(this, childNode).read());
            }else if (childName.equalsIgnoreCase("pack")){
            	packs.add(new XMLPackTag(this, childNode).read());
            }else if (childName.equalsIgnoreCase("main")){
            	main = new XMLMainTag(this, childNode);
            	main.read();
            	modules.add(main);
            }
		}
		
	}
	
	public static class XMLModuleTag extends XMLTag<XMLModuleTag>{
		private String name;
		private List<XMLVersionTag> versions = new ArrayList<XMLVersionTag>();

		public XMLModuleTag(XMLTag parent, Node node) {
			super(parent, node);
		}

		public String getName() {
			return name;
		}
		
		public XMLVersionTag getVersion(PC_Version versionV) {
			for(XMLVersionTag version:versions){
				if(versionV.compareTo(version.getVersion())==0){
					return version;
				}
			}
			return null;
		}
		
		public List<XMLVersionTag> getVersions() {
			return new ArrayList<XMLVersionTag>(versions);
		}
		
		public XMLVersionTag getNewestVersion() {
			if(versions.size()==0)
				return null;
			XMLVersionTag newest = versions.get(0);
			for(XMLVersionTag version:versions){
				if(newest.getVersion().compareTo(version.getVersion())<0){
					newest = version;
				}
			}
			return newest;
		}
		
		@Override
		protected void readAttributes(Element element) {
			name = element.getAttribute("modulename");
		}

		@Override
		protected void readChild(String childName, Node childNode) {
			if (childName.equalsIgnoreCase("version")){
            	versions.add(new XMLVersionTag(this, childNode).read());
            }
			if(PC_LauncherUtils.isDeveloperVersion()){
				if (childName.equalsIgnoreCase("devversion")){
					versions.add(new XMLVersionTag(this, childNode).read());
				}
			}
		}
		
	}
	
	public static class XMLVersionTag extends XMLTag<XMLVersionTag>{
		private PC_Version version;
		private String download;
		private PC_Version superVersion;
		private String info;
		
		public XMLVersionTag(XMLTag parent, Node node) {
			super(parent, node);
		}

		public PC_Version getVersion() {
			return version;
		}

		public String getDownloadLink() {
			return download;
		}
		
		public String getInfo() {
			return info;
		}
		
		@Override
		protected void readAttributes(Element element) {
			superVersion = new PC_Version(element.getAttribute("superversion"));
			version = new PC_Version(element.getAttribute("version"));
			download = element.getAttribute("downloadlink");
			info = element.getTextContent().trim();
		}

		@Override
		protected void readChild(String childName, Node childNode) {}
		
	}
	
	public static class XMLPackTag extends XMLTag<XMLPackTag>{
		private String name;
		private List<XMLChildModuleTag> modules = new ArrayList<XMLChildModuleTag>();
		private List<XMLVersionTag> versions = new ArrayList<XMLVersionTag>();
		
		public XMLPackTag(XMLTag parent, Node node) {
			super(parent, node);
		}

		public String getName() {
			return name;
		}

		public XMLModuleTag getModule(String moduleName) {
			for(XMLChildModuleTag module:modules){
				if(moduleName.equalsIgnoreCase(module.getModule().getName())){
					return module.getModule();
				}
			}
			return null;
		}
		
		public XMLVersionTag getVersion(PC_Version versionV) {
			for(XMLVersionTag version:versions){
				if(versionV.compareTo(version.getVersion())==0){
					return version;
				}
			}
			return null;
		}
		
		public List<XMLModuleTag> getModules(){
			List<XMLModuleTag> list = new ArrayList<XMLModuleTag>();
			for(XMLChildModuleTag module:modules){
				list.add(module.getModule());
			}
			return list;
		}
		
		public List<XMLVersionTag> getVersions() {
			return new ArrayList<XMLVersionTag>(versions);
		}
		
		@Override
		protected void readAttributes(Element element) {
			name = element.getAttribute("packname");
		}

		@Override
		protected void readChild(String childName, Node childNode) {
			if (childName.equalsIgnoreCase("childmodule")){
            	modules.add(new XMLChildModuleTag(this, childNode).read());
            }else if (childName.equalsIgnoreCase("version")){
            	versions.add(new XMLVersionTag(this, childNode).read());
            }else if(PC_LauncherUtils.isDeveloperVersion()){
            	if (childName.equalsIgnoreCase("devversion")){
            		versions.add(new XMLVersionTag(this, childNode).read());
            	}
            }
		}
		
	}
	
	public static class XMLChildModuleTag extends XMLTag<XMLChildModuleTag>{
		private String moduleName;
		
		public XMLChildModuleTag(XMLTag parent, Node node) {
			super(parent, node);
		}

		public XMLModuleTag getModule(){
			return ((XMLInfoTag)document).getModule(moduleName);
		}
		
		@Override
		protected void readAttributes(Element element) {
			moduleName = element.getTextContent();
		}

		@Override
		protected void readChild(String childName, Node childNode) {}
		
	}
	
	public static class XMLMainTag extends XMLModuleTag{

		public XMLMainTag(XMLTag parent, Node node) {
			super(parent, node);
		}

		@Override
		public String getName() {
			return "Api";
		}

		@Override
		protected void readChild(String childName, Node childNode) {
			if(childName.equalsIgnoreCase("Forge")){
				NodeList childNods = childNode.getChildNodes();
				for (int i = 0; i < childNods.getLength(); i++){
					Node childNode2 = childNods.item(i);
					String childName2 = childNode2.getNodeName();
					super.readChild(childName2, childNode2);
				}
			}
		}
		
	}
	
}
