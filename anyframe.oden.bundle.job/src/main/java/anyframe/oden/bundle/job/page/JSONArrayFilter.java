package anyframe.oden.bundle.job.page;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONArrayFilter {
	public JSONArrayFilter(){
	}
		
	public JSONArray run(JSONArray in, int page, int pgscale) 
			throws JSONException{
		int start = pgscale * page;
		int end = start + pgscale;
		
		JSONArray ret = new JSONArray();
		for(int i=start; i<in.length() && i<end; i++){
			ret.put(in.getJSONObject(i));
		}
		return ret;
	}
}
