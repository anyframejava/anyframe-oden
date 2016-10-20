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
package anyframe.oden.bundle.ent.http;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * 
 * Resource register class for web-console.
 * 
 * @author joon1k
 *
 */
public class ResourceRegister {

	protected void setHttpService(HttpService hs){
		try {
			hs.registerResources("/", "web", null);
		} catch (NamespaceException e) {
		}
	}
}
