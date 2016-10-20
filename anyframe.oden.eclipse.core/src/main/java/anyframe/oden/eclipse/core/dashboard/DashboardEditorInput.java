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
package anyframe.oden.eclipse.core.dashboard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Editorinput to open dashboard editor. This class impl IEditorInput class.
 * 
 * @author LEE Sujeong
 * 
 */
public class DashboardEditorInput implements IEditorInput {

	private String nickname;

	public DashboardEditorInput(Server server) {
		this.nickname = server.getNickname();
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// return null;
		ImageDescriptor image = ImageUtil
				.getImageDescriptor(UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardIcon);
		return image;
	}

	public String getName() {
		return this.nickname + UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardSuf;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return ""; //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public Server getServer() {
		return OdenActivator.getDefault().getAliasManager().getServerManager()
				.getServer(this.nickname);
	}
}
