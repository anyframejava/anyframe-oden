package anyframe.oden.bundle.hessiancli;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.deploy.DeployerFactory;
import anyframe.oden.bundle.deploy.DeployerService;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * @ThreadSafety
 * @author joon1k
 *
 */
public class HessianDeployerFactory implements DeployerFactory {
	private int TIMEOUT = 5000;
	private long READ_TIMEOUT = 120000;
	
	protected HessianProxyFactory proxyfactory;
	
	protected Map<String, DeployerService> deploys = 
		new ConcurrentHashMap<String, DeployerService>();
	
	protected void activate(ComponentContext context){
		BundleContext ctx = context.getBundleContext();
		
		String tmout = ctx.getProperty("deploy.timeout");
		if(tmout != null){
			try{
				TIMEOUT = Integer.valueOf(tmout);
			}catch(NumberFormatException e){
				Logger.error(e);
			}
		}
		
		String rtmout = ctx.getProperty("deploy.readtimeout");
		if(rtmout != null){
			try{
				READ_TIMEOUT = Long.valueOf(rtmout);
			}catch(NumberFormatException e){
				Logger.error(e);
			}
		}
	}
	
	public String getProtocol() {
		return "";
	}
	
	public DeployerService newInstance(String addr) {
		try{
			DeployerService ds = deploys.get(addr);
			if(ds == null){
				ds = createNewDeployer(addr);
				deploys.put(addr, ds);
			}
			// Hessian checks return type by this thread's context class loader.
			Thread.currentThread().setContextClassLoader(DeployerService.class.getClassLoader());
			return ds;
		}catch(Exception e) {
			Logger.error(e);
		}
		return null;
	}

	protected synchronized DeployerService createNewDeployer(String addr) 
			throws MalformedURLException, ClassNotFoundException {
		if(proxyfactory == null){
			initProxyFactory();
		}
		return (DeployerService) proxyfactory.create(
				Class.forName(DeployerService.class.getName()), 
				"http://" + addr + "/deploy",
				this.getClass().getClassLoader());
	}

	protected void initProxyFactory() {
		proxyfactory = new OdenProxyFactory(TIMEOUT, READ_TIMEOUT);
	}

	
}
