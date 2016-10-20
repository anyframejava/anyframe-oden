/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
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
package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;

/**
 * Domain class for history info.
 * 
 * @author junghwan.hong
 * 
 */
public class CfgHistoryDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String source;

	private String mode;

	private String errorlog;

	private String path;

	private String targets;

	private String success;

	public CfgHistoryDetail(String source, String path , String mode, String errorlog,
			String targets, String success) {
		this.source = source;
		this.path = path;
		this.mode = mode;
		this.errorlog = errorlog;
		this.targets = targets;
		this.success = success;
	}

	public String getSource() {
		return source;
	}

	public String getMode() {
		return mode;
	}

	public String getErrorlog() {
		return errorlog;
	}

	public String getPath() {
		return path;
	}

	public String getTargets() {
		return targets;
	}

	public String getSuccess() {
		return success;
	}

}
