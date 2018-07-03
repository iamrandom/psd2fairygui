package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerinfx implements LayerAdditionalInformationParser{
	public static final String TAG = "infx";
	
	public int Blend_interior;

	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		Blend_interior = stream.read();
		stream.skip(3);
	}

	

}
