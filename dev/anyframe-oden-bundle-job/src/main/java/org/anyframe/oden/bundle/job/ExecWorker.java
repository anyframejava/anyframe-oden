/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.job;

import java.util.List;

import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.job.config.CfgCommand;
import org.anyframe.oden.bundle.job.config.CfgTarget;

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
						c.getCommand(), path, timeout) + "\n").toString());
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
