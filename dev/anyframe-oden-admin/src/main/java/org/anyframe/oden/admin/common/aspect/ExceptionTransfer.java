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
