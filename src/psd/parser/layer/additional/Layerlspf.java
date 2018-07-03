package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerlspf implements LayerAdditionalInformationParser {
	public static final String TAG = "lspf";
	public int value;
	public int value0;
	public int value1;
	public int value2;
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		value = stream.read();
		value0 = stream.read();
		value1 = stream.read();
		value2 = stream.read();
	}
}
