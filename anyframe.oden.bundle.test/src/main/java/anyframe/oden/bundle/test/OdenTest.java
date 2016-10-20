package anyframe.oden.bundle.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.felix.framework.Felix;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

import anyframe.common.bundle.test.OSGiTest;


public abstract class OdenTest extends OSGiTest {
	public final static String BUNDLE_ROOT_PROP = "oden.bundle.root";
	
	public final static String LOCAL_BUNDLE_LOC = "../anyframe.oden.bundle/bundle";
	
	public final static String GATE_BND = "anyframe.common.bundle.gate";
	
	public final static String LOG_BND = "anyframe.common.bundle.log";
	
	public final static String ODEN_PREFS_BND = "anyframe.oden.bundle.prefs";
	
	public final static String ODEN_DEPLOY_BND = "anyframe.oden.bundle.deploy";
	
	public final static String ODEN_REPOSITORY_BND = "anyframe.oden.bundle.repository";
	
	public final static String ODEN_CORE_BND = "anyframe.oden.bundle.core";
	
	
	@Override
	final protected String bundleRootpath() {
		return System.getProperty(BUNDLE_ROOT_PROP, LOCAL_BUNDLE_LOC);
	}

	@Override
	final protected String[] bundlesToStart() {
		String[] bundleOrder = new String[]{
				"anyframe.common.bundle.gate",
//				"org.apache.felix.shell.tui",
//				"org.apache.felix.org.apache.felix.shell.remote",
				"org.apache.felix.scr",
				"org.apache.felix.log",
				"anyframe.common.bundle.log",
				"org.apache.felix.http.jetty",
				"org.apache.felix.prefs",
				"anyframe.oden.bundle.prefs",
				"ch.ethz.iks.r_osgi.remote",
				"anyframe.oden.bundle.deploy",
				"anyframe.oden.bundle.repository",
				"anyframe.oden.bundle.core"
			};
		
		for(int i=0; i<bundleOrder.length; i++){
			if(bundleOrder[i].equals(testBundle())){
				String[] x = new String[i+1];
				System.arraycopy(bundleOrder, 0, x, 0, i+1);
				return x;
			}
		}
		return bundleOrder;
	}

	private String[] concat(String[] a0, String[] a1) {
		String[] x = new String[a0.length+a1.length];
		System.arraycopy(a0, 0, x, 0, a0.length);
		System.arraycopy(a1, 0, x, a0.length, a1.length);
		return x;
	}
	
	abstract protected String testBundle();
	
//	abstract protected String[] requiredBundles();

	@Override
	final protected Framework startFramework() throws BundleException {
		createConfigFile();
		Framework fwk = new Felix(odenProperties());
		fwk.start();
		return fwk;
	}

	protected void createConfigFile() throws BundleException {
		FileOutputStream fout = null;
		try{
			File conf = new File("conf", "config.xml");
			PrintStream stream = new PrintStream(fout = new FileOutputStream(conf));
			stream.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			stream.println("<oden>");
			stream.println("<agents>");
			stream.println("<agent name=\"agent0\">");
			stream.println("<address host=\"localhost\" port=\"10009\"/>");
			stream.println("<default-location value=\"" + agentPath() + "/tttdest\"/>");
			stream.println("<location name=\"path01\" value=\"" + agentPath0() + "/tttdest01\"/>");
			stream.println("<location name=\"path02\" value=\"" + agentPath1() + "/tttdest02\"/>");
			stream.println("</agent>");
			stream.println("</agents>");
			stream.println("</oden>");
		}catch(IOException e) {
			throw new BundleException(e.getMessage());
		}finally {
			try{ if(fout != null) fout.close(); } catch(IOException e) { }
		}
	}
	
	protected String agentPath(){
		return new File("tttdest").getAbsolutePath().replaceAll("\\\\","/");
	}
	
	protected String agentPath0(){
		return new File("tttdest0").getAbsolutePath().replaceAll("\\\\","/");
	}

	protected String agentPath1(){
		return new File("tttdest1").getAbsolutePath().replaceAll("\\\\","/");
	}

	protected String srcPath(){
		File f = new File("tttsrc");
		if(!f.exists()) f.mkdirs();
		return f.getAbsolutePath().replaceAll("\\\\", "/");
		
	}
	
	protected Map odenProperties() {
		Map props = new TreeMap<String, String>();
		props.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
	            "org.osgi.framework; version=1.4.0," +
	            "org.osgi.service.packageadmin; version=1.2.0," +
	            "org.osgi.service.startlevel; version=1.1.0," +
	            "org.osgi.service.url; version=1.0.0," +
	            "org.osgi.util.tracker; version=1.3.3");
		props.put("felix.cache.rootdir", "meta");
		props.put("org.osgi.framework.storage.clean", "onFirstInit");
		props.put("osgi.shell.telnet", "off");
		try {
			props.put("osgi.shell.telnet.ip", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
		}
		props.put("ch.ethz.iks.r_osgi.port", "10009");
		props.put("felix.log.level", "0");
		props.put("org.osgi.service.http.port", "10007");
		props.putAll(bundleProperties());
		return props;
	}

	protected Map bundleProperties(){
		return Collections.EMPTY_MAP;
	}
	
//	@Test
	public void testOden() throws InvalidSyntaxException{
		for(Bundle b : context.getBundles())
			System.out.println(b.getSymbolicName() + ";" + state(b.getState()));
		
		for(ServiceReference ref : context.getAllServiceReferences(null, null)){
			System.out.println(context.getService(ref).getClass());
		}
	}
	
	private String state(int state) {
		switch(state){
		case Bundle.ACTIVE:
			return "ACTIVE";
		case Bundle.INSTALLED:
			return "INSTALLED";
		case Bundle.RESOLVED:
			return "RESOLVED";
		default:
			return "UNKNOWN";
		}
	}
}
