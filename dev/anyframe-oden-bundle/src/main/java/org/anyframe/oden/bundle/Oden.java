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
package org.anyframe.oden.bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * OSGi Framework Launcher. Lauch OSGi Framework, start some bundles and put
 * some properties to the OSGi Context.
 * 
 * @author Junghwan Hong
 */
public class Oden {
	private static String configFile = "conf/oden.ini";

	private static String cache = "meta";

	private final static String bundleLoc = "bundle";

	private final static int agentPort = 9872;

	private static Bundle odenFramework = null;

	private static String shellPort;

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			configFile = args[0];
			cache = "meta-agent";
		}

		try {
			printWelcome();

			File home = new File(Felix.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI()).getParentFile()
					.getParentFile();
			startFramework(setFelixProperties(new File(home, configFile),
					new File(home, cache)));

			installBundles(new File(home, bundleLoc));
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

	/**
	 * method to start a embedded osgi framework
	 * 
	 * @param props
	 * @return
	 * @throws BundleException
	 */
	public static Bundle startFramework(Map props) throws BundleException {
		odenFramework = new Felix(props);
		odenFramework.start();
		return odenFramework;
	}

	@SuppressWarnings("unchecked")
	private static Map setFelixProperties(File config, File cache)
			throws Exception {
		Map configMap = new StringMap(false);

		// This information is from felix 1.8.0
		// configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
		//   "org.osgi.framework; version=1.4.0," +
		//   "org.osgi.service.packageadmin; version=1.2.0," +
		// 	 "org.osgi.service.startlevel; version=1.1.0," +
		//   "org.osgi.service.url; version=1.0.0," +
		//   "org.osgi.util.tracker; version=1.3.3");
		configMap.put(BundleCache.CACHE_ROOTDIR_PROP, cache.getPath());
		configMap.put("osgi.shell.telnet", "on");
		configMap.put("obr.repository.url",
				"http://felix.apache.org/obr/releases.xml");
		configMap.put("org.osgi.framework.storage.clean", "onFirstInit");
		// configMap.put("org.osgi.framework.system.packages.extra",
		// "javax.naming, javax.naming.spi");

		Properties thirdProp = loadINI(config);
		if (thirdProp != null) {
			configMap.putAll(thirdProp);
			if (thirdProp.getProperty("http.port") == null) { // if there's no http.port
				configMap.put("http.port", String.valueOf(agentPort));
			}
		}

		// override properties from program's arguments
		// args2map(configMap, args);

		replaceKey(configMap, "log.level", "felix.log.level");
		replaceKey(configMap, "http.port", "org.osgi.service.http.port");
		replaceKey(configMap, "shell.ip", "osgi.shell.telnet.ip");
		Object o = configMap.get("shell.port");
		shellPort = o == null ? null : o.toString();
		replaceKey(configMap, "shell.port", "osgi.shell.telnet.port");
		replaceKey(configMap, "shell.maxconn", "osgi.shell.telnet.maxconn");
		return configMap;
	}

	/**
	 * replace map's key
	 * 
	 * @param map
	 *            object containing key
	 * @param key0
	 *            target key to be replaced
	 * @param key1
	 *            destination key
	 */
	private static void replaceKey(Map map, String key0, String key1) {
		if (map.containsKey(key0)) {
			map.put(key1, map.get(key0));
			map.remove(key0);
		}

	}

	private static void installBundles(File loc) throws IOException,
			BundleException {
		// install all bundles in loc folder
		File[] files = loc.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		for (File file : files) {
			String name = file.getName();
			if (file.isFile() && !"felix.jar".equals(name)) {
				odenFramework.getBundleContext().installBundle(
						"file:" + loc.getPath() + "/" + name);
			}
		}

		String[] libraries = libBundles();
		Bundle core_bnd = null;
		Bundle job_bnd = null;
		// start bundles which are not belonging to lib
		for (Bundle bnd : odenFramework.getBundleContext().getBundles()) {
			if (contains(libraries, bnd)) {
				continue;
			}
			if(bnd.getLocation().contains("anyframe-oden-bundle-core")){
				core_bnd = bnd;
				continue;
			}
			if(bnd.getLocation().contains("anyframe-oden-bundle-job")){
				job_bnd = bnd;
				continue;
			}
			bnd.start();
		}
		
		if(job_bnd != null) {
			job_bnd.start();
		}
		if(core_bnd != null) {
			core_bnd.start();
		}

		// if remote shell is loaded, show some guides.
		for (Bundle bnd : odenFramework.getBundleContext().getBundles()) {
			if (shellPort != null
					&& bnd.getSymbolicName().equals(
							"org.apache.felix.org.apache.felix.shell.remote")
					&& bnd.getState() == Bundle.ACTIVE) {
				String ip = odenFramework.getBundleContext().getProperty(
						"osgi.shell.telnet.ip");
				System.out
						.println("::: You can access Oden by Telnet. (e.g. telnet "
								+ (ip == null ? "localhost" : ip)
								+ " "
								+ shellPort + ") :::\n");
			}
		}
	}

	private static boolean contains(String[] list, Bundle b) {
		for (String s : list) {
			if (b.getLocation().endsWith(s)) {
				return true;
			}
		}
		return false;
	}

	private static String[] libBundles() {
		String bnds = odenFramework.getBundleContext().getProperty("bundle.libs");
		if (bnds == null) {
			return new String[0];
		}
		return bnds.split("\\s");
	}

	private static Properties loadINI(File iniFile) throws Exception {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(iniFile));
		} catch (FileNotFoundException e) {
			return null;
		}

		Properties prop = null;
		if (in != null) {
			prop = new Properties();
			try {
				prop.load(in);
			} catch (Exception e) {
				throw new Exception("Illegal format error: " + iniFile);
			}
			in.close();
		}
		return prop;

	}

	public static void stopFramework() throws BundleException {
		odenFramework.stop();
	}

	public static Bundle framework() {
		return odenFramework;
	}

}