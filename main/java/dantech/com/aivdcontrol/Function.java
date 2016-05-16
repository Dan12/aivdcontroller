package dantech.com.aivdcontrol;

/**
 * Created by Danweb on 5/16/16.
 */
public class Function {

    private static int[][] pointerNums = new int[][]{
            {1,-1},
            {1,0},
            {1,1},
            {0,1},
            {-1,1},
            {-1,0},
            {-1,-1},
            {0,-1}
    };

    public static boolean isInTolerance(int input, int[] targ){
        int[] test = getRGB(input);
        return Math.abs(test[0]-targ[0]) <= ObjectDetector.tolerance && Math.abs(test[1]-targ[1]) <= ObjectDetector.tolerance && Math.abs(test[2]-targ[2]) <= ObjectDetector.tolerance;
    }

    public static int[] getRGB(int[] input, int r, int c, int w){
        int val = input[r*w+c];
        return getRGB(val);
    }

    private static int[] getRGB(int input){
        return new int[]{(input) & 0xff, (input >> 8) & 0xff, (input >> 16) & 0xff};
    }

    public static int[] nextPos(int[] vals, int r, int c, int[] targ, int w, int h, int p){
        //TODO: possible bug, not finding next pos in irregular shapes, probably not tolerance issue, probably in seenOpenPixel part
        boolean seenOpenPixel = false;
        int pointerNum = 0;
        int[] ret = new int[]{r,c};
        for(int i = 0; i < 8; i++){
            int[] pointer = mapPointerNum(i, p);
            boolean isInBounds = inBounds(r+pointer[1],c+pointer[0], w, h);
            if(!isInBounds || (isInBounds && (vals[(r+pointer[1])*w+(c+pointer[0])] == -1 || !isInTolerance(vals[(r+pointer[1])*w+(c+pointer[0])], targ)))){
                pointerNum = i+p+1;
                if(pointerNum > 7)
                    pointerNum-=8;
                seenOpenPixel = true;
                if(isInBounds)
                    vals[(r+pointer[1])*w+(c+pointer[0])] = -1;
                break;
            }
        }
        if(seenOpenPixel){
            for(int i = 0; i < 8; i++){
                int[] pointer = mapPointerNum(i,pointerNum);
                boolean isInBounds = inBounds(r+pointer[1],c+pointer[0], w, h);
                boolean inTolerance = false;
                if(isInBounds)
                    inTolerance = isInTolerance(vals[(r+pointer[1])*w+(c+pointer[0])], targ);
                if(isInBounds && vals[(r+pointer[1])*w+(c+pointer[0])] != -1 && (vals[(r+pointer[1])*w+(c+pointer[0])] == -2 || inTolerance)){
                    ret = new int[]{r+pointer[1],c+pointer[0],i+pointerNum};
                    if(vals[(r+pointer[1])*w+(c+pointer[0])] == -2)
                        vals[(r+pointer[1])*w+(c+pointer[0])] = -3;
                    break;
                }
                else if(isInBounds && !inTolerance){
                    vals[(r+pointer[1])*w+(c+pointer[0])] = -1;
                }
            }
        }
        return ret;
    }

    public static double map(double x, double in_min, double in_max, double out_min, double out_max){
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    private static boolean inBounds(int r, int c, int w, int h){
        return (r >= 0 && r < h && c >= 0 && c < w);
    }

    //x,y
    private static int[] mapPointerNum(int i, int s){
        int test = s+i;
        if(test > 7)
            test -= 8;
        return pointerNums[test];
    }
}
