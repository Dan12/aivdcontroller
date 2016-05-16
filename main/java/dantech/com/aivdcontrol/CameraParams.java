package dantech.com.aivdcontrol;

import android.graphics.ImageFormat;
import android.hardware.Camera;

/**
 * Created by Danweb on 5/16/16.
 */
public class CameraParams {

    public static Camera.Parameters getParameters(Camera mCamera){
        Camera.Parameters params = mCamera.getParameters();
        String sParams = params.flatten();
        System.out.println("camera flatten: " + sParams);
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        System.out.println("Supported Exposure Modes:" + mCameraParameters.get("exposure-mode-values"));
        System.out.println("Supported White Balance Modes:" + mCameraParameters.get("whitebalance-values"));
        String supportedIsoValues = params.get("iso-values");
        System.out.println("ISO Vals " + supportedIsoValues);

        params.setPreviewFormat(ImageFormat.NV21);
        int maxZoom = params.getMaxZoom();
        int zoom = 0;
        params.set("iso", "100");
        params.setWhiteBalance(params.WHITE_BALANCE_CLOUDY_DAYLIGHT);
        params.setSceneMode(params.SCENE_MODE_ACTION);
        params.setExposureCompensation(0);
        if (params.isAutoExposureLockSupported())
            params.setAutoExposureLock(true);
        if (params.isAutoWhiteBalanceLockSupported())
            params.setAutoWhiteBalanceLock(true);
        if (params.isZoomSupported()) {
            if (zoom >= 0 && zoom < maxZoom) {
                params.setZoom(zoom);
                System.out.println("Zoom Supported");
            } else {
                System.out.println("Error Zoom");
            }
        }

        return params;
    }
}
