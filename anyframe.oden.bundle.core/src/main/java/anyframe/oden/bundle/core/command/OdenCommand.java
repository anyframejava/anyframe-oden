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

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.OdenConfigService;
import anyframe.oden.bundle.core.prefs.Prefs;
import anyframe.oden.bundle.core.prefs.PrefsService;

/**
 * abstract command to provides convenient methods to access DelegateService,
 * PrefsService and OdenConfigService.
 * 
 * @author joon1k
 *
 */
public abstract class OdenCommand implements CustomCommand {
	protected PrefsService prefsService;
	
	protected OdenConfigService configService;
		
	public void setPrefsService(PrefsService prefsService){
		this.prefsService = prefsService;
	}
	
	public void setConfigService(OdenConfigService configService) {
		this.configService = configService;
	}
	
	protected Prefs getPrefs(String name){
		return prefsService.getPrefs(name);
	}
	
	/**
	 * get name's Cmd form from the preferences
	 * @param preference
	 * @param name
	 * @return
	 * @throws OdenException
	 */
	protected Cmd toInfoCmd(String prefs, String name) throws OdenException {
		String info = getPrefs(prefs).get(name);
		if(info.length() == 0 )
			return null;
		return new Cmd("foo fooAction \"" + name + "\" " + info);
	}
	
	/**
	 * agentName을 configService에서 찾아 해당하는 host:port값을 리턴해 줌
	 * agentName이 configService에 없으면 ""를 리턴해 줌
	 * @param agentName
	 * @return
	 * @throws FileNotFoundException
	 * @throws OdenException
	 */
	protected String getURIFromAgent(String agentName) throws OdenException {
		AgentElement agent = configService.getAgent(agentName);
		if(agent != null){
			return agent.getHost() + ":" + agent.getPort();
		}
		return "";
	}
	
	protected String extractUserName(Cmd cmd) {
		String user = cmd.getOptionArg(new String[]{Cmd.USER_OPT});
		try{
			if(user.length() == 0)
				user = InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException e){
			user = "";
		}
		return user;
	}
	
}
