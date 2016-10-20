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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.jobmanager.JobManagerView;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Deploy by File Request(Ex. spectrum interface). This class extends Wizard
 * class.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */

public class DeployByFileReqWizard extends Wizard {
	
	private String serverName;
	
	private String runcommand;
	
	private String shellUrl;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	public DeployByFileReqWizard() {
		setWindowTitle(UIMessages.ODEN_JOBMANAGER_Wizards_AddFileReq_Title);
		setNeedsProgressMonitor(true);

		setDefaultPageImageDescriptor(ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		 addPage(new DeployByFileReqRepoWizardPage(serverName));
		 addPage(new DeployByFileReqPreViewWizardPage());

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if(page.getName().equals("setRepoPage"))
			return false;
		else{
			if(((DeployByFileReqPreViewWizardPage) page).isComplete()) 
				return true;
			else
				return false;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		// TODO Auto-generated method stub
		boolean ans = DialogUtil.confirmMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Confirmation,
				UIMessages.ODEN_JOBMANAGER_Wizards_FileRequest_CofirmFinish);
		if (ans) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					runSpectrum();
					
					JobManagerView jobmanagerview = OdenActivator.getDefault().getJObManagerView();
					jobmanagerview.refresh();
				}
			});
			return true;
		} else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	public boolean performCancel() {
		boolean ans = DialogUtil.confirmMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Confirmation,
				UIMessages.ODEN_JOBMANAGER_Wizards_FileRequest_CofirmCancel);
		if (ans) {
			return true;
		} else
			return false;
	}

	/*
	 * setter sever name 
	 */
	public void setSeverName(String serverName) {
		this.serverName = serverName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		String[] command = null;
	    String shellUrl = "";
	    
	    if(page instanceof DeployByFileReqRepoWizardPage) {
		    DeployByFileReqRepoWizardPage currentPage = (DeployByFileReqRepoWizardPage) page;
		    
	    	command = currentPage.getCommand();
	    	shellUrl = currentPage.getShellUrl();
			    
	    	DeployByFileReqPreViewWizardPage nextPage = (DeployByFileReqPreViewWizardPage) super.getNextPage(page);
		    
	    	
		    if(command != null) {
		    	nextPage.setTestCommand(command[0]);
		    	this.runcommand = command[1];
		    	
		    	nextPage.setShellUrl(shellUrl);
			    this.shellUrl = shellUrl;
		    	
		    	nextPage.getPreview();
		    }
			return (IWizardPage) nextPage;
	    } else 
	    	return null;
	}
	
	private void runSpectrum() {
		if(runcommand != null ) {
			try {
				OdenBroker.sendRequest(shellUrl, runcommand);
				
			} catch (OdenException odenException) {
			} catch (Exception odenException) {
				OdenActivator.error("Exception occured while deploying by file requests.",odenException);
			}
		}
	}
}
