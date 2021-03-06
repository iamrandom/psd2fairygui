package psd.parser;

public enum BlendMode {
    NORMAL("norm"),
    DISSOLVE("diss"),
    DARKEN("dark"),
    MULTIPLY("mul "),
    COLOR_BURN("idiv"),
    LINEAR_BURN("lbrn"),
    LIGHTEN("lite"),
    SCREEN("scrn"),
    COLOR_DODGE("div "),
    LINEAR_DODGE("lddg"),
    OVERLAY("over"),
    SOFT_LIGHT("sLit"),
    HARD_LIGHT("hLit"),
    VIVID_LIGHT("vLit"),
    LINEAR_LIGHT("lLit"),
    PIN_LIGHT("pLit"),
    HARD_MIX("hMix"),
    DIFFERENCE("diff"),
    EXCLUSION("smud"),
    HUE("hue "),
    SATURATION("sat "),
    COLOR("colr"),
    LUMINOSITY("lum "),
    PASS_THROUGH("pass");

    public String name;

    private BlendMode(String name) {
        this.name = name;
    }

    public static BlendMode getByName(String name) {
        for (BlendMode mode : values()) {
            if (mode.name.equals(name)) {
                return mode;
            }
        }
        return null;
    }
    
//    Normal 
//    正常模式，也是默认的模式。不和其他图层发生任何混合。 
//
//    Dissolve 
//    溶解模式。溶解模式产生的像素颜色来源于上下混合颜色的一个随机置换值，与像素的不透明度有关。 
//
//    Behind 
//    背后模式。只对图层的透明区域进行编辑。该种模式只有在图层的LockTransparentPixels（锁定透明区域）为不勾选状态才有效。 
//
//    Clear 
//    清除模式。任何编辑会让像素透明化。这种模式和画笔的颜色无关，只和笔刷的参数有关。该模式对形状工具（当FillPixel选项处于勾选状态时）、油漆桶工具、笔刷工具、铅笔工具、填充命令和描边命令都有效。 
//
//    Darken 
//    变暗模式。考察每一个通道的颜色信息以及相混合的像素颜色，选择较暗的作为混合的结果。颜色较亮的像素会被颜色较暗的像素替换，而较暗的像素就不会发生变化。 
//
//    Multiply 
//    正片叠底模式。考察每个通道里的颜色信息，并对底层颜色进行正片叠加处理。其原理和色彩模式中的“减色原理”是一样的。这样混合产生的颜色总是比原来的要暗。如果和黑色发生正片叠底的话，产生的就只有黑色。而与白色混合就不会对原来的颜色产生任何影响。 
//
//    ColorBurn 
//    颜色加深模式。让底层的颜色变暗，有点类似于正片叠底，但不同的是，它会根据叠加的像素颜色相应增加底层的对比度。和白色混合没有效果。 
//
//    LinearBurn 
//    线性颜色加深模式。同样类似于正片叠底，通过降低亮度，让底色变暗以反映混合色彩。和白色混合没有效果。 
//
//    Lighten 
//    变亮模式。和变暗模式相反，比较相互混合的像素亮度，选择混合颜色中较亮的像素保留起来，而其他较暗的像素则被替代。 
//
//    Screen 
//    屏幕模式。按照色彩混合原理中的“增色模式”混合。也就是说，对于屏幕模式，颜色具有相加效应。比如，当红色、绿色与蓝色都是最大值255的时候，以Screen模式混合就会得到RGB值为（255，255，255）的白色。而相反的，黑色意味着为0。所以，与黑色以该种模式混合没有任何效果，而与白色混合则得到RGB颜色最大值白色（RGB值为255，255，255）。 
//    搜索
//    ColorDodge 
//    颜色减淡模式。与ColorBurn刚好相反，通过降低对比度，加亮底层颜色来反映混合色彩。与黑色混合没有任何效果。 
//
//    LinearDodge 
//    线性颜色减淡模式。类似于颜色减淡模式。但是通过增加亮度来使得底层颜色变亮，以此获得混合色彩。与黑色混合没有任何效果。 
//
//    Overlay 
//    叠加模式。像素是进行Multiply（正片叠底）混合还是Screen（屏幕）混合，取决于底层颜色。颜色会被混合，但底层颜色的高光与阴影部分的亮度细节就会被保留。 
//
//    SoftLight 
//    柔光模式。变暗还是提亮画面颜色，取决于上层颜色信息。产生的效果类似于为图像打上一盏散射的聚光灯。如果上层颜色（光源）亮度高于50%灰，底层会被照亮（变淡）。如果上层颜色（光源）亮度低于50%灰，底层会变暗，就好像被烧焦了似的。 
//    如果直接使用黑色或白色去进行混合的话，能产生明显的变暗或者提亮效应，但是不会让覆盖区域产生纯黑或者纯白。 
//
//    HardLight 
//    强光模式。正片叠底或者是屏幕混合底层颜色，取决于上层颜色。产生的效果就好像为图像应用强烈的聚光灯一样。如果上层颜色（光源）亮度高于50%灰，图像就会被照亮，这时混合方式类似于Screen（屏幕模式）。反之，如果亮度低于50%灰，图像就会变暗，这时混合方式就类似于Multiply（正片叠底模式）。该模式能为图像添加阴影。如果用纯黑或者纯白来进行混合，得到的也将是纯黑或者纯白。 
//
//    VividLight 
//    艳光模式。调整对比度以加深或减淡颜色，取决于上层图像的颜色分布。如果上层颜色（光源）亮度高于50%灰，图像将被降低对比度并且变亮；如果上层颜色（光源）亮度低于50%灰，图像会被提高对比度并且变暗。 
//
//    LinearLight 
//    线性光模式。如果上层颜色（光源）亮度高于中性灰（50%灰），则用增加亮度的方法来使得画面变亮，反之用降低亮度的方法来使画面变暗。 
//
//    PinLight 
//    固定光模式。按照上层颜色分布信息来替换颜色。如果上层颜色（光源）亮度高于50%灰，比上层颜色暗的像素将会被取代，而较之亮的像素则不发生变化。如果上层颜色（光源）亮度低于50%灰，比上层颜色亮的像素会被取代，而较之暗的像素则不发生变化。 
//
//    Difference 
//    差异模式。根据上下两边颜色的亮度分布，对上下像素的颜色值进行相减处理。比如，用最大值白色来进行Difference运算，会得到反相效果（下层颜色被减去，得到补值），而用黑色的话不发生任何变化（黑色亮度最低，下层颜色减去最小颜色值0，结果和原来一样）。 
//
//    Exclusion 
//    排除模式。和Difference类似，但是产生的对比度会较低。同样的，与纯白混合得到反相效果，而与纯黑混合没有任何变化。 
//
//    Hue 
//    色调模式。决定生成颜色的参数包括:底层颜色的明度与饱和度，上层颜色的色调。 
//
//    Saturation 
//    饱和度模式。决定生成颜色的参数包括:底层颜色的明度与色调，上层颜色的饱和度。按这种模式与饱和度为0的颜色混合（灰色）不产生任何变化。 
//
//    Color 
//    着色模式。决定生成颜色的参数包括:底层颜色的明度，上层颜色的色调与饱和度。这种模式能保留原有图像的灰度细节。这种模式能用来对黑白或者是不饱和的图像上色。 
//
//    Luminosity 
//    明度模式。决定生成颜色的参数包括:底层颜色的色调与饱和度，上层颜色的明度。该模式产生的效果与Color模式刚好相反，它根据上层颜色的明度分布来与下层颜色混合。

//    1.Opacity 不透明度
//
//    C=d*A+(1-d)*B
//
//    相对于不透明度而言，其反义就是透明度。这两个术语之间的关系就类似于正负之间的关系：100%的不透明度就是0%的透明度。该混合模式相对来说比较简单，在该混合模式下，如果两个图层的叠放顺序不一样，其结果也是不一样的（当然50%透明除外）。该公式中，A代表了上面图层像素的色彩值（A=像素值/255），d表示该层的透明度，B代表下面图层像素的色彩值（B=像素值/255），C代表了混合像素的色彩值（真实的结果像素值应该为255*C）。该公式也应用于层蒙板，在这种情况下，d代表了蒙板图层中给定位置像素的亮度，下同，不再叙述。
//
// 2.Darken 变暗
//
//    B<=A 则 C=B
//    B>=A 则 C=A
//
//    该模式通过比较上下层像素后取相对较暗的像素作为输出，注意，每个不同的颜色通道的像素都是独立的进行比较，色彩值相对较小的作为输出结果，下层表示叠放次序位于下面的那个图层，上层表示叠放次序位于上面的那个图层，下同，不再叙述。
//
// 3.Lighten 变亮
//
//    B<=A 则 C=A
//    B>A 则 C=B
//
//    该模式和前面的模式是相似，不同的是取色彩值较大的（也就是较亮的）作为输出结果。
//
// 4.Multiply 正片叠底
//
//    C=A*B
//
//    该效果将两层像素的标准色彩值（基于0..1之间）相乘后输出，其效果可以形容成：两个幻灯片叠加在一起然后放映，透射光需要分别通过这两个幻灯片，从而被削弱了两次。
//
// 5.Screen 滤色
//
//    C=1-(1-A)*(1-B)
// 6.Color Dodge 颜色减淡
//
//    C=B/(1-A)
//
//    该模式下，上层的亮度决定了下层的暴露程度。如果上层越亮，下层获取的光越多，也就是越亮。如果上层是纯黑色，也就是没有亮度，则根本不会影响下层。如果上层是纯白色，则下层除了像素为255的地方暴露外，其他地方全部为白色（也就是255，不暴露）。结果最黑的地方不会低于下层的像素值。
//
// 7.Color Burn 颜色加深
//
//    C=1-(1-B)/A
//
//    该模式和上一个模式刚好相反。如果上层越暗，则下层获取的光越少，如果上层为全黑色，则下层越黑，如果上层为全白色，则根本不会影响下层。结果最亮的地方不会高于下层的像素值。
//
// 8.Linear Dodge 线形减淡
//
//    C=A+B
//
//    将上下层的色彩值相加。结果将更亮。
//
// 9.Linear Burn 线形加深
//
//    C=A+B-1
//
// 如果上下层的像素值之和小于255，输出结果将会是纯黑色。如果将上层反相，结果将是纯粹的数学减。
//
// 10.Overlay叠加
//
//    B<=0.5 则 C=2*A*B
//    B>0.5 则 C=1-2*(1-A)*(1-B)
//
//    依据下层色彩值的不同，该模式可能是Multiply，也可能是Screen模式。上层决定了下层中间色调偏移的强度。如果上层为50%灰，则结果将完全为下层像素的值。如果上层比50%灰暗，则下层的中间色调的将向暗地方偏移，如果上层比50%灰亮，则下层的中间色调的将向亮地方偏移。对于上层比50%灰暗，下层中间色调以下的色带变窄（原来为0~2*0.4*0.5，现在为0~2*0.3*0.5），中间色调以上的色带变宽（原来为2*0.4*0.5~1，现在为2*0.3*0.5~1）。反之亦然。
// 11.Hard Light 强光
//
//    A<=0.5 则 C=2*A*B
//    A>0.5 则 C=1-2*(1-A)*(1-B)
//
//    该模式完全相对应于Overlay模式下，两个图层进行次序交换的情况。如过上层的颜色高于50%灰，则下层越亮，反之越暗
//
// 12.Soft Light柔光
//
//    A<=0.5 则 C=(2*A-1)*(B-B*B)+B
//    A>0.5 则 C=(2*A-1)*(sqrt(B)-B)+B
//
//    该模式类似上层以Gamma值范围为2.0到0.5的方式来调制下层的色彩值。结果将是一个非常柔和的组合。
//
// 13.Vivid Light 亮光
//
//    A<=0.5: C=1-(1-B)/2*A
//    A>0.5: C=B/(2*(1-A))
//
//    该模式非常强烈的增加了对比度，特别是在高亮和阴暗处。可以认为是阴暗处应用Color Burn和高亮处应用Color Dodge。
//
// 14.Linear Light 线形光
//
//    C=B+2*A-1
//
//    相对于前一种模式而言，该模式增加的对比度要弱些。其类似于Linear Burn,只不过是加深了上层的影响力
//
// 15.Pin Light 点光
//
//    B<2*A-1 则 C=2*A-1
//    2*A-1<B<2*A 则 C=B
//    B>2*A 则 C=2*A
//
//    该模式结果就是导致中间调几乎是不变的下层，但是两边是Darken和Lighte年模式的组合
//
// 16.Hard Mix 实色混合
//
//    A<1-B 则 C=0
//    A>1-B 则 C=1
//
// 该模式导致了最终结果仅包含6种基本颜色，每个通道要么就是0，要么就是255。
//
// 17.Difference 差值
//
//    C=|A-B|
//
//    上下层色调的绝对值。该模式主要用于比较两个不同版本的图片。如果两者完全一样，则结果为全黑。
//
// 18.Exclusion 排除
//
//    C=A+B-2*A*B
//
//    亮的图片区域将导致另一层的反相，很暗的区域则将导致另一层完全没有改变。
//
// 19.Hue 色相
//
//    HcScYc =HASBVB
//
//    输出图像的色调为上层，饱和度和亮度保持为下层。对于灰色上层，结果为去色的下层。
//
// 20.Saturation 饱和度
//
//    HcScYc =HBSAVB
//
//    输出图像的饱和度为上层，色调和亮度保持为下层。
//
// 21.Color 颜色
//
//    HcScYc =HASAYB
//
//    输出图像的亮度为下层，色调和饱和度保持为上层。
//
// 22.Luminosity 亮度
//
//    HcScYc =HBSBYA
//
//    输出图像的亮度为上层，色调和饱和度保持为下层。
//
// 23.Dissolve 溶解
//
//     该模式根本不是真正的溶解，因此并不是适合Dissolve这个称谓，其表现仅仅和Normal类似。其从上层中随机抽取一些像素作为透明，使其可以看到下层，随着上层透明度越低，可看到的下层区域越多。如果上层完全不透明，则效果和Normal不会有任何不同。
}
