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
package anyframe.oden.bundle.ent.snapshot;

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
