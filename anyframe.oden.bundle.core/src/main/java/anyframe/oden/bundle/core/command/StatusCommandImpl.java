/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.core.command;

import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.DateUtil;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.Utils;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.job.JobManager;

/**
 * Oden command to show oden's current status.
 * 
 * @author joon1k
 *
 */
public class StatusCommandImpl implements CustomCommand {
	public final static String[] DETAIL_OPT = {"detail"};

	protected JobManager jobManager;
	
	protected void setJobManager(JobManager jm){
		this.jobManager = jm;
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		boolean isJSON = false;
		try{
			Cmd cmd = new Cmd(line);
			String action = cmd.getAction();
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if("info".equals(action)){
				Job[] jobs = jobManager.jobs();
				if(isJSON){
					JSONArray ja = new JSONArray();
					for(Job j : jobs) {
						ja.put(new JSONObject()
								.put("id", j.id())
								.put("status", j.status())
								.put("progress", j.progress())
								.put("totalWorks", j.todoWorks())			// this is for task deploy job
								.put("date", j.date())
								.put("desc", j.desc()));
					}
					out.println(ja.toString());
				}else{
					StringBuffer buf = new StringBuffer();
					for(Job j : jobs){ 
						String totalWorks = j.todoWorks() < 0 ? "?" : String.valueOf(j.todoWorks());
						String progress = j.status() == Job.RUNNING ? 
								" " + j.progress()+"% [total: " + totalWorks + "]" : "";
						buf.append("id: " + j.id() + ", desc: " + j.desc()
								+ ", date: " + DateUtil.toStringDate(j.date())
								+ " (" + status(j.status()) + progress + ")" + "\n");
					}
					
					if(cmd.getOption(DETAIL_OPT) != null){
						buf.append(Utils.jvmStat());
					}
					
					out.println(buf.toString());
				}
			}else if("stop".equals(action)){
				String id = cmd.getActionArg();
				if(id.length() == 0)
					throw new OdenException("Transaction id is required.");
				Job j = jobManager.job(id);
				if(j == null)
					throw new OdenException("Couldn't find that job: " + id);
				jobManager.cancel(j);
			}else if(action.length() == 0 || "help".equals(action)){
				out.println(getFullUsage());
			}else {
				throw new OdenException("Couldn't execute specified action: " + action);
			}
		}catch(OdenException e){
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
		}catch(Exception e){
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
		}
	}

	public String getName() {
		return "status";
	}

	public String getShortDescription() {
		return "retreive the Oden status";
	}

	private String getFullUsage() {
		return "status info" + 
			"\nstatus stop <txid>";
	}
	
	public String getUsage() {
		return "status help";
	}

	private String status(int s) {
		switch(s){
		case 0:
			return "NONE";
		case 0x01:
			return "SLEEPING";
		case 0x02:
			return "WAITING";
		case 0x04:
			return "RUNNING";
		}
		return "";
	}
}
