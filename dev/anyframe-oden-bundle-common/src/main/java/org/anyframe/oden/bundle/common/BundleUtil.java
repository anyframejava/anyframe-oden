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
package org.anyframe.oden.bundle.common;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * One of several ways to register the OSGi service.
 * 
 * @author Junghwan Hong
 */
public class BundleUtil {
	public static Object getService(BundleContext ctx, Class clz) {
		List<Object> svcs = getServices(ctx, clz);
		if (svcs.isEmpty()) {
			return null;
		}
		return svcs.get(0);
	}

	/**
	 * method to get the osgi service from the bundle context
	 * 
	 * @param ctx
	 * @param clz
	 * @return
	 */
	public static List<Object> getServices(BundleContext ctx, Class clz) {
		try {
			ServiceReference[] refs = ctx.getServiceReferences(clz.getName(),
					null);
			List<Object> svcs = new ArrayList<Object>();
			for (int i = 0; i < refs.length; i++) {
				svcs.add(ctx.getService(refs[i]));
			}
			return svcs;
		} catch (Throwable t) {
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * method to get the oden home directory. Through this you can get the oden
	 * home directory regardless of where you launch the oden.
	 * 
	 * @return
	 */
	public static File odenHome() {
		try {
			URL url = new URL(BundleUtil.class.getProtectionDomain()
					.getCodeSource().getLocation().toString());
			return new File(url.getPath()).getParentFile().getParentFile();
		} catch (Exception e) {
			return new File("..");
		}
	}
}
