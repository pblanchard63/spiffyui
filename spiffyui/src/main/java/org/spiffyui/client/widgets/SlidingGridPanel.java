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
package org.spiffyui.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * A panel that uses a sliding grid layout based on 
 * <a href="http://www.zackgrossbart.com/hackito/slidegrid/">this example</a> . 
 * The sliding grid arranges a set of cells to the best width to fit the 
 * available space and then resizes that grid when the browser window resizes. 
 */
public class SlidingGridPanel extends ComplexPanel
{
    private int m_cellWidth = 250;
    private int m_cellHeight = 150;
    private int m_padding = 30;
    private int m_offset = 0;

    /**
     * Create a new SlidingGridPanel
     */
    public SlidingGridPanel()
    {
        super();
        Element divEle = DOM.createDiv();
        setElement(divEle);
        setStyleName("slidegrid");
    }

    /**
     * Add a widget to a new cell in this sliding grid
     * 
     * @param w      the widget to add
     */
    @Override
    public void add(Widget w)
    {
        addWidget(w, null);
    }
    
    /**
     * Add a widget to a tall cell
     * @param w Widget
     */
    public void addTall(Widget w)
    {
        addWidget(w, "tallcell");
    }
    
    /**
     * Add a widget to a wide cell
     * @param w Widget
     */
    public void addWide(Widget w)
    {
        addWidget(w, "widecell");
    }
    
    /**
     * Add a widget to a big cell (tall and wide)
     * @param w Widget
     */
    public void addBig(Widget w)
    {
        addWidget(w, "bigcell");
    }
    
    /**
     * Add a widgets to a new cell in this sliding grid with the specified style
     * 
     * @param w      the widget to add
     * @param style  the style of the widget
     */
    private void addWidget(Widget w, String style)
    {
        SimplePanel div = new SimplePanel();
        div.addStyleName("cell");
        if (style != null) {
            div.addStyleName(style);
        }
        div.add(w);
        add(div, (Element) getElement());
    }
    
    /**
     * the width of each cell in px
     * @return the cellWidth
     */
    public int getCellWidth()
    {
        return m_cellWidth;
    }

    /**
     * the width of each cell in px
     * @param cellWidth the cellWidth to set
     */
    public void setCellWidth(int cellWidth)
    {
        m_cellWidth = cellWidth;
    }

    /**
     * the height of each cell in px
     * @return the cellHeight
     */
    public int getCellHeight()
    {
        return m_cellHeight;
    }

    /**
     * the height of each cell in px
     * @param cellHeight the cellHeight to set
     */
    public void setCellHeight(int cellHeight)
    {
        m_cellHeight = cellHeight;
    }

    /**
     * the padding between each cell in px
     * @return the padding
     */
    public int getPadding()
    {
        return m_padding;
    }

    /**
     * the padding between each cell in px
     * @param padding the padding to set
     */
    public void setPadding(int padding)
    {
        m_padding = padding;
    }

    /**
     * the offset width of the grid in px
     * 
     * @return the offset
     * @deprecated The grid offset is now deprecated since the sliding grid will use the parent width
     *             instead of the window width.  This field will be ignored and will be removed in the
     *             next release.
     */
    @Deprecated
    public int getGridOffset()
    {
        return m_offset;
    }

    /**
     * the offset width of the grid in px
     * 
     * @param offset the offset to set
     * 
     * @deprecated The grid offset is now deprecated since the sliding grid will use the parent width
     *             instead of the window width.  This field will be ignored and will be removed in the
     *             next release.
     */
    @Deprecated
    public void setGridOffset(int offset)
    {
        m_offset = offset;
    }
    
    /**
     * Force the grid to redraw.
     */
    public void reAlignGrid()
    {
        alignGrid(m_cellWidth, m_cellHeight, m_padding, m_offset);
    }

    @Override
    public void onLoad()
    {
        /*
         There is a small timing issue with the sliding grid that only shows up
         on Macs.  It causes the grid panel to get the wrong information for the
         width of the parent container.  This delay works around the issue.
         */
        Timer t = new Timer() {
            @Override
            public void run()
            {
                alignGrid(m_cellWidth, m_cellHeight, m_padding, m_offset);
            }
        };

        t.schedule(500);
        
        super.onLoad();
    }

    private static native void alignGrid(int cellWidth, int cellHeight, int padding, int gridOffset) /*-{
        $wnd.slidegrid.alignGrid(cellWidth, cellHeight, padding, gridOffset);
    }-*/;
    
    /**
     * Set the slidegrid 'hasDrawn' property
     * @param hasDrawn - property to set
     */
    public static native void setHasDrawn(boolean hasDrawn) /*-{
        $wnd.slidegrid.hasDrawn = hasDrawn;
    }-*/;
    

}
