package psd.parser.layer.additional;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerlfx2 implements LayerAdditionalInformationParser{
	public static final String TAG = "lfx2";
	public int object_version;
	public int version;
	public Descriptor des;
	
	public class Value
	{
		public String key;
		public String type;
	}

	
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		object_version = stream.readInt();
		version = stream.readInt();
		assert(object_version == 0);
		assert(version == 16);
		des = new Descriptor();
//		des.isPrint = true;
		if(des.isPrint)
		{
			System.out.print(TAG+":"+size+"[");
		}
		des.parse(stream, tag, size - 4);
		if(des.isPrint)
		{
			System.out.print("]");
		}
		parse_GrFl();
		parse_FrFX();
	}
	public class RGBC_type
	{
		public double Rd;
		public double Grn;
		public double Bl;
	}
	
	public class Clrt_type
	{
		public RGBC_type Clr;
		public enum_type Type;
		public long Lctn;
		public long Mdpn;
	}
	
	public class TrnS_type
	{
		public double Opct;
		public long Lctn;
		public long Mdpn;
	}
	
	public class enum_type
	{
		public String type;
		public String value;
	}
	

	public class Pnt_type
	{
		public double Hrzn;
		public double Vrtc;
	}
	
	public class Grad_type
	{
		public String Nm;
		public enum_type GrdF;
		public double Intr;
		public Clrt_type[] Clrs;
		public TrnS_type[] Trns;
	}

	
	public class GrFl_type
	{
		public boolean enab;
		public enum_type Md;
		public double Opct;
		public Grad_type Grad;
		public double Angl;
		public enum_type Type;
		public boolean Rvrs;
		public boolean Dthr;
		public boolean Algn;
		public double Scl;
		public Pnt_type Ofst;
		public boolean present;
		public boolean showInDialog;
	}
	
	public GrFl_type GrFl;
	
	private Object get_ooxx_help(Object b)
	{
		if(b instanceof Descriptor.double_type)
		{
			return ((Descriptor.double_type)b).value;
		}
		else if(b instanceof Descriptor.long_type)
		{
			return ((Descriptor.long_type)b).value;
		}
		else if(b instanceof Descriptor.bool_type)
		{
			return ((Descriptor.bool_type)b).value;
		}
		else if(b instanceof Descriptor.comp_type)
		{
			return ((Descriptor.comp_type)b).value;
		}
		else if(b instanceof Descriptor.enum_type)
		{
			enum_type e = new enum_type();
			e.type = ((Descriptor.enum_type)b).enum_type;
			e.value = ((Descriptor.enum_type)b).enum_value;
			return e;
		}
		else if(b instanceof Descriptor.UntF_type)
		{
			return ((Descriptor.UntF_type)b).value;
		}
		else if(b instanceof Descriptor.TEXT_type)
		{
			return ((Descriptor.TEXT_type)b).value;
		}
		return null;
	}
	
	
	private void ooxx(Object o, HashMap<String, Object> m, int height) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException
	{

		for(String k : m.keySet())
		{
//			for(int i =0; i < height; ++i)
//			{
//				System.out.print("	");
//			}
//			System.out.println("try to get feild " + o + "  " + k);
			Object b = m.get(k);
			Field f = o.getClass().getField(k);
			
			Object value = get_ooxx_help(b);
			if(value != null)
			{
				f.set(o, value);
			}
			else
			{
				if(b instanceof Descriptor.Objc_type)
				{
					Object oo = f.getType().getDeclaredConstructors()[0].newInstance(this);
					f.set(o, oo);
					ooxx(oo, ((Descriptor.Objc_type)b).map, height + 1);
				}
				else if(b instanceof Descriptor.VlLs_type)
				{
					int item_cnt = ((Descriptor.VlLs_type)b).item_cnt;
					Object items = Array.newInstance(f.getType().getComponentType(), item_cnt);
					f.set(o, items);
//					System.out.println( " +++++++++++++++++++++++++++ Array:" + f.getType().getComponentType() + "   " + items.getClass().isArray());
					
					for(int i = 0; i < item_cnt; ++i)
					{
						Object c = ((Descriptor.VlLs_type)b).items.get(i);
						Object v = get_ooxx_help(c);
						if(v != null)
						{
							Array.set(items, i, v);
						}
						else
						{
							if(c instanceof Descriptor.Objc_type)
							{
								Object item = f.getType().getComponentType().getConstructors()[0].newInstance(this);
								ooxx(item, ((Descriptor.Objc_type)c).map, height + 1);
								Array.set(items, i, item);
								
							}
						}
						
					}
				}
			}
		}
		
	}
	
	
	private void parse_GrFl()
	{
		if(!this.des.map.containsKey("GrFl"))
		{
			return;
		}
		
		Descriptor.Objc_type grfls = (Descriptor.Objc_type)this.des.map.get("GrFl");
		if(grfls == null)
		{
			return;
		}
		this.GrFl = new GrFl_type();
		HashMap<String, Object> m = grfls.map;
		try {
			ooxx(GrFl, m, 0);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
//		System.out.println("--------------------");
	}
	
	public class FrFX_type
	{
		public boolean enab;
		public enum_type Styl;
		public enum_type PntT;
		public enum_type Md;
		public double Opct;
		public double Sz;
		public RGBC_type Clr;
		public Grad_type Grad;
		public double Angl;
		public enum_type Type;
		public boolean Rvrs;
		public boolean Dthr;
		public double Scl;
		public boolean Algn;
		public Pnt_type Ofst;
		public boolean present;
		public boolean overprint;
		public boolean showInDialog;
	}
	
	public FrFX_type FrFX;
	
	private void parse_FrFX()
	{
		if(!this.des.map.containsKey("FrFX"))
		{
			return;
		}
		
		Descriptor.Objc_type frfx = (Descriptor.Objc_type)this.des.map.get("FrFX");
		if(frfx == null)
		{
			return;
		}
		this.FrFX = new FrFX_type();
		HashMap<String, Object> m = frfx.map;
		try {
			ooxx(FrFX, m, 0);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
//		System.out.println("--------------------");
	}
	
	
}
