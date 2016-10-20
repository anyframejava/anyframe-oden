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
 * Domain class for status info.
 * 
 * @author Hong JungHwan
 * @author LEE Sujeong
 *
 */
@SuppressWarnings("serial")
public class Status implements Serializable {

	private String jobname;
	private String id;
	private String date;
	private String status;
	private String desc;
	private String progress;
	private String totalWorks;

	public String getJobname() {
		return jobname;
	}

	/**
	 * 
	 * @param jobname
	 */
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	/**
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * 
	 * @param desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getProgress() {
		return progress;
	}

	/**
	 * 
	 * @param progress
	 */
	public void setProgress(String progress) {
		this.progress = progress;
	}
	
	public String getTotalWorks() {
		return totalWorks;
	}

	/**
	 * @param totalWorks
	 */
	public void setTotalWorks(String totalWorks) {
		this.totalWorks = totalWorks;
	}

}