/*******************************************************************************
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
package org.spiffyui.spsample.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the build documentation panel
 *
 */
public class BuildPanel extends HTMLPanel
{
    private static final SPSampleStrings STRINGS = (SPSampleStrings) GWT.create(SPSampleStrings.class);
    
    /**
     * Creates a new panel
     */
    public BuildPanel()
    {
        super("div", STRINGS.BuildPanel_html());
        
        getElement().setId("buildPanel");
        
        RootPanel.get("mainContent").add(this);
        
        setVisible(false);
        
        /*
         * Add TOC anchors
         */
        Index.addTocAnchor(this, "liBU_Less");
        Index.addTocAnchor(this, "liBU_Fewer");
        Index.addTocAnchor(this, "liBU_Get");
        Index.addTocAnchor(this, "liBU_Epoch");
        Index.addTocAnchor(this, "liBU_Closure");
        Index.addTocAnchor(this, "liBU_GZip");
        Index.addTocAnchor(this, "liBU_Props");
        Index.addTocAnchor(this, "liBU_JSLint");
        Index.addTocAnchor(this, "liBU_YUI");
    }
}
