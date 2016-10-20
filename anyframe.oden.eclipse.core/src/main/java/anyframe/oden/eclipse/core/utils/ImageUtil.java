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
