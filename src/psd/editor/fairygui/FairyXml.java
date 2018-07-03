package psd.editor.fairygui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import psd.model.Layer;
import psd.model.Psd;
import psd.parser.layer.LayerType;
import psd.parser.object.PsdDescriptor;
import psd.parser.object.PsdText;
import psd.parser.object.PsdTextData;
import psd.parser.object.PsdUnitFloat;

public class FairyXml {

	public static Map<String, ArrayList<FairyImage>> image_name_map = new HashMap<String, ArrayList<FairyImage>>();
	public static Map<String, Integer> mask_name_map = new HashMap<String, Integer>();

	public static Set<String> image_sets = new HashSet<String>();
	public static Set<String> forder_sets = new HashSet<String>();
	static {
		forder_sets.add(LayoutButton.TAG);
		forder_sets.add(LayoutComboBox.TAG);
		forder_sets.add(LayoutProgressBar.TAG);
		forder_sets.add(LayoutSlider.TAG);
		forder_sets.add(LayoutList.TAG);
		forder_sets.add(LayoutScrollBar.TAG);
		forder_sets.add("grip");
		image_sets.add(DisplayLoader.TAG);
	}

	private Psd psd;
	private Map<String, FairyForder> forders = new HashMap<String, FairyForder>();
	private Map<String, FairyImage> images = new HashMap<String, FairyImage>();
	private List<LayoutObject> components = new ArrayList<LayoutObject>();
	LayoutObject root_component;

	private String forlders_str = "";
	private String images_str = "";

	FairyForder root = new FairyForder("", null);

	public FairyXml(Psd psd) {
		this.psd = psd;
		root.id = "";
	}

	private void write_image(String outdir, FairyImage img) {
		if (img == null || img.image == null)
			return;
		String s = outdir + File.separator + img.get_file_name();
		
//		System.out.println(s + "			" + img.name);
//		String s = img.folder.path + File.separator + img.get_file_name();

		File file = new File(s);
		if(file.exists()) return;
		if (img.get_file_format().equals("jpg")
				|| img.get_file_format().equals("jpeg")) {
			BufferedImage rgbImage = new BufferedImage(img.image.getWidth(),
					img.image.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			ColorConvertOp op = new ColorConvertOp(null);
			op.filter(img.image, rgbImage);
			try {
				ImageIO.write(rgbImage, img.get_file_format(), file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
//			System.out.println(s + "	" + img.folder.relation_path);
			try {
				ImageIO.write(img.image, img.get_file_format(), file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	private void write_public_xml_exist(FairyPackage fp, String outpath, String autoid, boolean real) {
		fp.id = autoid;
		String cmpstr = "";
		String local_img_str = "";
		for(String key:this.images.keySet())
		{
			FairyImage img = this.images.get(key);
			if(img.is_resource)
			{
				local_img_str += "\n" + img.toxml();
			}
		}
		cmpstr += "    " + this.root_component.to_root_resource_xml() + "\n";
		for (LayoutObject c : components) {
			if (c.need_create_resource) {
				String s = c.to_resource_xml();
				if (s != null && s.length() > 0) {
					cmpstr += "    " + s + "\n";
				}
			}
		}
		String xmlStr = fp.toxml(forlders_str, local_img_str, cmpstr);
		if (real) {
			fp.write_xml_exist(outpath, "package.xml", cmpstr, local_img_str);
		}
	}

	private void write_public_xml(String outpath, String name, String autoid, boolean real) {
		FairyPackage fairypackage = new FairyPackage();
		fairypackage.id = autoid;
		fairypackage.name = name;
		String cmpstr = "";
		String local_img_str = "";
		for(String key:this.images.keySet())
		{
			FairyImage img = this.images.get(key);
			if(img.is_resource)
			{
				local_img_str += "\n" + img.toxml();
			}
		}
		cmpstr += "    " + this.root_component.to_root_resource_xml() + "\n";
		for (LayoutObject c : components) {
			if (c.need_create_resource) {
				String s = c.to_resource_xml();
				if (s != null && s.length() > 0) {
					cmpstr += "    " + s + "\n";
				}
			}
		}
		String xmlStr = fairypackage.toxml(forlders_str, local_img_str, cmpstr);
		if (real) {
			write_xml(outpath, "package.xml", xmlStr);
		}
	}

	public void write_xml(String outpath, String filename, String xmlStr) {
		File outFile = new File(outpath);
		if(!outFile.exists() || !outFile.isDirectory())
		{
			outFile.mkdirs();
		}
		String fulln = outpath + File.separator + filename;
		File xmlFile = new File(fulln);
		try {
			xmlFile.createNewFile();
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(xmlFile), "UTF-8");
			out.write(xmlStr.toCharArray());
			out.flush();
			out.close();

			// System.out.print(xmlStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			FairyShared.Error("error happend on write file : " + filename
					+ "\n" + xmlStr);
			e.printStackTrace();
		}
	}

	public class ChildInfo {
		public int min_x = 0;
		public int min_y = 0;
		public int max_x = 0;
		public int max_y = 0;
	}
	
	
	public boolean check_same_image(FairyImage select_one, FairyImage comp_one, int[] select_data)
	{
		if(select_one.dimg.width != comp_one.dimg.width || select_one.dimg.height != comp_one.dimg.height)
		{
			return false;
		}
		else
		{
			Raster rast = select_one.image.getData( new Rectangle(0, select_one.dimg.height/2, select_one.dimg.width, 1));
			int[] data = ((DataBufferInt)rast.getDataBuffer()).getData();
			for(int i = 0; i < data.length; ++i)
			{
				if(data[i] != select_data[i])
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean check_all_same(ArrayList<FairyImage> images)
	{
		FairyImage select_one = images.get(0);
		Raster select_rast = select_one.image.getData( new Rectangle(0, select_one.dimg.height/2, select_one.dimg.width, 1));
		int[] select_data = ((DataBufferInt)select_rast.getDataBuffer()).getData();
		
		for(int i = 1; i < images.size(); ++i)
		{
			if(!check_same_image(select_one, images.get(i), select_data))
			{
				return false;
			}
		}
		return true;
	}
	Map<String, Map<String, ArrayList<FairyImage>>> image_check;
	
	private void parse_no_folder(String work_dir, String outpath, String fn, File fd)
	{
		root.path = outpath;
		String autoid = FairyShared.instance.auto_cur_id(work_dir);
		if (null == autoid)
			return;
		root_component = new LayoutObject();
		ChildInfo info = parse_help(this.root_component, root, psd.layers, "",
				0);
		if (null == info) {
			return;
		}
		FairyShared.Log("auto gen package id:" + autoid);
		System.out.println("psd:"+"	" + this.psd.getWidth()+"	"+this.psd.getHeight());
		
		root_component.name = fn;
		root_component.forder = "";
		root_component.width = info.max_x - info.min_x;
		root_component.height = info.max_y - info.min_y;
		if(root_component.width > psd.getWidth())
		{
			root_component.width = psd.getWidth();
		}
		if(root_component.height > psd.getHeight())
		{
			root_component.height = psd.getHeight();
		}
		root_component.x = 0;
		root_component.y = 0;

		FairyShared.Log(String.format("the rect is : %d %d %d %d", info.min_x,
				info.min_y, (info.max_x - info.min_x),
				(info.max_y - info.min_y)));
		Map<String, Map<String, ArrayList<FairyImage>>> mf = new HashMap<String, Map<String, ArrayList<FairyImage>>>();
		this.image_check = mf;
		for (String filename : image_name_map.keySet()) {
			
			ArrayList<FairyImage> al = image_name_map.get(filename);
//			if(filename.equals("bg_jianzhu_fuzhuqian"))
//			{
//				System.out.println("filename:" + filename + "	" + al.size());
//			}
			if (al.size() > 1) {
				{
					Map<String, ArrayList<FairyImage>> ma = mf.get(filename);
					if(ma == null)
					{
						ma = new HashMap<String, ArrayList<FairyImage>>();
						mf.put(filename, ma);
					}
					
					for(FairyImage fi : al)
					{
						String new_name = fi.name + "_" + fi.dimg.width + "x" + fi.dimg.height;
						ArrayList<FairyImage> al2 = ma.get(new_name);
						if(al2 == null)
						{
							al2 = new ArrayList<FairyImage>();
							ma.put(new_name, al2);
						}
						al2.add(fi);
					}
				}
		
				
				for(String key1 : mf.keySet())
				{
					boolean is_error = false;
					Map<String, ArrayList<FairyImage>> ma = mf.get(key1);
					String s = "Error: same image name resource " + key1 + "\n";
					if(ma.size() == 1)
					{
						// only one size
						String[] key2s = new String[ma.size()];
						ma.keySet().toArray(key2s);
						ArrayList<FairyImage> aaf = ma.get(key2s[0]);
						FairyImage select_f = aaf.get(0);
						if(!check_all_same(aaf))
						{
							for(int tt = 0; tt < aaf.size(); ++tt)
							{
								FairyImage rr = aaf.get(tt);
								if(tt > 0)
								{
									rr.is_resource = false;
									rr.dimg.src = select_f.id;
								}
								if(rr.dimg.show_name == null)
								{
									rr.dimg.show_name = rr.name + (tt + 1);
								}
								s += String.format("	%d  %d	%s (same)\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
							}
							is_error = true;
						}
						else
						{
							for(int tt = 0; tt < aaf.size(); ++tt)
							{
								FairyImage rr = aaf.get(tt);
								if(tt > 0)
								{
									rr.is_resource = false;
									rr.dimg.src = select_f.id;
								}
								if(rr.dimg.show_name == null)
								{
									rr.dimg.show_name = rr.name + (tt + 1);
								}
								s += String.format("	%d  %d	%s\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
							}
							is_error = true;
						}
					}
					else
					{
						is_error = true;
						for(String key2 : ma.keySet())
						{
							ArrayList<FairyImage> aaf = ma.get(key2);
							FairyImage select_f = aaf.get(0);
							if(!check_all_same(aaf))
							{
								
								for(int tt = 0; tt < aaf.size(); ++tt)
								{
									FairyImage rr = aaf.get(tt);
									if(tt > 0)
									{
										rr.is_resource = false;
										rr.dimg.src = select_f.id;
									}
									if(rr.dimg.show_name == null)
									{
										rr.dimg.show_name = rr.name + (tt + 1);
									}
									rr.name = key2;
									s += String.format("	%d  %d	%s\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
								}
							}
							else
							{
								for(int tt = 0; tt < aaf.size(); ++tt)
								{
									FairyImage rr = aaf.get(tt);
									if(tt > 0)
									{
										rr.is_resource = false;
										rr.dimg.src = select_f.id;
									}
									if(rr.dimg.show_name == null)
									{
										rr.dimg.show_name = rr.name + (tt + 1);
									}
									rr.name = key2;
									s += String.format("	%d  %d	%s (same)\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
								}
							}
						}
					}
					if(is_error)
					{
//						FairyShared.Error(s);
					}
				}

//				else
//				{
//				for (int i = 1; i < fails.length; ++i) {
//					FairyImage rr = fails[i];
//					rr.name += "_" + i;
////					rr.dimg.src = select_one.id;
//					rr.need_out = true;
//				}
//				}
			}
		}
		root_component.toxml();
		String parent_comp = "component";

		for (LayoutObject o : this.components) {
			if(o.tag != null)
			{
				o.forder = o.tag;
			}
			else
			{
				o.forder = parent_comp;
			}
//			o.forder = o.
			o.toxml();
		}
		write_public_xml(outpath, fn, autoid, false);
		
		if (FairyShared.error_cnt > 0) {
			FairyShared.Warn("\nCommand Error first");
			return;
		}

//		root.create_dir();
		FairyShared.show_off = true;
		fd.mkdir();
//		for (String key : forders.keySet())
//		{
//			FairyForder ff = forders.get(key);
//			if(ff != null)
//			{
//				ff.create_dir();
//			}
//		}
		File imageDir = new File(outpath + File.separator + "image");
//		if(parent_comp.length() > 0)
//		{
//			File pff = new File(outpath + File.separator + parent_comp);
//			if(!pff.exists() || !pff.isDirectory())
//			{
//				pff.mkdirs();
//			}
//		}
		
		if(!imageDir.exists() || !imageDir.isDirectory())
		{
			imageDir.mkdir();
		}
		for (String key : images.keySet()) {
			FairyImage img = images.get(key);
			if(img != null && img.need_out)
			{
				write_image(imageDir.getAbsolutePath(), img);
			}
		}
		root_component.command_display_repeat_name();
		write_xml(outpath, root_component.get_root_file_name(),
				root_component.toxml());
		for (LayoutObject o : this.components) {
			if (o.need_create_resource) {
				o.command_display_repeat_name();
				write_xml(outpath + File.separator + o.forder, o.get_file_name(), o.toxml());
			}
		}
		write_public_xml(outpath, fn, autoid, true);
	}
	private void parse_exist_folder(String work_dir, String outpath, String fn, File fd)
	{
		FairyPackage fpack = FairyPackage.instance;
		fpack.parse_package(work_dir, fn);
		
		FairyShared.Warn(fn +" has existed, I will check the gen process.\nDo you wan continue to parse psd , Y/N:");
		try {
			char y = (char)System.in.read();
			if(y != 'y')
			{
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		FairyShared.Log("continue to create" + fn + "\n");
		root_component = new LayoutObject();
		root.path = outpath;


		ChildInfo info = parse_help(this.root_component, root, psd.layers, "",
				0);
		if (null == info) {
			return;
		}
		System.out.println("psd:"+"	" + this.psd.getWidth()+"	"+this.psd.getHeight());
		
		root_component.name = fn;
		root_component.forder = "";
		root_component.width = info.max_x - info.min_x;
		root_component.height = info.max_y - info.min_y;
		if(root_component.width > psd.getWidth())
		{
			root_component.width = psd.getWidth();
		}
		if(root_component.height > psd.getHeight())
		{
			root_component.height = psd.getHeight();
		}
		root_component.x = 0;
		root_component.y = 0;

		FairyShared.Log(String.format("the rect is : %d %d %d %d", info.min_x,
				info.min_y, (info.max_x - info.min_x),
				(info.max_y - info.min_y)));
		Map<String, Map<String, ArrayList<FairyImage>>> mf = new HashMap<String, Map<String, ArrayList<FairyImage>>>();
		this.image_check = mf;
		
		for (String filename : image_name_map.keySet()) {
			ArrayList<FairyImage> al = image_name_map.get(filename);
			if (al.size() > 1)
			{
				{
					Map<String, ArrayList<FairyImage>> ma = mf.get(filename);
					if(ma == null)
					{
						ma = new HashMap<String, ArrayList<FairyImage>>();
						mf.put(filename, ma);
					}
					
					for(FairyImage fi : al)
					{
						String new_name = fi.name + "_" + fi.dimg.width + "x" + fi.dimg.height;
						ArrayList<FairyImage> al2 = ma.get(new_name);
						if(al2 == null)
						{
							al2 = new ArrayList<FairyImage>();
							ma.put(new_name, al2);
						}
						al2.add(fi);
					}
				}
		
				for(String key1 : mf.keySet())
				{
					boolean is_error = false;
					Map<String, ArrayList<FairyImage>> ma = mf.get(key1);
					String s = "Warn: same image name resource " + key1 + "\n";
					if(ma.size() == 1)
					{
						
						// only one size
						String[] key2s = new String[ma.size()];
						ma.keySet().toArray(key2s);
						ArrayList<FairyImage> aaf = ma.get(key2s[0]);
						FairyImage select_f = aaf.get(0);
						if(!check_all_same(aaf))
						{
							for(int tt = 0; tt < aaf.size(); ++tt)
							{
								FairyImage rr = aaf.get(tt);
								if(rr != select_f)
								{
									rr.is_resource = false;
									rr.dimg.src = select_f.id;
								}
								s += String.format("	%d  %d	%s (same)\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
							}
							is_error = true;
						}
						else
						{
							for(int tt = 0; tt < aaf.size(); ++tt)
							{
								FairyImage rr = aaf.get(tt);
								if(rr != select_f)
								{
									rr.is_resource = false;
									rr.dimg.src = select_f.id;
								}
								s += String.format("	%d  %d	%s\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
							}
							is_error = true;
						}
						
						for(int tt = 0; tt < aaf.size(); ++tt)
						{
							FairyImage rr = aaf.get(tt);
							String image_old_name = rr.get_file_size_name();
							FairyPackage.ImageInfo image_info = fpack.image_infos.get(image_old_name);
							if(image_info != null)
							{
								rr.is_resource = false;
								rr.dimg.src = image_info.id;
							}
							else
							{
								image_info = fpack.image_infos.get( rr.name + rr.get_file_format());
								if(image_info != null)
								{
									rr.name = image_old_name;
								}
							}
						}
					}
					else
					{
						is_error = true;
						for(String key2 : ma.keySet())
						{
							ArrayList<FairyImage> aaf = ma.get(key2);
							FairyImage select_f = aaf.get(0);
							if(!check_all_same(aaf))
							{
								for(int tt = 0; tt < aaf.size(); ++tt)
								{
									FairyImage rr = aaf.get(tt);
									FairyPackage.ImageInfo image_info = fpack.image_infos.get(key2);
									if(image_info != null)
									{
										rr.is_resource = false;
										rr.dimg.src = image_info.id;
									}
									else
									{
										if(tt > 0)
										{
											rr.is_resource = false;
											rr.dimg.src = select_f.id;
										}
									}
									rr.name = key2;
									s += String.format("	%d  %d	%s\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
								}
							}
							else
							{
								for(int tt = 0; tt < aaf.size(); ++tt)
								{
									FairyImage rr = aaf.get(tt);
									FairyPackage.ImageInfo image_info = fpack.image_infos.get(key2);
									if(image_info != null)
									{
										rr.is_resource = false;
										rr.dimg.src = image_info.id;
									}
									else
									{
										if(tt > 0)
										{
											rr.is_resource = false;
											rr.dimg.src = select_f.id;
										}
									}
									rr.name = key2;
									s += String.format("	%d  %d	%s (same)\n", rr.dimg.width, rr.dimg.height, rr.parent_name);
								}
							}
						}
						
					}
					if(is_error)
					{
//						FairyShared.Error(s);
					}
				}
			}
			else
			{
				FairyImage rr = al.get(0);
				String image_old_name = rr.get_file_size_name();
				FairyPackage.ImageInfo image_info = fpack.image_infos.get(image_old_name);
				if(image_info != null)
				{
					rr.is_resource = false;
					rr.dimg.src = image_info.id;
				}
				else
				{
					image_info = fpack.image_infos.get( rr.name + rr.get_file_format());
					if(image_info != null)
					{
						rr.name = image_old_name;
					}
				}
			}
		}
		root_component.toxml();
		String autoid = FairyShared.instance.auto_cur_id(work_dir);
		if (null == autoid)
			return;
		String parent_comp = "component";
		for (LayoutObject o : this.components) {
			if(o.tag != null)
			{
				o.forder = autoid + "/" + o.tag;
			}
			else
			{
				o.forder = autoid + "/" + parent_comp;
			}
//			o.forder = o.
			o.toxml();
		}
		write_public_xml_exist(fpack, outpath, autoid, false);
		
		if (FairyShared.error_cnt > 0) {
			FairyShared.Warn("Command Error first");
			return;
		}

		FairyShared.show_off = true;
		fd.mkdir();
		File imageDir = new File(outpath + File.separator + "image");
		
		if(!imageDir.exists() || !imageDir.isDirectory())
		{
			imageDir.mkdir();
		}
		for (String key : images.keySet()) {
			FairyImage img = images.get(key);
			if(img != null && img.need_out && img.is_resource)
			{
				write_image(imageDir.getAbsolutePath(), img);
			}
		}
		root_component.forder = autoid;
		root_component.command_display_repeat_name();
		write_xml(outpath+ File.separator + root_component.forder, root_component.get_root_file_name(),
				root_component.toxml());
		for (LayoutObject o : this.components) {
			if (o.need_create_resource) {
				o.command_display_repeat_name();
				write_xml(outpath + File.separator + o.forder, o.get_file_name(), o.toxml());
			}
		}
		write_public_xml_exist(fpack, outpath, autoid, true);
	}
	
	public void parse(String work_dir) {
		File f = new File(work_dir);
		if (!f.exists() || f.isFile()) {
			return;
		}
		String fn = psd.name.substring(0, psd.name.length() - 4);
		String outpath = f.getAbsolutePath() + File.separator + fn;
		File fd = new File(outpath);
		FairyShared.instance.parser_other_fairy(work_dir);
		if(fd.exists() && fd.isDirectory())
		{
			parse_exist_folder(work_dir, outpath, fn, fd);
			return;
		}
		else
		{
			parse_no_folder(work_dir, outpath, fn, fd);
			return;
		}
		
		
	}

	public class NameInfo {
		public String name;
		public String pkg;
		public String tag;

		@Override
		public String toString() {
			return String.format("%s  %s  %s", tag, pkg, name);
		}
	}
	
	public NameInfo command_folder_name(String name, Set<String> s) {
		NameInfo info = new NameInfo();
		int start_ = name.lastIndexOf(".");
		if (start_ > 0) {
			String pref = name.substring(start_ + 1);
//			if(s.contains(pref))
			{
				info.tag = pref;
				info.name = name.substring(0, start_);
				return info;
			}
			
		}
		info.name = name;
		return info;
	}
	
	public NameInfo command_image_name(String name, Set<String> s) {
		NameInfo info = new NameInfo();
		int start_ = name.lastIndexOf("@");
		int end_ = name.lastIndexOf(".");
		if (end_ > 0 && end_ > start_) {
			info.tag =  name.substring(end_ + 1);
			name = name.substring(0, end_);
		}
		
		if(start_ > 0)
		{
			info.name = name.substring(0, start_);
			info.pkg = name.substring(start_ + 1);
		}
		else
		{
			info.name = name;
		}
		return info;
	}
	
//	public NameInfo command_name(String name, Set<String> s) {
//		NameInfo info = new NameInfo();
//		int start_ = name.indexOf("_");
//		if (start_ > 0) {
//			String pref = name.substring(0, start_);
//			if (pref.endsWith(")")) {
//				int startKH = pref.lastIndexOf("(");
//				info.pkg = pref.substring(startKH + 1, pref.length() - 1);
//				pref = pref.substring(0, startKH);
//			}
//
//			if (!s.contains(pref)) {
//				info.name = name;
//			} else {
//				info.tag = pref;
//				info.name = name.substring(start_ + 1);
//			}
//		} else {
//			info.name = name;
//		}
//		return info;
//	}
	
	
	private DisplayText command_text(String name, Layer layer)
	{
		DisplayText dt = new DisplayText();
		dt.name = name;
		dt.height = layer.getHeight();
		dt.width = layer.getWidth();
		dt.x = layer.getX();
		dt.y = layer.getY();
		if (layer.lfx2 != null) {
			if (layer.lfx2.FrFX != null) {
				if (layer.lfx2.FrFX.enab) {
					dt.is_stroke = true;
					dt.strokeSize = (int) layer.lfx2.FrFX.Sz;
					dt.strokeColor.r = (int) (layer.lfx2.FrFX.Clr.Rd + 0.5);
					dt.strokeColor.g = (int) (layer.lfx2.FrFX.Clr.Grn + 0.5);
					dt.strokeColor.b = (int) (layer.lfx2.FrFX.Clr.Bl + 0.5);
				}
			}
		}
		PsdText pt = (PsdText) layer.textDescrptor.get("Txt");
		PsdDescriptor pd = (PsdDescriptor)layer.textDescrptor.get("bounds");
		if(pd != null)
		{
			PsdUnitFloat fTop = (PsdUnitFloat)pd.getObjects().get("Top");
			PsdUnitFloat fLeft = (PsdUnitFloat)pd.getObjects().get("Left");
			PsdUnitFloat fRght = (PsdUnitFloat)pd.getObjects().get("Rght");
			PsdUnitFloat fBtom = (PsdUnitFloat)pd.getObjects().get("Btom");
			dt.bounds = dt.new Position(fLeft.getValue(), fTop.getValue(), fRght.getValue() - fLeft.getValue(), fBtom.getValue() - fTop.getValue());
		}
		
		PsdDescriptor pd2 = (PsdDescriptor)layer.textDescrptor.get("boundingBox");
		if(pd2 != null)
		{
			PsdUnitFloat fTop2 = (PsdUnitFloat)pd2.getObjects().get("Top");
			PsdUnitFloat fLeft2 = (PsdUnitFloat)pd2.getObjects().get("Left");
			PsdUnitFloat fRght2 = (PsdUnitFloat)pd2.getObjects().get("Rght");
			PsdUnitFloat fBtom2 = (PsdUnitFloat)pd2.getObjects().get("Btom");
			dt.boundingBox = dt.new Position(fLeft2.getValue(), fTop2.getValue(), fRght2.getValue() - fLeft2.getValue(), fBtom2.getValue() - fTop2.getValue());
		
		}
		dt.text = pt.getValue().trim();
		PsdTextData engineData = (PsdTextData) layer.textDescrptor
				.get("EngineData");
//		FairyShared.Log("        " + layer.name + "   " + pt.getValue());
		if (engineData != null) {
			Map<String, ?> documentResources = (Map<String, ?>) engineData
					.getProperties().get("DocumentResources");
			if (documentResources != null) {
				List<?> fontSet = (List<?>) documentResources
						.get("FontSet");
				if (fontSet != null) {
					for (Object c : fontSet) {
						if (c instanceof Map) {
							Map<String, ?> m = (Map<String, ?>) c;
							if (m.get("FontType") != null) {
								String fontStr = (String) m.get("Name");
								if (fontStr != null && fontStr.length() > 0) {
									dt.fontStr = fontStr;
									Font font = FairyShared.fonts
											.get(fontStr);
									if (font != null) {
										dt.fontStr = font.getFamily();
										dt.font = font;
									}
									break;
								}
							}
						}
					}
				}
			}

			Map<String, Object> engineDict = (Map<String, Object>) engineData
					.getProperties().get("EngineDict");
			if (engineDict != null) {
				Map<String, ?> styleRun = (Map<String, ?>) engineDict
						.get("StyleRun");

				if (styleRun != null) {
					List<?> runArray = (List<?>) styleRun.get("RunArray");
					if (runArray != null && runArray.size() > 0) {
						Map<String, ?> runArray0 = (Map<String, ?>) runArray
								.get(0);
						if (runArray0 != null) {
							Map<String, ?> styleSheet = (Map<String, ?>) runArray0
									.get("StyleSheet");
							if (styleSheet != null) {
								Map<String, ?> styleSheetData = (Map<String, ?>) styleSheet
										.get("StyleSheetData");
								// FauxItalic 斜体
								Object italic = styleSheetData
										.get("FauxItalic");
								if (italic != null && ((Boolean) italic)) {
									dt.italic = true;
								}
								// FontSize 字体大小
								Object fontSize = styleSheetData
										.get("FontSize");
								if (fontSize != null
										&& ((Double) fontSize) > 0) {
									dt.fontSize = ((Double) fontSize)
											.intValue();
								}
								Object auto_leading = styleSheetData.get("AutoLeading");
								if(auto_leading != null && (Boolean)auto_leading)
								{
									dt.auto_leading = true;
								}
								Object leading = styleSheetData.get("Leading");
								if(leading != null)
								{
									dt.leading = ((Double)leading).doubleValue();
								}
								Object horizontalScale = styleSheetData.get("HorizontalScale");
								if(horizontalScale != null)
								{
									dt.horizontalScale = ((Double)horizontalScale).doubleValue();
								}
								// Tracking 间隔 ， 转换的时候 除以86
								Object tracking = styleSheetData
										.get("Tracking");
								if (tracking != null
										&& ((Double) tracking) > 0) {
									dt.letterSpacing = ((Double) tracking)
											.intValue() / 90;
								}
								// FillColor 填充颜色
								Map<String, Object> fillColor = (Map<String, Object>) styleSheetData
										.get("FillColor");
								if (fillColor != null) {
									List<Double> rgb = (List<Double>) fillColor
											.get("Values");
									if (rgb != null) {
										dt.color.r = (int) (rgb.get(1) * 255 + 0.5);
										dt.color.g = (int) (rgb.get(2) * 255 + 0.5);
										dt.color.b = (int) (rgb.get(3) * 255 + 0.5);
									}
								}
								// Underline 下划线
								Object underline = styleSheetData
										.get("Underline");
								if (underline != null
										&& ((Boolean) underline)) {
									dt.underline = true;
								}
								// FauxBold 粗体
								Object fauxBold = styleSheetData
										.get("FauxBold");
								if (fauxBold != null
										&& ((Boolean) fauxBold)) {
									dt.bold = true;
								}
							}
						}

					}

				}
			}
		}
		int dd = (int)(dt.fontSize * 0.21);
		dt.x -= dd;
		dt.y -= dd;
		if(dt.bounds != null)
		{
			dt.width = dt.bounds.w + dd + 3;
			dt.height = dt.bounds.h + dd + 3;
		}
		return dt;
	}
	
	Graphics2D g = FairyShared.g;

	@SuppressWarnings("unchecked")
	public DisplayObject command_image(String name, LayoutObject layout_root,
			int floot, Layer layer) {
		
		NameInfo info = command_image_name(name, image_sets);
		if (layer.textDescrptor != null) {
			DisplayText dt = command_text(info.name, layer);
			if(info.pkg != null)
			{
				dt.pkg = info.pkg;
			}
			if(info.pkg != null)
			{
				if(layout_root.display_list.size() > 0)
				{
					DisplayObject dpo = layout_root.display_list.get(layout_root.display_list.size() - 1);
					if(dpo.name.equals(info.name) && dpo.pkg.equals(info.pkg))
					{
						if(dpo instanceof DisplayImage)
						{
							DisplayImage dig = (DisplayImage)dpo;
							if(dig != null)
							{
								LayoutButton lbnt = new LayoutButton();
								lbnt.title = dt;
								dig.create_ref_comp(lbnt);
							}
						}
						return null;
					}
				}
				
			}
			if(info.tag != null)
			{
				layout_root.command_text_key(dt, info.tag);
			}
			return dt;
		}

		String suf = info.tag;
		name = info.name;
		DisplayImage o = null;
		if (DisplayLoader.TAG.equals(info.tag)) {
			o = new DisplayLoader();
		} else {
			o = new DisplayImage();
		}
		o.suf = suf;
		o.height = layer.getHeight();
		o.width = layer.getWidth();
		o.x = layer.getX();
		o.y = layer.getY();
		o.name = info.name;
		o.pkg = info.pkg;
		
		if(info.pkg != null && info.pkg.length() > 0)
		{
			FairyShared.OtherFairy of = FairyShared.instance.ref_folder(info.pkg);
			FairyShared.OtherResource or = FairyShared.instance.ref_comp(of, info.name);
			if(or != null)
			{
				if(or.path.indexOf("/" + LayoutProgressBar.TAG) == 0)
				{
					LayoutProgressBar lpb = new LayoutProgressBar();
					o.create_ref_comp(lpb);
					
				}
//				else if(or.path.indexOf("/" + LayoutProgressBar.TAG) == 0)
//				{
//					
//				}
				else
				{
					o.create_ref_comp(null);
				}
			}
		}
		
		if (floot == 1) {
			if ("bg".equals(info.tag)) {
				o.need_export = true;
				layout_root.bg = o;
			}
			if (info.tag != null && layout_root.command_image_key(o, info.tag)) {
				if(o.pkg == null)
				{
					o.need_export = true;
				}
//				if (!name.equals("bg")) {
//				o.pkg = info.pkg;
//				}
				return o;
			}
		}
		
		return o;
	}

	public DisplayComponent command_forder(String name, String parent_path, 
			LayoutObject layout_root, int floot, Layer layer) {
		LayoutObject o = null;
		NameInfo info = command_folder_name(name, forder_sets);
//		NameInfo info = command_name(name, forder_sets);

		DisplayComponent dc = new DisplayComponent();
		dc.parent = layout_root;

		if (layout_root != null && floot == 1) {
			if (layout_root.command_forder_key(dc, info.tag)) {
//				dc.o.name = layout_root.name + "_" + name;
				dc.o.name = info.name;
				dc.name = dc.o.name;
				dc.src = dc.o.id;
//				dc.o.pkg = layout_root.pkg;
				dc.pkg = info.pkg;
				dc.o.pkg = info.pkg;
				return dc;
			}
		}

		if (info.tag != null) {
			if (info.tag.equals(LayoutButton.TAG)) {
				o = new LayoutButton();
			} else if (info.tag.equals(LayoutComboBox.TAG)) {
				o = new LayoutComboBox();
			} else if (info.tag.equals(LayoutList.TAG)) {
				LayoutList ll = new LayoutList();
				ll.layout = "flow_hz";
				ll.overflow = "scroll";
				o = ll;

			} else if (info.tag.equals(LayoutProgressBar.TAG)) {
				o = new LayoutProgressBar();
			} else if (info.tag.equals(LayoutScrollBar.TAG)) {
				o = new LayoutScrollBar();
			} else if (info.tag.equals(LayoutSlider.TAG)) {
				o = new LayoutSlider();
			}
			else
			{
				FairyShared.Error("ERROR have not tag " + info.tag +" in " + parent_path + name);
				o = new LayoutObject();
			}
		}else
		{
			o = new LayoutObject();
		}
		o.tag = info.tag;
		if (o != null) {
			o.name = info.name;
			o.pkg = info.pkg;
//			if (info.tag != null) {
//				o.name = info.tag + "_" + o.name;
//			}
			dc.o = o;
			dc.name = o.name;
			dc.src = o.id;
			dc.o = o;
			return dc;
		}
		return null;
	}
	private Map<String, ArrayList<DisplayComponent>> forder_names = new HashMap<String, ArrayList<DisplayComponent>>();
	private ChildInfo parse_help(LayoutObject layout_root,
			FairyForder parentfd, List<Layer> layers, String parentName,
			int floot) {
		ChildInfo child_info = null;
		for (int i = 0; i < layers.size(); ++i) {
			Layer layer = layers.get(i);
			String filename = layer.name;
			filename = filename.replace('/', '_');
			filename = filename.replace('\\', '_');
			filename = filename.replace(':', '_');
			filename = filename.replace('*', '_');
			filename = filename.replace('?', '_');
			filename = filename.replace('<', '_');
			filename = filename.replace('>', '_');
			filename = filename.replace('|', '_');
			filename = filename.trim();


			if (!layer.visible) {
				continue;
			}
//			System.out.println(layer.name + " " + layer.opacity+ " " + layer.iOpa.fillOpacity + "	" + layer.iOpa.used + "	" + layer.getWidth() + "," + layer.getHeight());
			if (layer.clipping) {
				FairyShared.Error(parentName + filename + " is clipping!!");
				continue;
			}
			// NameInfo name_info = command_name(filename, layout_root,
			// parentfd, floot + 1, layer);
			if (layer.type == LayerType.FOLDER) {
				
				DisplayComponent dc = command_forder(filename, parentName, layout_root,
						floot + 1, layer);
//				if(filename.equals("btn_jianzhu_fenjianzhu"))
//				{
//					System.out.println(filename);
//				}
				if(dc != null && dc.o != null && dc.pkg.length() == 0)
				{
					ArrayList<DisplayComponent> alayer = forder_names.get(dc.name);
					if(alayer == null)
					{
						alayer = new ArrayList<DisplayComponent>();
						forder_names.put(dc.name, alayer);
					}
					if(alayer.size() > 0)
					{
						dc.o.name += "_" + alayer.size();
					}
					alayer.add(dc);
				}

				
				FairyForder forder = new FairyForder(filename, parentfd);
				forders.put(forder.id, forder);
//				forlders_str += "    " + forder.toxml() + "\n";
				// forder
				ChildInfo info = null;
				if (dc == null) {
					info = parse_help(layout_root, forder, layer.layers,
							parentName + filename + "/", floot + 1);
				} else {
					dc.o.parent_path = parentName;
					if (dc.add_to_displaylist) {
						layout_root.display_list.add(dc);
					}
					info = parse_help(dc.o, forder, layer.layers, parentName
							+ filename + "/", 0);
					if (dc.o.bg == null) {
						if (info != null) {
							dc.x = info.min_x;
							dc.y = info.min_y;
							dc.width = info.max_x - info.min_x;
							dc.height = info.max_y - info.min_y;
						}
					} else {
						dc.x = dc.o.bg.x;
						dc.y = dc.o.bg.y;
						dc.width = dc.o.bg.width;
						dc.height = dc.o.bg.height;
					}
					dc.o.x = dc.x;
					dc.o.y = dc.y;
					dc.o.width = dc.width;
					dc.o.height = dc.height;
					this.components.add(0, dc.o);
				}

				if (null == info) {

				} else if (dc != null && dc.neglect_rect) {

				} else if (child_info == null) {
					child_info = info;
				} else {
					if (child_info.min_x > info.min_x) {
						child_info.min_x = info.min_x;
					}
					if (child_info.min_y > info.min_y) {
						child_info.min_y = info.min_y;
					}
					if (child_info.max_x < info.max_x) {
						child_info.max_x = info.max_x;
					}
					if (child_info.max_y < info.max_y) {
						child_info.max_y = info.max_y;
					}
				}
			} else {
				if (layer.image == null && layer.textDescrptor == null)
					continue;
				if(layer.getWidth() == 0 && layer.getHeight() == 0) continue;
				DisplayObject dimg = command_image(filename, layout_root,
						floot + 1, layer);
				if(dimg == null) continue;
				dimg.parent = layout_root;
				dimg.parent_path = parentName;
				
//				if(dimg.name.equals("banghui_icon_bg"))
//				{
//					System.out.println(dimg.name);
//				}
				if (dimg instanceof DisplayImage) {
					((DisplayImage) dimg).layer = layer;
					if (((DisplayImage) dimg).is_resource) {
						if (dimg.pkg != null && dimg.pkg.length() > 0) {
							
						} else {
							FairyImage img = new FairyImage(dimg.name, parentfd);
							// image
							ArrayList<FairyImage> image_names = image_name_map
									.get(dimg.name);
							if (image_names == null) {
								image_names = new ArrayList<FairyImage>();
								image_name_map.put(dimg.name, image_names);
							}
							img.parent_name = parentName;
							image_names.add(img);
							
							images.put(img.id, img);
							img.dimg = (DisplayImage) dimg;
							img.image = layer.image;
							img.maskImage = layer.maskImage;
							img.need_export = ((DisplayImage) dimg).need_export;
//							images_str += "    " + img.toxml() + "\n";
							((DisplayImage) dimg).src = img.id;
							if (layer.layers.size() > 0) {
								FairyShared.Error(parentName + filename
										+ ".png have " + layer.layers.size()
										+ " children !!");
							}
						}

						if (layer.textDescrptor != null && layer.image == null) {
							continue;
						} else {
							layout_root.display_list.add(dimg);
						}
					}
				} else if (dimg instanceof DisplayText) {
					layout_root.display_list.add(dimg);
				}

				if (dimg.neglect_rect) {

				} else if (child_info == null) {
					child_info = new ChildInfo();
					child_info.min_x = dimg.x;
					child_info.min_y = dimg.y;
					child_info.max_x = dimg.x + dimg.width;
					child_info.max_y = dimg.y + dimg.height;
				} else {
					if (child_info.min_x > dimg.x) {
						child_info.min_x = dimg.x;
					}
					if (child_info.min_y > dimg.y) {
						child_info.min_y = dimg.y;
					}
					if (child_info.max_x < (dimg.x + dimg.width)) {
						child_info.max_x = (dimg.x + dimg.width);
					}
					if (child_info.max_y < (dimg.y + dimg.height)) {
						child_info.max_y = (dimg.y + dimg.height);
					}
				}
				
			}
		}
		return child_info;
	}

}
