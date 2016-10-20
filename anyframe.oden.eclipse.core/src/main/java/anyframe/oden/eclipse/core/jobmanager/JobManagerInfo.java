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
package anyframe.oden.eclipse.core.jobmanager;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Represents the combination of an Agent name and a location variable 
 * for Deploy Now action on the selected Build Repository item 
 * with zero-configuration at run-time; 
 * if never configured, the default-location of all available Agent 
 * will be used for Deploy Now action.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */
public class JobManagerInfo {

	// propety strings for Job Done
	private String txSe;
	// 1: today 2: 1 weeks 3:1 months 4: others
	private String txId;
	private String desc;
	private String date;
	private String status;
	private String count;
	private String parent;
	private String progress;
	
	/**
	 * Default Constructor
	 */
	public JobManagerInfo() {
		super();
	}
	
	/**
	 * Constructor
	 * @param transaction Kind
	 * @param transaction ID
	 * @param transaction description
	 * @param transaction date
	 * @param transaction status
	 */
	public JobManagerInfo(String txSe, String txId , String  desc , String date , String status) {
		super();
		this.txSe = txSe;
		this.txId = txId;
		this.desc = desc;
		this.date = date;
		this.status = status;
	}
	
	/**
	 * Constructor
	 * @param transaction Kind
	 * @param transaction count
	 */
	public JobManagerInfo(String txSe, String count) {
		super();
		this.txSe = txSe;
		this.count = count;
	}
	
	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getTxSe() {
		return txSe;
	}

	public void setTxSe(String txSe) {
		this.txSe = txSe;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	public HashMap<String, String> getTxCount(ArrayList<JobManagerInfo> jobcount) {
		HashMap<String, String> hm = new HashMap<String, String>();
		
		for(JobManagerInfo jobdone : jobcount)
			hm.put(jobdone.txSe, jobdone.count);
			
		return hm;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

}
