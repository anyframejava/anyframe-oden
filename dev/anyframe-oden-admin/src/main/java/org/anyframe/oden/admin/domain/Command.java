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
package org.anyframe.oden.admin.domain;

import java.io.Serializable;

/**
 * Domain class for command info.
 * 
 * @author Hong JungHwan
 * @author LEE Sujeong
 *
 */
@SuppressWarnings("serial")
public class Command implements Serializable {

	private String name;
	private String path;
	private String cmd;
	private String hidden;
	private String hiddenname;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public String getHidden() {
		return hidden;
	}

	public void setHiddenname(String hiddenname) {
		this.hiddenname = hiddenname;
	}

	public String getHiddenname() {
		return hiddenname;
	}

}