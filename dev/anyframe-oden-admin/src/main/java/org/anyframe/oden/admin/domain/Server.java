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
import java.util.List;

/**
 * Domain class for server info in job.
 * 
 * @author Hong JungHwan
 * @author LEE Sujeong
 *
 */
@SuppressWarnings("serial")
public class Server implements Serializable {

//	private static final long serialVersionUID = 1L;
	private String status;
	private String file;
	private List<Fileinfo> fileinfo;
	private String jobname;
	private String jobId;
	private String date;

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

	public String getFile() {
		return file;
	}

	/**
	 * 
	 * @param file
	 */
	public void setFile(String file) {
		this.file = file;
	}

	public List<Fileinfo> getFileinfo() {
		return fileinfo;
	}

	/**
	 * 
	 * @param file
	 */
	public void setFileinfo(List<Fileinfo> fileinfo) {
		this.fileinfo = fileinfo;
	}

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

	public String getJobid() {
		return jobId;
	}

	/**
	 * 
	 * @param jobid
	 */
	public void setJobid(String jobId) {
		this.jobId = jobId;
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

}