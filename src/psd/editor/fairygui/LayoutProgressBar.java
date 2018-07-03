package psd.editor.fairygui;

public class LayoutProgressBar  extends LayoutObject{
	public static String TAG = "progress";
	public DisplayImage bar;
	
	public static String PROGRESS_TEMPLATE = "";
	
	static
	{
		PROGRESS_TEMPLATE += Util.XML_HEADER;
		PROGRESS_TEMPLATE += "<component size=\"%d,%d\" extention=\"ProgressBar\">\n";
		PROGRESS_TEMPLATE += "  <ProgressBar/>\n";
		PROGRESS_TEMPLATE += "  <displayList>\n";
		PROGRESS_TEMPLATE += "%s";
		PROGRESS_TEMPLATE += "  </displayList>\n";
		PROGRESS_TEMPLATE += "</component>\n";
	}
	
	public LayoutProgressBar()
	{
		this.tag = TAG;
	}
	
	@Override
	public String toxml() {
		
		if(bar == null)
		{
			FairyShared.Error("Error progress have no bar \n" + "	" + this.parent_path + this.name);
		}
		String s = "";
		for(DisplayObject d : display_list)
		{
			s +=  d.toxml() ;
		}
		return String.format(PROGRESS_TEMPLATE, width, height, s);
	}
	
	@Override
	public void command_text_key(DisplayText dt, String key) {
		if(key.equals("title"))
		{
			dt.show_name = "title";
//			dt.relation = new Relation("", "width,height");
			return;
		}
	}
	
	@Override
	public boolean command_image_key(DisplayImage o, String key) {
		if(key.equals("bg"))
		{
//			o.name = name + "_" + key;
//			o.show_name = key;
			o.relation = new Relation("", "width,height");
			return true;
		}
		if(key.equals("bar"))
		{
			this.bar = o;
//			o.name = name + "_" + key;
			o.show_name = key;
			return true;
		}
		return false;
	}
	
	@Override
	public String ref_xml() {
		return  "      <ProgressBar value=\"50\" max=\"100\"/>";
	}
	
}
