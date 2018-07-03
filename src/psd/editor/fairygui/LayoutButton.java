package psd.editor.fairygui;


public class LayoutButton extends LayoutObject{
	
	public static String TAG = "btn";

	public static String BUTTON_TEMPLATE = "";
	
	static
	{
		BUTTON_TEMPLATE += Util.XML_HEADER;
		BUTTON_TEMPLATE += "<component size=\"%d,%d\" extention=\"Button\">\n";
		BUTTON_TEMPLATE += "  <Button/>\n";
		BUTTON_TEMPLATE += "%s";
		BUTTON_TEMPLATE += "  <displayList>\n";
		BUTTON_TEMPLATE += "%s";
		BUTTON_TEMPLATE += "  </displayList>\n";
		BUTTON_TEMPLATE += "</component>\n";
	}
	
	public DisplayText title = null;
	
	public LayoutButton()
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
		return String.format(BUTTON_TEMPLATE, width, height, controller_str, s);
	}
	
	@Override
	public void command_text_key(DisplayText dt, String key) {
		if(key.equals("title"))
		{
			dt.show_name = "title";
			this.title = dt;
		}
	}
	
	@Override
	public boolean command_image_key(DisplayImage o, String key) {
		if(Controller.PAGES.get(key) == null) return false;
//		o.name = name + "_" + key;
		
		if(controller == null)
		{
			controller = new Controller("button");
		}
		o.controller = controller;
		controller.images.put(key, o);
		o.active_page = key;
		o.relation = new Relation("", "width,height");
		return true;
	}
	
	
	@Override
	public String ref_xml() {
		return "      <Button title=\""+ this.title.text +"\"/>\n";
	}
}
