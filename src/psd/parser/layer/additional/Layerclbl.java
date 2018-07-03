package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerclbl implements LayerAdditionalInformationParser{
	public static final String TAG = "clbl";
	public int blendClip;
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		blendClip = stream.read();
		stream.skip(3);
	}
}
