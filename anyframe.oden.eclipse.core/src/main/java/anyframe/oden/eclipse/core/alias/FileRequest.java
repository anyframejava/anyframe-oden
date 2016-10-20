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
package anyframe.oden.eclipse.core.alias;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import anyframe.oden.eclipse.core.OdenActivator;

/**
 * Represents a configured File Request profile. This class extends Repository class
 * and implements some "File Request" specific cases.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
public class FileRequest extends Repository {

	static final String FILEREQUESTS = "filerequests"; 
	static final String FILEREQUEST = "filerequest"; 

	static final String DESCRIPTION = "desc";

	private String desc;
	
	private static int filerequestSerialNo = 0;
	
	/**
	 * Constructs a new File Request with a given nickname
	 * 
	 * @param nickname
	 */
	public FileRequest(String nickname) {
		this.nickname = nickname;
	}
	
	/**
	 * Constructs a new File Request with a unique nickname
	 */
	public FileRequest() {
		this("new-repository-" + (++filerequestSerialNo));
	}
	
	/**
	 * Constructs a File Request from stored XML
	 * 
	 * @param root
	 */
	public FileRequest(Element root) {
		serverToUse = root.attributeValue(SERVER_TO_USE);
		String hasNoUserNameString = root.attributeValue(HAS_NO_USER_NAME);
		if (hasNoUserNameString != null)
			hasNoUserName = Boolean.parseBoolean(hasNoUserNameString);
		nickname = root.elementText(NICKNAME);
		url = root.elementText(URL);
		protocol = root.elementText(PROTOCOL);
		path = root.elementText(PATH);
		desc = root.elementText(DESCRIPTION);

		if (hasNoUserName) {
			setUser("anonymous"); 
			setPassword(""); 
		} else {
			user = root.elementText(USER);
			password = root.elementText(PASSWORD);
		}
	}

	/**
	 * Expresses this File Request in XML expression
	 * 
	 * @return
	 */
	public Element expressFileRequestInXML() {
		DefaultElement root = new DefaultElement(FILEREQUEST);
		root.addAttribute(SERVER_TO_USE, serverToUse);
		root.addAttribute(HAS_NO_USER_NAME, Boolean.toString(hasNoUserName));
		root.addElement(NICKNAME).setText(nickname);
		root.addElement(DESCRIPTION).setText(desc);
		root.addElement(URL).setText(url);
		root.addElement(PROTOCOL).setText(protocol);
		root.addElement(PATH).setText(path);
		root.addElement(USER).setText(user);
		root.addElement(PASSWORD).setText(password);

		return root;
	}

	/**
	 * Removes a File Request with a given nickname
	 */
	public void remove() {
		OdenActivator.getDefault().getAliasManager().getRepositoryManager()
				.removeRepository(getNickname());
	}

	/**
	 * @return the description
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
