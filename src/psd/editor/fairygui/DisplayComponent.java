package psd.editor.fairygui;

public class DisplayComponent extends DisplayObject {
	
	public static String COMPONENT_TEMPLATE = "";
	static{
		COMPONENT_TEMPLATE += "    <component id=\"%s\" name=\"%s\" src=\"%s\" xy=\"%d,%d\" %s>\n";
		COMPONENT_TEMPLATE += "%s";
		COMPONENT_TEMPLATE += "    </component>\n";
	}

	public LayoutObject o;
	public LayoutObject ref;
	public String src;
	public String other_center_str = "";
	public boolean add_to_displaylist = true;
	public boolean need_resize = true;
	
	@Override
	public String toxml() {
		String n = name;
		if(this.show_name != null)
		{
			n = this.show_name;
		}
		String other_attr = "";
		if(this.pkg != null && this.pkg.length() > 0)
		{
			other_attr += " pkg=\"" + pkg + "\" ";
		}
		String center_str = "";
		if(this.relation != null)
		{
			center_str += this.relation.to_resource_xml();
		}
		if(this.other_center_str.length() > 0)
		{
			center_str += other_center_str;
		}
		if(ref != null)
		{
			String rex = ref.ref_xml();
			if(rex != null && rex.length() > 0)
			{
				if(center_str != null && center_str.length() > 0)
				{
					center_str += "\n";
				}
				center_str += rex;
			}
		}
		if(need_resize)
		{
			other_attr += "size=\""+ width + "," + height + "\"";
		}
		 
		return String.format(COMPONENT_TEMPLATE, id, n, src, this.x - this.parent.x, this.y - this.parent.y , other_attr, center_str);
	}
}
