/*
 * This file is part of java-psd-library.
 * 
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;
import psd.parser.object.*;

public class LayerMetaDataParser implements LayerAdditionalInformationParser {

	public static final String TAG = "shmd";
	private final LayerMetaDataHandler handler;
	public String show_msg;
	
	public LayerMetaDataParser(LayerMetaDataHandler handler) {
		this.handler = handler;
	}

	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		int countOfMetaData = stream.readInt();
		String s = "    shmd " +countOfMetaData + ":{";
		for (int i = 0; i < countOfMetaData; i++) {
			s += "[";
			String dataTag = stream.readString(4);
			if (!dataTag.equals("8BIM")) {
				throw new IOException("layer meta data section signature error");
			}
			s += dataTag;
			String key = stream.readString(4);
			s += "," + key;
			int copyOnSheetDuplication = stream.readByte();
			s += "," + copyOnSheetDuplication;
			stream.skipBytes(3); // padding
			int len = stream.readInt();
			s += "," + len;
			int pos = stream.getPos();
			if (key.equals("mlst")) {
				parseMlstSection(stream);
			} else {
			}
			stream.skipBytes(len - (stream.getPos() - pos));
			s += "]";
		}
		s += "}";
		show_msg = s;
//		System.out.print(s);
	}
	
	private void parseMlstSection(PsdInputStream stream) throws IOException {
		stream.skipBytes(4); // ???
		PsdDescriptor descriptor = new PsdDescriptor(stream);
		if (handler != null) {
			handler.metaDataMlstSectionParsed(descriptor);
		}
	}

}
