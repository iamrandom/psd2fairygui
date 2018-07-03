package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class LayerLMsk  implements LayerAdditionalInformationParser{
	
	public static final String TAG = "LMsk";
	
	public ColorSpace colorSpace;
	
	
	public int opacity;
	public int flag;
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		
		this.colorSpace = new ColorSpace();
		colorSpace.read(stream);
		
		opacity = stream.readShort();
		flag = stream.read();
	}

}
