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
package anyframe.oden.eclipse.core.explorer;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenTrees.RepoParent;
import anyframe.oden.eclipse.core.OdenTrees.RepoRootParent;
import anyframe.oden.eclipse.core.OdenTrees.ServerParent;
import anyframe.oden.eclipse.core.OdenTrees.ServerRootParent;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.AliasManager;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.alias.RepositoryManager;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.alias.ServerManager;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Tree content provider for Oden view outline.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class ExplorerViewContentProvider implements ITreeContentProvider,IStructuredContentProvider{
	private TreeParent invisibleRoot;
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
		if(inputElement instanceof AliasManager){
			if (invisibleRoot == null)
				initialize(inputElement);
			return getChildren(invisibleRoot);
		}
		return getChildren(inputElement);
	}

	/**
	 * 
	 */
	public void dispose() {

	}

	/**
	 * 
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	private void initialize(Object inputElement) {

		String[] roots = {
				UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel,
				UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel };
		invisibleRoot = new TreeParent("");
		for (String rootnm : roots) {

			if (rootnm.equals(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel)) {
				ServerRootParent root = new ServerRootParent(rootnm);
				// 1 Level TreeContents
				invisibleRoot.addChild(root);

				// 2 Level Servers
				Collection<Server> col = OdenActivator.getDefault().getAliasManager().getServerManager().getServers();
				for (Server server : col) 
					root.addChild(new ServerParent(server.getNickname()));
				
			} else {
				RepoRootParent root = new RepoRootParent(rootnm);
				invisibleRoot.addChild(root);

				// 2 Level Repositories
				Collection<Repository> col = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepositories();
				for (Repository repo : col) 
					root.addChild(new RepoParent(repo.getNickname()));
			}
		}
	}
}
