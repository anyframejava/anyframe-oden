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
package anyframe.oden.eclipse.core.explorer.actions;

import java.util.Set;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Edit an existing agent action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class DeleteRepositoryAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	public DeleteRepositoryAction() {
		super(
				OdenMessages.ODEN_EXPLORER_Actions_DeleteRepositoryAction_RemoveRepository,
				OdenMessages.ODEN_EXPLORER_Actions_DeleteRepositoryAction_RemoveRepositoryToolTip,
				OdenMessages.ODEN_EXPLORER_Actions_DeleteRepositoryAction_RemoveRepositoryIcon);
	}

	/**
	 * 
	 */
	public void run() {
		Repository repository = getView().getSelectedRepository(false);

		if (DialogUtil.confirmMessageDialog(
				OdenMessages.ODEN_CommonMessages_Title_ConfirmDelete,
				OdenMessages.ODEN_EXPLORER_Actions_DeleteRepositoryAction_ConfirmDelete_MessagePre +
				repository.getNickname() +
				OdenMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {

			if (repository != null) {
				OdenActivator.getDefault().getAliasManager().getRepositoryManager().removeRepository(repository.getNickname());
			}

			// notify that there has been changes
			OdenActivator.getDefault().getAliasManager().getRepositoryManager().modelChanged();

			// reload data for data consistency
			try {
				OdenActivator.getDefault().getAliasManager().save();
				OdenActivator.getDefault().getAliasManager().load();
			} catch (OdenException odenException) {
				OdenActivator.error("Exception occured while reloading Build Repository profiles.", odenException);
				odenException.printStackTrace();
			}

			getView().refresh();
		}
	}

	/**
	 * 
	 */
	public boolean isAvailable() {
		if (getView() == null) {
			return false;
		}

		Set<Repository> repositories = getView().getSelectedRepositories(false);

		if (repositories.isEmpty()) {
			return false;
		}

		return true;
	}

}
