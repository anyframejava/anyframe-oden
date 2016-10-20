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
package anyframe.oden.eclipse.core.dashboard;

/**
 * 
 * DataSet about dashboard. 
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class DashboardData {

	private String id;
	private String date;
	private int numItem;
	private int numSuccessDeploy;
	private int numExcuteDeploy;
	private boolean boolDeploySuccess;
	private String transferSuccess;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}
	
	public int getNumItem() {
		return numItem;
	}
	
	public void setNumItem(int numItem) {
		this.numItem = numItem;
	}
	
	public int getNumSuccessDeploy() {
		return numSuccessDeploy;
	}
	
	public void setNumSuccessDeploy(int numSuccessDeploy) {
		this.numSuccessDeploy = numSuccessDeploy;
	}
	
	public int getNumExcuteDeploy() {
		return numExcuteDeploy;
	}
	
	public void setNumExcuteDeploy(int numExcuteDeploy) {
		this.numExcuteDeploy = numExcuteDeploy;
	}
	
	public boolean isBoolDeploySuccess() {
		return boolDeploySuccess;
	}
	
	public void setBoolDeploySuccess(boolean boolDeploySuccess) {
		this.boolDeploySuccess = boolDeploySuccess;
	}
	
	public String getTransferSuccess() {
		return transferSuccess;
	}
	
	public void setTransferSuccess(String transferSuccess) {
		this.transferSuccess = transferSuccess;
	}
	
	public DashboardData(String id, int numItem, int numExcuteDeploy,
			int numSuccessDeploy, boolean boolDeploySuccess,
			String transferSuccess) {
		super();
		this.id = id;
		this.numItem = numItem;
		this.numSuccessDeploy = numSuccessDeploy;
		this.numExcuteDeploy = numExcuteDeploy;
		this.boolDeploySuccess = boolDeploySuccess;
		this.transferSuccess = transferSuccess;
	}

	public DashboardData() {
		super();
	}

}
