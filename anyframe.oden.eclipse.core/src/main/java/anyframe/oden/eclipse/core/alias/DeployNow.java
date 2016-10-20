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

/**
 * Represents the combination of an Agent name and a location variable 
 * for Deploy Now action on the selected Build Repository item 
 * with zero-configuration at run-time; 
 * if never configured, the default-location of all available Agent 
 * will be used for Deploy Now action.
 * 
 * @author RHIE Jihwan
 * @author HONG JungHwan
 * @version 1.0.0
 *
 */
public class DeployNow {

	// XML tag strings for Deploy-Now destination
	static final String DESTINATION = "destination"; //$NON-NLS-1$
	static final String AGENT = "agent"; //$NON-NLS-1$
	static final String LOCATION = "location"; //$NON-NLS-1$

	// agent name and location variable name to use as Deploy-Now destination
	private String destinedAgentName;
	private String destinedLocation;
	
	// The Build Repository profile alias which destination info belongs to
	private Repository repository;

	/**
	 * Default Constructor
	 */
	public DeployNow() {
		super();
	}
	
	/**
	 * Constructor
	 * @param destinedAgentName
	 * @param destinedLocation
	 */
	public DeployNow(String destinedAgent, String destinedLocation ) {
		super();
		this.destinedAgentName = destinedAgent;
		this.destinedLocation = destinedLocation;
	}

	/**
	 * Constructs a destined Agent, from a user defined configuration 
	 * stored by expressDeployNowInXML()
	 * @param root
	 */
	public DeployNow(Element root) {
		super();
		this.destinedAgentName = root.elementText(AGENT);
		this.destinedLocation = root.elementText(LOCATION);
	}

	/**
	 * Expresses this Deploy-Now destination information in XML expression
	 * @return
	 */
	public Element expressDeployNowInXML() {
		Element root = new DefaultElement(DESTINATION);
		root.addElement(AGENT).setText(destinedAgentName);
		root.addElement(LOCATION).setText(destinedLocation);
		return root;
	}

	/**
	 * Gets a destined Agent name for Deploy-Now action
	 * @return
	 */
	public String getDestinedAgentName() {
		return destinedAgentName;
	}

	/**
	 * Sets the destined Agent name for Deploy-Now action
	 * @param destinedAgent
	 */
	public void setDestinedAgentName(String destinedAgent) {
		this.destinedAgentName = destinedAgent;
	}

	/**
	 * Gets a destined location variable for Deploy-Now action
	 * @return
	 */
	public String getDestinedLocation() {
		return destinedLocation;
	}

	/**
	 * Sets the destined location variable for Deploy-Now action
	 * @param destinedLocation
	 */
	public void setDestinedLocation(String destinedLocation) {
		this.destinedLocation = destinedLocation;
	}

	/**
	 * Returns the Build Repository for this Deploy-Now destination information
	 * @return
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * Sets the Build Repository for this Deploy-Now destination information
	 * @param repository
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

}
