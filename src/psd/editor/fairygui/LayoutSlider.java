package psd.editor.fairygui;

public class LayoutSlider extends LayoutObject {
	public static int UNKOWN = 0;
	public static int HORIZONTAIL = 1;
	public static int VERTICAL = 2;
	public static String TAG = "slide";
	public static String SLIDER_TEMPLATE = "";

	public DisplayImage bar;
	public DisplayComponent slider_dc;
	public int scroll = UNKOWN;

	static {
		SLIDER_TEMPLATE += Util.XML_HEADER;
		SLIDER_TEMPLATE += "<component size=\"%d,%d\" extention=\"Slider\">\n";
		SLIDER_TEMPLATE += "  <displayList>\n";
		SLIDER_TEMPLATE += "%s\n";
		SLIDER_TEMPLATE += "  </displayList>\n";
		SLIDER_TEMPLATE += "  <Slider/>\n";
		SLIDER_TEMPLATE += "</component>\n";
	}
	
	public LayoutSlider()
	{
		this.tag = TAG;
	}

	@Override
	public String toxml() {
		if(bar == null)
		{
			FairyShared.Error("Error Slider have no bar \n" + "	" + this.parent_path + this.name);
		}
		if(slider_dc == null)
		{
			FairyShared.Error("Error Slider have no grip \n" + "	" + this.parent_path + this.name);
		}
		
		String s = "";
		for (DisplayObject d : display_list) {
			s += d.toxml();
		}
		return String.format(SLIDER_TEMPLATE, width, height, s);
	}

	@Override
	public boolean command_image_key(DisplayImage o, String key) {
		if (key.equals("bg")) {
//			o.name = name + "_" + key;
			o.relation = new Relation("", "width-width,height-height");
			if (o.width >= o.height) {
				scroll = HORIZONTAIL;
				
			} else {
				scroll = VERTICAL;
				if(bar != null && !bar.show_name.endsWith("_v"))
				{
					bar.show_name += "_v";
				}
				if(slider_dc != null)
				{
					slider_dc.relation.sidePair = "top-bottom";
				}
			}
			return true;
		}
		if (key.equals("bar")) {
			this.bar = o;
//			o.name = name + "_" + key;

			o.show_name = key;

			if (this.slider_dc != null) {
				this.slider_dc.relation.target = this.bar.id;
			}
			if(this.scroll == VERTICAL)
			{
				bar.show_name += "_v";
				if(slider_dc != null)
				{
					slider_dc.relation.sidePair = "top-bottom";
				}
			}
			return true;
		}
		if(key.equals("grip"))
		{
			DisplayComponent dc = o.create_ref_comp(null);
			this.slider_dc = dc;
			dc.show_name = key;
			dc.relation = new Relation("", "right-right");
			if (this.bar != null) {
				dc.relation.target = this.bar.id;
			}
			if(this.scroll == VERTICAL)
			{
				slider_dc.relation.sidePair = "top-bottom";
			}
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
			dc.relation = new Relation("", "right-right");
			if (this.bar != null) {
				dc.relation.target = this.bar.id;
			}
			if(this.scroll == VERTICAL)
			{
				slider_dc.relation.sidePair = "top-bottom";
			}
			return true;
		}
		return false;
	}

}
