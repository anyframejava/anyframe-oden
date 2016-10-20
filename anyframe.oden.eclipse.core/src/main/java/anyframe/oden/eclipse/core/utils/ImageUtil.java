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
package anyframe.oden.eclipse.core.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import anyframe.oden.eclipse.core.OdenActivator;

/**
 * Image Utility which gets, disposes images.
 * 
 * @author Eric Clayberg, Dan Rubel
 * @author RHIE Jihwan
 * @version 1.0.0 M3
 *
 */
public class ImageUtil {

	private final static Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>();

	/**
	 * 
	 */
	public static Image getImage(ImageDescriptor imageDescriptor) {
		if (imageDescriptor == null) {
			return null;

		}

		Image image = (Image) imageCache.get(imageDescriptor);

		if (image == null) {
			image = imageDescriptor.createImage();
			imageCache.put(imageDescriptor, image);
		}

		return image;
	}

	public static void disposeImage(String imageProperty) {
		try {
			Image image = (Image) imageCache.get(imageProperty);

			if (image == null) {
				return;

			}

			Iterator<Image> iter = imageCache.values().iterator();
			while (iter.hasNext()) {
				iter.next().dispose();
				imageCache.remove(imageProperty);
			}

		} catch (Throwable throwable) {
			OdenActivator.error("Error while disposing images", throwable);
		}
	}

	public void disposeImages() {
		Iterator<Image> iter = imageCache.values().iterator();
		while (iter.hasNext()) {
			iter.next().dispose();
		}

		imageCache.clear();
	}

	public static ImageDescriptor getImageDescriptor(String imageProperty) {
		try {
			if (imageProperty == null) {
				return null;
			}

			String path = imageProperty;
			if (path == null || path.trim().length() == 0) {
				OdenActivator.error("Missing image path for " + imageProperty, null);
				return null;

			}

			URL url = null;
			try {
				url = new URL(OdenActivator.getDefault().getDescriptor().getInstallURL(), imageProperty);
				return ImageDescriptor.createFromURL(url);
			} catch (MalformedURLException e) {
				return null;
			}

		} catch (Exception e) {
			OdenActivator.error("Unable to create image for " + imageProperty, e);
			return null;
		}
	}

}
