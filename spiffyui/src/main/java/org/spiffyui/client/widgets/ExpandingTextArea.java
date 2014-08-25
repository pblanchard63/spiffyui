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
package org.spiffyui.client.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * This is a self-expanding text area.  It's height grows
 * dynamically depending on the text within it.
 * 
 * See http://www.alistapart.com/articles/expanding-text-areas-made-elegant
 * 
 * 
 */
public class ExpandingTextArea extends TextArea implements ChangeHandler, KeyUpHandler, BlurHandler
{
    private JavaScriptObject m_span;
    private int m_maxHeight = -1;
    /**
     * Constructor
     * @param id - the text area's element ID
     */
    public ExpandingTextArea(String id)
    {
        super();        
        getElement().setId(id);
    }

    /**
     * Constructor
     * A unique ID will be generated.
     */
    public ExpandingTextArea()
    {
        this(HTMLPanel.createUniqueId());
    }

    @Override
    public void setValue(String value)
    {
        super.setValue(value);
        if (m_span != null) {
            manuallyUpdateSpanJS(m_span, value, m_maxHeight, getElement().getId());
        }
    }

    @Override
    public void setValue(String value, boolean fireEvents)
    {
        super.setValue(value, fireEvents);
        if (m_span != null) {
            manuallyUpdateSpanJS(m_span, value, m_maxHeight, getElement().getId());
        }
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);
        if (m_span != null) {
            manuallyUpdateSpanJS(m_span, text, m_maxHeight, getElement().getId());
        }
    }
    
    @Override
    protected void onLoad()
    {
        super.onLoad();
        /*
         * This is the markup we want
         * <div class="expandingArea">
         *     <pre><span></span><br></pre>
         *     <textarea></textarea>
         * </div>
         *
         * So when this TextArea is attached
         * let's add the the other DOM elements.
         * 
         * Doing it this way instead of extending Composite
         * or HTMLPanel allows us to inherit from TextArea.
         */
        m_span = markupTextAreaJS(getElement().getId());
        addChangeHandler(this);
        addKeyUpHandler(this);
        addBlurHandler(this);
        updateSpan();
        
        if (m_maxHeight > -1) {
            /*
             * We need to set this here once the elements are created.
             */
            setMaxHeight(m_maxHeight);
        }
    }
    
    /**
     * Set the maximum height of this text area.  The text area will
     * grow to this height and will show vertical scroll bars after 
     * that height.
     *
     * @param height the maximum height of this text area in pixels
     */
    public void setMaxHeight(int height)
    {
        m_maxHeight = height;
        setMaxHeightJS(getElement().getId(), height);
    }
    
    /**
     * Get the maximum height of this text area.  
     * 
     * @return the maximum height of this text area or -1 if it hasn't been set
     */
    public int getMaxHeight()
    {
        return m_maxHeight;
    }
    
    /**
     * Clear out the maximum height setting for this text area so 
     * the text area will grow as large as it has to to show the contents.
     */
    public void clearMaxHeight()
    {
        clearMaxHeightJS(getElement().getId());
    }
    
    private native JavaScriptObject clearMaxHeightJS(String textAreaId) /*-{
        $wnd.jQuery("#" + textAreaId).parent().children("pre").css("max-height", "");
        $wnd.jQuery("#" + textAreaId).css("max-height", "");
        $wnd.jQuery("#" + textAreaId).css("overflow-y", "");
    }-*/;
    
    private native JavaScriptObject setMaxHeightJS(String textAreaId, int height) /*-{
        $wnd.jQuery("#" + textAreaId).parent().children("pre").css("max-height", height + "px");
        $wnd.jQuery("#" + textAreaId).css("max-height", height + "px");
    }-*/;

    private native JavaScriptObject markupTextAreaJS(String textAreaId) /*-{
        var ta = $wnd.jQuery("#" + textAreaId);    
        var prev = ta.prev();
        var contId = textAreaId + "Cont";
        var container = $wnd.jQuery("<div id=\"" + contId + "\" class=\"expandingArea active\"></div>");
        if (prev.length == 0) {
            //no previous siblings to come after, just wrap
            ta.wrap(container);
        } else {
            //make next sibling of prev the container
            prev.after(container);
            container.append(ta);
        }
        ta.before("<pre><span></span><br></pre>");
        //Return the span as a JQuery object
        return $wnd.jQuery("#" + contId + " span");      
    }-*/;
        
    private native void manuallyUpdateSpanJS(JavaScriptObject spanJQueryObject, String text, int maxHeight, String textAreaId) /*-{
        //We are using JQuery for this because trying to wrap a span element in GWT has a history of not working in Dev Mode 
        if (spanJQueryObject.length > 0) {
            spanJQueryObject.text(text);
            
            if (maxHeight > -1 && $wnd.jQuery('#' + textAreaId).height() > maxHeight - 20) {
                $wnd.jQuery("#" + textAreaId).css("overflow-y", "auto");
            } else {
                $wnd.jQuery("#" + textAreaId).css("overflow-y", "");
            }
        }
    
    }-*/;

    private void updateSpan()
    {
        if (m_span != null) {
            manuallyUpdateSpanJS(m_span, getValue(), m_maxHeight, getElement().getId());
        }
    }
    
    @Override
    public void onChange(ChangeEvent event)
    {
        updateSpan();
    }

    @Override
    public void onBlur(BlurEvent event)
    {
        updateSpan();
    }

    @Override
    public void onKeyUp(KeyUpEvent event)
    {
        updateSpan();
    }
}
