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
 * Define CommonMessages property
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
public abstract class CommonMessages extends NLS {
	private static final String BUNDLE_NAME = CommonMessages.class.getName();

	public static String ODEN_CommonMessages_Confirm_MessageSuf;
	public static String ODEN_CommonMessages_Exception_JSONExceptionLog;
	public static String ODEN_CommonMessages_Exception_KnownDlgTitle;
	public static String ODEN_CommonMessages_Exception_KnownExceptionLog;
	public static String ODEN_CommonMessages_Exception_UnknownDlgText;
	public static String ODEN_CommonMessages_Exception_UnknownDlgTitle;
	public static String ODEN_CommonMessages_Exception_UnknownExceptionLog;
	public static String ODEN_CommonMessages_NameAlreadyExists;
	public static String ODEN_CommonMessages_NameShouldBeSpecified;
	public static String ODEN_CommonMessages_OperationFailed;
	public static String ODEN_CommonMessages_OperationSucceeded;
	public static String ODEN_CommonMessages_ProtocolString_File;
	public static String ODEN_CommonMessages_ProtocolString_FTP;
	public static String ODEN_CommonMessages_ProtocolString_HTTP;
	public static String ODEN_CommonMessages_ProtocolString_HTTPsuf;
	public static String ODEN_CommonMessages_SelectItemFirst;
	public static String ODEN_CommonMessages_SetConfigXML;
	public static String ODEN_CommonMessages_Title_ConfirmDelete;
	public static String ODEN_CommonMessages_Title_ConfirmSave;
	public static String ODEN_CommonMessages_Title_Error;
	public static String ODEN_CommonMessages_Title_Information;
	public static String ODEN_CommonMessages_Title_Warning;
	public static String ODEN_CommonMessages_UnableToConnectServer;

	public static String ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem;
	public static String ODEN_ALIAS_RepositoryManager_ProtocolSet_FTP;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CommonMessages.class);
	}

	private CommonMessages() {
	}
}
