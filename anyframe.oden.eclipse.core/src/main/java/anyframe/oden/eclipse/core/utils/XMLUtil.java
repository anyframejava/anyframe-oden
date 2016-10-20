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
package anyframe.oden.eclipse.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import anyframe.oden.eclipse.core.OdenActivator;

/**
 * Utility class for reading and writing XML files for Anyframe Oden Eclipse plug-in.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0 RC1
 *
 */
public class XMLUtil {

	/**
	 * 
	 * @param xmlRoot
	 * @param xmlFile
	 */
	public static void save(Element xmlRoot, File xmlFile) {
		try {
			XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(xmlFile), OutputFormat.createPrettyPrint());
			xmlWriter.startDocument();
			xmlWriter.write(xmlRoot);
			xmlWriter.endDocument();
			xmlWriter.flush();
			xmlWriter.close();
		} catch (Exception e) {
			OdenActivator.error("Save failed: " + xmlFile.getAbsolutePath(), e);
		}
	}

	/**
	 * 
	 * @param xmlFile
	 * @return
	 */
	public static Element readRoot(File xmlFile) {
		if(!xmlFile.exists()) {
			return null;
		}
		try {
			return readRoot(new FileInputStream(xmlFile));
		} catch (DocumentException t) {
			OdenActivator.error("Unable to load: " + xmlFile.getAbsolutePath(), t);
		} catch (FileNotFoundException fileNotFoundException) {
			// cannot handle exception
		}
		return null;
	}

	/**
	 * 
	 * @param xmlFile
	 * @return
	 * @throws DocumentException
	 */
	public static Element readRoot(InputStream xmlFile) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(xmlFile).getRootElement();
	}

}
