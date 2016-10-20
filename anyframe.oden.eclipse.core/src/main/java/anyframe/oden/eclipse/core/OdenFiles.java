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
package anyframe.oden.eclipse.core;

import java.io.File;

/**
 * The path and file name information for Anyframe Oden Eclipse plug-in.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class OdenFiles {

	public static final String USER_CONFIG_FOLDER = OdenActivator.getDefault().getStateLocation().toFile().getAbsolutePath();
	public static final String USER_SERVER_FILE_NAME = USER_CONFIG_FOLDER + File.separator + "OdenServers.xml";
	public static final String USER_REPOSITORY_FILE_NAME = USER_CONFIG_FOLDER + File.separator + "OdenRepositories.xml";
	public static final String USER_FILEREQUEST_FILE_NAME = USER_CONFIG_FOLDER + File.separator + "OdenFileRequests.xml";
	
	public static final String LICENSE_TRIAL_FILE_CONF = "configuration" + "/" + OdenActivator.PLUGIN_ID;
	public static final String LICENSE_FILE_NAME = "license.txt";
	public static final String TRIAL_FILE_NAME = "trial.txt";
	public static final String LICENSE_FILE_FULL_NAME = LICENSE_TRIAL_FILE_CONF + "/" + LICENSE_FILE_NAME;
	public static final String TRIAL_FILE_FULL_NAME = LICENSE_TRIAL_FILE_CONF + "/" + TRIAL_FILE_NAME;
}

