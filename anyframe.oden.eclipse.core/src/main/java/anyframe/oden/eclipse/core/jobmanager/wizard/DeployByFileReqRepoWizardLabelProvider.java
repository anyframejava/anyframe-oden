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
package anyframe.oden.eclipse.core.jobmanager.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Tree label provider for Oden view outline.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */
public  class DeployByFileReqRepoWizardLabelProvider extends StyledCellLabelProvider implements ILabelProvider{

	// icon for the folder
	private ImageDescriptor _folderImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FolderIcon);
	private Image _folderImage = ImageUtil.getImage(_folderImageDescriptor);

	// icon for the file
	private ImageDescriptor _fileImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FileIcon);
	private Image _fileImage = ImageUtil.getImage(_fileImageDescriptor);

	/**
	 * Constructor
	 */
	public DeployByFileReqRepoWizardLabelProvider() {

	}

	/**
	 * Disposes all the images
	 */
	public void dispose() {
		//		super.dispose();
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FolderIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FileIcon);
	}

	/**
	 * Gets an image for a specific element type
	 */
	public Image getImage(Object element) {
		
		if( element instanceof TreeParent) {
			return _folderImage;
		} else if (element instanceof TreeObject) {
			return _fileImage;
		}
		
		// items that file
		return _fileImage;
	}

	/**
	 * Gets a visible text upon the nickname of the element
	 */
	public String getText(Object element) {
		if(((DeployByFileReqRepoInfo)element).getType().equals(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Directory)) {
			return ((DeployByFileReqRepoInfo) element).getName();
		}
		
		return ((DeployByFileReqRepoInfo) element).getName() + " " + "[" + ((DeployByFileReqRepoInfo) element).getDate() + "]";
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
		
		// items that have more than one child
		if (element instanceof TreeParent) {
			cell.setText(element.toString());
		}

		// items that have no children
		else if (element instanceof TreeObject) {
			StyledString stylizeRepoFileInfo = stylizeRepoFileInfo(element);
			cell.setText(stylizeRepoFileInfo.toString());
			cell.setStyleRanges(stylizeRepoFileInfo.getStyleRanges());
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
}
