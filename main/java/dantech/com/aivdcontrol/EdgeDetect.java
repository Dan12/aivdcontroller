package dantech.com.aivdcontrol;

import java.util.ArrayList;

/**
 * Created by Danweb on 5/16/16.
 */
public class EdgeDetect {

    /*TODO: possible edge detect bug, not always getting entire shape when there are bumps,
        * probably not a tolerance issue
        */

    public static ArrayList<ShapeRectangle> runRoutine(int[] vals, int[] targ, int w, int h) {
        ArrayList<ShapeRectangle> ret = new ArrayList<ShapeRectangle>();
        //System.out.println("Running edge detect routine...");
        //System.out.println("Target RGB: " + targ[0] + "," + targ[1] + "," + targ[2]);
        int r = 0;
        int c = 0;
        while (r < h) {
            if (vals[r * w + c] != -1) {
                if (Functions.isInTolerance(vals[r * w + c], targ)) {
                    int[] initEdge = new int[]{r, c};
                    int[] curPos = new int[]{r, c};
                    ShapeRectangle temp = new ShapeRectangle();
                    temp.newPoint(curPos);
                    boolean validShape = true;
                    int startPointer = 0;
                    while (true) {
                        ///System.out.println(curPos[0]+","+curPos[1]);
                        int[] newPos = Functions.nextPos(vals, curPos[0], curPos[1], targ, w, h, startPointer);
                        if (newPos[0] == initEdge[0] && newPos[1] == initEdge[1]) {
                            //System.out.println("Success");
                            break;
                        }
                        if (newPos[0] == curPos[0] && newPos[1] == curPos[1]) {
                            //System.out.println("Didn't Close");
                            break;
                        }
                        curPos = new int[]{newPos[0],newPos[1]};
                        if(vals[curPos[0]*w+curPos[1]] != -3)
                            vals[curPos[0]*w+curPos[1]] = -2;
                        temp.newPoint(curPos);
                        startPointer = newPos[2]+5;
                        while(startPointer > 7)
                            startPointer-=8;
                    }
                    int density = 0;
                    int timesChecked = 0;
                    for (int rr = temp.getMinY()+temp.getCheckHeight(); rr <= temp.getMaxY()-temp.getCheckHeight(); rr+=temp.getCheckHeight()) {
                        for (int cc = temp.getMinX()+temp.getCheckWidth(); cc <= temp.getMaxX()-temp.getCheckWidth(); cc+=temp.getCheckWidth()) {
                            if (vals[rr * w + cc] != -1 && Functions.isInTolerance(vals[rr * w + cc], targ))
                                density++;
                            timesChecked++;
                        }
                    }
                    for (int rr = temp.getMinY(); rr <= temp.getMaxY(); rr++) {
                        for (int cc = temp.getMinX(); cc <= temp.getMaxX(); cc++) {
                            vals[rr*w+cc] = -1;
                        }
                    }
                    temp.setDensity(((float)density)/timesChecked);
                    if (validShape && temp.isValid()) {
                        //System.out.println("Added");
                        ret.add(temp);
                    }
                }
            }
            c++;
            if (c >= w) {
                r++;
                c = 0;
            }
        }

        return ret;
    }
}
