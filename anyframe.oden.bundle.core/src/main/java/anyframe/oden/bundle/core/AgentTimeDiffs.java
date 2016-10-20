package anyframe.oden.bundle.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.deploy.ByteArray;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;

public class AgentTimeDiffs {
	private Map<DeployerService, Long> diffs = 
			new HashMap<DeployerService, Long>();
	
	private final String TMPDIR = FileUtil.temporaryDir().getPath();
	
	public AgentTimeDiffs() throws IOException {		
	}

	private Long getDiff(DeployerService deplyr, String loc) throws IOException {
		File f = uniqueFile(TMPDIR, deplyr, loc);
		if(f == null)
			throw new IOException("Fail to find unique file name.");
		
		try {
			long ds_t = getDeployerTime(deplyr, loc, f.getName());
			return System.currentTimeMillis() - ds_t;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	private long getDeployerTime(DeployerService deplyr, String loc, String name) throws Exception {
		DoneFileInfo donef = null;	// this will be initialized in finally block
		try{
			deplyr.init(loc, name, -1);		// TODO: if date is 0, lastmodifed will be 0
			deplyr.write(loc, name, new ByteArray(new byte[4]));
		}finally{
			try{
				donef = deplyr.close(loc, name, null, null);	
			} finally {
				deplyr.removeFile(loc, name);
			}
		}
		return donef.lastModified();
	}

	private File uniqueFile(String tmpdir, DeployerService deplyr, String loc){
		Exception excepn= null;
		
		final int maxiter = 100;
		for(int i=0; i<maxiter; i++){
			try{
				File tmp = new File(tmpdir, "oden0" + String.valueOf(i) + ".tmp");
				if(tmp.exists())
					continue;
				
				if(!deplyr.exist(loc, tmp.getName())){
					return tmp;
				}
			}catch(Exception e){
				excepn = e;
			}
		}
		
		if(excepn != null)		// write one exception only 
			Logger.error(excepn);
		return null;
	}

	public long getDiffTime(DeployerService deplyr, String agentLoc) throws IOException{
		Long diff = diffs.get(deplyr);
		if(diff == null){
			diffs.put(deplyr, diff = getDiff(deplyr, agentLoc));
		}
		return diff.longValue();
	}
	
}
