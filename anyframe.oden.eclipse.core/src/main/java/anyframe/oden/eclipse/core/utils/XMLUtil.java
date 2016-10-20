/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
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
