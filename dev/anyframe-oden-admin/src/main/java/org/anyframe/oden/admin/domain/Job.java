/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.admin.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Domain class for job info.
 * 
 * @author Junghwan Hong
 * @author Sujeong Lee
 */
@SuppressWarnings("serial")
public class Job implements Serializable {

	/**
	 * 
	 */
	private String id = "";
	private String name = "";
	private String group="";
	private String date = "";
	private String buildDate = "";
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
	private String build;
	
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

	/**
	 * @param jobname
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param jobGroup
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	/**
	 * 
	 * @param date
	 */
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * 
	 * @param buildDate
	 */
	public String getBuildDate() {
		return buildDate;
	}
	
	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	
	/**
	 * 
	 * @param status
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 
	 * @param repo
	 */
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}


	/**
	 * 
	 * @param include
	 */
	public String getIncludes() {
		return includes;
	}
	
	public void setIncludes(String includes) {
		this.includes = includes;
	}

	/**
	 * 
	 * @param exclude
	 */
	public String getExcludes() {
		return excludes;
	}
	
	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	/**
	 * 
	 * @param List
	 *            <Target>
	 */
	public List<Target> getTarget() {
		return target;
	}
	
	public void setTarget(List<Target> target) {
		this.target = target;
	}

	/**
	 * 
	 * @param mode
	 */
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * 
	 * @param file
	 */
	public String getFile() {
		return file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * 
	 * @param destination
	 */
	public String getDestination() {
		return destination;
	}
	
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

	/**
	 * @param command
	 */
	public List<Command> getCommand() {
		return command;
	}
	
	public void setCommand(List<Command> command) {
		this.command = command;
	}
	
	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}
}