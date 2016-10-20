package org.anyframe.oden.dimmension.compile;

import org.anyframe.oden.dimmension.domain.BuildInfo;
import org.apache.tools.ant.Task;

public class CompileBroker extends Task {
	private String projectName;
	
	private String requestId;
	
	private String srcRoot;
	
	private String webRoot;
	
	private String srcAppd;
	
	private String webAppd;
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setSrcRoot(String srcRoot) {
		this.srcRoot = srcRoot;
	}


	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public void setSrcAppd(String srcAppd) {
		this.srcAppd = srcAppd;
	}


	public void setWebAppd(String webAppd) {
		this.webAppd = webAppd;
	}


	public void execute(){
		try {
			// 0. transfer vo object
			BuildInfo build = new BuildInfo(projectName,
					convertJson(requestId), srcRoot, webRoot, srcAppd, webAppd);
			
			Compile compile = new Compile(getProject());
		
			// 1. mkdir(build) and compile
			compile.execute(build);
			// 2. transfer web source
			compile.webTransfer(build);
			// 3. transfer backup(class)
			compile.copyBackup(build);
			// 4. transfer reference(class)
			compile.copyReference(build);
			
			
			
		} catch (Exception e) {
			getProject().fireBuildFinished(e);
			System.exit(-1);
		}
		
	}
	
	private String convertJson(String request) {
		String trans = request;
		
		trans = trans.replace("[{", "[{\"");
		trans = trans.replace(":", "\":\"");
		trans = trans.replace(",", "\",\"");
		trans = trans.replace("}", "\"}");
		
		trans = trans.replace("\"{", "{\"");
		trans = trans.replace("}\"", "}");
		
		return trans;
	}
}
