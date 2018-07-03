package psd.editor.fairygui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LayoutObject {
	
	private static int COMPONENT_ID = 0;
	private static String COMPONENT_TEMPLATE = "cmp%05d";
	private static String RESOURCE_TEMPLATE = "<component id=\"%s\" name=\"%s\" path=\"%s\" />";
	
	private static String LAYOUT_TEMPLATE = "";
	static
	{
		LAYOUT_TEMPLATE += Util.XML_HEADER;
		LAYOUT_TEMPLATE += "<component size=\"%d,%d\">\n";
		LAYOUT_TEMPLATE += "  <displayList>\n";
		LAYOUT_TEMPLATE += "%s\n";
		LAYOUT_TEMPLATE += "  </displayList>\n";
		LAYOUT_TEMPLATE += "</component>\n";
	}
	
	private static String auto_id()
	{
		String key = String.format(COMPONENT_TEMPLATE, ++COMPONENT_ID);
		while(FairyPackage.instance.ids.contains(key))
		{
			key = String.format(COMPONENT_TEMPLATE, ++COMPONENT_ID);
		}
		return key;
	}
	
	public String id;
	public String name;
	public String forder = "";
	public int width;
	public int height;
	public int x;
	public int y;
	public Controller controller;
	public DisplayImage bg;
	public boolean need_create_resource = true;
	public String pkg;
	public String tag;
	public String parent_path;
	
	public List<DisplayObject> display_list = new ArrayList<DisplayObject>();
	
	public LayoutObject()
	{
		this.id = auto_id();
	}
	
	//这里面是组件中包含的各种组件，图片
	
	public void command_display_repeat_name()
	{
		HashMap<String, Integer> repeat_names = new HashMap<String, Integer>();
		for(DisplayObject d : display_list)
		{
			String nn = d.show_name;
			if(nn == null)
			{
				nn = d.name;
			}
			Integer count = repeat_names.get(nn);
			if(count == null)
			{
				count = 0;
			}
			++count;
			repeat_names.put(nn, count);
		}
		HashMap<String, Integer> repeat_names2 = new HashMap<String, Integer>();
		for(DisplayObject d : display_list)
		{
			if(d.show_name != null)
			{
				continue;
			}
			Integer count = repeat_names.get(d.name);
			if(count == null || count <= 1)
			{
				continue;
			}
			count = repeat_names2.get(d.name);
			if(count == null)
			{
				count = 0;
			}
			++count;
			d.show_name = d.name + count;
			while(repeat_names.containsKey(d.show_name))
			{
				++count;
				d.show_name = d.name + count;
			}
			repeat_names2.put(d.name, count);			
		}
	}
	
	public String toxml()
	{
		String s = "";
		for(DisplayObject d : display_list)
		{
			s +=  d.toxml() ;
		}
		return String.format(LAYOUT_TEMPLATE, width, height, s);
	}
	
	public String to_resource_xml()
	{
		String fd = "";
		if(this.forder != null && this.forder.length() > 0)
		{
			fd = "/" + this.forder + "/";
		}
		return String.format(RESOURCE_TEMPLATE, this.id, this.get_file_name(), fd);
	}
	
	public String to_root_resource_xml()
	{
		String fd = "";
		if(this.forder != null && this.forder.length() > 0)
		{
			fd = "/" + this.forder + "/";
		}
		return String.format(RESOURCE_TEMPLATE, this.id, this.get_root_file_name(), fd);
	}
	
	public String get_file_name()
	{
		return this.name + ".xml";
	}
	
	public String get_root_file_name()
	{
		return this.name + ".xml";
	}
	
	public void command_text_key(DisplayText dt, String key)
	{
		
	}
	
	public boolean command_image_key(DisplayImage o, String key)
	{
		return false;
	}
	
	public boolean command_forder_key(DisplayComponent dc, String key)
	{
		return false;
	}
	
	public String ref_xml()
	{
		return null;
	}
	
}
