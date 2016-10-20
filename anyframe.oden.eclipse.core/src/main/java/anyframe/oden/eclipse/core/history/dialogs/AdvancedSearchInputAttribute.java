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
package anyframe.oden.eclipse.core.history.dialogs;

import java.util.Vector;

/**
 * AdvancedSearchInputAttribute,
 * for the Model AdvacedSearch parameter.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class AdvancedSearchInputAttribute {

    // Attribute Name
    private String attributeName;

    // Attribute Relation
    private String attributeRelation;

    // Attribute Value
    private String attributeValue;

    // Array of input parameter
    @SuppressWarnings("unchecked")
	private static Vector inputParamVect = new Vector();

    public AdvancedSearchInputAttribute() {
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeRelation() {
        return attributeRelation;
    }

    public void setAttributeRelation(String attributeRelation) {
        this.attributeRelation = attributeRelation;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @SuppressWarnings("unchecked")
	public static Vector getInputParamVect() {
        return inputParamVect;
    }

    @SuppressWarnings("unchecked")
	public static void addElementToInputParamVect(
            AdvancedSearchInputAttribute element) {
        AdvancedSearchInputAttribute.inputParamVect.add(element);
    }

    public static void clearInputParamVect() {
        inputParamVect.clear();
    }

}
