package psd.editor.fairygui;

public class LayoutScrollBar  extends LayoutObject{
	public static int UNKOWN = 0;
	public static int HORIZONTAIL = 1;
	public static int VERTICAL = 2;
	
	public static String TAG = "scroll";
	public static String SCROLL_TEMPLATE = "";
	static {
		SCROLL_TEMPLATE += Util.XML_HEADER;
		SCROLL_TEMPLATE += "<component size=\"%d,%d\" extention=\"ScrollBar\">\n";
		SCROLL_TEMPLATE += "  <ScrollBar/>\n";
		SCROLL_TEMPLATE += "  <displayList>\n";
		SCROLL_TEMPLATE += "%s\n";
		SCROLL_TEMPLATE += "  </displayList>\n";
		SCROLL_TEMPLATE += "</component>\n";
	}
	public DisplayGraph bar;
	public DisplayComponent slider_dc;
	public int scroll = UNKOWN;
	
	public LayoutScrollBar()
	{
		this.tag = TAG;
	}
	
	
	@Override
	public boolean command_image_key(DisplayImage o, String key) {
		if (key.equals("bg")) {
//			o.name = name + "_" + key;
			if (o.width >= o.height) {
				o.relation = new Relation("", "width");
				scroll = HORIZONTAIL;
			} else {
				o.relation = new Relation("", "height");
				scroll = VERTICAL;
			}
			bar = new DisplayGraph();
			bar.relation = o.relation;
			bar.show_name = "bar";
			return true;
		}
		else if(key.equals("grip"))
		{
			DisplayComponent dc = o.create_ref_comp(null);
			this.slider_dc = dc;
			dc.show_name = key;
			return true;
		}
		return false;
	}

	@Override
	public boolean command_forder_key(DisplayComponent dc, String key) {
		if (key.equals("grip")) {
			slider_dc = dc;
			dc.o = new LayoutButton();
			dc.show_name = key;
			return true;
		}
		return false;
	}
	
	@Override
	public String toxml() {
		if(bar == null)
		{
			FairyShared.Error("Error Scroll have no bar \n" + "	" + this.parent_path + this.name);
		}
		if(slider_dc == null)
		{
			FairyShared.Error("Error Scroll have no grip \n" + "	" + this.parent_path + this.name);
		}
		String s = "";
		for (DisplayObject d : display_list) {
			s += d.toxml();
			if(this.bg == d)
			{
				bar.parent = d.parent;
				bar.x = d.x;
				bar.y = d.y;
				bar.width = d.width;
				bar.height = d.height;
				s += bar.toxml();
			}
		}
		return String.format(SCROLL_TEMPLATE, width, height, s);
	}
}
