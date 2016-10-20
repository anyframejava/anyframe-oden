/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import sun.misc.JarFilter;

/**
 * OSGi Framework Launcher. Lauch OSGi Framework, start some bundles
 * and put some properties to the OSGi Context.
 * 
 * @author joon1k
 *
 */
public class Oden {
	private final static String CONFIG_FILE = "conf/oden.ini";
	
	private final static String CACHE = "meta";
	
	private final static String BUNDLE_LOC = "bundle";
	
	private static Bundle framework = null;
	
    public static void main(String[] args) throws Exception
    {
        try
        {            
            printWelcome();
            
            startFramework(setFelixProperties(args));
            
            installBundles(BUNDLE_LOC);
        } catch (BundleException e) {
            System.err.println("Could not create framework: " + e);
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
        	System.err.println("Could not create framework: " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

	private static void printWelcome() {
        System.out.println("\n::: Welcome to Anyframe Oden :::");
        System.out.println("==============================\n");
	}

	public static Bundle startFramework(Map props) throws BundleException {
		framework = new Felix(props);
		framework.start();
		return framework;
	}

	@SuppressWarnings("unchecked")
	private static Map setFelixProperties(String... args) throws Exception{
        Map configMap = new StringMap(false);
        
        // This information is from felix 1.8.0
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
            "org.osgi.framework; version=1.4.0," +
            "org.osgi.service.packageadmin; version=1.2.0," +
            "org.osgi.service.startlevel; version=1.1.0," +
            "org.osgi.service.url; version=1.0.0," +
            "org.osgi.util.tracker; version=1.3.3");
        configMap.put(BundleCache.CACHE_ROOTDIR_PROP, CACHE);
        configMap.put("osgi.shell.telnet", "on");
        configMap.put("obr.repository.url", "http://felix.apache.org/obr/releases.xml");
        configMap.put("osgi.shell.telnet.ip", InetAddress.getLocalHost().getHostAddress());
        configMap.put("org.osgi.framework.storage.clean", "onFirstInit");
        
        Properties thirdProp = loadINI(CONFIG_FILE);
        if(thirdProp != null){
        	configMap.putAll(thirdProp);
        }
        
        int agentPort = getPort(args);
        if(agentPort > 0)
        	configMap.put("agent.port", String.valueOf(agentPort));
        replaceKey(configMap, "agent.port", "ch.ethz.iks.r_osgi.port");
        replaceKey(configMap, "log.level", "felix.log.level");
        replaceKey(configMap, "http.port", "org.osgi.service.http.port");
        replaceKey(configMap, "shell.port", "osgi.shell.telnet.port");
        return configMap;
        
    }
    
    private static int getPort(String[] args) {
    	try{
	    	int idx = Arrays.binarySearch(args, "-port");
	    	if(idx != -1 && args.length > idx+1)
	    		return Integer.parseInt(args[idx+1]);
    	}catch(Exception e) {
    		// ignore
    	}
    	return -1;
	}
	
	/**
     * replace map's key
     * @param map object containing key
     * @param key0 target key to be replaced
     * @param key1 destination key 
     */
    private static void replaceKey(Map map, String key0, String key1){
    	if(map.containsKey(key0)){
    		map.put(key1, map.get(key0));
    		map.remove(key0);
    	}
    	
    }
    
    private static void installBundles(String loc) throws IOException, BundleException{
    	File[] files =new File(loc).listFiles(new JarFilter());
    	for(File file : files){
    		String name = file.getName();
    		if(file.isFile() && !"felix.jar".equals(name)){
    			framework.getBundleContext().installBundle("file:"+loc+"/"+name);
    		}	
    	}
    	
    	String[] bundlesToStart = defaultBundles();
    	if(bundlesToStart == null){		// not defined...
    		for(Bundle bnd : framework.getBundleContext().getBundles()) {
    				bnd.start();
    		}
    	}else {
	    	for(String defname : defaultBundles()) {
	    		for(Bundle bnd : framework.getBundleContext().getBundles()) {
	    			if(bnd.getSymbolicName().equals(defname))
	    				bnd.start();
	    		}
	    	}
    	}
    	
    }

    private static String[] defaultBundles() {
    	String bnds = framework.getBundleContext().getProperty("bundle.start");
    	if(bnds == null) return null;
    	return bnds.split("\\s");
    }
    
    private static Properties loadINI(String ini) throws Exception{
		File iniFile = new File(ini); 
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(iniFile));
		}catch(FileNotFoundException e){
			return null;
		}
		
		Properties prop = null;
		if(in != null){
			prop = new Properties();
			try{
				prop.load(in);
			}catch(Exception e){
				throw new Exception("Illegal format error: " + ini);
			}
			in.close();
		}
		return prop;
		
	}
    
    public static void stopFramework() throws BundleException{
    	framework.stop();
    }
    
    public static Bundle framework(){
    	return framework;
    }

}
