/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.eclipse.core.jobmanager.wizard;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.AliasManager;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.alias.RepositoryManager;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.alias.ServerManager;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Tree content provider for Oden view outline.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */
public class DeployByFileReqRepoWizardContentProvider implements ITreeContentProvider,IStructuredContentProvider{
	private TreeParent invisibleRoot;
	
	private String shellUrl;
	
	private String command;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	/**
	 * Gets children of the element
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeParent) {
			return ((TreeParent) parentElement).getChildren();
		}
		return new Object[0];
	}

	/**
	 * Gets a parent of the element
	 */
	public Object getParent(Object element) {
		if (element instanceof AliasManager) {
			if (((AliasManager) element).getServerManager() instanceof ServerManager) {
				return null;

			} else if (element instanceof Server) {
				return OdenActivator.getDefault().getAliasManager().getServerManager();

			} else if (((AliasManager) element).getRepositoryManager() instanceof RepositoryManager) {
				return null;

			} else if (element instanceof Repository) {
				return OdenActivator.getDefault().getAliasManager().getRepositoryManager();
			}
		}

		return null;
	}

	/**
	 * Returns boolean value on existence of child element
	 */
	public boolean hasChildren(Object element) {
		Object[] object = getChildren(element);

		return object != null && object.length != 0;
	}

	/**
	 * Returns Object array with child elements
	 */
	public Object[] getElements(Object inputElement) {
		if (invisibleRoot == null)
			initialize(inputElement);
		return getChildren(invisibleRoot);
	}

	/**
	 * 
	 */
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	
	private void initialize(Object inputElement) {
		invisibleRoot = new TreeParent("");
		ArrayList<DeployByFileReqRepoInfo> treeRoot = getRepository();
		
		TreeParent directory = null;
		TreeObject file = null;
		
		for(DeployByFileReqRepoInfo treeinfo : treeRoot) {
			if(treeinfo.getType().equals(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Directory)){
				directory = new TreeParent(treeinfo.getName());
				invisibleRoot.addChild(directory);
			} else {
				file = new TreeObject(treeinfo.getFileTree());
				invisibleRoot.addChild(file);
			}
			
		}
	}
	
	private ArrayList<DeployByFileReqRepoInfo> getRepository() {
		ArrayList<DeployByFileReqRepoInfo> returnList = new ArrayList<DeployByFileReqRepoInfo>();
		
		String result = ""; 
		try {
			if(shellUrl != null) {
				result = OdenBroker.sendRequest(shellUrl, command);
				if(result != null){
					JSONArray array = new JSONArray(result);
	
					for (int i = 0; i < array.length(); i++) {
						String type = (String) ((JSONObject) array.get(i)).get(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Type);
						String name = (String) ((JSONObject) array.get(i)).get(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Name);
						
						if (name.lastIndexOf(File.separator) > 0) {
							// when the separator follows Microsoft Windows or equivalent operating system conventions
							name = name.substring(name.lastIndexOf(File.separator) + 1);
						} else if (name.lastIndexOf("/") > 0) { 
							// when the separator follows UNIX or equivalent operating system conventions
							name = name.substring(name.lastIndexOf("/") + 1); 
						}
	
						String date = (String) ((JSONObject) array.get(i)).get(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Date);
						
						DeployByFileReqRepoInfo tree = new DeployByFileReqRepoInfo(type , name , date);
						   
						returnList.add(tree);
						
					}
				}
			}
		} catch (OdenException odenException) {
		} catch (Exception odenException) {
			OdenActivator.error(UIMessages.ODEN_EXPLORER_ExplorerView_Msg_ExceptionRepoList, odenException);
			odenException.printStackTrace();
		}

		
		return returnList;
	}
	
	/*
	 * setter url and command using wizard 
	 */
	public void setShellUrl(String shellUrl) {
		this.shellUrl = shellUrl;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	

}
