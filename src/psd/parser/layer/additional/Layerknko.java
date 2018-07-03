package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerknko implements LayerAdditionalInformationParser{
	public static final String TAG = "knko";
	
	public int Knockout;
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		Knockout = stream.read();
		stream.skip(3);
	}
}
