/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd.
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
package anyframe.oden.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author LEE Sujeong
 *
 */
public class AgentInfoUtil {

	public ArrayList getAgents(String strFile, String id) {
		ArrayList list = new ArrayList();
		File file = new File(strFile);
		ArrayList idList = OdenAntUtil.parseArg(id);
		for (int i = 0; i < idList.size(); i++) {
			ArrayList temp = parseAgentsFile(file, idList.get(i) + "");
			list.addAll(temp);
		}
		return list;
	}

	private ArrayList parseAgentsFile(File file, String id) {
		ArrayList resultList = new ArrayList();
		Document document = getDocument(file);

		NodeList list = document.getDocumentElement().getElementsByTagName(
				"group");

		for (int i = 0; i < list.getLength(); i++) {
			Element node = (Element) list.item(i);
			if (node == null)
				break;
			if (list.item(i).getAttributes().getNamedItem("id").getNodeValue()
					.equals(id)) {
				Node group = list.item(i);
				NodeList children = group.getChildNodes();

				for (int j = 0; j < children.getLength(); j++) {
					Node agent = children.item(j);
					if (agent.hasAttributes()) {
						String name = getAgentAttribute(agent, "name");
						String path = getAgentAttribute(agent, "path");
						String locvar = getAgentAttribute(agent, "locvar");

						Agent info = new Agent();
						info.setName(name);
						info.setPath(path);
						info.setLocvar(locvar);

						resultList.add(info);
					} else {
					}
				}
			}
		}

		return resultList;
	}

	private String getAgentAttribute(Node agent, String nodeName) {
		String result = "";
		NamedNodeMap nodeMap = agent.getAttributes();
		if (nodeMap == null || nodeMap.getLength() == 0) {
			result = "";
		} else {
			Node node = nodeMap.getNamedItem(nodeName);
			if (node == null || node.getNodeValue().equals("")) {
				result = "";
			} else {
				result = node.getNodeValue();
			}
		}
		return result;
	}

	private Document getDocument(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document document = null;
			try {
				builder = factory.newDocumentBuilder();
				document = builder.parse(file);
			} catch (ParserConfigurationException e) {
				OdenAntUtil.buildFailMsg("ParserConfigurationException", e);
			} catch (SAXException e) {
				OdenAntUtil.buildFailMsg("SAXException", e);
			} catch (IOException e) {
				OdenAntUtil.buildFailMsg("IOException", e);
			}
		return document;
	}

}
