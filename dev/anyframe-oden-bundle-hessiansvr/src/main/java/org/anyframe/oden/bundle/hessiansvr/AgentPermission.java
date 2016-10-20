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
package org.anyframe.oden.bundle.hessiansvr;

//import java.io.FilePermission;
//import java.io.IOException;
//import java.io.SerializablePermission;
//import java.net.NetPermission;
//import java.net.SocketPermission;
//import java.util.PropertyPermission;
//
//import org.osgi.framework.AdminPermission;
//import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * This is AgentPermission class.
 * 
 * @author Junghwan Hong
 */
public class AgentPermission {
	private final static String ROSGI = "ch.ethz.iks.r_osgi.remote";

	private BundleContext context;

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();

//		try{
//			if(pa == null) return;
//		
//			final String server = this.context.getProperty("oden.server");
//			if(server == null) return; // ignore
//		
//			final String rosgi_loc = rosgiLocation();
//			if(rosgi_loc == null){
//				Logger.error(new IOException("Couldn't find this bundle: " + ROSGI));
//				return;
//			}
//		
//			pa.setPermissions(rosgi_loc, new PermissionInfo[]{
//					new PermissionInfo( NetPermission.class.getName(), "specifyStreamHandler", null),
//					new PermissionInfo( RuntimePermission.class.getName(), "getClassLoader", null),
//					new PermissionInfo( FilePermission.class.getName(), "*", "read,write,delete"),
//					new PermissionInfo( SerializablePermission.class.getName(), "enableSubclassImplementation", "*"),
//					new PermissionInfo( PropertyPermission.class.getName(), "*", "read,write"),
//					new PermissionInfo( AdminPermission.class.getName(), "*", "*"),
//					new PermissionInfo( PackagePermission.class.getName(), "*", "export,import"),
//					new PermissionInfo( ServicePermission.class.getName(), "*", "get,register"),
//					new PermissionInfo( SocketPermission.class.getName(), "*", "connect,listen,resolve"),
//					new PermissionInfo( SocketPermission.class.getName(), server, "accept")});
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}

//	private PermissionAdmin pa;
//
//	protected void setPermissionAdmin(PermissionAdmin pa){
//		this.pa = pa;
//	}
//
//	private String rosgiLocation(){
//		for(Bundle b : context.getBundles()) {
//			if(b.getSymbolicName().equals(ROSGI))
//				return b.getLocation(); 
//		}
//		return null;
//	}
}
