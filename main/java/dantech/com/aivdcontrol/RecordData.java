package dantech.com.aivdcontrol;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Danweb on 5/11/16.
 */
public class RecordData {

    ArrayList<int[]> dataPoints = new ArrayList<>();
    int[][] listToArray;
    int curPos = 0;
    long startTime;
    long playbackStart;
    private int hey;

    public RecordData(){}

    public void startRecord(){
        startTime = System.currentTimeMillis();
        dataPoints.clear();
    }

    public void recordPoint(int x, int y){
        int[] point = new int[]{x,y,(int)(System.currentTimeMillis()-startTime)};
        dataPoints.add(point);
        //System.out.println("Size: "+dataPoints.size()+"    Time: "+point[2]+" ms");
    }

    public void finishRecording(){
        listToArray = new int[dataPoints.size()][3];
        dataPoints.toArray(listToArray);
        //System.out.println("Points: ");
//        for(int[] i : listToArray)
//            System.out.println(Arrays.toString(i));
    }

    public void startPlayback(){
        playbackStart = System.currentTimeMillis();
        curPos = 0;
        //System.out.println("Start playback");
    }

    public int[] getPoint(){
        long curTimeElapsed = System.currentTimeMillis()-playbackStart;
        //System.out.println("Checking size: "+dataPoints.size()+"   "+ Arrays.toString(dataPoints.peek()));
        //System.out.println("Time elapsed: "+curTimeElapsed);
        //System.out.println("Cur pos: "+curPos+"/"+listToArray.length);
        if(curPos != listToArray.length) {
            while (curPos != listToArray.length && listToArray[curPos][2] < curTimeElapsed)
                curPos++;
            if(curPos != listToArray.length)
                return new int[]{listToArray[curPos][0], listToArray[curPos][1]};
            else
                return new int[]{Integer.MIN_VALUE,-1};
        }
        return new int[]{Integer.MIN_VALUE,-1};
    }
}
