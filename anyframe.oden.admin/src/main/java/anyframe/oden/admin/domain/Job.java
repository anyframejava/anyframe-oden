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
package anyframe.oden.admin.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Domain class for job info.
 * 
 * @author Hong JungHwan
 * @author LEE Sujeong
 * 
 */
@SuppressWarnings("serial")
public class Job implements Serializable {

	/**
	 * 
	 */
	private String id = "";
	private String jobname = "";
	private String date = "";
	private String status = "";
	private String repo = "";
	private String includes = "";
	private String excludes = "";
	private List<Target> target = null;
	private String mode = "";
	private String file = "";
	private String destination = "";
	private String txId = "";
	private String hidden = ""; // checked option hidden property
	private String toggle = "";
	private List<Command> command = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getTxId() {
		return txId;
	}

	public String getJobname() {
		return jobname;
	}

	/**
	 * @param jobname
	 */
	public void setJobname(String jobname) {
		this.jobname = jobname;
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

	public String getRepo() {
		return repo;
	}

	/**
	 * 
	 * @param repo
	 */
	public void setRepo(String repo) {
		this.repo = repo;
	}

	public String getIncludes() {
		return includes;
	}

	/**
	 * 
	 * @param include
	 */
	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public String getExcludes() {
		return excludes;
	}

	/**
	 * 
	 * @param exclude
	 */
	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public List<Target> getTarget() {
		return target;
	}

	/**
	 * 
	 * @param List
	 *            <Target>
	 */
	public void setTarget(List<Target> target) {
		this.target = target;
	}

	public String getMode() {
		return mode;
	}

	/**
	 * 
	 * @param mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
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

	public String getDestination() {
		return destination;
	}

	/**
	 * 
	 * @param destination
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public String getHidden() {
		return hidden;
	}

	public void setToggle(String toggle) {
		this.toggle = toggle;
	}

	public String getToggle() {
		return toggle;
	}

	public List<Command> getCommand() {
		return command;
	}

	/**
	 * @param command
	 */
	public void setCommand(List<Command> command) {
		this.command = command;
	}

}