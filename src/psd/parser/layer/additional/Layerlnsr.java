package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerlnsr implements LayerAdditionalInformationParser {
	public static final String TAG = "lnsr";
	
	public String name;
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		name = stream.readString(4);

	}

}
