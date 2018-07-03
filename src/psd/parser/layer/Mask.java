package psd.parser.layer;

public class Mask {
	public int top;            // Rectangle enclosing layer mask: Top, left, bottom, right
	public int left;
	public int bottom;
	public int right;
	public int defaultColor;    // 0 or 255
	public boolean relative;        // position relative to layer
	public boolean disabled;        // layer mask disabled
	public boolean invert;            // invert layer mask when blending
    //byte*mask_data;
	
	
	
	public class AdjustMask
	{
		public int top;            // Rectangle enclosing layer mask: Top, left, bottom, right
		public int left;
		public int bottom;
		public int right;
		public int defaultColor;    // 0 or 255
		public boolean relative;        // position relative to layer
		public boolean disabled;        // layer mask disabled
		public boolean invert;  
	}
	
	public AdjustMask mask;

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getWidth() {
        return right - left;
    }

    public int getHeight() {
        return bottom - top;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public boolean isRelative() {
        return relative;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isInvert() {
        return invert;
    }
}
