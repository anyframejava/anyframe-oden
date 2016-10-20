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
package org.anyframe.oden.admin.common.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class LoggingAspect {

//	@Pointcut("execution(* test1..*Impl.*(..)) || execution(* anyframe.core.ria.mip..*MiPServiceImpl.*(..))")
	@Pointcut("execution(* anyframe.oden.admin..*Impl.*(..))")
	public void serviceMethod() {
	}

	@Before("serviceMethod()")
	public void beforeLogging(JoinPoint thisJoinPoint) {
		Class<? extends Object> clazz = thisJoinPoint.getTarget().getClass();
		String methodName = thisJoinPoint.getSignature().getName();
		Object[] arguments = thisJoinPoint.getArgs();

		StringBuffer argBuf = new StringBuffer();
		StringBuffer argValueBuf = new StringBuffer();
		int i = 0;
		for (Object argument : arguments) {
			String argClassName = argument.getClass().getSimpleName();
			if (i > 0) {
				argBuf.append(", ");
			}
			argBuf.append(argClassName + " arg" + ++i);
			argValueBuf.append(".arg" + i + " : " + argument.toString() + "\n");
		}

		if (i == 0) {
			argValueBuf.append("No arguments\n");
		}

		StringBuffer messageBuf = new StringBuffer();
		messageBuf.append("before executing " + methodName + "("
				+ argBuf.toString() + ") method");
		messageBuf
				.append("\n-------------------------------------------------------------------------------\n");
		messageBuf.append(argValueBuf.toString());
		messageBuf
				.append("-------------------------------------------------------------------------------");

		Log logger = LogFactory.getLog(clazz);
		if (logger.isDebugEnabled()) {
			logger.debug(messageBuf.toString());
		}
	}
}
