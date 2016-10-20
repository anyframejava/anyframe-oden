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
package anyframe.oden.admin.common;

import anyframe.oden.admin.exception.BrokerException;


/**
 * This class provides some methods to interface server 
 * 
 * @author HONG JungHwan
 *
 */
public interface OdenBrokerService {
	/**
     * sendRequest and result Response
     * 
     * @param shellUrl
     * @param msg
     * @return response
     * @throws BrokerException
	 * @throws CommandNotFoundException 
     */
	
	public String sendRequest(String shellUrl, String msg) throws Exception;
	
	public boolean checkUser(String shellUrl , String userid, String password) throws Exception;
	
}
