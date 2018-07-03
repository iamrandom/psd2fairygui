package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerlclr implements LayerAdditionalInformationParser{
	public static final String TAG = "lclr";
	public int color1;
	public int color2;
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		color1 = stream.readInt();
		color2 = stream.readInt();
//		System.out.print("lclr:"+color1 + ","+color2);
	}
	
	
}
