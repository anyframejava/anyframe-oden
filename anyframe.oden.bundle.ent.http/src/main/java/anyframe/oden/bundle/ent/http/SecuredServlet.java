package anyframe.oden.bundle.ent.http;

import javax.servlet.http.HttpServlet;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

import anyframe.oden.bundle.common.Logger;

public class SecuredServlet extends HttpServlet{
	String name;
	
	private HttpContext httpContext;
	
	private SecurityHandler securityHandler;
	
	protected void setHttpService(HttpService hs){
		try {
			hs.registerServlet("/" + name, this, null, 
					httpContext = new ShellHttpContext(hs, securityHandler));
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	
	protected void setSecurityHandler(SecurityHandler handler){
		this.securityHandler = handler;
		if(httpContext != null)		// handler is already binded.
			((ShellHttpContext)httpContext).setSecurityHandler(handler);
	}
	
	protected void unsetSecurityHandler(SecurityHandler handler) {
		setSecurityHandler(null);
	}
	
	public SecuredServlet(String name){
		this.name = name;
	}
}
