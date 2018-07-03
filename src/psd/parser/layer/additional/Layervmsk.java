package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layervmsk implements LayerAdditionalInformationParser{
	public static final String TAG = "vmsk";
	public int version;
	public int flags;
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		version = stream.readInt();
		flags = stream.readInt();
		stream.readString(size - 8);
//		System.out.print(TAG+ ":" + size+","+s);
	}
	
	
}
