package dantech.com.aivdcontrol;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Danweb on 5/15/16.
 */
public class BluetoothHandler {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private volatile boolean stopWorker;
    private String fullMessage = "";
    private boolean findingBluetooth = false;
    private volatile boolean stopDeviceFinder = false;
    private MainActivity mainActivity;

    public BluetoothHandler(MainActivity activity){
        mainActivity = activity;
    }

    void findBT(){
        if(!findingBluetooth) {
            findingBluetooth = true;
            mmInputStream = null;
            mmOutputStream = null;
            mmDevice = null;
            mmSocket = null;
            stopWorker = true;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //System.out.println("No bluetooth adapter available");
                mainActivity.toastMessage("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mainActivity.startActivityForResult(enableBluetooth, 0);
            }

            final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            stopDeviceFinder = false;
            if (pairedDevices.size() > 0) {
                CharSequence devices[] = new CharSequence[pairedDevices.size()];
                int i = 0;
                for (BluetoothDevice device : pairedDevices) {
                    devices[i] = device.getName();
                    i++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("Pick a device");
                builder.setItems(devices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int k = 0;
                        for (BluetoothDevice device : pairedDevices) {
                            if (i == k) {
                                tryConnection(device);
                                break;
                            }
                            k++;
                        }

                    }
                });

                builder.show();
            }

        }

    }

    void tryConnection(BluetoothDevice tempDiv){
        final BluetoothDevice device = tempDiv;
        Thread openDeviceThread = new Thread(new Runnable() {
            public void run() {
                boolean success = false;
                for (int i = 0; i < 1; i++) {
                    mainActivity.toastMessage("Bluetooth try "+device.getName());
                    if(stopDeviceFinder) {
                        System.out.println("Stopping device finder");
                        break;
                    }
                    System.out.println(device.getName());
                    if (mmOutputStream != null) {
                        success = true;
                        break;
                    }
                    mmDevice = device;
                    try {
                        openBT();
                        //System.out.println("success here");
                        success = true;
                        break;
                    } catch (IOException e) {
                        //System.out.println("Failed");
                    }
                }
                if (!success)
                    mainActivity.toastMessage("Failed To Open The Device");
                else{}
                //System.out.println("Bluetooth Device Found");
                findingBluetooth = false;
            }
        });
        openDeviceThread.start();
    }

    void openBT() throws IOException{
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        System.out.println("Bluetooth Opened");
        mainActivity.toastMessage("Bluetooth Opened");

        if(mmOutputStream != null) {
            sendData("-1");
        }
    }

    void beginListenForData(){
        stopWorker = false;
        workerThread = new Thread(new Runnable(){
            public void run(){
                while(!Thread.currentThread().isInterrupted() && !stopWorker){
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0){
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++){
                                byte b = packetBytes[i];
                                //if(b != 10 && b != 13);
                                byte[] encodedBytes = new byte[1];
                                encodedBytes[0] = b;
                                String data = new String(encodedBytes, "US-ASCII");
                                if(!data.equals("*"))
                                    fullMessage+=data;
                                else{
                                    System.out.println("Full message"+fullMessage);
                                    mainActivity.getViewContainer().recievedBTMessage(fullMessage);
                                    fullMessage = "";
                                }
                                //}
                            }
                        }
                    }
                    catch (IOException ex) {stopWorker = true;}
                }
            }
        });
        workerThread.start();
    }

    public void sendData(String m) throws IOException{
        String msg = m;
        if(mmOutputStream != null)
            mmOutputStream.write(msg.getBytes());
        //System.out.println("Data Sent");
    }

    public void closeBT() throws IOException {
        stopWorker = true;
        if(mmOutputStream != null)
            mmOutputStream.close();
        if(mmInputStream != null)
            mmInputStream.close();
        if(mmSocket != null)
            mmSocket.close();
        System.out.println("BlueTooth Closed");
    }
}
