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

    public static Vector getInputParamVect() {
        return inputParamVect;
    }

    public static void addElementToInputParamVect(
            AdvancedSearchInputAttribute element) {
        AdvancedSearchInputAttribute.inputParamVect.add(element);
    }

    public static void clearInputParamVect() {
        inputParamVect.clear();
    }

}
