package psd.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.List;

import psd.parser.layer.Channel;

public class BufferedImageBuilder {

	public final List<Channel> channels;
	private final byte[][] uncompressedChannels;
	private final int width;
	private final int height;
	private int opacity = -1;

	public BufferedImageBuilder(List<Channel> channels, int width, int height) {
		this.uncompressedChannels = null;
		this.channels = channels;
		this.width = width;
		this.height = height;
	}

	public BufferedImageBuilder(byte[][] channels, int width, int height) {
		this.uncompressedChannels = channels;
		this.channels = null;
		this.width = width;
		this.height = height;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public BufferedImage makeImage(int other_opacity) {
		if (width == 0 || height == 0) {
			return null;
		}

		byte[] rChannel = getChannelData(Channel.RED, width, height);
		byte[] gChannel = getChannelData(Channel.GREEN, width, height);
		byte[] bChannel = getChannelData(Channel.BLUE, width, height);
		byte[] aChannel = getChannelData(Channel.ALPHA, width, height);
		applyOpacity(aChannel);
		BufferedImage im = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		int[] data = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
		int n = width * height - 1;
		if(other_opacity < 0)
		{
			other_opacity = 0xff;
		}
		if(other_opacity == 255)
		{
			while (n >= 0) {
				int a = aChannel[n] & 0xff;
				int r = rChannel[n] & 0xff;
				int g = gChannel[n] & 0xff;
				int b = bChannel[n] & 0xff;
				data[n] = a << 24 | r << 16 | g << 8 | b;
				n--;
			}
		}
		else
		{
			float f = (other_opacity & 0xff) / 255.0f;
			while (n >= 0) {
				int a = (int)((aChannel[n] & 0xff) * f);
				int r = rChannel[n] & 0xff;
				int g = gChannel[n] & 0xff;
				int b = bChannel[n] & 0xff;
				data[n] = a << 24 | r << 16 | g << 8 | b;
				n--;
			}
		}

		return im;
	}

	public BufferedImage makeMaskImage(int mwidth, int mheight) {
		if (mwidth == 0 || mheight == 0) {
			return null;
		}
		byte[] mChannel = getChannelData(Channel.MASK ,mwidth, mheight);
		BufferedImage im = new BufferedImage(mwidth, mheight,
				BufferedImage.TYPE_BYTE_GRAY);
		
		byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
		int n = mwidth * mheight - 1;
		while(n >= 0)
		{
			data[n] = mChannel[n];
			n--;
		}
		return im;
	}

	private void applyOpacity(byte[] a) {
		if (opacity != -1) {
			double o = (opacity & 0xff) / 256.0;
			for (int i = 0; i < a.length; i++) {
				a[i] = (byte) ((a[i] & 0xff) * o);
			}
		}
	}

	private byte[] getChannelData(int channelId, int w, int h) {
		if (uncompressedChannels == null) {
			for (Channel c : channels) {
				if (channelId == c.getId() && c.getCompressedData() != null) {
					ChannelUncompressor uncompressor = new ChannelUncompressor();
					byte[] uncompressedChannel = uncompressor.uncompress(
							c.getCompressedData(), w, h);
					if (uncompressedChannel != null) {
						return uncompressedChannel;
					}
				}
			}
		} else {
			if (channelId >= 0 && uncompressedChannels[channelId] != null) {
				return uncompressedChannels[channelId];
			}
		}
		return fillBytes(w * h,
				(byte) (channelId == Channel.ALPHA ? 255 : 0));
	}

	private byte[] fillBytes(int size, byte value) {
		byte[] result = new byte[size];
		if (value != 0) {
			for (int i = 0; i < size; i++) {
				result[i] = value;
			}
		}
		return result;
	}

}
