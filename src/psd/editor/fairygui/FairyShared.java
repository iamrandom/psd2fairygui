package psd.editor.fairygui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import psd.editor.fairygui.FairyPackage.ImageInfo;

public class FairyShared {

	public static FairyShared instance = new FairyShared();
	public static boolean show_off = false;
	
	public class OtherResource {
		public String id;
		public String filename;
		public String path;
		public ArrayList<OtherResource> others = new ArrayList<OtherResource>();
		public int width;
		public int height;
		
		@Override
		public String toString() {
			return filename + "   " + id;
		}
	}

	public class OtherFairy {
		public String dir;
		public String id;
		public Map<String, OtherResource> filename_ids = new HashMap<String, OtherResource>();
		public Map<String, OtherResource> comp_ids = new HashMap<String, OtherResource>();
		public FairyPackage pkg;
	}


	public String cur_spd_id;
	public Map<String, OtherFairy> forder_ids = new HashMap<String, OtherFairy>();

	public Set<String> get_forder_ids() {
		HashSet<String> idset = new HashSet<String>();

		for (OtherFairy fo : forder_ids.values()) {
			idset.add(fo.id);
		}
		return idset;
	}
	
	public OtherFairy ref_folder(String pkg)
	{
		if(pkg == null) return null;
		OtherFairy of = FairyShared.instance.forder_ids.get(pkg);
		if(of == null)
		{
			Error("Error have no resource " + pkg);
			PrintCallStatck();
		}
	
		return of;
	}
	
	public OtherResource ref_comp(FairyShared.OtherFairy ot, String name)
	{
		if(ot == null) return null;
		FairyShared.OtherResource or = ot.comp_ids.get(name + ".xml");
		return or;
	}
	
	public OtherResource ref_image(FairyShared.OtherFairy ot, String name)
	{
		if(ot == null) return null;
		FairyShared.OtherResource or = ot.filename_ids.get(name);
		if(or != null)
		{
			for(String suf : Suffix.FileSuffixs)
			{
				if(or.filename.endsWith("." + suf))
				{
					return or;
				}
			}
		}
		else
		{
			for(String suf : Suffix.FileSuffixs)
			{
				or = ot.filename_ids.get(name + "." + suf);
				if(or != null)
				{
					return or;
				}
			}
		}
		return null;
	}

	public String auto_cur_id(String path) {
		File file = new File(path);

		if (!file.exists()) {
			return null;
		}
		Set<String> idset = get_forder_ids();

		Random rr = new Random(System.currentTimeMillis());
		String R = "0123456789abcdefghijklmnopqrstuvwxyz";
		String autoID = "";
		do
		{
			autoID = "";
			for (int i = 0; i < 7; ++i) {
				int n = rr.nextInt(R.length());
				autoID += R.charAt(n);
			}
			autoID += "_";
		}while(idset.contains(autoID));
		
		cur_spd_id = autoID;
		return autoID;
	}

	public String find_forder_id(String forder, String err) {
		if (forder == null || forder.length() == 0) {
			return this.cur_spd_id;
		}
		OtherFairy fo = forder_ids.get(forder);
		if (fo == null){
			Error("Error have no Forder " + forder + "\n	" + err);
			return null;
		}
		return fo.id;
	}

	public String find_resource_id(String forder, String filename, String err) {
		OtherFairy fo = forder_ids.get(forder);
		if (fo == null) {
			Error("Error have no Forder " + forder + "\n	" + err);
//			PrintCallStatck();
			return null;
		}
		OtherResource o = fo.filename_ids.get(filename);
		if (o == null) {
			Error("Error have no resource " + forder + File.separator
					+ filename + "\n	" + err);
			ref_resource(filename);
			PrintCallStatck();
			return null;
		}
		return o.id;
	}

	public void parser_other_fairy(String work_dir) {
		File file = new File(work_dir);
		if (!file.exists() || !file.isDirectory()) {
			return;
		}
		BufferedReader reader = null;
		for (File f : file.listFiles()) {

			if (!f.isDirectory())
				continue;
			String childname = f.getAbsolutePath() + File.separator
					+ "package.xml";
			File cf = new File(childname);
			if (!cf.exists()) {
				continue;
			}
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(cf),"UTF8"));
//				reader = new BufferedReader(new FileReader(cf));
				OtherFairy other = new OtherFairy();
				other.dir = f.getName();
				other.pkg = new FairyPackage();
				other.pkg.parse_package(work_dir, f.getName());
				other.id = other.pkg.id;
				Map<String, ImageInfo> mm = other.pkg.image_id_infos;
				for(String key : mm.keySet())
				{
					ImageInfo imgInfo = mm.get(key);
					OtherResource o = new OtherResource();
					o.id = imgInfo.id;
					o.filename = imgInfo.xml_name;
					o.path = imgInfo.path;
					OtherResource oldO = other.filename_ids.get(o.filename);
					if(oldO != null)
					{
						oldO.others.add(o);
					}
					else
					{
						other.filename_ids.put(o.filename, o);
					}
				}
				mm = other.pkg.comp_id_infos;
				for(String key : mm.keySet())
				{
					ImageInfo imgInfo = mm.get(key);
					OtherResource o = new OtherResource();
					o.id = imgInfo.id;
					o.filename = imgInfo.xml_name;
					o.path = imgInfo.path;
					OtherResource oldO = other.comp_ids.get(o.filename);
					if(oldO != null)
					{
						oldO.others.add(o);
					}
					else
					{
						other.comp_ids.put(o.filename, o);
					}
				}
		
				if (other.id != null) {
					forder_ids.put(f.getName(), other);
				}
				String error_str_image = f.getName() + " have same image name: \n	";
				int error_str_image_cnt = 0;
				String error_str_component = f.getName() + " have same component name: \n	";
				int error_str_component_cnt = 0;
				for(String cck : other.comp_ids.keySet())
				{
					OtherResource o = other.comp_ids.get(cck);
					if(o.others.size() > 0)
					{
						error_str_component_cnt ++;
						error_str_component += o.id + " " + o.path + o.filename + "\n	";
						for(int ii = 0; ii < o.others.size(); ++ii)
						{
							o = o.others.get(ii);
							error_str_component += o.id + " " + o.path + o.filename + "\n	";
						}
					}
					if( o.path == null || "".equals( o.path.trim()))
					{
						o.path = "/";
					}
					String xml_name = file.getAbsolutePath() + "/" + other.dir + o.path + o.filename;
					int[] wh = read_comp_xml_size(xml_name);
					if(wh != null)
					{
						o.width = wh[0];
						o.height = wh[1];
					}
				}
				for(String cck : other.filename_ids.keySet())
				{
					OtherResource o = other.filename_ids.get(cck);
					if(o.others.size() > 0)
					{
						error_str_image_cnt ++;
						error_str_image += o.id + " " + o.path + o.filename + "\n	";
						for(int ii = 0; ii < o.others.size(); ++ii)
						{
							o = o.others.get(ii);
							error_str_image += o.id + " " + o.path + o.filename + "\n	";
						}
					}
				}
				if(error_str_component_cnt > 0)
				{
//					Error(error_str_component);
				}
				if(error_str_image_cnt > 0)
				{
//					Error(error_str_image);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public int[] read_comp_xml_size(String filename)
	{
		File f = new File(filename);
		if (!f.exists()) {
			Warn("Error not exist file : " + filename);
			return null;
		}
		Pattern rcomp = Pattern
				.compile(".*<\\s*component\\.*?\\s+size=\\s*?\"(\\S+)\"");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			reader.readLine();
			String line = reader.readLine();
			Matcher m = rcomp.matcher(line);
			if(m.find())
			{
				String s = m.group(1);
				String[] size_str = s.split(",");
				int[] ss = new int[2];
				ss[0] = Integer.valueOf(size_str[0]);
				ss[1] = Integer.valueOf(size_str[1]);
				return ss;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("error --------- " + filename);
		return null;
		
		
	}

	public void print() {
		for (Map.Entry<String, OtherFairy> k : forder_ids.entrySet()) {
			OtherFairy of = k.getValue();
			System.out.println(k.getKey());
			for (OtherResource o : of.filename_ids.values()) {
				System.out.println("  " + o.id + "       " + o.filename);
			}
		}
	}

	public static Map<String, Font> fonts;
	public static Map<String, Font> fonts2;
	public static Graphics2D g;
	static {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		fonts = new HashMap<String, Font>();
		BufferedImage im = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		g = ge.createGraphics(im);
		for (Font f : ge.getAllFonts()) {
//			FontMetrics fm = g.getFontMetrics(f);
//			LineMetrics lm = f.getLineMetrics("abcd", fm.getFontRenderContext());
//			String s = String.format("%s %f %f %f %d", f.getFamily(), lm.getAscent(), lm.getDescent(), lm.getHeight(), f.getBaselineFor('a'));
//			Log(s);
			fonts.put(f.getPSName(), f);
		}
	}

	public static int error_cnt = 0;
	public static int warn_cnt = 0;

	public static void Log(String s) {
		if(show_off) return;
		System.out.println(s);
		System.out.flush();
	}

	public static void Warn(String s) {
		if(show_off) return;
		warn_cnt++;
		System.out.println(s);
		System.out.flush();
	}

	public static void Error(String s) {
		if(show_off) return;
		error_cnt++;
		System.err.println(s);
		System.err.flush();
	}

	public static void PrintCallStatck() {
//		Throwable ex = new Throwable();
//		ex.printStackTrace();
	}
	
	public static void ref_resource(String filename)
	{
		if(show_off) return;
		
		int index = filename.lastIndexOf(".");
		String xml_filename = filename;
		if (index > 0)
		{
			xml_filename = filename.substring(0, index);
		}
		for(String key : FairyShared.instance.forder_ids.keySet())
		{
			OtherFairy o = FairyShared.instance.forder_ids.get(key);
			OtherResource or = o.comp_ids.get(xml_filename + ".xml");
			if(or == null)
			{
				or = o.filename_ids.get(filename);
			}
			if (null != or)
			{
				String s = or.path;
				if(s.length() == 0)
				{
					s = "/";
				}
				System.err.println("	[" + o.dir + s + or.filename + "]");
			}
			
			
			
		}
	
	}

}
