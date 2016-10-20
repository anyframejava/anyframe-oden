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
package anyframe.oden.eclipse.core.history;

/**
 * Model of History Search Result Value.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */
public class DeploymentHistoryViewDetails {

	private String deployId;

	private String deployItem;

	private String deployItemSize;
	
	private String deployItemMode;
	
	private String deployServer;

	private String deployPath;

	private String deployDate;

	private String deployerIp;

	private String deployStatus;

	private String totalQuery;
	
	private String undoAbsolutePath;
	
	private String undoFilePath;
	
	private String deployLog;
	
	public String getUndoFilePath() {
		return undoFilePath;
	}

	public void setUndoFilePath(String undoFilePath) {
		this.undoFilePath = undoFilePath;
	}

	public DeploymentHistoryViewDetails() {

	}

	public DeploymentHistoryViewDetails(String deployId, String deployItem, String deployItemSize , String deployItemMode ,
			String deployPath, String deployDate, String deployerIp, String deployStatus, String totalQuery, String deployServer) {
			
		this.deployId = deployId;
		this.deployItem = deployItem;
		this.deployItemSize = deployItemSize;
		this.deployItemMode = deployItemMode;
		this.deployPath = deployPath;
		this.deployDate = deployDate;

		this.deployerIp = deployerIp;
		this.deployStatus = deployStatus;
		this.totalQuery = totalQuery;
		this.deployServer = deployServer;
	}

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}

	public String getDeployItem() {
		return deployItem;
	}

	public void setDeployItem(String deployItem) {
		this.deployItem = deployItem;
	}
	
	public String getDeployItemSize() {
		return deployItemSize;
	}

	public void setDeployItemSize(String deployItemSize) {
		this.deployItemSize = deployItemSize;
	}
	
	public String getDeployItemMode() {
		return deployItemMode;
	}

	public void setDeployItemMode(String deployItemMode) {
		this.deployItemMode = deployItemMode;
	}
	
	public String getDeployPath() {
		return deployPath;
	}

	public void setDeployPath(String deployPath) {
		this.deployPath = deployPath;
	}

	public String getDeployDate() {
		return deployDate;
	}

	public void setDeployDate(String deployDate) {
		this.deployDate = deployDate;
	}

	public String getDeployerIp() {
		return deployerIp;
	}

	public void setDeployerIp(String deployerIp) {
		this.deployerIp = deployerIp;
	}

	public String getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
	}

	public String getTotalQuery() {
		return totalQuery;
	}

	public void setTotalQuery(String totalQuery) {
		this.totalQuery = totalQuery;
	}

	public String getDeployServer() {
		return deployServer;
	}

	public void setDeployServer(String deployServer) {
		this.deployServer = deployServer;
	}
	
	public String getUndoAbsolutePath() {
		return undoAbsolutePath;
	}

	public void setUndoAbsolutePath(String undoAbsolutePath) {
		this.undoAbsolutePath = undoAbsolutePath;
	}

	public String getDeployLog() {
		return deployLog;
	}

	public void setDeployLog(String deployLog) {
		this.deployLog = deployLog;
	}
}
