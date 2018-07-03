package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;

public class Layerfxrp  implements LayerAdditionalInformationParser{
	public static final String TAG = "fxrp";
	public double referencePoint1;
	public double referencePoint2;
	@Override
	public void parse(PsdInputStream stream, String tag, int size)
			throws IOException {
		// TODO Auto-generated method stub
		referencePoint1 = stream.readDouble();
		referencePoint2 = stream.readDouble();
//		System.out.print(TAG+referencePoint1 + ","+referencePoint2);
	}
}
