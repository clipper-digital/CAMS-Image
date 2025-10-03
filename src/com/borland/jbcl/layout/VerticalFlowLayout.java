package com.borland.jbcl.layout;

import java.awt.*;

/**
 * Replacement for Borland JBuilder's VerticalFlowLayout
 * A layout manager that arranges components in a vertical flow
 */
public class VerticalFlowLayout implements LayoutManager {
    
    public static final int TOP = 0;
    public static final int CENTER = 1;
    public static final int BOTTOM = 2;
    
    private int alignment;
    private int hgap;
    private int vgap;
    
    public VerticalFlowLayout() {
        this(CENTER, 5, 5);
    }
    
    public VerticalFlowLayout(int alignment) {
        this(alignment, 5, 5);
    }
    
    public VerticalFlowLayout(int alignment, int hgap, int vgap) {
        this.alignment = alignment;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        // Not used
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // Not used
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return calculateSize(parent, true);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return calculateSize(parent, false);
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int maxWidth = parent.getWidth() - (insets.left + insets.right + hgap * 2);
            int y = insets.top + vgap;
            int x = insets.left + hgap;
            
            Component[] components = parent.getComponents();
            for (Component comp : components) {
                if (comp.isVisible()) {
                    Dimension d = comp.getPreferredSize();
                    int compX = x;
                    
                    // Apply alignment
                    if (alignment == CENTER) {
                        compX = x + (maxWidth - d.width) / 2;
                    } else if (alignment == BOTTOM) { // Use as right alignment for vertical
                        compX = x + maxWidth - d.width;
                    }
                    
                    comp.setBounds(compX, y, d.width, d.height);
                    y += d.height + vgap;
                }
            }
        }
    }
    
    private Dimension calculateSize(Container parent, boolean preferred) {
        int maxWidth = 0;
        int totalHeight = 0;
        int componentCount = 0;
        
        Component[] components = parent.getComponents();
        for (Component comp : components) {
            if (comp.isVisible()) {
                Dimension d = preferred ? comp.getPreferredSize() : comp.getMinimumSize();
                maxWidth = Math.max(maxWidth, d.width);
                totalHeight += d.height;
                componentCount++;
            }
        }
        
        Insets insets = parent.getInsets();
        maxWidth += insets.left + insets.right + hgap * 2;
        totalHeight += insets.top + insets.bottom + vgap * (componentCount + 1);
        
        return new Dimension(maxWidth, totalHeight);
    }
}