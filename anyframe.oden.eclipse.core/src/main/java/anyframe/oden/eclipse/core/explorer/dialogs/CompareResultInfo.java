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
package anyframe.oden.eclipse.core.explorer.dialogs;

/**
 * DataSet about compare result. 
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class CompareResultInfo {

	private String fileName;
	private String directory;
	private String[] agents;
	private String[] size;
	private String[] date;
	private String match;
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getDirectory() {
		return directory;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public void setAgents(String[] agents) {
		this.agents = agents;
	}

	public String[] getAgents() {
		return agents;
	}
	
	public String[] getSize() {
		return size;
	}
	
	public void setSize(String[] size) {
		this.size = size;
	}
	
	public String[] getDate() {
		return date;
	}
	
	public void setDate(String[] date) {
		this.date = date;
	}

	public String getMatch() {
		return match;
	}
	
	public void setMatch(String match) {
		this.match = match;
	}

	
	
}
