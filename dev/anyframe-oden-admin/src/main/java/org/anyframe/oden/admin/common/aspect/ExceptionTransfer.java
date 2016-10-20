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

import java.util.Locale;

import javax.inject.Inject;

import org.anyframe.oden.admin.exception.BrokerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.framework.Advised;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import anyframe.common.exception.BaseException;

@Aspect
@Service
public class ExceptionTransfer {

	@Pointcut("execution(* anyframe.oden.admin..*ServiceImpl.*(..))")
	public void serviceMethod(){}

	@Inject
	private MessageSource messageSource;
	
	@AfterThrowing(pointcut="serviceMethod()", throwing="exception")
	public void transfer(JoinPoint thisJoinPoint, Exception exception)
			throws BrokerException {
		Object target = thisJoinPoint.getTarget();
		while (target instanceof Advised) {
			try {
				target = ((Advised) target).getTargetSource().getTarget();
			} catch (Exception e) {
				LogFactory.getLog(this.getClass()).error(
						"Fail to get target object from JointPoint.", e);
				break;
			}
		}

		String className = target.getClass().getSimpleName().toLowerCase();
		String opName = (thisJoinPoint.getSignature().getName()).toLowerCase();
		Log logger = LogFactory.getLog(target.getClass());

		// Oden Server Interface Exception
		if (exception instanceof BrokerException) {
			BrokerException odenEx = (BrokerException) exception;
			String msg = messageSource.getMessage(odenEx.getMessage(),
					new String[] {}, Locale.getDefault());
			if(! odenEx.getMessage().contains("broker.info"))
				logger.error(msg, odenEx);
			throw new BrokerException(msg, odenEx);
		}
		
		// Processing serviceImpl Exception
		if (exception instanceof BaseException) {
			BaseException baseEx = (BaseException) exception;
			logger.error(baseEx.getMessage(), baseEx);
			throw new BrokerException(messageSource, "error." + className + "."
					+ opName, new String[] {}, exception);
		}		

		logger.error(messageSource.getMessage("error." + className + "."
				+ opName, new String[] {}, "no messages", Locale.getDefault()),
				exception);

		throw new BrokerException(messageSource, "error." + className + "."
				+ opName, new String[] {}, exception);
	}
}
