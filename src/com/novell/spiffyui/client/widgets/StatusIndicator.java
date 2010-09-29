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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.novell.spiffyui.client.SpiffyUIStrings;

/**
 * This is a reusable loading icon widget
 *
 */
public class StatusIndicator extends FlowPanel
{
    /**
     * Status In Progress
     */
    public static final int IN_PROGRESS = 1;

    /**
     * Status Failed
     */
    public static final int FAILED = 2;
    
    /**
     * Status Succeeded
     */
    public static final int SUCCEEDED = 3;
    
    private static final SpiffyUIStrings STRINGS = (SpiffyUIStrings) GWT.create(SpiffyUIStrings.class);
    
    public StatusIndicator(int status) 
    {
    	this(status, null);
    }
    /** 
     * Show the status of the report
     * @param status - int status constant
     * @param tooltip - String tooltip to show, if null, will use the defaults
     */
    public StatusIndicator(int status, String tooltip)
    {
        switch (status) {
        case IN_PROGRESS:
        	setStyleName("spiffy-status-inprogress");
        	tooltip = tooltip == null ? STRINGS.statusInProgress() : tooltip;
            break;
        case FAILED:
            setStyleName("spiffy-status-failed");
            tooltip = tooltip == null ? STRINGS.statusFailed() : tooltip;
            break;
        case SUCCEEDED:
            setStyleName("spiffy-status-succeeded");
            tooltip = tooltip == null ? STRINGS.statusSucceeded() : tooltip;
            break;
        default:
        	tooltip = tooltip == null ? "" : tooltip;
            setStyleName("");
            break;
        }
        setTitle(tooltip);
    }
}