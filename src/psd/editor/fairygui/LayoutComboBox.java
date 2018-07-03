package psd.editor.fairygui;

public class LayoutComboBox extends LayoutObject {
	public static String TAG = "combobox";
	
	public static String COMBOBOX_TEMPLATE = "";
	static
	{
		COMBOBOX_TEMPLATE += Util.XML_HEADER;
		COMBOBOX_TEMPLATE += "<component size=\"%d,%d\" extention=\"ComboBox\">\n";
		COMBOBOX_TEMPLATE += "  <ComboBox  %s />\n";
		COMBOBOX_TEMPLATE += "%s";
		COMBOBOX_TEMPLATE += "  <displayList>\n";
		COMBOBOX_TEMPLATE += "%s";
		COMBOBOX_TEMPLATE += "  </displayList>\n";
		COMBOBOX_TEMPLATE += "</component>\n";
	}
	
	public LayoutList list;
	public DisplayImage ref_list;
	public Relation relation = new Relation("", "width,height");
	
	public LayoutComboBox()
	{
		this.tag = TAG;
	}
	
	@Override
	public String toxml() {
		String controller_str = "";
		if(this.controller != null)
		{
			controller_str = "  "+this.controller.toxml() + "\n";
		}
		String s = "";
		for(DisplayObject d : display_list)
		{
			s += d.toxml();
		}
		
		String dropitem = "";
		if(list != null)
		{
			dropitem = String.format("dropdown=\"ui://%s%s\"", FairyShared.instance.cur_spd_id, list.id);
		}
		else if(ref_list != null)
		{
			FairyShared.OtherFairy of = FairyShared.instance.ref_folder(ref_list.pkg);
			FairyShared.OtherResource or = FairyShared.instance.ref_comp(of, ref_list.name);
			if(or == null)
			{
				FairyShared.instance.find_resource_id(ref_list.pkg, ref_list.name , ref_list.parent_path + ref_list.name);
			}
			else
			{
				dropitem = String.format("dropdown=\"ui://%s%s\"", of.id, or.id);
			}
		}
		else
		{
			FairyShared.Error("Error combobox have no list \n" + "	" + this.parent_path + this.name);
		}
		return String.format(COMBOBOX_TEMPLATE, width, height, dropitem, controller_str, s);
	}
	
	@Override
	public void command_text_key(DisplayText dt, String key) {
		if(key.equals("title"))
		{
			dt.show_name = key;
			dt.relation = relation;
		}
	}
	
	@Override
	public boolean command_image_key(DisplayImage o, String key) {
		if(Controller.PAGES.get(key) != null)
		{
//			o.name = name + "_" + key;
			
			if(controller == null)
			{
				controller = new Controller("button");
			}
			o.controller = controller;
			controller.images.put(key, o);
			o.active_page = key;
			o.relation = relation;
			return true;
		}
		if(key.equals(LayoutList.TAG))
		{
			this.ref_list = o;
			o.show_name = key;
			o.neglect_rect = true;
			o.is_resource = false;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean command_forder_key(DisplayComponent dc, String key) {
		// TODO Auto-generated method stub
		if(key.equals(LayoutList.TAG))
		{
			list = new LayoutList();
			list.item_cnt = 0;
			list.show_name = "list";
			dc.o = list;
			dc.add_to_displaylist = false;
			dc.neglect_rect = true;
			return true;
		}
		return false;
	}
}
