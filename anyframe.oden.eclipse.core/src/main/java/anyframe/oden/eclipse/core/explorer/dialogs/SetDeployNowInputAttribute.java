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
package anyframe.oden.eclipse.core.explorer.dialogs;

import java.util.Vector;

/**
 * SetDeployNowInputAttribute,
 * for the Model Set Deploy Now.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class SetDeployNowInputAttribute {

    // Attribute Agent
    private String attributeAgent;

    // Attribute LocationValue
    private String attributeLocationVal;

    // Attribute Deploy URL
    private String attributeDeployUrl;

    // Array of input parameter
    @SuppressWarnings("unchecked")
	private static Vector inputParamVect = new Vector();

    public SetDeployNowInputAttribute() {
    }

    public String getAttributeAgent() {
        return attributeAgent;
    }

    public void setAttributeAgent(String attributeAgent) {
        this.attributeAgent = attributeAgent;
    }

    public String getAttributeLocationVal() {
        return attributeLocationVal;
    }

    public void setAttributeLocationVal(String attributeLocationVal) {
        this.attributeLocationVal = attributeLocationVal;
    }

    public String getAttributeDeployUrl() {
        return attributeDeployUrl;
    }

    public void setAttributeDeployUrl(String attributeDeployUrl) {
    	this.attributeDeployUrl = attributeDeployUrl;
    }

    @SuppressWarnings("unchecked")
	public static Vector<SetDeployNowInputAttribute> getInputParamVect() {
        return inputParamVect;
    }

    @SuppressWarnings("unchecked")
	public static void addElementToInputParamVect(
            SetDeployNowInputAttribute element) {
        SetDeployNowInputAttribute.inputParamVect.add(element);
    }

    public static void clearInputParamVect() {
        inputParamVect.clear();
    }

}
