/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Encapsulates the display context passed into the getXXRendererComponent.<p>
 * 
 * Introduced to extract common state on which renderer configuration might
 * rely. Similar to the view part of ComponentAdapter - difference is that 
 * the properties are not "live" dependent on the component but those 
 * passed-in are used.<p>
 * 
 * PENDING: currently, only a dump record - can move services here, like
 * getting default colors, fonts,...? Partly done: colors and borders
 * are decided here (taken from UIManager or from component, if supported
 * in component api).
 *  
 * @author Jeanette Winzenburg
 */
public class CellContext<T extends JComponent> implements Serializable {

    /* PENDING: move border to CellContext. */
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1,
            1);

    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
            return SAFE_NO_FOCUS_BORDER;
        } else {
            return noFocusBorder;
        }
    }

    transient T component;
    transient Object value;
    transient int row;
    transient int column;
    transient boolean selected;
    transient boolean focused;
    transient boolean expanded;
    transient boolean leaf;
    
    public void installContext(T component, Object value, int row, int column, 
            boolean selected, boolean focused, boolean expanded, boolean leaf) {
        this.component = component;
        this.value = value;
        this.row = row;
        this.column = column;
        this.selected = selected;
        this.focused = focused;
        this.expanded = expanded;
        this.leaf = leaf;
    }

    
    public boolean isEditable() {
        return false;
    }
    
    public int getColumn() {
        return column;
    }
    public T getComponent() {
        return component;
    }
    public boolean isExpanded() {
        return expanded;
    }
    public boolean isFocused() {
        return focused;
    }
    public boolean isLeaf() {
        return leaf;
    }
    public int getRow() {
        return row;
    }
    public boolean isSelected() {
        return selected;
    }
    public Object getValue() {
        return value;
    }

    protected Color getForeground() {
        return getComponent() != null ? getComponent().getForeground() : null;
    }

    protected Color getBackground() {
        return getComponent() != null ? getComponent().getBackground() : null;
    }

    /**
     * @return
     */
    protected Color getSelectionBackground() {
        return null;
    }

    /**
     * @return
     */
    protected Color getSelectionForeground() {
        return null;
    }

    protected Border getFocusBorder() {
        Border border = null;
        if (isSelected()) {
            border = UIManager.getBorder(getUIKey("focusSelectedCellHighlightBorder"));
        }
        if (border == null) {
            border = UIManager.getBorder(getUIKey("focusCellHighlightBorder"));
        }
        return border;
    }

    protected Border getBorder() {
        if (isFocused()) {
            return getFocusBorder();
        } 
        return getNoFocusBorder();
    }

    protected String getUIKey(String key) {
        return getUIPrefix() + key;
    }


    protected String getUIPrefix() {
        return "";
    }


    protected Color getFocusForeground() {
        Color col;
        col = UIManager.getColor(getUIKey("focusCellForeground"));
        return col;
    }


    protected Color getFocusBackground() {
        Color col;
        col = UIManager.getColor(getUIKey("focusCellBackground"));
        return col;
    }

    
    public Icon getIcon() {
        return null;
    }
    
}