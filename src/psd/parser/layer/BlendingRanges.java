package psd.parser.layer;

public class BlendingRanges {
    public int grayBlackSrc;    // Composite gray blend source. Contains 2 black values followed by 2 white values. Present but irrelevant for Lab & Grayscale.
    public int grayWhiteSrc;
    public int grayBlackDst;    // Composite gray blend destination range
    public int grayWhiteDst;
    public int numberOfBlendingChannels;
    public int[] channelBlackSrc;// channel source range
    public int[] channelWhiteSrc;
    public int[] channelBlackDst;// First channel destination range
    public int[] channelWhiteDst;
}
