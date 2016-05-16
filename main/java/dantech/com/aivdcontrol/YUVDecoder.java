package dantech.com.aivdcontrol;

/**
 * Created by Danweb on 5/16/16.
 */
public class YUVDecoder {

    public static int[] decodeYUV(byte[] fg, int width, int height, int res) {
        long st = System.nanoTime();
        int sz = width*height;
        int[] ret = new int[(width/ObjectDetector.resolution) * (height/ObjectDetector.resolution)];
        int i, j;
        int Y, Cr = 0, Cb = 0;
        int rgbPix = 0;
        for (j = 0; j < height-(height%ObjectDetector.resolution); j+=ObjectDetector.resolution) {
            int pixPtr = j * width;
            final int jDiv2 = j >> 1;
            for (i = 0; i < width-(width%ObjectDetector.resolution); i+=ObjectDetector.resolution) {
                Y = fg[pixPtr];
                if (Y < 0)
                    Y += 255;
                if ((i & 0x1) != 1) {
                    final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
                    Cb = fg[cOff];
                    if (Cb < 0)
                        Cb += 127;
                    else
                        Cb -= 128;
                    Cr = fg[cOff + 1];
                    if (Cr < 0)
                        Cr += 127;
                    else
                        Cr -= 128;
                }
                int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if (R < 0)
                    R = 0;
                else if (R > 255)
                    R = 255;
                int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                if (G < 0)
                    G = 0;
                else if (G > 255)
                    G = 255;
                int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if (B < 0)
                    B = 0;
                else if (B > 255)
                    B = 255;
                ret[rgbPix++] = 0xff000000 + (B << 16) + (G << 8) + R;
                pixPtr+=ObjectDetector.resolution;
            }
        }
        long et = System.nanoTime();

        if(FoxScreen.verbose)
            System.out.println("Decode time: "+((et-st)/1000000)+"ms");
        return ret;
    }
}
