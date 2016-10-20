package anyframe.oden.bundle.job;

import java.util.List;

import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.job.config.CfgCommand;
import anyframe.oden.bundle.job.config.CfgTarget;

public class ExecWorker extends Thread{
	DeployerService deployer;
	
	CfgTarget target;
	
	List<CfgCommand> commands;
	
	long timeout;
	
	StringBuffer result = new StringBuffer();
	
	public ExecWorker(DeployerService deployer, CfgTarget target, 
			List<CfgCommand> commands, long timeout){
		this.deployer = deployer;
		this.target = target;
		this.commands = commands;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		try {
			for(CfgCommand c : commands){
				String path = FileUtil.isAbsolutePath(c.getPath()) ?
						c.getPath() : 
						FileUtil.combinePath(target.getPath(), c.getPath());
				result.append(StringUtil.makeEmpty(deployer.execShellCommand(
						c.getCommand(), path, timeout) + "\n"));
			}
		} catch (Exception e) {
			Logger.error(e);
			result.append(e.getMessage() + "\n");
		}
	}
	
	public String getResult(){
		return result.toString();
	}
	
	public String getTargetName(){
		return target.getName();
	}
}
