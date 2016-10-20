package anyframe.common.bundle.test;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.JUnitCore;
import org.junit.runners.JUnit4;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;

public abstract class OSGiTest {

	private Framework fwk;
	
	protected BundleContext context;
	
	@Before
	public void _before() {
		try{
			if(fwk == null){
				// start felix: required felix.jar in the classpath
				fwk = startFramework();
				
				if(fwk == null)
					throw new Exception("Fail to run the testcase. fwk shouldn't be null.");
				
				context = fwk.getBundleContext();
				
				// get bundle location from system property
				startBundles();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void startBundles() throws BundleException, InterruptedException {		
		// install bundles in the bundle root path
		String brpath = bundleRootpath();
		File[] files =new File(brpath).listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
    	for(File file : files){
    		String name = file.getName();
    		if(file.isFile() && !"felix.jar".equals(name)){
    			context.installBundle("file:"+brpath+"/"+name);
    		}	
    	}
    	
    	for(int i=30000; i>0; i--);
    	
		// start bundles
    	for(String bname : bundlesToStart()) {
    		for(Bundle bnd : context.getBundles()) {
    			if(bnd.getSymbolicName().equals(bname))
    				bnd.start();
    		}
    	}
    	for(int i=30000; i>0; i--);
    	
	}

	abstract protected Framework startFramework() throws BundleException;
	
	/**
	 * Bundle root location will be set with this value.
	 */
	abstract protected String bundleRootpath();
	
	/**
	 * @return list of bundle symbolic names to start 
	 */
	abstract protected String[] bundlesToStart();

	@After
	public void _after() throws Exception {
		if(fwk != null){
//			for(Bundle bnd : fwk.getBundleContext().getBundles()) {
//				bnd.stop();
//			}
//			fwk.uninstall();
			fwk.stop();
			fwk.waitForStop(5000);
			fwk = null;
		}
	}
	
	/**
	 * run method(methodName) of the class(serviceClz) whose impl is implClz.
	 * 
	 * @param serviceClz
	 * @param implClz can be null
	 * @param methodName
	 * @return
	 * @throws Exception 
	 */
	protected Object invokeMethod(Class serviceClz, Class implClz, String methodName, 
			Object... args) throws Exception{
		// get service
		Object svc = getService(serviceClz, implClz);
		if(svc == null)
			throw new Exception("Couldn't find a service: " + 
					implClz);
		
		return invokeMethod(svc, methodName, args);
	}
	
	/**
	 * @param svc
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	protected Object invokeMethod(Object svc, String methodName, Object... args)
			throws Exception{		
		// get method
		Method method = null;
		for(Method m : svc.getClass().getMethods()){
			if(m.getName().equals(methodName) && sameTypes(m.getParameterTypes(), args)){
					method = m;
					break;
			}
		}
		if(method == null)
			throw new Exception("Couldn't find a method: " + methodName);
		
		// run method
		return method.invoke(svc, args);
	}
	
	private boolean sameTypes(Class<?>[] parameterTypes, Object[] args) {
		if(parameterTypes.length != args.length)
			return false;
		
		for(int i=0; i<parameterTypes.length; i++){
			if(!parameterTypes[i].getName().equals(args[i].getClass().getName()))
				return false;
		}
		return true;
	}

	/**
	 * dont' cast returned object to original one. just use invokeMethod
	 * @param service
	 * @param impl
	 * @return
	 * @throws InvalidSyntaxException
	 */
	protected Object getService(Class service, Class impl) throws InvalidSyntaxException {
		ServiceTracker tracker = null;
	      try {
	          tracker = new ServiceTracker(context, service.getName(), null);
	          tracker.open();
	          tracker.waitForService(2000);
	          Object[] svcs = tracker.getServices();
	          for(Object o : svcs)
	        	  if(o.getClass().getName().equals(impl.getName()))
	        		  return o;
	      	}catch (InterruptedException e) {
	        } finally{
	        	if(tracker != null) tracker.close();
	        }
	        return null;
	}

}
