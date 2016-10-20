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
package org.anyframe.oden.bundle.hessiancli;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.caucho.hessian.client.HessianProxyFactory;

public class OdenProxyFactory extends HessianProxyFactory {
	int timeout;
	long readTimeout;
	
	public OdenProxyFactory(int timeout, long readTimeout) {
		this.timeout = timeout;
		this.readTimeout = readTimeout;
	}

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		setReadTimeout(readTimeout);
		URLConnection conn = super.openConnection(url);
		conn.setConnectTimeout(timeout);
		return conn;
	}
}
