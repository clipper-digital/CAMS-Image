package com.borland.jbcl.layout;

/**
 * Replacement for Borland JBuilder's XYConstraints
 * Used to specify absolute positioning for components in XYLayout
 */
public class XYConstraints {
    public int x;
    public int y;
    public int width;
    public int height;
    
    public XYConstraints() {
        this(0, 0, 0, 0);
    }
    
    public XYConstraints(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public String toString() {
        return "XYConstraints[" + x + "," + y + "," + width + "," + height + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XYConstraints)) {
            return false;
        }
        XYConstraints other = (XYConstraints) obj;
        return x == other.x && y == other.y && width == other.width && height == other.height;
    }
    
    @Override
    public int hashCode() {
        return x ^ y ^ width ^ height;
    }
}