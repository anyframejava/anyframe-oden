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
package anyframe.oden.bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

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
	
	private final static int AGENT_PORT = 9862;
	
	private static Bundle framework = null;
	
	private static String SHELL_PORT;
	
    public static void main(String[] args) throws Exception
    {
        try
        {            
            printWelcome();
            
            File home = new File(Felix.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
            startFramework(setFelixProperties(new File(home, CONFIG_FILE), new File(home, CACHE), args));
            
            installBundles(new File(home, BUNDLE_LOC)); 
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

    /**
     * method to log running threads' stacks when the process is died.
     * 
     * @param cache
     */
	private static void addShutdownHook(final File cache) {
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				StringBuffer buf = new StringBuffer();
				buf.append("#" + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis()) + "\n");
				buf.append("Memory usage: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000) +"kb\n");
				buf.append("Thread: " + Thread.activeCount() + " threads are running.\n");
				Map<Thread, StackTraceElement[]> ttraces = Thread.getAllStackTraces();
				for(Thread t : ttraces.keySet()){
					buf.append("#" + t.getId() + "\n");
					for(StackTraceElement ele : ttraces.get(t)){
						buf.append(ele + "\n");
					}
				}
				
				PrintStream out = null;
				try{
					out = new PrintStream(new FileOutputStream(new File(cache, "lastlog.log")));
					out.println(buf.toString());
				}catch(Exception e){
					// ignore
				}
				if(out != null) out.close();
			}
		});
	}

	private static void printWelcome() {
        System.out.println("\n::: Welcome to Anyframe Oden :::");
        System.out.println("==============================\n");
	}

	/**
	 * method to start a embedded osgi framework
	 * 
	 * @param props
	 * @return
	 * @throws BundleException
	 */
	public static Bundle startFramework(Map props) throws BundleException {
		framework = new Felix(props);
		framework.start();
		return framework;
	}

	@SuppressWarnings("unchecked")
	private static Map setFelixProperties(File config, File cache, String... args) throws Exception{
        Map configMap = new StringMap(false);
        
        // This information is from felix 1.8.0
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
            "org.osgi.framework; version=1.4.0," +
            "org.osgi.service.packageadmin; version=1.2.0," +
            "org.osgi.service.startlevel; version=1.1.0," +
            "org.osgi.service.url; version=1.0.0," +
            "org.osgi.util.tracker; version=1.3.3");
        configMap.put(BundleCache.CACHE_ROOTDIR_PROP, cache.getPath());
        configMap.put("osgi.shell.telnet", "on");
        configMap.put("obr.repository.url", "http://felix.apache.org/obr/releases.xml");
        configMap.put("org.osgi.framework.storage.clean", "onFirstInit");
        
        Properties thirdProp = loadINI(config);
        if(thirdProp != null){
        	configMap.putAll(thirdProp);
        	if(thirdProp.getProperty("agent.port") == null)
        		configMap.put("agent.port", String.valueOf(AGENT_PORT));
        }else {
        	// get port from args or 9862
            configMap.put("agent.port", String.valueOf(getPort(args)));
        }
        
        replaceKey(configMap, "agent.port", "ch.ethz.iks.r_osgi.port");
        replaceKey(configMap, "log.level", "felix.log.level");
        replaceKey(configMap, "http.port", "org.osgi.service.http.port");
        replaceKey(configMap, "shell.ip", "osgi.shell.telnet.ip");
        Object o = configMap.get("shell.port");
        SHELL_PORT = o == null ? null : o.toString();
        replaceKey(configMap, "shell.port", "osgi.shell.telnet.port");
        replaceKey(configMap, "shell.maxconn", "osgi.shell.telnet.maxconn");
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
    	return AGENT_PORT;
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
    
    private static void installBundles(File loc) throws IOException, BundleException{
    	File[] files =loc.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
    	for(File file : files){
    		String name = file.getName();
    		if(file.isFile() && !"felix.jar".equals(name)){
    			framework.getBundleContext().installBundle("file:"+loc.getPath()+"/"+name);
    		}	
    	}

    	String[] libraries = libBundles();
    	if(libraries == null){		// start all bundles if not defined.
    		for(Bundle bnd : framework.getBundleContext().getBundles()) {
    			bnd.start();
    		}
    	}else { 	// start bundles which are not belonging to lib
    		for(Bundle bnd : framework.getBundleContext().getBundles()) {
    			if(contains(libraries, bnd))
    				continue;
    			bnd.start();
	    	}
    	}

    	for(Bundle bnd : framework.getBundleContext().getBundles()) {
    		if(SHELL_PORT != null &&
    				bnd.getSymbolicName().equals("org.apache.felix.org.apache.felix.shell.remote") &&
    				bnd.getState() == Bundle.ACTIVE){
    			String ip = framework.getBundleContext().getProperty("osgi.shell.telnet.ip");
    			System.out.println("::: You can access Oden by Telnet. (e.g. telnet " 
    					+ (ip == null ? "localhost" : ip) + " " + SHELL_PORT +") :::\n");
    		}
    	}
    }
    
    private static boolean contains(String[] list, Bundle b){
    	for(String s : list){
			if(b.getLocation().endsWith(s))
				return true;
		}
    	return false;
    }

    private static String[] libBundles() {
    	String bnds = framework.getBundleContext().getProperty("bundle.libs");
    	if(bnds == null) return null;
    	return bnds.split("\\s");
    }
    
    private static Properties loadINI(File iniFile) throws Exception{
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
				throw new Exception("Illegal format error: " + iniFile);
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
