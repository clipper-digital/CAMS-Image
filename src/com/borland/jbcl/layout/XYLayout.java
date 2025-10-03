package com.borland.jbcl.layout;

import java.awt.*;

/**
 * Replacement for Borland JBuilder's XYLayout
 * Simplified version that uses absolute positioning
 */
public class XYLayout implements LayoutManager2 {
    private int width = 0;
    private int height = 0;
    
    public XYLayout() {
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        // XYLayout uses XYConstraints for positioning
        if (constraints instanceof XYConstraints) {
            XYConstraints c = (XYConstraints) constraints;
            comp.setBounds(c.x, c.y, c.width, c.height);
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        // Not used in XY layout
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // Nothing to do for removal
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return calculateSize(parent, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return calculateSize(parent, false);
    }

    @Override
    public void layoutContainer(Container parent) {
        // Components should already be positioned by addLayoutComponent
        // But we'll ensure they're positioned correctly
        Component[] components = parent.getComponents();
        for (Component comp : components) {
            // The component should already have its bounds set
            // This is a no-op since XYLayout uses absolute positioning
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container target) {
        // Nothing to invalidate
    }

    private Dimension calculateSize(Container parent, boolean preferred) {
        int maxX = 0;
        int maxY = 0;
        
        Component[] components = parent.getComponents();
        for (Component comp : components) {
            Rectangle bounds = comp.getBounds();
            maxX = Math.max(maxX, bounds.x + bounds.width);
            maxY = Math.max(maxY, bounds.y + bounds.height);
        }
        
        return new Dimension(maxX, maxY);
    }
}