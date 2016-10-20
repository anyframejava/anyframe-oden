package anyframe.oden.bundle.samsunglife;
/**
 * 
 * Information about target host and path.
 * 
 * @author joon1k
 *
 */
public class SPFTarget {
	private String host;
	private String path;
	
	public SPFTarget(String host, String path) {
		super();
		this.host = host;
		this.path = path;
	}
	
	public String getHost() {
		return host;
	}
	public String getPath() {
		return path;
	}
	
	
}
