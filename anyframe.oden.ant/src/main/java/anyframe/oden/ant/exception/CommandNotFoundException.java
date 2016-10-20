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
package anyframe.oden.ant.exception;

/**
 * Generic exception class for Anyframe Oden Eclipse plug-in.
 * 
 * @author LEE Sujeong
 * @version 1.2.0
 *
 */
@SuppressWarnings("serial")
public class CommandNotFoundException extends Exception{

	/**
	 * @param
	 */
	public CommandNotFoundException() {
		super();
	}
	
	/**
	 * @param string
	 * @param throwable
	 */
	public CommandNotFoundException(String string, Throwable throwable) {
		super(string, throwable);
	}
	
	/**
	 * @param string
	 */
	public CommandNotFoundException(String string) {
		super(string);
	}
	
	/**
	 * @param throwable
	 */
	public CommandNotFoundException(Throwable throwable) {
		super(throwable);
	}
	
}
