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
package org.spiffyui.spsample.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the documentation panel
 *
 */
public class GetStartedPanel extends HTMLPanel
{
    private static final SPSampleStrings STRINGS = (SPSampleStrings) GWT.create(SPSampleStrings.class);    
    
    /**
     * Creates a new panel
     */
    public GetStartedPanel()
    {        
        super("div", STRINGS.GetStartedPanel_html());

        getElement().setId("getStartedPanel");

        RootPanel.get("mainContent").add(this);

        setVisible(false);

        /*
         * Add the samples anchor
         */
        Anchor samples = new Anchor(Index.getStrings().helloSpiffySample(), Index.generateNavItemURL(Index.SAMPLES_NAV_ITEM_ID));
        samples.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event)
            {
                event.preventDefault();
                Index.selectItem(Index.SAMPLES_NAV_ITEM_ID);
            }
        });
        add(samples, "getStartedSamples");
        
        /*
         * Add Project Creator
         */
        ProjectCreatorPanel projCreator = new ProjectCreatorPanel("getStartedPanel");
        add(projCreator, "getStartedCreateSpiffy");
        
        Index.addToc(this);
    }
    

}
