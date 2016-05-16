package dantech.com.aivdcontrol;

/**
 * Created by Danweb on 5/16/16.
 */
public class ProcessedImage {

    private int[] rgbInts;
    private final int width;
    private final int height;

    public ProcessedImage(int[] rgb, int w, int h){
        rgbInts = rgb;
        width = w;
        height = h;
    }

    public byte[][][] getOrganizedData() {
        int retWidth = width / ObjectDetector.resolution;
        int retHeight = height / ObjectDetector.resolution;
        byte[][][] ret = new byte[retHeight][retWidth][3];
        int widthCutoff = (width % ObjectDetector.resolution);
        int row = 0;
        int col = 0;
        int tempPixInc = ObjectDetector.resolution;
        int rowInc = (ObjectDetector.resolution - 1) * width;
        for (int i = 0; i < rgbInts.length; i += tempPixInc) {
            int b = rgbInts[i] >> 16;
            int g = rgbInts[i] >> 8;
            int r = rgbInts[i];
            ret[row][col] = new byte[]{(byte)r,(byte)g,(byte)b};
            col++;
            if (col >= retWidth) {
                col = 0;
                row++;
                i += widthCutoff;
                i += rowInc;
            }
            if (row >= retHeight)
                break;
        }
        return ret;
    }

    public void setRgbInts(int[] rgb){
        rgbInts = rgb;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
}
