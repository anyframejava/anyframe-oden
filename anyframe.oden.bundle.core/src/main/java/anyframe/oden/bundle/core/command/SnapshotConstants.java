/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.core.command;

/**
 * Constants which are used by snapshot command. rollback command 
 * also access to this.
 * 
 * @author joon1k
 *
 */
public interface SnapshotConstants {

	public final static String PLAN_NODE = "plan";
	
	public final static String FILE_NODE = "file";
	
	// for plan
	public final static String REPO_URI = "repo-uri";
	
	public final static String REPO_LOC = "repo-loc";
	
	public final static String TARGET_LOC = "target-loc";
	
	// for file
	public final static String FILE_NAME = "file-name";
	
	public final static String FILE_SIZE = "file-size"; 
	
	// common
	public final static String DESC = "desc";
	
	public final static String FULL = "full";		// for compatibility
}
