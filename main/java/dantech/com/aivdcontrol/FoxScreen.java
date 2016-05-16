package dantech.com.aivdcontrol;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Danweb on 5/16/16.
 */
public class FoxScreen extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

    private MainActivity mainActivity;
    private SurfaceHolder mHolder;
    private DrawView viewClass;
    private Camera mCamera;
    private boolean isPreviewRunning = false;
    private volatile byte[] yuvData;
    private Bitmap bmp;
    public int width;
    public int height;
    private long startTime = System.nanoTime();

    private ObjectDetector objectDetector1;
    private ObjectDetector objectDetector2;
    private ObjectDetector objectDetector3;
    private boolean sendData = false;

    private int numSetting = 3;

    private boolean isFollow = false;

    public FoxScreen(Context context, MainActivity activity){
        super(context);

        mainActivity = activity;

        mHolder = getHolder();
        mHolder.addCallback(this);
        System.out.println(mHolder);
        System.out.println("Initializing surfaceview");

        viewClass = new DrawView();

        objectDetector1 = new ObjectDetector(new int[]{0,255,0});
        objectDetector2 = new ObjectDetector(new int[]{255,255,0});
        objectDetector3 = new ObjectDetector(new int[]{255,0,0});
    }

    public ViewClass getViewClass(){
        return viewClass;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface Created");
        synchronized (this) {
            if (isPreviewRunning)
                return;
            this.setWillNotDraw(false); // This allows us to make our own draw
            // calls to this canvas

            mCamera = Camera.open();

            //isPreviewRunning = true;
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            width = size.width;
            height = size.height;
//            objectDetector1.setDims(width/ObjectDetector.resolution, height/ObjectDetector.resolution);
//            objectDetector2.setDims(width/ObjectDetector.resolution, height/ObjectDetector.resolution);
//            objectDetector3.setDims(width/ObjectDetector.resolution, height/ObjectDetector.resolution);
            System.out.println(width + "," + height);

            mCamera.setParameters(CameraParams.getParameters(mCamera));

            System.out.println("New");

            //comment out if you don't want live preview
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.e("Camera", "mCamera.setPreviewDisplay(holder);");
            }

            mCamera.setPreviewCallback(this);

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (this) {
            try {
                if (mCamera != null) {
                    mHolder.removeCallback(this);
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    isPreviewRunning  = false;
                    mCamera.release();
                }
            } catch (Exception e) {
                Log.e("Camera", e.getMessage());
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        System.out.println("Got Preview Data");
        if (!isPreviewRunning)
            return;

        Canvas canvas = null;

        if (mHolder == null) {
            return;
        }

        try {
            synchronized (mHolder) {
                yuvData = data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }

        long endTime = System.nanoTime();
        System.out.print("Time to get pixel data: ");
        System.out.println((endTime - startTime) / 1000000);
        startTime = System.nanoTime();
    }

    private class DrawView extends ViewClass{

        public DrawView() {}

        public void initMenu(){
            menu = new Menu((int) ViewContainer.densViewWidth - 50, 10);

            final NumberSlider n1 = new NumberSlider("Tolerance", 10, 90, 35, true, 30, 30, 100, 32);
            n1.addTouchListener(new TouchListener() {
                @Override
                void onTouch() {
                    ObjectDetector.tolerance = (int) n1.getValue();
                }
            });
            menu.addSlider(n1);

            final NumberSlider n2 = new NumberSlider("Resolution", 1, 16, 4, true, 30, 130, 100, 32);
            n2.addTouchListener(new TouchListener() {
                @Override
                void onTouch() {
                    ObjectDetector.resolution = (int) n2.getValue();
                }
            });
            menu.addSlider(n2);

            final NumberSlider n3 = new NumberSlider("Min Shape Dim", 1, 40, 4, true, 30, 230, 100, 32);
            n3.addTouchListener(new TouchListener() {
                @Override
                void onTouch() {
                    ObjectDetector.minShapeDim = (int) n3.getValue();
                }
            });
            menu.addSlider(n3);

            final NumberSlider n4 = new NumberSlider("Min Shape Dens", 0, 1, 0.5f, false, 200, 30, 100, 32);
            n4.addTouchListener(new TouchListener() {
                @Override
                void onTouch() {
                    ObjectDetector.minShapeDensity = n4.getValue();
                }
            });
            menu.addSlider(n4);

            final NumberSlider n5 = new NumberSlider("Shape Dens Check", 1, 16, 10, true, 200, 130, 100, 32);
            n5.addTouchListener(new TouchListener() {
                @Override
                void onTouch() {
                    ObjectDetector.shapeDensCheck = (int) n5.getValue();
                }
            });
            menu.addSlider(n5);

            final NumberSlider n6 = new NumberSlider("Zoom", 1, 30, 1, true, 380, 30, 100, 32);
            n6.addTouchListener(new TouchListener() {
                @Override
                void onTouch() {
                    setZoom((int) n6.getValue());
                }
            });
            menu.addSlider(n6);
        }

        @Override
        public void setupView(int thisInd, int curInd){
            if(curInd == 2 || curInd == 3) {
                initMenu();

                if(!isPreviewRunning)
                    mCamera.startPreview();
                isPreviewRunning = true;

                mainActivity.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                if(curInd == 3)
                    isFollow = true;
                else
                    isFollow = false;
                mainActivity.toastMessage("Is Following: "+isFollow);
            }
            else {
                mCamera.stopPreview();
                isPreviewRunning = false;
            }
        }

        @Override
        public void drawElements(Canvas canvas, Paint paint, float density) {

            System.out.println("Draw and Compute");
            long st = System.nanoTime();
            if(yuvData != null) {
                int res = ObjectDetector.resolution;
                int[] rgbints = YUVDecoder.decodeYUV(yuvData, width, height, res);
                yuvData = null;
                bmp = Bitmap.createScaledBitmap(Bitmap.createBitmap(rgbints, 0, width / ObjectDetector.resolution, width / ObjectDetector.resolution, height / ObjectDetector.resolution, Bitmap.Config.RGB_565),width,height,false);

                runObjectDetector(res, rgbints);

                sendData = true;
            }
            if(bmp != null)
                canvas.drawBitmap(bmp,0,0,null);

            drawObjectDetector(canvas);

            menu.drawElements(canvas, paint, density);

            sendBTMessage();

            paint.setColor(Color.BLACK);
            paint.setTextSize(24*density);
            canvas.drawText((isFollow ? "Follow" : "Foxtrot"), width+10, 100*density, paint);

            long et = System.nanoTime();
            System.out.println("Drawing and Computations completed in " + ((et - st) / 1000000) + "ms");
            //invalidate();
        }

        @Override
        public void touchEvent(MotionEvent event){
            if(menu == null || !menu.isMenuOpen())
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    getObjectDetectorForTouchEvent().touchDown(event);
                }

            if(menu != null && event.getAction() == MotionEvent.ACTION_DOWN)
                menu.touchEvent(event);
        }
    }

    public void runObjectDetector(int res, int[] rgbints){
        objectDetector1.setDims(width/res,height/res);
        objectDetector1.updateRoutine(rgbints);

        if(!isFollow) {
            objectDetector2.setDims(width / res, height / res);
            objectDetector2.updateRoutine(rgbints);

            objectDetector3.setDims(width / res, height / res);
            objectDetector3.updateRoutine(rgbints);
        }
    }

    public void drawObjectDetector(Canvas canvas){
        objectDetector1.drawObjectDetector(canvas);

        if(!isFollow) {
            objectDetector2.drawObjectDetector(canvas);
            objectDetector3.drawObjectDetector(canvas);
        }

    }

    public void sendBTMessage(){
        if(sendData) {
            int[] shapeInfo = getCenterX();
            try {
                MainActivity.btHandler.sendData((isFollow ? "9," : "8,") + shapeInfo[0] + "," + shapeInfo[1] + "," + width);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setZoom(int v){
        Camera.Parameters p = mCamera.getParameters();
        p.setZoom(v);
        mCamera.setParameters(p);
    }

    public int[] getCenterX(){
        sendData = false;
        ShapeRectangle[] bestRect = new ShapeRectangle[]{objectDetector1.getBestShapeRect(), objectDetector2.getBestShapeRect(), objectDetector3.getBestShapeRect()};
        int bestNum = 0;
        for(int i = 1; i < 3; i++){
            if(bestRect[bestNum] == null)
                bestNum = i;
            else if(bestRect[i] != null && bestRect[bestNum].getFitness() < bestRect[i].getFitness())
                bestNum = i;
        }
        if(bestRect[bestNum] != null)
            return new int[]{bestRect[bestNum].getCenterX(),bestNum};
        else
            return new int[]{-1,-1};
    }

    public ObjectDetector getObjectDetectorForTouchEvent(){
        if(isFollow)
            return objectDetector1;
        else {
            numSetting = numSetting % 3 + 1;
            mainActivity.toastMessage("Setting number " + numSetting);
            if (numSetting == 1)
                return objectDetector1;
            else if (numSetting == 2)
                return objectDetector2;
            else
                return objectDetector3;
        }
    }
}
