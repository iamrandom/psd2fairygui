package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerFMsk implements LayerAdditionalInformationParser{
	
	public static final String TAG = "FMsk";
	
	public ColorSpace colorSpace;
	public int opcacity;
	

	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		this.colorSpace = new ColorSpace();
		colorSpace.read(stream);
		this.opcacity = stream.readShort();
	}
}
