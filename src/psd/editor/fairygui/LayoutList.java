package psd.editor.fairygui;

public class LayoutList extends LayoutObject {
	public static String TAG = "list";

	public static String LIST_NORMAL_TEMPLATE = "";
	public static String LIST_ITEM_TEMPLATE = "";
	public static String ITEM_TEMPLATE = "";

	static {
		LIST_NORMAL_TEMPLATE += Util.XML_HEADER;
		LIST_NORMAL_TEMPLATE += "<component size=\"%d,%d\">\n";
		LIST_NORMAL_TEMPLATE += "  <displayList>\n";
		LIST_NORMAL_TEMPLATE += "%s";
		LIST_NORMAL_TEMPLATE += "  </displayList>\n";
		LIST_NORMAL_TEMPLATE += "%s";
		LIST_NORMAL_TEMPLATE += "</component>\n";

		LIST_ITEM_TEMPLATE += "    <list id=\"%s\" name=\"%s\" xy=\"%d,%d\" size=\"%d,%d\" margin=\"%d,%d,%d,%d\"  %s>\n";
		LIST_ITEM_TEMPLATE += "%s";
		LIST_ITEM_TEMPLATE += "    </list>\n";
	}
	public int item_cnt = 1;
	public String show_name;
	public LayoutItem forder_item;
	public DisplayImage single_item;
	public String display_id;

	public String layout;
	public String overflow;

	public Relation width_releation = new Relation("", "width");
	public Relation height_releation = new Relation("", "height");

	public class ChildInfo {
		public int min_x = 0;
		public int min_y = 0;
		public int max_x = 0;
		public int max_y = 0;
	}
	
	public LayoutList()
	{
		this.tag = TAG;
	}

	public String get_list_name() {
		return show_name != null ? show_name : this.name;
	}

	public String make_item_image(DisplayImage img) {
		String mid = FairyShared.instance.cur_spd_id;
		String item_id = img.src;
		if (img.pkg != null && img.pkg.length() > 0) {
			FairyShared.OtherFairy ot = FairyShared.instance.ref_folder(img.pkg);
			FairyShared.OtherResource or = FairyShared.instance.ref_comp(ot, img.name);
			if(or == null)
			{
				or = FairyShared.instance.ref_image(ot, img.name);
			}
			if(null == or)
			{
				mid = FairyShared.instance.find_forder_id(img.pkg, img.parent_path
						+ img.name);
				item_id = FairyShared.instance
						.find_resource_id(img.pkg,
								img.name + "." + img.get_file_format(),
								img.parent_path + img.name);
				return "";
			}
			mid = ot.id;
			item_id = or.id;
		}
		return String.format(" defaultItem=\"ui://%s%s\" ", mid, item_id);
	}
	
	
	private String item_toxml()
	{
		int left_margin = 0;
		int right_margin = 0;
		int top_margin = 0;
		int bottom_margin = 0;
		int linegap = 1;
		if ((single_item != null) || (forder_item != null)) {
			if (single_item != null) {
				left_margin = single_item.x - x;
				top_margin = single_item.y - y;
			}
			if (forder_item != null) {
				left_margin = forder_item.x - x;
				top_margin = forder_item.y - y;
			}
			right_margin = left_margin;
			bottom_margin = top_margin;

			int py = (single_item != null) ? (single_item.y + single_item.height)
					: (forder_item.y + forder_item.height);
			linegap = 100;
			for (DisplayObject d : display_list) {
				if (py > d.y) {
					continue;
				}
				if (linegap > (d.y - py)) {
					linegap = d.y - py;
				}
			}
			if (linegap == 100 || linegap < 0) {
				linegap = 1;
			}
		}
		else
		{
			FairyShared.Error("Error List have no item \n" + "	" + this.parent_path + this.name);
		}
		String s = "";
		if (this.bg != null) {
			s += bg.toxml();
		}
		String itemStr = "";
		itemStr += width_releation.to_resource_xml();
		if (this.item_cnt > 0) {
			String title_str = "";
			if(this.single_item != null && this.single_item.ref_dc != null && this.single_item.ref_dc.ref != null)
			{
				if(this.single_item.ref_dc.ref instanceof LayoutButton)
				{
					LayoutButton btn = (LayoutButton)this.single_item.ref_dc.ref;
					if(btn.title != null)
					{
						title_str = "title=\"" + btn.title.text +"\"";
					}
				}
			}
			for (int i = 0; i < item_cnt; ++i) {
				itemStr += "      <item  " + title_str + "/>\n";
			}
		}
		String sss = "";

		int ww = 0;
		int line_cnt = 0;

		if (single_item != null) {
			ww = single_item.width;
			sss += make_item_image(single_item);

		} else if (forder_item != null) {
			ww = forder_item.width;
			if (forder_item.display_list.size() < 2) {
				forder_item.need_create_resource = false;
			}

			if (!forder_item.need_create_resource) {
				if (forder_item.display_list.size() == 1) {
					DisplayObject ddo = forder_item.display_list.get(0);
					if (ddo instanceof DisplayImage) {
						sss += make_item_image((DisplayImage) ddo);
					} else if (ddo instanceof DisplayComponent) {
						String mid = FairyShared.instance.cur_spd_id;
						String item_id = ((DisplayComponent) ddo).src;
						sss += String.format(" defaultItem=\"ui://%s%s\" ",
								mid, item_id);
					}
				}
			} else {
				String mid = FairyShared.instance.cur_spd_id;
				String item_id = forder_item.id;
				sss += String.format(" defaultItem=\"ui://%s%s\" ", mid,
						item_id);
			}
		}
		if (ww > 0) {
			line_cnt = this.width / ww;
		}

		if (item_cnt > 0) {
			if (layout != null) {
				// flow_hz
				sss += String.format(" layout=\"%s\" ", layout);
			}

			if (overflow != null) {
				// scroll
				sss += String.format(" overflow=\"%s\" ", overflow);
			}

			if (linegap != 0) {
				sss += String.format(" lineGap=\"%d\" ", linegap);
			}
			if (ww > 0 && line_cnt > 1) {
				int col_gap = (this.width % ww - left_margin - right_margin)
						/ (line_cnt - 1);
				if (col_gap < 0)
				{
					col_gap = 1;
				}
				sss += String.format(" colGap=\"%d\" ", col_gap);
			}
			
			if (line_cnt > 1) {
				line_cnt = (this.width - left_margin - right_margin) / ww;
				if(line_cnt < 0)
				{
					line_cnt = 1;
				}
				sss += String.format(" lineItemCount=\"%d\" ", line_cnt);
			}
		}
		if (this.display_id == null) {
			this.display_id = DisplayObject.auto_id();
		}
//		left_margin = 0;
//		right_margin = 0;
//		top_margin = 0;
//		bottom_margin = 0;
		s += String.format(LIST_ITEM_TEMPLATE, this.display_id,
				this.get_list_name(), 0, 0, width, height, top_margin,
				bottom_margin, left_margin, right_margin, sss, itemStr);
		height_releation.target = this.display_id;
		return s;
	}

	@Override
	public String toxml() {
		String s = "";
		for (DisplayObject d : display_list) {
			boolean is_item = false;
			if(d == this.single_item)
			{
				is_item = true;
			}
			if(d instanceof DisplayComponent)
			{
				if(((DisplayComponent)d).o == this.forder_item)
				{
					is_item = true;
				}
			}
			if(is_item)
			{
				s += this.item_toxml();
			}
			else
			{
				s += d.toxml();
			}
		}
		return String.format(LIST_NORMAL_TEMPLATE, width, height, s,
				this.height_releation.to_resource_xml());
	}

	@Override
	public boolean command_image_key(DisplayImage o, String key) {
		if (key.equals(LayoutItem.TAG)) {
			single_item = o;
			return true;
		}
		if (!key.equals("bg")) {
//			o.is_resource = false;
		} else {
//			o.name = name + "_" + key;
			o.relation = new Relation("", "width,height");
			return true;
		}
		return false;
	}

	@Override
	public boolean command_forder_key(DisplayComponent dc, String key) {
		// TODO Auto-generated method stub
		if (key.equals(LayoutItem.TAG)) {
			forder_item = new LayoutItem();
			dc.o = forder_item;
			display_id = dc.id;
			return true;
		}
		return false;
	}
}
