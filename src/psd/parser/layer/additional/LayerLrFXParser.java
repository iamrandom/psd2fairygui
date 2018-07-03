package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerLrFXParser implements LayerAdditionalInformationParser {

	public static final String TAG = "lrFX";
	
	
	public class cmnS_type
	{
		public int size;
		public int version;
		public boolean visible;
		public short unused;
	}
	
	public class dw_type
	{
		public int size;
		public int version;
		public int blur;
		public int blur2;
		public int intensity;
		public int intensity2;
		public int angle;
		public int angle2;
		public int distance;
		public int distance2;
		
		public ColorSpace colorSpace;
		
		public String blendMode;
		public int effectEnable;
		public int useAngleInAllLayer;
		public int opacity;
		public ColorSpace nativeColorSpace;
	}
	
	public class oglw_type
	{
		public int size;
		public int version;
		public int blur;
		public int blur2;
		public int intensity;
		public int intensity2;
		public ColorSpace colorSpace;
		
		public String blendMode;
		public boolean effectEnable;
		public int opacity;
		public ColorSpace nativeColorSpace;
	}
	
	public class iglw_type
	{
		public int size;
		public int version;
		public int blur;
		public int intensity;
		
		public ColorSpace colorSpace;
		
		public String blendMode;
		public boolean enable;
		public int opacity;
		public boolean invert;
		public ColorSpace nativeColorSpace;
	}
	

	public class bevl_type
	{
		public int size;
		public int version;
		public int angle;
		public int strength;
		public int blur;
		public String hs;
		public String hk;
		public String ss;
		public String sk;
		public ColorSpace hc;
		public ColorSpace sc;
		public int bevelStyle;
		public int hOpacity;
		public int sOpacity;
		public boolean effectEnable;
		public boolean useAngleInAll;
		public int upordown;
		public ColorSpace rhc;
		public ColorSpace rsc;
	}
	
	public class sofi_type
	{
		public int size;
		public int version;
		public String key;
		public String key2;
		
		public ColorSpace colorSpace;
		
		public int opacity;
		public int enable;
		public ColorSpace nativeColorSpace;
		
	}
	
	public cmnS_type cmns;
	public dw_type dsdw;
	public dw_type isdw;
	public oglw_type oglw;
	public iglw_type iglw;
	public bevl_type bevl;
	public sofi_type sofi;
	

	public LayerLrFXParser() {
	}

	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		stream.readShort();
		int effects_count = stream.readShort();
		for (int i = 0; i < effects_count; ++i) {
			String dataTag = stream.readString(4);
			if (!dataTag.equals("8BIM")) {
				throw new IOException(
						"LayerLrFXParser data section signature error");
			}
			String effectSign = stream.readString(4);
			if (effectSign.equals("cmnS")) {
				cmns = new cmnS_type();
				cmns.size = stream.readInt();
				cmns.version = stream.readInt();
				cmns.visible = stream.readBoolean();
				cmns.unused = stream.readShort();
			}else if(effectSign.equals("dsdw"))
			{
				dsdw = new dw_type();
				dsdw.size = stream.readInt();
				dsdw.version = stream.readInt();
				
				dsdw.blur = stream.readShort();
				dsdw.blur2 = stream.readShort();
				dsdw.intensity = stream.readShort();
				dsdw.intensity2 = stream.readShort();
				dsdw.angle = stream.readShort();
				dsdw.angle2 = stream.readShort();
				dsdw.distance = stream.readShort();
				dsdw.distance2 = stream.readShort();
				dsdw.colorSpace = new ColorSpace();
				dsdw.colorSpace.read(stream);
				
				dsdw.blendMode = stream.readString(8);
				dsdw.effectEnable = stream.read();
				dsdw.useAngleInAllLayer = stream.read();
				dsdw.opacity = stream.read();
				dsdw.nativeColorSpace = new ColorSpace();
				dsdw.nativeColorSpace.read(stream);
			}else if(effectSign.equals("isdw"))
			{
				isdw = new dw_type();
				isdw.size = stream.readInt();
				isdw.version = stream.readInt();
				
				isdw.blur = stream.readShort();
				stream.skip(2);
				isdw.intensity = stream.readShort();
				stream.skip(2);
				isdw.angle = stream.readShort();
				stream.skip(2);
				isdw.distance = stream.readShort();
				stream.skip(2);
				
				isdw.colorSpace = new ColorSpace();
				isdw.colorSpace.read(stream);
				
				isdw.blendMode = stream.readString(8);
				isdw.effectEnable = stream.read();
				isdw.useAngleInAllLayer = stream.read();
				isdw.opacity = stream.read();
				isdw.nativeColorSpace =  new ColorSpace();
				isdw.nativeColorSpace.read(stream);
			}else if(effectSign.equals("oglw"))
			{
				oglw = new oglw_type();
				oglw.size = stream.readInt();
				oglw.version = stream.readInt();
				oglw.blur = stream.readShort();
				oglw.blur2 = stream.readShort();
				oglw.intensity = stream.readShort();
				oglw.intensity2 = stream.readShort();
				
				oglw.colorSpace = new ColorSpace();
				oglw.colorSpace.read(stream);
				
				
				oglw.blendMode = stream.readString(8);
				oglw.effectEnable = stream.readBoolean();
				oglw.opacity = stream.read();
				if (oglw.size == 42){
					oglw.nativeColorSpace = new ColorSpace();
					oglw.nativeColorSpace.read(stream);
				}
				
			}else if(effectSign.equals("iglw"))
			{
				iglw = new iglw_type();
				iglw.size = stream.readInt();
				iglw.version = stream.readInt();
				iglw.blur = stream.readShort();
				stream.skip(2);
				iglw.intensity = stream.readShort();
				stream.skip(2);
				iglw.colorSpace = new ColorSpace();
				iglw.colorSpace.read(stream);
				
				
				iglw.blendMode = stream.readString(8);
				iglw.enable = stream.readBoolean();
				iglw.opacity = stream.read();
				iglw.invert = stream.readBoolean();
				if (iglw.size == 43)
				{
					iglw.nativeColorSpace = new ColorSpace();
					iglw.nativeColorSpace.read(stream);
				}
				
			}else if(effectSign.equals("bevl"))
			{
				bevl = new bevl_type();
				bevl.size = stream.readInt();
				bevl.version = stream.readInt();
				bevl.angle = stream.readInt();
				bevl.strength = stream.readInt();
				bevl.blur = stream.readInt();
				bevl.hs = stream.readString(4);
				bevl.hk = stream.readString(4);
				bevl.ss = stream.readString(4);
				bevl.sk = stream.readString(4);
				
				bevl.hc = new ColorSpace();
				bevl.sc = new ColorSpace();
				
				bevl.hc.read(stream);
				bevl.sc.read(stream);
				
				bevl.bevelStyle = stream.read();
				bevl.hOpacity = stream.read();
				bevl.sOpacity = stream.read();
				bevl.effectEnable = stream.readBoolean();
				bevl.useAngleInAll = stream.readBoolean();
				bevl.upordown = stream.read();
				if (bevl.size == 78)
				{
					bevl.rhc = new ColorSpace();
					bevl.rhc.read(stream);
					bevl.rsc = new ColorSpace();
					bevl.rsc.read(stream);
				}
			}else if(effectSign.equals("sofi"))
			{
				sofi = new sofi_type();
				sofi.size = stream.readInt();
				sofi.version = stream.readInt();
				sofi.key = stream.readString(4);
				sofi.key2 = stream.readString(4);
				sofi.colorSpace = new ColorSpace();
				sofi.colorSpace.read(stream);
				
				sofi.opacity = stream.read();
				sofi.enable = stream.read();
				sofi.nativeColorSpace = new ColorSpace();
				sofi.nativeColorSpace.read(stream);
				
			}else
			{
				throw new IOException(
						"LayerLrFXParser effectSign error:" + effectSign );
			}
		}
	}

}
