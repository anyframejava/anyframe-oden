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
package anyframe.oden.eclipse.core;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Tree objects and parents for Anyframe Oden Eclipse plug-in's tree type views.
 * 
 * @author RHIE Jihwan
 * @author HONG JungHwan
 * @author LEE Sujeong
 * @version 1.0.0 RC1
 *
 */
public class OdenTrees {

	public static class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;

		public TreeObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
			return null;
		}

	}

	public static class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

	}
	
	public static class ServerRootParent extends TreeParent {
		
		public ServerRootParent(String name) {
			super(name);
		}
	}
	
	public static class ServerParent extends TreeParent {
		
		public ServerParent(String name) {
			super(name);
		}
	}
	
	public static class ServerChild extends TreeObject {
		
		public ServerChild(String name) {
			super(name);
		}
	}
	
	public static class RepoRootParent extends TreeParent {
		
		public RepoRootParent(String name) {
			super(name);
		}
	}

	public static class RepoParent extends TreeParent {
		
		public RepoParent(String name) {
			super(name);
		}
	}
	
	public static class RepoDirectory extends TreeParent {
		
		public RepoDirectory(String name) {
			super(name);
		}
	}

	public static class RepoFile extends TreeObject {
		
		public RepoFile(String name) {
			super(name);
		}
	}
}
