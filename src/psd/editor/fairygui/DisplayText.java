package psd.editor.fairygui;

import java.awt.Font;

public class DisplayText extends DisplayObject {
	public static String TAG = "text";
	private static String TEXT_TEMPLATE = "";
	static
	{
		TEXT_TEMPLATE += "    <text id=\"%s\" name=\"%s\" xy=\"%d,%d\" size=\"%d,%d\"  %s/>\n";
	}
	
	public class Color
	{
		public int r, g, b;
	}
	
	public class Position
	{
		public Position(double dx, double dy, double dw, double dh)
		{
			dx += 0.5;
			dy += 0.5;
			dw += 0.5;
			dh += 0.5;
			this.x = (int)dx;
			this.y = (int)dy;
			this.w = (int)dw;
			this.h = (int)dh;
		}
		public int x, y, w, h;
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.format("%d %d %d %d", x, y, w, h);
		}
	}
	
	public String text;
	public String fontStr;
	public Font font;
	public int fontSize;	//字体大小
	public Color color = new Color(); // 填充颜色
	public boolean is_stroke= false;
	public Color strokeColor = new Color(); // 填充颜色
	public int strokeSize = 0;
	public double leading = 0; // 行距
	public int letterSpacing = 0; // 字距
	public boolean underline = false; // 下划线
	public boolean italic = false;// 斜体
	public boolean bold = false; //粗体
	public Position bounds;
	public Position boundingBox;
	public boolean auto_leading = false;
	public double horizontalScale = 0;
	
	public String to_button_ref_xml()
	{
		FairyShared.OtherFairy of = FairyShared.instance.ref_folder(this.pkg);
		FairyShared.OtherResource or = FairyShared.instance.ref_comp(of, this.name);
		if(null == or)
		{
			FairyShared.instance.find_resource_id(this.pkg, this.name + ".xml" , this.parent_path + this.name);
			return "";
		}
		DisplayComponent dc = new DisplayComponent();
		dc.pkg = of.id;
		dc.src = or.id;
		dc.x = this.x;
		dc.y = this.y;
		dc.parent = this.parent;
		dc.width = this.width;
		dc.height = this.height;
		dc.other_center_str = "      <Button title=\""+ this.text +"\"/>\n";
		return dc.toxml();
	}
	
	@Override
	public String toxml() {
		String n = name;
		if(this.show_name != null)
		{
			n = this.show_name;
		}
		String s = "";
		if(this.font != null)
		{
			s += " font=\"" + this.fontStr + "\"";
		}
		if(this.fontSize != 0)
		{
			double fs = this.fontSize;
			if(this.leading > 1)
			{
				if(this.fontSize > this.leading)
				{
//					fs = this.leading;
				}
			}
			s += " fontSize=\"" + (int)fs + "\"";
		}
		if(this.color.r != 0 || this.color.g != 0 || this.color.b != 0)
		{
			String rgbStr = String.format("#%02x%02x%02x", this.color.r, this.color.g, this.color.b);
			s += " color=\"" + rgbStr + "\"";
		}
		if (this.auto_leading)
		{
			s += " autoSize=\"height\"";
		}
		else if(this.leading != 0)
		{
			s += " leading=\"" + (int)(this.leading - this.fontSize) + "\"";
			s += " autoSize=\"none\"";
		}
		else
		{
			s += " autoSize=\"none\"";
		}
		if(this.letterSpacing != 0)
		{
			s += " letterSpacing=\"" + this.letterSpacing + "\"";
		}
		if(underline)
		{
			s += " underline=\"" + true + "\"";
		}
		if(bold)
		{
			s += " bold=\"" + true + "\"";
		}
		if(italic)
		{
			s += " italic=\"" + true + "\"";
		}
		if(this.is_stroke)
		{
			s += " strokeSize=\"" + this.strokeSize + "\"";
			if(this.strokeColor.r != 0 || this.strokeColor.g != 0 || this.strokeColor.b != 0)
			{
				String rgbStr = String.format("#%02x%02x%02x", this.strokeColor.r, this.strokeColor.g, this.strokeColor.b);
				s += " strokeColor=\"" + rgbStr + "\"";
			}
		}
	
		if(this.text != null && this.text.length() > 0)
		{
			s += " text=\"" + this.text + "\"";
		}
		if(horizontalScale < 0.2 || horizontalScale > 1)
		{
			horizontalScale = 1;
		}
		int w_ex = 2;
		int h_ex = 2;
		if(this.width * 1.4 <  this.height)
		{
			h_ex =  (int)(this.height * (1 - horizontalScale) + 3);
		}
		else
		{
			w_ex = (int)(this.width * (1.1 - horizontalScale) + 3);
		}
		return String.format(TEXT_TEMPLATE, this.id, n, this.x - this.parent.x, this.y - this.parent.y, this.width + w_ex, this.height + h_ex, s);
	}
	
	
}
