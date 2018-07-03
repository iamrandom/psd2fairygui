package psd.editor.fairygui;

import java.io.File;

public class FairyForder {
	private static int FORDER_ID = 0; // Forder的自增ID
	private static String FORDER_TEMPLATE = "fd%03d";
	
	public FairyForder folder;
	public String name;
	public String id;
	public boolean  is_create = false;
	public String path;
	public String relation_path;
	
	
	private static String auto_id()
	{
		return String.format(FORDER_TEMPLATE, ++FORDER_ID);
	}
	
	public FairyForder(String name, FairyForder folder)
	{
		this.name = name;
		this.folder = folder;
		this.id = FairyForder.auto_id();
		if(folder != null)
		{
			this.path = folder.path  + File.separator + name;
			if(folder.relation_path != null)
			{
				this.relation_path = folder.relation_path + "/" + name;
			}
			else
			{
				this.relation_path = "/" + name;
			}
		}
	}
	
	public void create_dir()
	{
		if(folder != null)
		{
			if(!folder.is_create)
			{
				folder.create_dir();
			}
		}
		if(!this.is_create)
		{
			this.is_create = true;
		
			File fd = new File(this.path);
			if(fd.exists() && fd.isDirectory())
			{
				return;
			}
			System.out.println(this.relation_path);
			fd.mkdir();
		}
	}
	
	public String toxml()
	{
		String s = "<folder id=\"%s\" name=\"%s\" folder=\"%s\"/>";
		return String.format(s, id, name, folder.id);
	}
}
