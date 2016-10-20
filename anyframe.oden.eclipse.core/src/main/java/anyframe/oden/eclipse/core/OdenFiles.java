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
	public static final String USER_AGENT_FILE_NAME = USER_CONFIG_FOLDER + File.separator + "OdenServers.xml";
	public static final String USER_REPOSITORY_FILE_NAME = USER_CONFIG_FOLDER + File.separator + "OdenRepositories.xml";

}
