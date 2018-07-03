package psd.parser.layer.additional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import psd.parser.PsdInputStream;

public class Descriptor {
	
	public boolean isPrint= false;
	
	public class Objc_type
	{
		public String classID;
		public String className;
		public String type;
		public HashMap<String, Object> map;
	}
	
	public class double_type
	{
		public String type;
		public double value;
	}
	
	public class long_type
	{
		public String type;
		public int value;
	}
	public class bool_type
	{
		public String type;
		public boolean value;
	}
	
	public class comp_type
	{
		public String type;
		public long value;
	}
	
	public class enum_type
	{
		public String type;
		public String enum_type;
		public String enum_value;
	}
	
	public class UntF_type
	{
		public String type;
		public String key;
		public double value;
	}
	
	public class TEXT_type
	{
		public String type;
		public String value;
	}
	
	public class VlLs_type
	{
		public String type;
		public int item_cnt;
		public ArrayList<Object> items;
	}
	
	public String print_src = "";
	private Object get_paser_node(PsdInputStream stream,  String ktype)throws IOException {
		Object result = null;
		print_src += ktype + "(";
		if(ktype.equals("Objc"))
		{
			Objc_type objc = new Objc_type();
			objc.type = ktype;
			
			objc.className = stream.readUnicodeString();
			objc.classID = stream.readPsdString();
			print_src += objc.className +" "+ objc.classID;
			objc.map = new HashMap<String, Object>();
			int nn = stream.readInt();
			print_src += " "+nn+"{";
			for(int i = 0; i < nn; ++i)
			{
				if(paser_objc(objc.map, stream) == null)
				{
					return null;
				}
			}
			print_src += "}";
			result = objc;
		}
		else if(ktype.equals("VlLs"))
		{
			VlLs_type t = new VlLs_type();
			t.type = ktype;
			t.item_cnt = stream.readInt();
			print_src+= " "+t.item_cnt+"[";
			t.items = new ArrayList<Object>();
			for(int i = 0; i < t.item_cnt; ++i)
			{
				String newkType = stream.readString(4);
				newkType = newkType.trim();
				Object n = get_paser_node(stream, newkType);
				if(n == null)
				{
					return null;
				}
				t.items.add(n);
			}
			print_src+= "]";
			result = t;
		}
		else if(ktype.equals("doub"))
		{
			double_type t = new double_type();
			t.type = ktype;
			t.value = stream.readDouble();
			print_src += t.value;
			result = t;
		}
		else if(ktype.equals("UntF"))
		{
			UntF_type t = new UntF_type();
			t.type = ktype;
			t.key = stream.readString(4);
			t.value = stream.readDouble();
			print_src += " "+t.key +":"+t.value;
			result = t;
		}
		else if(ktype.equals("enum"))
		{
			enum_type t = new enum_type();
			t.type = ktype;
			t.enum_type = stream.readPsdString();
			t.enum_value = stream.readPsdString();
			print_src += " "+t.enum_type +":"+t.enum_value;
			result = t;
		}
		else if(ktype.equals("long"))
		{
			long_type t = new long_type();
			t.type = ktype;
			t.value = stream.readInt();
			print_src += t.value;
			result = t;
		}
		else if(ktype.equals("comp"))
		{
			comp_type t = new comp_type();
			t.type = ktype;
			t.value = stream.readLong();
			print_src += t.value;
			result = t;
		}
		else if(ktype.equals("bool"))
		{
			bool_type t = new bool_type();
			t.type = ktype;
			t.value = stream.readBoolean();
			print_src += t.value;
			result = t;
		}
		else if(ktype.equals("TEXT"))
		{
			TEXT_type t = new TEXT_type();
			t.type = ktype;
			t.value = stream.readUnicodeString();
			print_src += t.value;
			result = t;
			
		}
		else if(ktype.equals("GlbO"))
		{
			
		}
		else if(ktype.equals("type"))
		{
			
		}
		else if(ktype.equals("GlbC"))
		{
			
		}
		else if(ktype.equals("alis"))
		{
			
		}
		else if(ktype.equals("tdta"))
		{
			
		}
		else
		{
			throw new IOException("xxxxxxxxxx");
		}
		print_src += ")";
//		if(isPrint)
//		{
//			System.out.print(s + ", ");
//		}
		return result;
	}
	
	private Object paser_objc(HashMap<String, Object> m,PsdInputStream stream)throws IOException 
	{
		String key = stream.readPsdString();
		key = key.trim();
		String ktype = stream.readString(4);
		print_src +="    " + key + ":";
		Object n = this.get_paser_node(stream, ktype);
		if(n != null)
		{
			m.put(key, n);
		}
		return n;
	}
	
	public String className;
	public String classID;
	public int number;
	public HashMap<String, Object> map;
	
	public void parse(PsdInputStream stream, String tag, int size)throws IOException {
		
		className = stream.readUnicodeString();
		classID = stream.readPsdString();
		number = stream.readInt();
		
		map = new HashMap<String, Object> ();
		print_src = className + ","+classID;
		for(int i=0; i<number;++i)
		{
			if(paser_objc(map, stream) == null)
			{
				break;
			}
			print_src += "\n";
		}
		if(isPrint)
		{
			System.out.print(print_src);
		}
	}
}
