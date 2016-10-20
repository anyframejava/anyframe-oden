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

	// 1. agent command
	public static String ODEN_CLI_COMMAND_agent_info_json;
	
	// 2. history command
	public static String ODEN_CLI_COMMAND_history_info;
	public static String ODEN_CLI_COMMAND_history_redeploy;
	public static String ODEN_CLI_COMMAND_history_show;
	public static String ODEN_CLI_COMMAND_history_undo;
	
	// 3. command option
	public static String ODEN_CLI_OPTION_agent;
	public static String ODEN_CLI_OPTION_date;
	public static String ODEN_CLI_OPTION_locvarsign;
	public static String ODEN_CLI_OPTION_defaultlocsign;
	public static String ODEN_CLI_OPTION_desc;
	public static String ODEN_CLI_OPTION_desc_deploynow;
	public static String ODEN_CLI_OPTION_destination;
	public static String ODEN_CLI_OPTION_detail;
	public static String ODEN_CLI_OPTION_fetchloglistid;
	public static String ODEN_CLI_OPTION_hiddenfolder;
	public static String ODEN_CLI_OPTION_include;
	public static String ODEN_CLI_OPTION_json;
	public static String ODEN_CLI_OPTION_path;
	public static String ODEN_CLI_OPTION_plansource;
	public static String ODEN_CLI_OPTION_policy;
	public static String ODEN_CLI_OPTION_repository;
	public static String ODEN_CLI_OPTION_status;
	public static String ODEN_CLI_OPTION_targetagent;
	public static String ODEN_CLI_OPTION_userip;
	public static String ODEN_CLI_OPTION_delete;
	
	// 4. policy command
	public static String ODEN_CLI_COMMAND_policy_add;
	public static String ODEN_CLI_COMMAND_policy_delete;
	public static String ODEN_CLI_COMMAND_policy_info;
	public static String ODEN_CLI_COMMAND_policy_info_json;
	public static String ODEN_CLI_COMMAND_policy_tempname;
	
	// 5. repository command
	public static String ODEN_CLI_COMMAND_repository_show;
	
	// 6. snapshot command
	public static String ODEN_CLI_COMMAND_snapshot_add;
	public static String ODEN_CLI_COMMAND_snapshot_filedel;
	public static String ODEN_CLI_COMMAND_snapshot_fileinfo;
	public static String ODEN_CLI_COMMAND_snapshot_plandel;
	public static String ODEN_CLI_COMMAND_snapshot_planinfo;
	public static String ODEN_CLI_COMMAND_snapshot_rollback;
	public static String ODEN_CLI_COMMAND_snapshot_run;
	
	// 7. task command
	public static String ODEN_CLI_COMMAND_task_add;
	public static String ODEN_CLI_COMMAND_task_delete;
	public static String ODEN_CLI_COMMAND_task_info;
	public static String ODEN_CLI_COMMAND_task_info_json;
	public static String ODEN_CLI_COMMAND_task_run;
	public static String ODEN_CLI_COMMAND_task_tempname;
	public static String ODEN_CLI_COMMAND_task_test;
	
	// 8. status command
	public static String ODEN_CLI_COMMAND_status_info;
	public static String ODEN_CLI_COMMAND_status_stop;
	
	// 9. spectrum command
	public static String ODEN_CLI_COMMAND_spectrum_test;
	public static String ODEN_CLI_COMMAND_spectrum_run;
	public static String ODEN_CLI_COMMAND_spectrum_fetchlist;
	public static String ODEN_CLI_COMMAND_spectrum_fetchlog;
	public static String ODEN_CLI_COMMAND_spectrum_compare;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CommandMessages.class);
	}

	private CommandMessages() {
	}
}
