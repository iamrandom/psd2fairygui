package psd.editor.fairygui;

public class DisplayObject {
	
	private static int DISPLAY_ID = 0; // image 的 自增ID
	private static String DISPLAY_TEMPLATE = "dsp%05d";
	public static String auto_id()
	{
		String key = String.format(DISPLAY_TEMPLATE, ++DISPLAY_ID);
		return key;
	}
	
	public String id;
	public String name = "";
	public int width;
	public int height;
	public int x = 0;
	public int y = 0;
	public String pkg = "";
	public LayoutObject parent;
	public String parent_path;
	public Relation relation;
	public String show_name = null;
	public boolean neglect_rect = false; 
	
	
	public DisplayObject()
	{
		this.id = auto_id();
	}
	
	public String toxml()
	{
		return "";
	}
	
	
}
