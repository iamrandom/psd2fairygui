package psd.editor.fairygui;

public class DisplayGraph extends DisplayObject
{
	private static String GRAPH_TEMPLATE = "";
	
	static
	{
		GRAPH_TEMPLATE += "    <graph id=\"%s\" name=\"%s\" xy=\"%d,%d\" size=\"%d,%d\" %s>\n";
		GRAPH_TEMPLATE += "%s";
		GRAPH_TEMPLATE += "    </graph>\n";
	}
	
	
	@Override
	public String toxml() {
		String center_str = "";
		if(this.relation != null)
		{
			center_str += this.relation.to_resource_xml();
		}
		// TODO Auto-generated method stub
		return to_template_xml(center_str);
	}

	public String to_template_xml(String s)
	{
		String n = name;
		if(this.show_name != null)
		{
			n = this.show_name;
		}
		return String.format(GRAPH_TEMPLATE, this.id, n, this.x - this.parent.x, this.y - this.parent.y, this.width, this.height, "", s);
	}
	
}
