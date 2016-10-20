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
package org.anyframe.oden.admin.service;

import java.util.HashMap;
import java.util.List;

import org.anyframe.oden.admin.domain.Job;

import anyframe.common.Page;

/**
 * @version 1.0
 * @created 14-7-2010 占쏙옙占쏙옙 10:13:31
 * @author LEE Sujeong
 */
public interface JobService {

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Page test(Object page, String param, String opt) throws Exception;

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String run(String[] param, String opt, String job, Object objPage,
			String cmd, String userid) throws Exception;

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Page compare(Object objPage, String param, String flag)
			throws Exception;

	public void stop(String param) throws Exception;

	public HashMap compareHeader(String param) throws Exception;

	public Page findList(String param) throws Exception;

	public Job findByName(String param) throws Exception;

	public void update(String[] param, String[] cmd, String[] mappings,
			String jobname, String repository, String excludes)
			throws Exception;
	
	public void insert(String[] param, String[] cmd, String[] mappings,
			String jobname, String repository, String excludes)
			throws Exception;

	public void remove(String name) throws Exception;

	public List<HashMap> excel(String param) throws Exception;

	public Page loadMappings(String param) throws Exception;

	public Page findMappings(String param) throws Exception;
	
	public List<Job> findJob() throws Exception;
	
	public String rollback(String txid) throws Exception;

}