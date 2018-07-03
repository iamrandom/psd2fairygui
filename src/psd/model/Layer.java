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

package psd.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import psd.parser.BlendMode;
import psd.parser.layer.BlendingRanges;
import psd.parser.layer.Channel;
import psd.parser.layer.LayerHandler;
import psd.parser.layer.LayerParser;
import psd.parser.layer.LayerType;
import psd.parser.layer.Mask;
import psd.parser.layer.additional.LayerFMsk;
import psd.parser.layer.additional.LayerIdParser;
import psd.parser.layer.additional.LayerLMsk;
import psd.parser.layer.additional.LayerLrFXParser;
import psd.parser.layer.additional.LayerMetaDataHandler;
import psd.parser.layer.additional.LayerMetaDataParser;
import psd.parser.layer.additional.LayerSectionDividerHandler;
import psd.parser.layer.additional.LayerSectionDividerParser;
import psd.parser.layer.additional.LayerSoco;
import psd.parser.layer.additional.LayerTypeToolHandler;
import psd.parser.layer.additional.LayerTypeToolParser;
import psd.parser.layer.additional.LayerUnicodeNameHandler;
import psd.parser.layer.additional.LayerUnicodeNameParser;
import psd.parser.layer.additional.Layerclbl;
import psd.parser.layer.additional.Layerfxrp;
import psd.parser.layer.additional.LayeriOpa;
import psd.parser.layer.additional.Layerinfx;
import psd.parser.layer.additional.Layerknko;
import psd.parser.layer.additional.Layerlclr;
import psd.parser.layer.additional.Layerlfx2;
import psd.parser.layer.additional.Layerlnsr;
import psd.parser.layer.additional.Layerlspf;
import psd.parser.layer.additional.Layertsly;
import psd.parser.layer.additional.Layervmsk;
import psd.parser.layer.additional.Matrix;
import psd.parser.object.PsdDescriptor;
import psd.util.BufferedImageBuilder;

public class Layer implements LayersContainer {

	public class cmnS_type {
		public int size;
		public int version;
		public boolean visible;
		public short unused;
	}

	public class dw_type {
		public int size;
		public int version;
		public int blur;
		public int intensity;
		public int angle;
		public int distance;
		public String colorSpace;
		public String blendMode;
		public boolean effectEnable;
		public boolean useAngleInAllLayer;
		public int opacity;
		public String nativeColorSpace;
	}

	public class oglw_type {
		public int size;
		public int version;
		public int blur;
		public int intensity;
		public String colorSpace;
		public String blendMode;
		public boolean effectEnable;
		public int opacity;
		public String nativeColorSpace;
	}

	public class iglw_type {
		public int size;
		public int version;
		public int blur;
		public int intensity;
		public String colorSpace;
		public String blendMode;
		public boolean enable;
		public int opacity;
		public boolean invert;
		public String nativeColorSpace;
	}

	public class bevl_type {
		public int size;
		public int version;
		public int angle;
		public int strength;
		public int blur;
		public String hs;
		public String hk;
		public String ss;
		public String sk;
		public String hc;
		public String sc;
		public int bevelStyle;
		public int hOpacity;
		public int sOpacity;
		public boolean effectEnable;
		public boolean useAngleInAll;
		public int upordown;
		public String rhc;
		public String rsc;
	}

	public class sofi_type {
		public int size;
		public int version;
		public String key;
		public String colorSpace;
		public int opacity;
		public boolean enable;
		public String nativeColorSpace;
	}

	public cmnS_type cmns;
	public dw_type dsdw;
	public dw_type isdw;
	public oglw_type oglw;
	public iglw_type iglw;
	public bevl_type bevl;
	public sofi_type sofi;

	public int top = 0;
	public int left = 0;
	public int bottom = 0;
	public int right = 0;

	public int opacity = 255;
	public boolean clipping = false;

	public boolean visible = true;

	public String name;

	public BufferedImage image;
	public BufferedImage maskImage;

	public BufferedImageBuilder imageBuilder;
	public LayerType type = LayerType.NORMAL;
	public int dividerType;

	public ArrayList<Layer> layers = new ArrayList<Layer>();

	public BlendMode blendMode = BlendMode.NORMAL;
	public Mask mask;

	public BlendingRanges ranges;

	public LayerLrFXParser lrfx;

	public Layertsly tsly;

	public LayerLMsk LMsk;

	public LayerFMsk FMsk;
	
	public PsdDescriptor textDescrptor;

	public Layer(LayerParser parser) {
		parser.setHandler(new LayerHandler() {
			@Override
			public void boundsLoaded(int left, int top, int right, int bottom) {
				Layer.this.left = left;
				Layer.this.top = top;
				Layer.this.right = right;
				Layer.this.bottom = bottom;
			}

			@Override
			public void blendModeLoaded(BlendMode blendMode) {
				Layer.this.blendMode = blendMode;
			}

			@Override
			public void blendingRangesLoaded(BlendingRanges ranges) {
				Layer.this.ranges = ranges;
			}

			@Override
			public void opacityLoaded(int opacity) {
				if(opacity < 255)
				{
					Layer.this.opacity = opacity;
				}
				Layer.this.opacity = opacity;
			}

			@Override
			public void clippingLoaded(boolean clipping) {
				Layer.this.clipping = clipping;
			}

			@Override
			public void flagsLoaded(boolean transparencyProtected,
					boolean visible, boolean obsolete,
					boolean isPixelDataIrrelevantValueUseful,
					boolean pixelDataIrrelevant) {
				Layer.this.visible = visible;
			}

			@Override
			public void nameLoaded(String name) {
				Layer.this.name = name;
			}

			@Override
			public void channelsLoaded(List<Channel> channels) {
				imageBuilder = new BufferedImageBuilder(channels, getWidth(),
						getHeight());
				if(Layer.this.name.equals("蒙版"))
				{
					System.out.println("-----------" + Layer.this.name);
				}
				if(Layer.this.iOpa.used)
				{
					image = imageBuilder.makeImage(Layer.this.iOpa.fillOpacity);
				}
				else
				{
					image = imageBuilder.makeImage(255);
				}
				
				if (Layer.this.mask != null
						&& Layer.this.mask.disabled == false) {
					int mwidth = Layer.this.mask.right - Layer.this.mask.left;
					int mheight = Layer.this.mask.bottom - Layer.this.mask.top;
					int w =  (Layer.this.right - Layer.this.left);
					int h = (Layer.this.bottom - Layer.this.top);
					String s = Layer.this.name + "    "
							+ w + ","
							+ h + ","
							+ mwidth + "," + mheight + "    " + imageBuilder.channels.get(4).getId() + 
							"," + imageBuilder.channels.get(4).getDataLength();
					if(Layer.this.mask.mask != null)
					{
						s += "--------------  " + (Layer.this.mask.mask.right - Layer.this.mask.mask.left) + "," + (Layer.this.mask.mask.bottom - Layer.this.mask.mask.top);
					}
					s += "\n";
//					System.out.print(s);
					if(mwidth > 0 &&  mheight > 0)
					{
						maskImage = imageBuilder.makeMaskImage(mwidth, mheight);
					}
				}
			}

			@Override
			public void maskLoaded(Mask mask) {
				Layer.this.mask = mask;
			}

		});
		
		

		parser.putAdditionalInformationParser(LayerSectionDividerParser.TAG,
				new LayerSectionDividerParser(new LayerSectionDividerHandler() {
					@Override
					public void sectionDividerParsed(LayerType type, int dividerType) {
						Layer.this.type = type;
						Layer.this.dividerType = dividerType;
					}
				}));

		parser.putAdditionalInformationParser(LayerUnicodeNameParser.TAG,
				new LayerUnicodeNameParser(new LayerUnicodeNameHandler() {
					@Override
					public void layerUnicodeNameParsed(String unicodeName) {
						name = unicodeName;
					}
				}));

		parser.putAdditionalInformationParser(LayerMetaDataParser.TAG,
				new LayerMetaDataParser(new LayerMetaDataHandler() {

					@Override
					public void metaDataMlstSectionParsed(
							PsdDescriptor descriptor) {
						// TODO Auto-generated method stub

					}

				}));

		parser.putAdditionalInformationParser(LayerTypeToolParser.TAG,
				new LayerTypeToolParser(new LayerTypeToolHandler() {

					@Override
					public void typeToolTransformParsed(Matrix transform) {
						// TODO Auto-generated method stub

					}

					@Override
					public void typeToolDescriptorParsed(int version,
							PsdDescriptor descriptor) {
						// TODO Auto-generated method stub
						Layer.this.textDescrptor = descriptor;

					}

				}));
		
		lrfx = new LayerLrFXParser();
		parser.putAdditionalInformationParser(LayerLrFXParser.TAG, lrfx);

		tsly = new Layertsly();
		parser.putAdditionalInformationParser(Layertsly.TAG, tsly);

		LMsk = new LayerLMsk();
		parser.putAdditionalInformationParser(LayerLMsk.TAG, LMsk);

		FMsk = new LayerFMsk();
		parser.putAdditionalInformationParser(LayerFMsk.TAG, FMsk);

		parser.putAdditionalInformationParser(LayerIdParser.TAG,
				new LayerIdParser(null));

		lnsr = new Layerlnsr();
		parser.putAdditionalInformationParser(Layerlnsr.TAG, lnsr);

		clbl = new Layerclbl();
		parser.putAdditionalInformationParser(Layerclbl.TAG, clbl);

		knko = new Layerknko();
		parser.putAdditionalInformationParser(Layerknko.TAG, knko);

		infx = new Layerinfx();
		parser.putAdditionalInformationParser(Layerinfx.TAG, infx);

		lspf = new Layerlspf();
		parser.putAdditionalInformationParser(Layerlspf.TAG, lspf);

		lclr = new Layerlclr();
		parser.putAdditionalInformationParser(Layerlclr.TAG, lclr);

		fxrp = new Layerfxrp();
		parser.putAdditionalInformationParser(Layerfxrp.TAG, fxrp);

		iOpa = new LayeriOpa();
		parser.putAdditionalInformationParser(LayeriOpa.TAG, iOpa);

		SoCo = new LayerSoco();
		parser.putAdditionalInformationParser(LayerSoco.TAG, SoCo);
		
		vmsk = new Layervmsk();
		parser.putAdditionalInformationParser(Layervmsk.TAG, vmsk);
		
		lfx2 = new Layerlfx2();
		parser.putAdditionalInformationParser(Layerlfx2.TAG, lfx2);
		
	}

	public LayeriOpa iOpa;
	public Layerfxrp fxrp;
	public Layerlclr lclr;
	public Layerlspf lspf;
	public Layerknko knko;
	public Layerclbl clbl;
	public Layerlnsr lnsr;
	public Layerinfx infx;
	public LayerSoco SoCo;
	public Layervmsk vmsk;
	public Layerlfx2 lfx2;

	public void addLayer(Layer layer) {
		layers.add(layer);
	}

	@Override
	public Layer getLayer(int index) {
		return layers.get(index);
	}

	@Override
	public int indexOfLayer(Layer layer) {
		return layers.indexOf(layer);
	}

	@Override
	public int getLayersCount() {
		return layers.size();
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getX() {
		return left;
	}

	public int getY() {
		return top;
	}

	public int getWidth() {
		return right - left;
	}

	public int getHeight() {
		return bottom - top;
	}

	public LayerType getType() {
		return type;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getAlpha() {
		return opacity;
	}
}
