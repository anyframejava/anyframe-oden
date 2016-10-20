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
	public static String ODEN_CommonMessages_Title_Confirmation;
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
