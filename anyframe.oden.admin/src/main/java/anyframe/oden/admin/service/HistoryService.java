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
package anyframe.oden.admin.service;

import java.util.List;

import anyframe.common.Page;
import anyframe.oden.admin.domain.Job;
import anyframe.oden.admin.domain.Log;

/**
 * @version 1.0
 * @created 14-7-2010 ���� 10:13:33
 * @author HONG JungHwan
 */
public interface HistoryService {

	public Page show(Object objPage, String param, String opt) throws Exception;

	public Page findByPk(Object objPage, String param) throws Exception;

	public List<Job> findJob() throws Exception;

	public String undo(String param) throws Exception;

	public String redeploy(String param) throws Exception;

	public List<Log> findByPkExcel(String param) throws Exception;

	public List<Log> showExcel(String txid , String param) throws Exception;

}