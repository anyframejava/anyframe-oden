/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Using this, you can use java.util.Properties more conveniently.
 * 
 * @author joon1k
 *
 */
public class PropertiesUtil {
	public static Properties loadProperties(String name) 
			throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			File f = new File(name);
			FileUtil.mkdirs(f);
			
			in = new BufferedInputStream(
					new FileInputStream(f));
			prop.load(in);
		}finally {
			try {
				if(in != null) in.close();
			} catch (IOException e) {
			}
		}
		return prop;
		
	}
	
	public static String getKeys(String name) 
			throws FileNotFoundException, IOException {
		StringBuffer buf = new StringBuffer();
		Properties prop = PropertiesUtil.loadProperties(name);
		for(Object key : prop.keySet()){
			buf.append(key.toString() + "\n");
		}
		return buf.toString();
	}
	
	public static void storeProperties(String name, Properties prop) 
			throws FileNotFoundException, IOException {
		OutputStream out = null;
		try {
			File f = new File(name);
			FileUtil.mkdirs(f);
			
			out = new BufferedOutputStream(
					new FileOutputStream(f));
			prop.store(out, null);
		} finally {
			try {
				if(out != null) out.close();
			} catch (IOException e) {
			}
		}
	}

	public static String getProp(String name, String key) 
			throws FileNotFoundException, IOException {
		Properties prop = loadProperties(name);
		return prop.getProperty(key);
	}
	
	public static void addProp(String name, String key, String value) 
			throws FileNotFoundException, IOException {
		Properties prop = loadProperties(name);
		prop.put(key, value);
		storeProperties(name, prop);
	}

	public static void removeProp(String name, String key) 
			throws FileNotFoundException, IOException {
		Properties prop = loadProperties(name);
		prop.remove(key);
		storeProperties(name, prop);
	}

	public static String toString(String name) 
			throws FileNotFoundException, IOException {
		StringBuffer buf = new StringBuffer();
		Properties prop = PropertiesUtil.loadProperties(name);
		Set<Object> keys = prop.keySet();
		for(Object key : keys){
			buf.append(key).append(" = ").append(prop.get(key)).append("\n");
		}
		return buf.toString();
	}
	
	
}
