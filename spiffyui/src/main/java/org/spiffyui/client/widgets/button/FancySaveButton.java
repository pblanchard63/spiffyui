/*******************************************************************************
 * 
 * Copyright 2011-2014 Spiffy UI Team   
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
 ******************************************************************************/
package org.spiffyui.client.widgets.button;

/**
 * This widget is a fancy save button with an embedded icon
 */
public class FancySaveButton extends FancyButton
{
    /**
     * Create a new FancySaveButton 
     *  
     */
    public FancySaveButton()
    {
        getElement().setClassName("spiffy-save-button");
        getElement().addClassName("spiffy-fancy-button");
    }
    
    
    /**
     * Create a new FancySaveButton with the specified text
     * 
     * @param text   the text of the button
     */
    public FancySaveButton(String text)
    {
        this();
        
        setText(text);
    }
}
