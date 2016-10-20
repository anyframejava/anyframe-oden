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
package anyframe.oden.eclipse.core.jobmanager.actions;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.jobmanager.AbstractJobManagerViewAction;
import anyframe.oden.eclipse.core.jobmanager.wizard.DeployByFileReqWizard;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of DeployByFileReqAction,
 * for the Anyframe Oden Job Manager view.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */
public class DeployByFileReqAction extends AbstractJobManagerViewAction {

	public DeployByFileReqAction() {
		
		super(
				UIMessages.ODEN_JOBMANAGER_Actions_NewFileRequestAction_NewFileRequest,
				UIMessages.ODEN_JOBMANAGER_Actions_NewFileRequestAction_NewFileRequestToolTip,
				UIMessages.ODEN_JOBMANAGER_Actions_NewFileRequestAction_NewFileRequestIcon);
	}

	public void run() {
		DeployByFileReqWizard wizard = new DeployByFileReqWizard(); 
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
	    wizard.setSeverName(OdenActivator.getDefault().getJObManagerView().getServerCombo().getText());
		dialog.open();
	}

}
