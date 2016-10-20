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
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.FileRequest;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Deploy by File Request(Ex. spectrum interface). This class extends Wizard
 * class.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */

public class DeployByFileReqRepoWizardPage extends WizardPage {
	private static final String CMD_REPOSITORY_SHOW = CommandMessages.ODEN_CLI_COMMAND_repository_show + " ";
	
	private CheckboxTreeViewer treeViewer;
	
	private Combo nickNameCombo;
	
	private String shellUrl;
	
	private String command;
	
	private ArrayList<FileRequest> nickNameComboList;
	
	private FileRequest filerequest;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	private String absolutePath;
	
	private String relativePath;
	
	private boolean isExpand = false;
	
	protected IResource fOriginalResource, fParentResource;
	
	protected boolean isChecked;
	
	private String serverName;
	
	DeployByFileReqRepoWizardPage(String serverName) {
		super("setRepoPage");
		setTitle(UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryTree_Title);
		setDescription(UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryTree_Description);
		this.serverName = serverName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		 
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();

		composite.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		
		nickNameCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN | SWT.LEFT);
		
		nickNameCombo.setLayoutData(gridData);

		populateCombo();
		
		GridData gridDataTree = new GridData( GridData.FILL_BOTH);
		
		treeViewer = new CheckboxTreeViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.CHECK);
		
		// use hash lookup to improve performance
		treeViewer.setUseHashlookup(true);
		
		DeployByFileReqRepoWizardContentProvider contentprovider = new DeployByFileReqRepoWizardContentProvider();
		
		contentprovider.setCommand(command);
		
		contentprovider.setShellUrl(shellUrl);
		
		treeViewer.setContentProvider(contentprovider);
		treeViewer.setLabelProvider(new DeployByFileReqRepoWizardLabelProvider());
		
		treeViewer.setInput(OdenActivator.getDefault().getAliasManager());
		
		treeViewer.getTree().setLayoutData(gridDataTree);
		
		treeViewEvent();
		
		validate();
		
		comboEvent();
		
		setControl(composite);

	}
	private void treeViewEvent() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				Object[] selections = getSelected();
				Object element = selections[0];
				
				if( element instanceof TreeParent) {
					isExpand = treeViewer.getExpandedState((TreeParent) element);
					if(! isExpand)
						 getChildTree();
					else if (isExpand) {
						// when double click expanded tree cell
						treeViewer.collapseToLevel((TreeParent) element, 1);
						treeViewer.refresh();
					}
				}
			}
		});
		
		
		treeViewer.addCheckStateListener(new ICheckStateListener() {
		
			public void checkStateChanged(final CheckStateChangedEvent event) {
				final Object element = event.getElement();
				
				BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
					public void run() {
						validate();
						if (element instanceof TreeParent) {
							// when click folder
							TreeParent folder = (TreeParent) event.getElement();
							handleFolderCheckStateChanged(folder, event.getChecked());
							
						} else {
							// when click file
							TreeObject file = (TreeObject) event.getElement();
							handleFileCheckStateChanged(file, event.getChecked());
						}
					}
				});
			}
		});
	}
	
	private void handleFolderCheckStateChanged(TreeParent resource, boolean checked) {
		
		isChecked = checked;
		
		treeViewer.setGrayed(resource, false);
		
		setGrayChecked(resource);
		
		treeViewer.setChecked(resource, isChecked);
		
		childAllselected(resource, resource.getName());
		treeViewer.refresh();
	}
	
	private void setGrayChecked(TreeObject obj) {
		
		TreeParent parent = obj.getParent();
		while (parent != null) {
			treeViewer.setGrayChecked(parent, true);  
			parent = parent.getParent();
		}
		
	}
	
	private void childAllselected(TreeParent resource, String name) {
		TreeObject[] treeobjects = resource.getChildren();
		
		if(treeobjects.length > 0) {
			for(TreeObject treeobject : treeobjects) {
				treeViewer.setGrayChecked(treeobject, false);
				treeViewer.setSubtreeChecked(treeobject, isChecked);
			}
		}
		treeViewer.refresh();
	}
	
	private void handleFileCheckStateChanged(TreeObject resource , boolean checked) {
		isChecked = checked;
		
		setGrayChecked(resource);
		treeViewer.refresh();
	}
	
	private void comboEvent() {
		nickNameCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				newData();
				
				getServerData();
				getRepoData();
				DeployByFileReqRepoWizardContentProvider contentprovider = new DeployByFileReqRepoWizardContentProvider(); 
				contentprovider.setCommand(command);
				contentprovider.setShellUrl(shellUrl);
				
				treeViewer.setContentProvider(contentprovider);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}
	
	private void populateCombo() {
		Collection<FileRequest> filerequests = OdenActivator.getDefault().getAliasManager().getFileRequestManager().getFileRequests();
		nickNameComboList = new ArrayList<FileRequest>();
		
		for(FileRequest filerequest : filerequests) {
			if(serverName.equals(filerequest.getServerToUse())) {
				nickNameCombo.add(filerequest.getNickname() + "(" + filerequest.getDesc() + ")") ;
				nickNameComboList.add(filerequest);
			}
		}
		nickNameCombo.select(0);
		
		if( ! nickNameCombo.getText().equals("")) {
			nickNameCombo.select(0);
			
			//get Server, Repository Data
			getServerData();
			getRepoData();
		}
	}
	
	private void newData() {
		command = "";
		absolutePath = "";
		relativePath = "";	
	}
	
	private void getServerData() {
		// get Shell URL
		String nickName = "";
		
		for(FileRequest list : nickNameComboList) {
			if((list.getNickname() + "(" + list.getDesc() + ")").equals(nickNameCombo.getText())) {
				nickName = list.getNickname();
			}
		}
			
		filerequest = OdenActivator.getDefault().getAliasManager().getFileRequestManager().getFileRequest(nickName);
		// set ShellURL , -r (repository path)
		String serverNickName = filerequest.getServerToUse();
		String serverUrl = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(serverNickName).getUrl();
		shellUrl = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP + serverUrl + CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf;
	}
	
	private void getRepoData() {
		String repopath = "";
		
		if (filerequest.getProtocol().equals(CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
			// when the protocol is Filesystem
			absolutePath = CommonMessages.ODEN_CommonMessages_ProtocolString_File + filerequest.getPath();
		} else {
			// when the protocol is FTP
			absolutePath = filerequest.getPath();
		}
		
		repopath = relativePath==null ? absolutePath : absolutePath + "/" + relativePath ;
		
		if (filerequest.getProtocol().equals(
				CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
			// when the protocol is Filesystem
			command = CMD_REPOSITORY_SHOW + '"' + repopath + '"'; 
		} else {
			// when the protocol is FTP
			command = CMD_REPOSITORY_SHOW + CommonMessages.ODEN_CommonMessages_ProtocolString_FTP + filerequest.getUrl() + " " + '"' + repopath + '"' + " " + filerequest.getUser() + " " + filerequest.getPassword();  
		}
	}
	
	private void getChildTree() {
		Object[] selections = this.getSelected();
		Object element = selections[0];
		boolean isCheck = treeViewer.getChecked(element);
		
		if(((TreeParent)element).getChildren().length == 0) {
			relativePath = getFullpath((TreeParent) element);
			
			// get command
			getRepoData();
			redrawTree((TreeParent) element);
		}
		
		treeViewer.expandToLevel((TreeParent) element, 1);
		
		TreeObject[] treeitems = ((TreeParent) element).getChildren();
		
		if(treeitems.length > 0) 
			for(TreeObject treeitem : treeitems)
				treeViewer.setChecked(treeitem, isCheck);
		treeViewer.refresh();
	}
	
	private void redrawTree(TreeParent parent) {
		// connection server and get tree object
		ArrayList<DeployByFileReqRepoInfo> treeinfos = getTreeObject();
		TreeParent directory = null;
		TreeObject file = null;
		// add Tree child
		for(DeployByFileReqRepoInfo treeinfo : treeinfos) {
			if(treeinfo.getType().equals(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Directory)){
				directory = new TreeParent(treeinfo.getName());
				parent.addChild(directory);
			} else {
				file = new TreeObject(treeinfo.getFileTree());
				parent.addChild(file);
			}
		}
	}
	
	private ArrayList<DeployByFileReqRepoInfo> getTreeObject() {
		ArrayList<DeployByFileReqRepoInfo> returnList = new ArrayList<DeployByFileReqRepoInfo>();
		
		String result = ""; 
		
		try {
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
		} catch (OdenException odenException) {
		} catch (Exception odenException) {
			OdenActivator.error(UIMessages.ODEN_EXPLORER_ExplorerView_Msg_ExceptionRepoList, odenException);
			odenException.printStackTrace();
		}

		return returnList;
	}
	private String getFullpath(final TreeObject obj) {
		StringBuffer full = new StringBuffer(obj.getName());
		TreeParent parent = obj.getParent();
		while (parent != null) {
			full.insert(0, parent.getName() + "/"); 
			parent = parent.getParent();
		}
		if (full.toString().substring(0, 1).equals("/")) {
			return full.toString().substring(1);
		} else {
			return full.toString();
		}

	}
	
	/**
	 * Returns the objects which are currently selected. NOTE this is package
	 * private and should remain that way. - the implementation of the
	 * ExplorerView is now hidden from the rest of the application (see the
	 * getSelectedXxxx() methods below for a structured API)
	 * @return
	 */
	final Object[] getSelected() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if (selection == null) {
			return null;
		}
		Object[] result = selection.toArray();
		if (result.length == 0) {
			return null;
		}
		return result;
	}
	
	/*
	 * make command(spectrum test and run)
	 */
	public String[] getCommand() {
		String[] returnArry = new String[2];
		String includeOption = "-i";
		String testrepositoryOption = CommandMessages.ODEN_CLI_COMMAND_spectrum_test
				+ " " + "-r" + " " + absolutePath;
		String runrepositoryOption = CommandMessages.ODEN_CLI_COMMAND_spectrum_run
				+ " " + "-r" + " " + absolutePath;
		String path = "";
		
		String allFolderPath = "";
		
		for(Object checkObject : getCheckedObject()) {
			if(! isGrayedObject(checkObject)) {
				if(checkObject instanceof TreeParent) {
					String fullPath = getFullpath((TreeParent) checkObject);
					if (!allFolderPath.equals(fullPath)
							&& (!fullPath.matches(allFolderPath + ".*") || allFolderPath
									.equals(""))) {
						allFolderPath = fullPath;
						path = fullPath + "/**";
						includeOption = includeOption + " " + path;
					}
				} else if(checkObject instanceof TreeObject && ! (checkObject instanceof TreeParent)) {
					String fullPath = getFullpath((TreeObject) checkObject);
					if(! allFolderPath.equals("")) {
						if( ! fullPath.matches(allFolderPath + ".*" )) {
							path = fullPath.substring(0, fullPath.indexOf(" "));
							includeOption = includeOption + " " + path;
						}
					} else {
						path = fullPath.substring(0, fullPath.indexOf(" "));
						includeOption = includeOption + " " + path;
					}
				}
			}
		}
		
		String testCommand = testrepositoryOption + " " + includeOption + " " + CommandMessages.ODEN_CLI_OPTION_json; 
		String runcommand = runrepositoryOption + " " + includeOption;
		
		returnArry[0] = testCommand ;
		returnArry[1] = runcommand ;
		
		return returnArry;
	}
	private Object[] getCheckedObject() {
		return treeViewer.getCheckedElements();
	}
	
	private boolean isGrayedObject(Object element) {
		return treeViewer.getGrayed(element);
	}

	public String getShellUrl() {
		return shellUrl;
	}
	
	private boolean isCheckedTree() {
		return treeViewer.getCheckedElements().length > 0 ? true : false;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	public boolean canFlipToNextPage(){
		if(isCheckedTree()) 
			return true;
		else
			return false;
	}
	
	private void validate () {
	    if(isCheckedTree()) {
	    	setPageComplete(false);
	    	getWizard().getContainer().updateButtons();
	    } else {
	    	setPageComplete(false);
	    	canFlipToNextPage();
	    }
	    
	}
}
