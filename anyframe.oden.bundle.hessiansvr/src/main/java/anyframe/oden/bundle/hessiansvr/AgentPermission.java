package anyframe.oden.bundle.hessiansvr;

//import java.io.FilePermission;
//import java.io.IOException;
//import java.io.SerializablePermission;
//import java.net.NetPermission;
//import java.net.SocketPermission;
//import java.util.PropertyPermission;
//
//import org.osgi.framework.AdminPermission;
//import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public class AgentPermission {
	private final static String ROSGI = "ch.ethz.iks.r_osgi.remote";
	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
		
//		try{
//			if(pa == null) return;
//			
//			final String server = this.context.getProperty("oden.server");
//			if(server == null) return; // ignore
//			
//			final String rosgi_loc = rosgiLocation();
//			if(rosgi_loc == null){
//				Logger.error(new IOException("Couldn't find this bundle: " + ROSGI));
//				return;
//			}
//			
//			pa.setPermissions(rosgi_loc, new PermissionInfo[]{
//					new PermissionInfo( NetPermission.class.getName(), "specifyStreamHandler", null),
//					new PermissionInfo( RuntimePermission.class.getName(), "getClassLoader", null),
//					new PermissionInfo( FilePermission.class.getName(), "*", "read,write,delete"),
//					new PermissionInfo( SerializablePermission.class.getName(), "enableSubclassImplementation", "*"),
//					new PermissionInfo( PropertyPermission.class.getName(), "*", "read,write"),
//					new PermissionInfo( AdminPermission.class.getName(), "*", "*"),
//					new PermissionInfo( PackagePermission.class.getName(), "*", "export,import"),
//					new PermissionInfo( ServicePermission.class.getName(), "*", "get,register"),
//					new PermissionInfo( SocketPermission.class.getName(), "*", "connect,listen,resolve"),
//					new PermissionInfo( SocketPermission.class.getName(), server, "accept")});
//		}catch (Exception e){
//			e.printStackTrace();
//		}
	}
	
//	private PermissionAdmin pa;
//	
//	protected void setPermissionAdmin(PermissionAdmin pa){
//		this.pa = pa;
//	}
//	
//	private String rosgiLocation(){
//		for(Bundle b : context.getBundles()) {
//			if(b.getSymbolicName().equals(ROSGI))
//				return b.getLocation(); 
//		}
//		return null;
//	}
}
