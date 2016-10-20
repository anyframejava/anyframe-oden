package anyframe.oden.bundle.job.config;

import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.common.Utils;

public class CfgMapping {
	String dir;
	String checkoutDir;
	
	public CfgMapping(String dir, String checkoutDir){
		this.dir = dir;
		this.checkoutDir = checkoutDir;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getCheckoutDir() {
		return checkoutDir;
	}

	public void setCheckoutDir(String checkoutDir) {
		this.checkoutDir = checkoutDir;
	}
	
	public JSONObject toJSON() throws JSONException{
		JSONObject o = new JSONObject();
		o.put("dir", dir);
		o.put("checkout-dir", checkoutDir);
		return o;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CfgMapping){
			CfgMapping ct = (CfgMapping)obj;
			if(StringUtil.equals(dir, ct.getDir()) &&  
					StringUtil.equals(checkoutDir, ct.getCheckoutDir()) )
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Utils.hashCode(dir, checkoutDir);
	}
}
