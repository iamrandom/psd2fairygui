package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayeriOpa implements LayerAdditionalInformationParser{
	public static final String TAG = "iOpa";
	public boolean used;
	public int fillOpacity;
	public int value1;
	public int value2;
	public int value3;
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		used = true;
		// TODO Auto-generated method stub
		fillOpacity = stream.read();
		value1 = stream.read();
		value2 = stream.read();
		value3 = stream.read();
//		System.out.print(TAG+":" + fillOpacity + ","+value1+","+value2 + ","+value3 + "  ");
	}
}
