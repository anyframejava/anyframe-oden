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

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenFiles;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.utils.XMLUtil;

/**
 * Manages the list of File Request objects, which have File Request profile settings.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 *
 */
public class FileRequestManager implements ModelListener {

	private TreeMap<String, FileRequest> filerequests = new TreeMap<String, FileRequest>();
	private LinkedList<ModelListener> modelListners = new LinkedList<ModelListener>();
	private String[] protocolSet = {CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem, CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FTP};

	/**
	 * Loads File Requests from the user preference XML file
	 * @throws OdenException
	 */
	@SuppressWarnings("unchecked") // The expression of type List needs unchecked conversion to conform to List<Element>
	public void loadFileRequests(String file) throws OdenException {
		filerequests.clear();

		Element root = XMLUtil.readRoot(new File(file));
		if (root != null) {
			List<Element> list = root.elements(FileRequest.FILEREQUEST);
			if (list != null)
				for (Element elem : list)
					addFileRequest(new FileRequest(elem));
		}
	}

	/**
	 * Saves all the File Requests to the user preference XML file
	 * @throws OdenException
	 */
	public void saveFileRequests() throws OdenException {
		DefaultElement root = new DefaultElement(FileRequest.FILEREQUESTS);
		for (FileRequest filerequest : filerequests.values())
			root.add(filerequest.expressFileRequestInXML());

		XMLUtil.save(root, new File(OdenFiles.USER_FILEREQUEST_FILE_NAME));
	}

	/**
	 * Adds a File Request with a nickname as a key
	 * @param repository
	 */
	public void addFileRequest(FileRequest filerequest) {
		filerequests.put(filerequest.getNickname(), filerequest);
	}

	/**
	 * Gets a File Request with a given nickname
	 * @param nickname
	 * @return
	 */
	public FileRequest getFileRequest(String nickname) {
		return filerequests.get(nickname);
	}
	
	/**
	 * Removes a File Request with a given nickname
	 * @param nickname
	 */
	public void removeFileRequest(String nickname) {
		FileRequest filerequest = filerequests.remove(nickname);
		if (filerequest != null) {
			OdenActivator.getDefault().getAliasManager().getFileRequestManager().modelChanged();
		}
	}

	/**
	 * Provides a list of all the File Requests
	 * @return
	 */
	public Collection<FileRequest> getFileRequests() {
		return filerequests.values();
	}

	/**
	 * Returns "true" if the File Request is in the list
	 * @param repository
	 * @return
	 */
	public boolean contains(FileRequest filerequest) {
		return filerequests.values().contains(filerequest);
	}

	/**
	 * Adds a listener
	 * @param listener
	 */
	public void addListener(ModelListener listener) {
		modelListners.add(listener);
	}

	/**
	 * Removes a listener
	 * @param listener
	 */
	public void removeListener(ModelListener listener) {
		modelListners.remove(listener);
	}

	/**
	 * Called to notify that the list has changed
	 */
	public void modelChanged() {
		for (ModelListener listener : modelListners)
			listener.modelChanged();
	}

	/**
	 * @return the protocolSet
	 */
	public String[] getProtocolSet() {
		return protocolSet;
	}

	/**
	 * @param protocolSet the protocolSet to set
	 */
	public void setProtocolSet(String[] protocolSet) {
		this.protocolSet = protocolSet;
	}

}
