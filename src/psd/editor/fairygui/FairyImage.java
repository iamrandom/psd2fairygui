package psd.editor.fairygui;

import java.awt.image.BufferedImage;

public class FairyImage {
	private static int IMAGE_ID; // image 鐨� 鑷ID
	private static String IMAGE_TEMPLATE = "img%03d";
	
	public String id;
	public String name;
	public FairyForder folder;
	public DisplayImage dimg;
	public String parent_name;
	
	public BufferedImage image = null;
	public BufferedImage maskImage = null;
	public boolean need_export = false;
	public boolean need_out = true;
	public boolean is_resource = true;
	
	private static String auto_id()
	{
		String key = String.format(IMAGE_TEMPLATE, ++IMAGE_ID);
		while(FairyPackage.instance.ids.contains(key))
		{
			key = String.format(IMAGE_TEMPLATE, ++IMAGE_ID);
		}
		return key;
	}
	
	public FairyImage(String name, FairyForder folder)
	{
		this.name = name;
		this.folder = folder;
		this.id = FairyImage.auto_id();
	}
	
	public String get_file_format()
	{
		return dimg.get_file_format();
	}
	
	public String get_file_size_name()
	{
		return this.name + "_" + this.dimg.width + "x" + this.dimg.height + "." + this.get_file_format();
	}
	
	public String get_file_name()
	{
		return name + "." + get_file_format();
	}
	
	public String toxml()
	{
		String exp = "";
		if(this.need_export)
		{
			exp = "exported=\"true\"";
		}
		String s = "    <image id=\"%s\" name=\"%s\" path=\"%s\" %s/>";
		return String.format(s, id, get_file_name(), "/image/", exp);
//		String s = "<image id=\"%s\" name=\"%s\" path=\"%s\" qualityOption=\"source\" file=\"%s\" size=\"%d,%d\" %s/>";
//		return String.format(s, id, name, folder.relation_path + "/", get_file_name(), dimg.width, dimg.height, exp);
	}
}
