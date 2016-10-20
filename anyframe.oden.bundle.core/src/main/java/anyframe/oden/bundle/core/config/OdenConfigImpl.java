/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenParseException;
import anyframe.oden.bundle.common.StringUtil;

/**
 * @see anyframe.oden.bundle.core.config.OdenConfigService
 * 
 * @author joon1k
 *
 */
public class OdenConfigImpl implements OdenConfigService {

	protected String CONFIG = "conf/config.xml";
	
	public void addAgent(AgentElement agent) throws OdenException {
		removeAgent(agent.getName());
		List<AgentElement> agents = loadAgentList();
		agents.add(agent);
		storeAgentList(agents);
	}

	public AgentElement getAgent(String name) {
		try{
			for(AgentElement agent : loadAgentList()){
				if(name.equals(agent.getName()) && isValid(agent)){
					return agent;
				}
			}
		}catch(Exception e){
		}
		return null;
	}
	
	private boolean isValid(AgentElement agent) {
		if(StringUtil.empty(agent.getName()) ||
				StringUtil.empty(agent.getAddr()) ||
				StringUtil.empty(agent.getPort()) ||
				agent.getDefaultLoc() == null ||
				agent.getBackupLoc() == null)
			return false;
		return true;
	}

	public String getBackupLocation(String agentName) throws OdenException {
		AgentElement a = getAgent(agentName);
		if(a != null){
			AgentLocation l = a.getBackupLoc();
			if(l != null)
				return l.getValue();
		}
		throw new OdenException("Couldn't find any backup location from config.xml.");
	}

	public List<String> getAgentNames() throws OdenException {
		List<String> names = new ArrayList<String>();
		for(AgentElement agent : loadAgentList()){
			names.add(agent.getName());
		}
		return names;
	}

	public void removeAgent(String name) throws OdenException {
		List<AgentElement> agents = loadAgentList();
		for(AgentElement agent : agents){
			if(agent.getName().equals(name)){
				agents.remove(agent);
				break;
			}
		}
		storeAgentList(agents);
	}
	
	protected List<AgentElement> loadAgentList() throws OdenException{
		List<AgentElement> agents = new ArrayList<AgentElement>();
		InputStream in = null;
		
		try{
			XmlPullParser parser = new KXmlParser();
			in = new FileInputStream(new File(CONFIG));
			try {
				parser.setInput(in, "UTF8");
				
				int eventType;
				// <oden>
				if(parser.nextTag() == XmlPullParser.START_TAG){
					parser.require(XmlPullParser.START_TAG, null, "oden");
					
					// <agents>
					if(parser.nextTag() == XmlPullParser.START_TAG){
						parser.require(XmlPullParser.START_TAG, null, "agents");
						
						// <agent> or </agents>
						eventType = parser.nextTag();
						while(eventType == XmlPullParser.START_TAG){	
							parser.require(XmlPullParser.START_TAG, null, "agent");
							
							AgentElement agent = new AgentElement();
							agents.add(agent);
							agent.setName(getAttributeValue(parser, "name"));
							
							eventType = parser.nextTag();	// <addr> or </agent>
							if(eventType == XmlPullParser.START_TAG){	// <addr>..
								do{
									String tag = parser.getName();
									if(tag.equals("address")){
										agent.setHost(getAttributeValue(parser, "host"));
										agent.setPort(getAttributeValue(parser, "port"));
									}else if(tag.equals("default-location")){
										agent.setDefaultLoc(getAttributeValue(parser, "value"));
									}else if(tag.equals("backup-location")){
										agent.setBackupLoc(getAttributeValue(parser, "value"));
									}else if(tag.equals("location")){
										agent.addLoc(
												getAttributeValue(parser, "name"),
												getAttributeValue(parser, "value"));
									}
									eventType = parser.nextTag();		// <.../>
								} while(parser.nextTag() == XmlPullParser.START_TAG);	// <addr>... or </agent>
							}
							eventType = parser.nextTag();	// <agent> or </agents>
						}
					}
					
				}
			} catch (IOException e) {
				throw new OdenException("Fail to parse: " + CONFIG);
			} catch (XmlPullParserException e) {
				throw new OdenException("Fail to parse: " + CONFIG);
			}
		} catch (FileNotFoundException e) {
			throw new OdenException(e);
		}finally {
			try { if(in != null) in.close(); } catch (IOException e) {}
		}
		return agents;
	}
	
	protected String getAttributeValue(XmlPullParser parser, String attrib) 
			throws OdenParseException {
		String name = parser.getAttributeValue(null, attrib);
		if(name == null) 
			throw new OdenParseException(parser.getPositionDescription());
		return name;
	}
	
	protected void storeAgentList(List<AgentElement> agents) 
			throws OdenException {
		PrintWriter writer = null;
		try {
			File configFile = new File(CONFIG);
			writer = new PrintWriter(
					new FileOutputStream(configFile));
			
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<oden>");
			writer.println("\t<agents>");
			for(AgentElement agent : agents){
				writer.println("\t\t<agent name=\"" + agent.getName() + "\">");
				writer.println("\t\t\t<address host=\"" + agent.getHost() + "\" port=\"" + agent.getPort() + "\"/>");
				writer.println("\t\t\t<default-location value=\"" + agent.getDefaultLoc() + "\"/>");
				for(String locName : agent.getLocNames()){
					writer.println("\t\t\t<location name=\"" + locName + "\" value=\"" + agent.getLoc(locName) + "\"/>");
				}
				writer.println("\t\t</agent>");
			}
			writer.println("\t</agents>");
			writer.println("</oden>");
		} catch (FileNotFoundException e) {
			throw new OdenException(e);
		} finally {
			if(writer != null)
				writer.close();
		}
	}
	
}
