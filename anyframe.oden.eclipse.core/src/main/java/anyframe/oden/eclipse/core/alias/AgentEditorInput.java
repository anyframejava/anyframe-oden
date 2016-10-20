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
package anyframe.oden.eclipse.core.alias;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.alias.Agent;


/**
 * Represents a configured Agent profile.
 * This class extends Alias class and implements some "Agent" specific cases.
 * 
 * @author HongJungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 *
 */
public class AgentEditorInput implements  IEditorInput  {

	private String nickname;
	private String url;
	private String user;
	private String password;


	public AgentEditorInput(Agent agent) {
		this.nickname = agent.getNickname();
		this.url = agent.getUrl();
		this.user = agent.getUser();
		this.password = agent.getPassword();
	}


	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return this.nickname;
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		// TODO Auto-generated method stub
		return "";
	}

	public Object getAdapter(Class class1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Agent getAgent() {
		return OdenActivator.getDefault().getAliasManager().getAgentManager()
		.getAgent(this.nickname);
	}

}
