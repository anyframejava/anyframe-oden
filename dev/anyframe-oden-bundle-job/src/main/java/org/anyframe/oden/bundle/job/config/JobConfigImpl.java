package org.anyframe.oden.bundle.job.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.StringUtil;

public class JobConfigImpl implements JobConfigService{
	private final File CONFIG_FILE;
	private long loadtime = -1;
	Document doc;
	
	public JobConfigImpl(){
		this.CONFIG_FILE = new File(BundleUtil.odenHome(), "conf/jobs.xml");
	}
	
	public synchronized void addJob(CfgJob job) throws Exception{
		parse();
		
		Element gnode = doc.createElement("job");
		gnode.setAttribute("name", notNull(job.getName()));
		
		// source
		CfgSource source = job.getSource();
		if(source == null)
			throw new Exception("source is required.");
		gnode.appendChild(createSourceNode(source));
		
		// targets
		for(CfgTarget target : job.getTargets()){
			gnode.appendChild(createTargetNode(target));
		}
		
		// commands
		for(CfgCommand command : job.getCommands()){
			gnode.appendChild(createCommandNode(command));
		}
		
		Element target = getJobNode(job.getName());
		if(target == null){
			doc.getDocumentElement().appendChild(gnode);
		}else {
			doc.getDocumentElement().replaceChild(gnode, target);
		}
		
		store();
	}
	
	private Element getJobNode(String name){
		Element root = doc.getDocumentElement();
		NodeList childs = root.getChildNodes();
		for(int i=0; i<childs.getLength(); i++){
			Node node = childs.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element job = (Element) node;
			if(name.equals(job.getAttribute("name")))
				return job;
		}
		return null;
	}

	private Node createCommandNode(CfgCommand command) {
		Element cnode = doc.createElement("command");
		cnode.setAttribute("name", notNull(command.getName()));
		cnode.setAttribute("command", notNull(command.getCommand()));
		cnode.setAttribute("dir", notNull(command.getPath()));
		return cnode;
	}

	private Element createTargetNode(CfgTarget target) {
		Element tnode = doc.createElement("target");
		tnode.setAttribute("name", notNull(target.getName()));
		tnode.setAttribute("address", notNull(target.getAddress()));
		tnode.setAttribute("dir", notNull(target.getPath()));
		return tnode;
	}
	
	private Element createSourceNode(CfgSource source) throws Exception {
		Element srcnode = doc.createElement("source");
		srcnode.setAttribute("dir", notNull(source.getPath()));
		if(source.getExcludes().size() != 0)
			srcnode.setAttribute("excludes", 
					StringUtil.collectionToString(source.getExcludes()));
		
		for(CfgMapping mapping : source.getMappings()){
			Element subnode = doc.createElement("mapping");
			subnode.setAttribute("dir", mapping.getDir());
			subnode.setAttribute("checkout-dir", mapping.getCheckoutDir());
			srcnode.appendChild(subnode);
		}
		return srcnode;
	}
	
	private String notNull(String s){
		return s == null ? "" : s;
	}
		
	public synchronized void removeJob(String name) throws Exception{
		parse();
		
		Element root = doc.getDocumentElement();
		NodeList childs = root.getChildNodes();
		
		for(int i=0; i<childs.getLength(); i++){
			Node node = childs.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element job = (Element) node;
			if(name.equals(job.getAttribute("name"))){
				root.removeChild(job);
				break;
			}
		}
		
		store();
	}
	
	public synchronized List<String> listJobs() throws Exception{
		parse();

		List<String> jobs = new ArrayList<String>();
		NodeList childs = doc.getDocumentElement().getChildNodes();
		
		for(int i=0; i<childs.getLength(); i++){
			Node node = childs.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element job = (Element) node;
			jobs.add(job.getAttribute("name"));
		}
		return jobs;
	}
	
	public synchronized CfgJob getJob(String name) throws Exception{
		if(StringUtil.empty(name)) return null;
		
		parse();
		
		NodeList childs = doc.getDocumentElement().getChildNodes();
		
		for(int i=0; i<childs.getLength(); i++){
			Node node = childs.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element job = (Element) node;
			if(name.equals(job.getAttribute("name")) ){
				return new CfgJob(name, 
						getSource(job.getElementsByTagName("source")), 
						getTargets(job.getElementsByTagName("target")), 
						getCommands(job.getElementsByTagName("command")) );
			}
		}
		return null;
	}
	
	private List<CfgCommand> getCommands(NodeList nodes) throws Exception {
		List<CfgCommand> cmds = new ArrayList<CfgCommand>();
		for(int i=0; i<nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element ele = (Element) node;
			CfgCommand cmd = new CfgCommand(reqAttrib(ele, "name"), 
					reqAttrib(ele, "command"), 
					reqAttrib(ele, "dir").replaceAll("\\\\", "/"));
			cmds.add(cmd);
		}
		return cmds;
	}

	private List<CfgTarget> getTargets(NodeList nodes) throws Exception {
		List<CfgTarget> cmds = new ArrayList<CfgTarget>();
		for(int i=0; i<nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element ele = (Element) node;
			CfgTarget cmd = new CfgTarget(reqAttrib(ele, "name"), 
					reqAttrib(ele, "address"), 
					reqAttrib(ele, "dir").replaceAll("\\\\", "/"));
			cmds.add(cmd);
		}
		if(cmds.size() == 0)
			throw new Exception("at least 1 target is required.");
		return cmds;
	}
	
	private String reqAttrib(Element ele, String name) throws Exception{
		String attrib = ele.getAttribute(name);
		if(attrib == null || "".equals(attrib))
			throw new Exception(ele.getTagName() + "\'s "+name + " attribute is required.");
		return attrib;
	}
	
	private CfgSource getSource(NodeList nodes) throws Exception {
		if(nodes.getLength() != 1)
			throw new Exception("only one source is allowed.");
		
		Node node = nodes.item(0);
		if(node.getNodeType() != Node.ELEMENT_NODE)
			throw new Exception("source is required.");
		Element ele = (Element) node;
		
		return new CfgSource(reqAttrib(ele, "dir").replaceAll("\\\\", "/"),
				ele.getAttribute("excludes"),
				getMappings(ele.getChildNodes()));
	}
	
	private List<CfgMapping> getMappings(NodeList nodes) throws Exception{
		List<CfgMapping> ret = new ArrayList<CfgMapping>();
		for(int i=0; i<nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element ele = (Element) node;
			String dir = reqAttrib(ele, "dir").replaceAll("\\\\", "/");
			String checkoutDir = reqAttrib(ele, "checkout-dir").replaceAll("\\\\", "/");
			ret.add(new CfgMapping(dir, checkoutDir));
		}
		return ret;
	}

	private void parse() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		if(!CONFIG_FILE.exists())
			createNewFile();
		
		if(loadtime == CONFIG_FILE.lastModified())
			return;
		
		doc = builder.parse(CONFIG_FILE);
		loadtime = CONFIG_FILE.lastModified();	
	}
	
	private void createNewFile() throws IOException {
		CONFIG_FILE.createNewFile();
		
//		PrintWriter writer = null;
		OutputStreamWriter writer = null;
		try {
			  
//			writer = new PrintWriter(new FileOutputStream(CONFIG_FILE));
//			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//			writer.println("<oden>");
//			writer.println("</oden>");
			
			writer = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), "UTF-8");
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.write("<oden>");
			writer.write("</oden>");
		} finally {
			if(writer != null) writer.close();
		}
	}

	private void store() throws TransformerFactoryConfigurationError, TransformerException, IOException{
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute("indent-number", 2);
		Transformer trans = factory.newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		BufferedWriter w = null;
		try{
			w =new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), "utf-8"));
			trans.transform(new DOMSource(doc.getDocumentElement()), new StreamResult(w));
			loadtime = CONFIG_FILE.lastModified();
		}finally{
			if(w!=null) w.close();
		}
	}
}
