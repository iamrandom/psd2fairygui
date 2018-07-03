package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layertsly implements LayerAdditionalInformationParser{
	public static final String TAG = "tsly";
	
	public int value;
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		value =  stream.read();
		stream.skip(3);
		
	}

}
