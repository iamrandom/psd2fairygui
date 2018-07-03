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
import java.io.*;
import java.util.*;

import psd.parser.*;
import psd.parser.header.*;
import psd.parser.imagedata.ImageDataSectionHandler;
import psd.parser.layer.*;
import psd.util.BufferedImageBuilder;

public class Psd implements LayersContainer {
	public Header header;
	public List<Layer> layers = new ArrayList<Layer>();
	public BufferedImage image;
	public String name;

	public Psd(File psdFile) throws IOException {
		name = psdFile.getName();
		final byte[][] channels = new byte[3][];

		PsdFileParser parser = new PsdFileParser();
		parser.getHeaderSectionParser().setHandler(new HeaderSectionHandler() {
			@Override
			public void headerLoaded(Header header) {
				Psd.this.header = header;
			}
		});

		final List<Layer> fullLayersList = new ArrayList<Layer>();
		parser.getLayersSectionParser().setHandler(new LayersSectionHandler() {
			@Override
			public void createLayer(LayerParser parser) {
				fullLayersList.add(new Layer(parser));
			}
		});

		parser.getImageDataSectionParser().setHandler(
				new ImageDataSectionHandler() {
					@Override
					public void channelLoaded(int channelId, byte[] channelData) {
						if (channelId >= 0 && channelId < 3) {
							channels[channelId] = channelData;
						}
					}
				});

		BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(psdFile));
		parser.parse(stream);
		stream.close();

		layers = makeLayersHierarchy(fullLayersList);

		if (parser.getHeaderSectionParser().getHeader().getColorMode() == ColorMode.GRAYSCALE) {
			channels[1] = channels[2] = channels[0];
		}
		
		image = new BufferedImageBuilder(channels, header.getWidth(),
				header.getHeight()).makeImage(255);
	}

	private List<Layer> makeLayersHierarchy(List<Layer> layers) {
		LinkedList<LinkedList<Layer>> layersStack = new LinkedList<LinkedList<Layer>>();
		ArrayList<Layer> rootLayers = new ArrayList<Layer>();
//		for(Layer layer : layers)
//		{
//			System.out.println(layer.name + "   " + layer.getType());
//		}
		
		boolean need_check = false;
		for (Layer layer : layers) {
			// corren
			if(layer.image == null && layer.textDescrptor == null && (layer.type == LayerType.NORMAL) && layer.name.equals("</Layer group>"))
			{
				layer.type = LayerType.HIDDEN;
				layer.dividerType = 3;
			}
			switch (layer.getType()) {
			case HIDDEN: {
				if (need_check) {
					need_check = false;
					LinkedList<Layer> folderLayers = layersStack.removeFirst();
					for (Layer l : folderLayers) {
						rootLayers.add(l);
					}
				}
				layersStack.addFirst(new LinkedList<Layer>());
				break;
			}
			case FOLDER: {
				if (!layersStack.isEmpty()) {
					LinkedList<Layer> folderLayers = layersStack.removeFirst();
					;
					for (Layer l : folderLayers) {
						layer.addLayer(l);
					}
				}
			}
			// break isn't needed
			case NORMAL: {
				if (layersStack.isEmpty()) {
					need_check = true;
					layersStack.addFirst(new LinkedList<Layer>());
				}
				layersStack.getFirst().add(layer);
				break;
			}
			default:
				assert false;
			}
		}
		if (layersStack.size() > 0) {
			LinkedList<Layer> folderLayers = layersStack.removeFirst();
			for (Layer l : folderLayers) {
				rootLayers.add(l);
			}
		}
		// for (Layer layer : layers) {
		// switch (layer.getType()) {
		// case HIDDEN: {
		// layersStack.addFirst(new LinkedList<Layer>());
		// break;
		// }
		// case FOLDER: {
		// if(layersStack.isEmpty())
		// {
		//
		// }
		// assert !layersStack.isEmpty();
		// LinkedList<Layer> folderLayers = null;
		// folderLayers = layersStack.removeFirst();
		// // folderLayers = new LinkedList<Layer>();
		// for (Layer l : folderLayers) {
		// layer.addLayer(l);
		// }
		// }
		// // break isn't needed
		// case NORMAL: {
		// if (layersStack.isEmpty()) {
		//
		// rootLayers.add(layer);
		// } else {
		// layersStack.getFirst().add(layer);
		// }
		// break;
		// }
		// default:
		// assert false;
		// }
		// }
		return rootLayers;
	}

	public int getWidth() {
		return header.getWidth();
	}

	public int getHeight() {
		return header.getHeight();
	}

	public BufferedImage getImage() {
		return image;
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

	@Override
	public String toString() {
		return name;
	}
}
