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
package anyframe.oden.eclipse.core.editors.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.TableItem;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.OdenEditor;
import anyframe.oden.eclipse.core.editors.PolicyDetails;
import anyframe.oden.eclipse.core.editors.PolicyPage;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Save policy action in the Oden editor view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class SavePolicyAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_POLICY_SAVE = CommandMessages.ODEN_CLI_COMMAND_policy_add;
	private static final String FTP_PROT = "ftp://";
	private static final String FILE_PROT = "file://";
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	public SavePolicyAction() {
		super(
				UIMessages.ODEN_EDITORS_PolicyPage_PolicySave_Btn,
				UIMessages.ODEN_EDITORS_PolicyPage_PolicySave_Btn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		PolicyPage page = OdenEditor.getDefault(title).getPolicypage();
		String commnd = "";
		String repoType = "";
		PolicyDetails details = null;

		if(! page.getRepoKind().getText().equals("")) 
			repoType = page.getRepoKind().getText().equals(
			CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem) ? "file server" : "ftp";
		
		String deployUrl = "";
		String dCommand = "";
		String locationVal = "";
		String location = "";
		String updateOpt = "-u";
		int size = page.getDeployViewer().getTable().getItemCount();
		// agent info
		for (int i = 0; i < size; i++) {
			TableItem item = page.getDeployViewer().getTable().getItem(i);
			details = (PolicyDetails) item.getData();
			deployUrl = details.getDeployUrl();
			locationVal = details.getLocationVar();
			location = details.getLocation()==null ?"" : details.getLocation();
			
			if(locationVal.equals(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault)) { 
				// Default Location
				if(location.equals("")){
					dCommand = dCommand + '"' + deployUrl + ":~" + '"' + " ";
				} else {
					// using Location path
					dCommand = dCommand + '"' + deployUrl + ":~" + "/" + location + '"'  + " ";
				}
			} else if(locationVal.equals(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboAbsolutePath)) {
				// absolute path
				dCommand = dCommand + '"' + deployUrl + ":/"+ details.getLocation() + '"'  + " ";
			} else {
				// using location variable
				if(location.equals("")){
					dCommand = dCommand + '"' + deployUrl + ":$"+ locationVal + '"'  + " ";
				} else {
					// using Location path
					dCommand = dCommand + '"' + deployUrl + ":$"+ locationVal + "/" + location + '"'  + " ";
				}
			}
		}

		if (repoType
				.equals(UIMessages.ODEN_EDITORS_PolicyPage_Repo_Kind1) && ! page.getDeleteOptionRequired().getSelection())
			commnd = MSG_POLICY_SAVE + " " + '"' + page.getPolicyNameText().getText() + '"' + " "
			+ "-r " + FTP_PROT + page.getBuildRepoUriText().getText() + " "
			+ page.getBuildRepoRootText().getText() + " "
			+ '"' + page.getUserField().getText() + '"' + " " + '"' + page.getPasswordField().getText() + '"' 
			+ " " + "-d" + " " + dCommand ;
		else if (repoType
				.equals(UIMessages.ODEN_EDITORS_PolicyPage_Repo_Kind2) && ! page.getDeleteOptionRequired().getSelection() )
			commnd = MSG_POLICY_SAVE + " " + '"' + page.getPolicyNameText().getText()
			+ '"' + " " + "-r " + FILE_PROT
			+ page.getBuildRepoRootText().getText() + " " + "-d" + " "  + dCommand ;
		else if(repoType.equals("") && page.getDeleteOptionRequired().getSelection())
			commnd = MSG_POLICY_SAVE + " " + '"' + page.getPolicyNameText().getText()
			+ '"' + " " + "-d" + " "  + dCommand + " " + CommandMessages.ODEN_CLI_OPTION_delete;
		
		if (!(page.getDescriptionText().getText().equals(""))) {
			commnd = commnd + " " + "-desc" + " " + '"' + page.getDescriptionText().getText() + '"';
		} 

		if (!(page.getIncludeText().getText().equals(""))) {
			String include = " " + "-i" + " " + changeCludeValue(page.getIncludeText().getText());
			commnd = commnd + include;
		}
		if (!(page.getExcludeText().getText().equals(""))) {
			String exclude = " " + "-e" + " " + changeCludeValue(page.getExcludeText().getText());
			commnd = commnd + exclude;
			// default exclude (.svn, CVS)
			String[] hiddenFolder = CommandMessages.ODEN_CLI_OPTION_hiddenfolder.split(",");
			for(String val : hiddenFolder) {
				commnd = commnd + " " + '"' + "**/" + val + "/**" + '"' ; 
			}
		} else {
			// default exclude (.svn, CVS)
			String[] hiddenFolder = CommandMessages.ODEN_CLI_OPTION_hiddenfolder.split(",");
			commnd = commnd + " " + "-e" ;
			for(String val : hiddenFolder) {
				commnd = commnd  + " " + '"' + "**/" + val + "/**" + '"'; 
			}
		}
		// update option check
		if(page.getUpdateOptionRequired().getSelection()){
			commnd = commnd + " " + updateOpt + " " + CommandMessages.ODEN_CLI_OPTION_json;
		} else {
			commnd = commnd + " " + CommandMessages.ODEN_CLI_OPTION_json;
		}
		try {
			OdenBroker.sendRequest(page.getShellUrl(), commnd);
			DialogUtil.openMessageDialog("Policy",
					CommonMessages.ODEN_CommonMessages_OperationSucceeded,
					MessageDialog.INFORMATION);
			page.loadInitData(page.getShellUrl());
			page.showPolicyDetail(page.getPolicyNameText().getText());
			page.getPolicyNameText().setEnabled(false);
			
		} catch (OdenException e) {
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while saving policy info.",
					odenException);
		}

	}
	
	private String changeCludeValue(String inputVal) {
		String[] cludeArr = inputVal.split(";");
		if(cludeArr.length == 0) {
			return inputVal;
		} else {
			String returnVal = "";
			for(String val : cludeArr) {
				returnVal = returnVal + '"' + val.trim() + '"' + " ";
			}
			return returnVal.substring(0,returnVal.lastIndexOf(" "));
		}
	}
}