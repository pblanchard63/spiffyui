/*
 * Copyright (c) 2010 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */
package com.novell.spiffyui.client.widgets;

import com.google.gwt.user.client.ui.SimplePanel;


/**
 * This widget wraps the JQuery UI progress bar
 */
public class ProgressBar extends SimplePanel
{
    private int m_value = 0;
    
    /**
     * Create a progress bar with the specified ID.  The ID is required
     * because the progress bar needs a specific ID to connect to.
     * 
     * @param id     the id for the progress bar
     */
    public ProgressBar(String id)
    {
        super();
        getElement().setId(id);
    }

    @Override
    public void onAttach()
    {
        super.onAttach();
        addProgressBarJS(getElement().getId(), m_value);
    }
    
    /**
     * Set the value for this progress bar
     * 
     * @return the value of the bar
     */
    public int getValue()
    {
        return m_value;
    }
    
    /**
     * Get the value of this progress bar
     * 
     * @param value  the bar value
     */
    public void setValue(int value)
    {
        m_value = value;
        setValueJS(getElement().getId(), m_value);
    }
    
    private static native void setValueJS(String id, int barValue) /*-{
        $wnd.$('#' + id).progressbar('destroy');
        $wnd.$('#' + id).progressbar({
            value: barValue
        });
    }-*/;
    
    private static native void addProgressBarJS(String id, int barValue) /*-{
        $wnd.$("#" + id).progressbar({
            value: barValue
        });
    }-*/;
    
    
}