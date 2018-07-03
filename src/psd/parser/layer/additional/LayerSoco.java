package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerSoco implements LayerAdditionalInformationParser{
	public static final String TAG = "SoCo";
	public int version;
	public Descriptor des;
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
//		int a = stream.getPos();
		version = stream.readInt();
		des = new Descriptor();
		des.isPrint = false;
		if(des.isPrint)
		{
			System.out.print(TAG+":" +size+":");
		}
		des.parse(stream, tag, size - 4);

//		int b = stream.getPos() - a;
//		String p4 = stream.readString(size - b );
//		System.out.print("["+p4 + "]");
	}
	
}
