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
package anyframe.oden.eclipse.core.alias;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import anyframe.oden.eclipse.core.OdenActivator;


/**
 * Represents a configured Server profile.
 * This class extends Alias class and implements some "Server" specific cases.
 * 
 * @author HongJungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 *
 */
public class ServerEditorInput implements  IEditorInput  {

	private String nickname;

	public ServerEditorInput(Server server) {
		this.nickname = server.getNickname();
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

	public Server getServer() {
		return OdenActivator.getDefault().getAliasManager().getServerManager()
		.getServer(this.nickname);
	}

}
