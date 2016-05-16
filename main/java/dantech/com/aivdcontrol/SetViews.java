package dantech.com.aivdcontrol;

/**
 * Created by Danweb on 5/15/16.
 */
public class SetViews {

    public static void setViews(ViewContainer view){
        ViewClass startView = new ViewClass();
        InitSensors.initSensors(startView);
        view.addView(startView);
        startView.addMenu(new Menu(10,(int)ViewContainer.densViewHeight-50));
    }
}
