/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class BundleUtil {
	public static Object getService(BundleContext ctx, Class clz) {
		List<Object> svcs = getServices(ctx, clz);
		if(svcs.size() == 0)
			return null;
		return svcs.get(0);
	}
	
	public static List<Object> getServices(BundleContext ctx, Class clz) {
		try{
			ServiceReference[] refs = ctx.getServiceReferences(clz.getName(), null);
			List<Object> svcs = new ArrayList<Object>();
			for(int i=0; i<refs.length; i++)
				svcs.add(ctx.getService(refs[i]));
			return svcs;
		}catch(Throwable t){
		}
		return Collections.EMPTY_LIST;
	}
}
