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
package anyframe.oden.eclipse.core.messages;

import org.eclipse.osgi.util.NLS;

/**
 * The externalized strings for Anyframe Oden Eclipse plug-in.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
public abstract class CommandMessages extends NLS {
	private static final String BUNDLE_NAME = CommandMessages.class.getName();

	public static String ODEN_EDITORS_PolicyPage_MsgAgentInfo;
	public static String ODEN_EDITORS_PolicyPage_MsgDetailShow;
	public static String ODEN_EDITORS_PolicyPage_MsgPolicyDele;
	public static String ODEN_EDITORS_PolicyPage_MsgPolicySave;
	public static String ODEN_EDITORS_PolicyPage_MsgPolicyShow;
	public static String ODEN_EDITORS_TaskPage_MsgPolicyInfo;
	public static String ODEN_EDITORS_TaskPage_MsgTaskAdd;
	public static String ODEN_EDITORS_TaskPage_MsgTaskDel;
	public static String ODEN_EDITORS_TaskPage_MsgTaskDescOpt;
	public static String ODEN_EDITORS_TaskPage_MsgTaskInfo;
	public static String ODEN_EDITORS_TaskPage_MsgTaskPolicyOpt;
	public static String ODEN_EDITORS_TaskPage_MsgTaskRun;
	public static String ODEN_EDITORS_TaskPage_MsgTaskShow;

	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyAddCommand_Action;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyAddCommand_Exclude;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyAddCommand_Include;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyDel;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyOpt;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_RepoShow;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskAdd1;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskAdd2;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskDel;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskRun;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_TempPolicyName;
	public static String ODEN_EXPLORER_Dialogs_DeployItemDialog_TempTaskName;
	public static String ODEN_EXPLORER_ExplorerView_HiddenFolder;
	public static String ODEN_EXPLORER_ExplorerView_Msg_RepoShow;
	public static String ODEN_EXPLORER_ExplorerViewContentProvider_MsgRepsitoryShow;

	public static String ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
	public static String ODEN_HISTORY_DeploymentHistoryView_History_Opt_Agent;
	public static String ODEN_HISTORY_DeploymentHistoryView_History_Opt_Date;
	public static String ODEN_HISTORY_DeploymentHistoryView_History_Opt_Host;
	public static String ODEN_HISTORY_DeploymentHistoryView_History_Opt_Name;
	public static String ODEN_HISTORY_DeploymentHistoryView_History_Opt_Path;
	public static String ODEN_HISTORY_DeploymentHistoryView_History_Opt_Status;
	public static String ODEN_HISTORY_DeploymentHistoryView_Msg_History_Show;

	public static String ODEN_SNAPSHOT_Actions_MsgDelFile;
	public static String ODEN_SNAPSHOT_Actions_MsgDelPlan;
	public static String ODEN_SNAPSHOT_Actions_MsgRollback;
	public static String ODEN_SNAPSHOT_Actions_MsgRun;
	public static String ODEN_SNAPSHOT_Dialogs_NewPlanCmdDesc;
	public static String ODEN_SNAPSHOT_Dialogs_NewPlanCmdDest;
	public static String ODEN_SNAPSHOT_Dialogs_NewPlanCmdHead;
	public static String ODEN_SNAPSHOT_Dialogs_NewPlanCmdSource;
	public static String ODEN_SNAPSHOT_SnapshotView_MsgInfoFile;
	public static String ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CommandMessages.class);
	}

	private CommandMessages() {
	}
}
