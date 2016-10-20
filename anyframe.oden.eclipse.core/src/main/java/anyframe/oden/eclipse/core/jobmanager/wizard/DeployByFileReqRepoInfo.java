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
package anyframe.oden.eclipse.core.jobmanager.wizard;

/**
 * The DataSet of Deploy by file request repository,
 * for the Anyframe Oden Job Manager view. 
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */
public class DeployByFileReqRepoInfo {

	private String type;
	private String name;
	private String date;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public DeployByFileReqRepoInfo(String type , String name , String date) {
		this.type = type;
		this.name = name;
		this.date = date;
	}
	
	public String getFileTree() {
		return this.name + " " + "[" + this.date + "]"; 
	}
}
