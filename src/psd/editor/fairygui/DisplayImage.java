package psd.editor.fairygui;

import psd.model.Layer;

public class DisplayImage extends DisplayObject {
	public static String TAG = "image";
	private static String DISPAY_TEMPLATE = "";
	
	static
	{
		DISPAY_TEMPLATE += "    <image id=\"%s\" name=\"%s\" src=\"%s\" xy=\"%d,%d\" %s>\n";
		DISPAY_TEMPLATE += "%s";
		DISPAY_TEMPLATE += "    </image>\n";
	}
	
	public String src;
	public String suf;
	public Controller controller;
	public String active_page;
	public boolean is_resource = true;
	public boolean need_export = false;
	public Layer layer;
	
//	public DisplayText title = null;
	
	public String get_file_format()
	{
		if(suf == null) return "png";
		String s = suf.toLowerCase();
		if(Suffix.FileSuffixs.contains(s)) return s;
		return "png";
	}
	

	@Override
	public String toxml() {
		String center_str = "";
		if(controller != null)
		{
			center_str += controller.to_resource_xml(active_page);
		}
		if(this.relation != null)
		{
			center_str += this.relation.to_resource_xml();
		}
		return to_template_xml(center_str);
	}
	
	public DisplayComponent ref_dc;
	public DisplayComponent create_ref_comp(LayoutObject obj)
	{
		FairyShared.OtherFairy of = FairyShared.instance.ref_folder(this.pkg);
		if(this.name.equals("btn_pub_xuanfu"))
		{
			System.out.print("");
		}
		FairyShared.OtherResource or = FairyShared.instance.ref_comp(of, this.name);
		DisplayComponent dc = new DisplayComponent();
		dc.ref = obj;
		ref_dc = dc;
		ref_dc.need_resize = false;
		ref_dc.x = this.x;
		ref_dc.y = this.y;
		if(!"9".equals(this.suf))
		{
			if(or == null)
			{
				FairyShared.Error("Error need a button component, but can't find it : " + this.name + "   " + this.parent_path);
				FairyShared.OtherResource ot = FairyShared.instance.ref_image(of, this.name);
				if(ot != null)
				{
					FairyShared.Error("Note:  " + this.name + " is a image, not a button  ");
				}
			}
			if(or != null && (or.width != 0 || or.height != 0))
			{
				ref_dc.x += ( this.width - or.width)/2;
				ref_dc.width = or.width;
				ref_dc.y += (this.height - or.height)/2;
				ref_dc.height = or.height;
			}
			ref_dc.need_resize = true;
		}
		return dc;
	}
	
	public String to_template_xml(String s)
	{
		FairyShared.OtherFairy of = FairyShared.instance.ref_folder(this.pkg);
		FairyShared.OtherResource or = FairyShared.instance.ref_comp(of, this.name);
		if (ref_dc == null)
		{
			String n = name;
			if(this.show_name != null)
			{
				n = this.show_name;
			}
			String other_attr = "";
			String src_ext = this.src;
			boolean need_size = false;
			if(this.pkg != null && this.pkg.length() > 0)
			{
				
				FairyShared.OtherResource fo = FairyShared.instance.ref_image(of, this.name);
				if(fo == null)
				{
					src_ext = FairyShared.instance.find_resource_id(this.pkg, this.name + "." + this.get_file_format(), this.parent_path + this.name);
				}
				else
				{
					other_attr += " pkg=\"" + of.id + "\" ";
					src_ext = fo.id;
					FairyPackage.ImageInfo imgInfo = of.pkg.image_infos.get(fo.filename);
					if(imgInfo != null)
					{
						if(imgInfo.scale9grid != null)
						{
							need_size = true;
						}
						if(imgInfo.exported == false)
						{
							FairyShared.Error(this.pkg + imgInfo.path + this.name+"."+this.get_file_format() + " not export");
						}
					}
				}
			
			}
			if(this.layer != null && this.layer.opacity > 0 && this.layer.opacity < 255)
			{
				other_attr += " alpha=\"" + (this.layer.opacity/255.0f) + "\" ";
			}
			if("9".equals(this.suf) || need_size)
			{
				other_attr += String.format(" size=\"%d,%d\" ", this.width, this.height);
			}
			return String.format(DISPAY_TEMPLATE, this.id, n, src_ext, this.x - this.parent.x, this.y - this.parent.y, other_attr, s);
		}
		else
		{
			if(or == null)
			{
				FairyShared.instance.find_resource_id(this.pkg, this.name , this.parent_path + this.name);
				return "";
			}
			ref_dc.name = this.name;
			ref_dc.pkg = of.id;
			ref_dc.src = or.id;
//			ref_dc.x = this.x;
//			ref_dc.y = this.y;
			ref_dc.parent = this.parent;
//			ref_dc.width = this.width;
//			ref_dc.height = this.height;
			return ref_dc.toxml();
		}
	
	}
}
