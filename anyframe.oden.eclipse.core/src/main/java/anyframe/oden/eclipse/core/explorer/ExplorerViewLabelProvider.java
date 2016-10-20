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
package anyframe.oden.eclipse.core.explorer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import anyframe.oden.eclipse.core.OdenTrees.RepoDirectory;
import anyframe.oden.eclipse.core.OdenTrees.RepoFile;
import anyframe.oden.eclipse.core.OdenTrees.RepoParent;
import anyframe.oden.eclipse.core.OdenTrees.RepoRootParent;
import anyframe.oden.eclipse.core.OdenTrees.ServerChild;
import anyframe.oden.eclipse.core.OdenTrees.ServerParent;
import anyframe.oden.eclipse.core.OdenTrees.ServerRootParent;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Tree label provider for Oden view outline.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public  class ExplorerViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider{

	// icon for the "Servers" root
	private ImageDescriptor _serverRootImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootIcon);
	private Image _serverRootImage = ImageUtil.getImage(_serverRootImageDescriptor);

	// icon for the Servers
	private ImageDescriptor _serverImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServerIcon);
	private Image _serverImage = ImageUtil.getImage(_serverImageDescriptor);

	// icon for Agents at normal status
	private ImageDescriptor _agentNormalImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentNormalIcon);
	private Image _agentNormalImage = ImageUtil.getImage(_agentNormalImageDescriptor);

	// icon for Agents at abnormal status
	private ImageDescriptor _agentAbnormalImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentAbnormalIcon);
	private Image _agentAbnormalImage = ImageUtil.getImage(_agentAbnormalImageDescriptor);

	// icon for Agent at unknown status
	private ImageDescriptor _agentUnknownImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentUnknownIcon);
	@SuppressWarnings("unused")
	private Image _agentUnknownImage = ImageUtil.getImage(_agentUnknownImageDescriptor);

	// Icon for the "Build Repositories" root
	private ImageDescriptor _repositoryRootImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootIcon);
	private Image _repositoryRootImage = ImageUtil.getImage(_repositoryRootImageDescriptor);

	// icon for the Build Repositories
	private ImageDescriptor _repositoryImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoryIcon);
	private Image _repositoryImage = ImageUtil.getImage(_repositoryImageDescriptor);

	// icon for the folder
	private ImageDescriptor _folderImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FolderIcon);
	private Image _folderImage = ImageUtil.getImage(_folderImageDescriptor);

	// icon for the file
	private ImageDescriptor _fileImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FileIcon);
	private Image _fileImage = ImageUtil.getImage(_fileImageDescriptor);

	/**
	 * Constructor
	 */
	public ExplorerViewLabelProvider() {

	}

	/**
	 * Disposes all the images
	 */
	public void dispose() {
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServerIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentNormalIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentAbnormalIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentUnknownIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoryIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FolderIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FileIcon);
	}

	/**
	 * Gets an image for a specific element type
	 */
	public Image getImage(Object element) {

		if(element instanceof ServerRootParent) {
			return _serverRootImage; 	// root folder image for "Servers"
		} else if(element instanceof RepoRootParent){
			return _repositoryRootImage;	// root folder image for "Build Repositories"
		} else if(element instanceof ServerParent) {
			return _serverImage;	// server Icon image
		} else if (element instanceof RepoParent) {
			return _repositoryImage;	// repository Icon image
		} else if (element instanceof ServerChild) {	// agent status Icon image
			if (((TreeObject) element).getName().startsWith("O")) {
				return _agentNormalImage;
			} else {
				return _agentAbnormalImage;
			}
		} else if (element instanceof RepoDirectory) {
			return _folderImage;	// directory Icon image
		} else if (element instanceof RepoFile) {
			return _fileImage;	// file Icon image
		} 

		return _fileImage;
	}

	/**
	 * Gets a visible text upon the nickname of the element
	 */
	public String getText(Object element) {

		return element.toString();
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(ViewerCell cell) {

		Object element = cell.getElement();
		
		if(element instanceof ServerChild) {
			StyledString styledFileInfoString = stylizeServerFileInfo(element);
			cell.setText(styledFileInfoString.toString());
			cell.setStyleRanges(styledFileInfoString.getStyleRanges());
		} else if (element instanceof RepoFile) {
			StyledString styledFileInfoString = stylizeRepoFileInfo(element);
			cell.setText(styledFileInfoString.toString());
			cell.setStyleRanges(styledFileInfoString.getStyleRanges());
		} else {
			cell.setText(element.toString());
		}
		cell.setImage(getImage(element));
		super.update(cell);
	}

	/**
	 * @param element
	 * @return repository file info
	 */
	private StyledString stylizeRepoFileInfo(Object element) {
		CommonUtil util = new CommonUtil();
		
		String fileInfoString = element.toString();
		String[] changeFileInfoString = util.getTreeObjectSplitElement(fileInfoString);
		
		fileInfoString = changeFileInfoString[0];
		StyledString styledFileInfoString = new StyledString(changeFileInfoString[1], null);
		styledFileInfoString.append(" " + fileInfoString, StyledString.DECORATIONS_STYLER);

		return styledFileInfoString;
	}
	
	/**
	 * @param element
	 * @return
	 */
	private StyledString stylizeServerFileInfo(Object element) {
		String[] fileInfoString = element.toString().split(" ");

		StyledString styledFileInfoString = new StyledString(CommonUtil.replaceIgnoreCase(element.toString().substring(1), fileInfoString[fileInfoString.length - 1], ""), null);
		styledFileInfoString.append(" " + fileInfoString[fileInfoString.length - 1], StyledString.DECORATIONS_STYLER);

		return styledFileInfoString;
	}
}
