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
package anyframe.oden.eclipse.core.explorer;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import anyframe.oden.eclipse.core.OdenActivator;
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
		// 1 Level TreeContents
		String[] roots = {UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel, UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel};
		invisibleRoot = new TreeParent("");
		for(String rootnm : roots){
			TreeParent root = new TreeParent(rootnm);
			invisibleRoot.addChild(root);

			if(rootnm.equals(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel)){
				// 2 Level Servers 
				Collection<Server> col = OdenActivator.getDefault().getAliasManager()
				.getServerManager().getServers();
				for (Server server : col){
					TreeParent sub = new TreeParent(server.getNickname());
					root.addChild(sub);
				}
			} else {
				// 2 Level Repositories
				Collection<Repository> col = OdenActivator.getDefault().getAliasManager()
				.getRepositoryManager().getRepositories();
				for (Repository repo : col){
					TreeParent sub = new TreeParent(repo.getNickname());
					root.addChild(sub);
				}
			}
		}

	}
}
