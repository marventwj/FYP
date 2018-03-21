package com.marven.fyp.memorytraining;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public abstract class BaseActivity extends BlunoLibrary {

    Intent i;

    Intent music;
    HomeWatcher mHomeWatcher;
    private boolean mIsBound = false;
    private boolean serviceConnected = false;
    protected MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
            serviceConnected = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    MusicService getMusicService(){
        return mServ;
    }

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //service for bluetooth
        onCreateProcess();														//onCreate Process by BlunoLibrary
        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        //service for music

        if (DataHolder.getBackgroundMusicStatus()) {
            doBindService();
            music = new Intent();
            music.setClass(this, MusicService.class);
            startService(music);
        }

        //when user presses home button
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                mServ.pauseMusic();
                System.out.println("home pressed");
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();


    }

    protected void onResume(){
        super.onResume();
        onResumeProcess(); //onResume Process by BlunoLibrary

        if (DataHolder.getBackgroundMusicStatus()) {
            if (serviceConnected)
                mServ.resumeMusic();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {     //close app if refuses bluetooth?
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        System.out.println("BaseActivity ON PAUSE!  @#!@#@!#@!#");
        super.onPause();
        //onPauseProcess();
        //mServ.pauseMusic();
    }

    protected void onStop() {
        System.out.println("BaseActivity ON STOP!  @#!@#!#!#!@#!@");
        super.onStop();
        //mServ.stopMusic();
        //onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        System.out.println("BaseActivity ON DESTROY!  @#!#@#!@#!#!@");
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
        mHomeWatcher.stopWatch();
        doUnbindService();
        stopService(music);
    }


    @Override
    public void onBackPressed() {
        System.out.println("BaseActivity ON BACKPRESS  !@#!#@#!@#!#!@");
        super.onBackPressed();
        //moveTaskToBack(true);
    }


    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
//        switch (theConnectionState) {											//Four connection state
//            case isConnected:
//                progress.dismiss();
//                Toast.makeText(this, "Connection Successful!",
//                        Toast.LENGTH_LONG).show();
//                DataHolder.setBluetoothConnected(true);
//                break;
//            case isToScan:
//                Log.e("hi", "Connection CHANGED TO IS-TO-SCAN");
//                buttonScanOnClickProcess();                                        //Connect to board process
//             //   progress = new ProgressDialog(BaseActivity.this);
//                progress.setMessage("Connecting To The Board...");
//                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//                progress.show();
////                progress.dismiss();
//                break;
////            case isConnecting:
////                buttonConnectBluetooth.setText("Connecting");
////                break;
////            case isScanning:
////                buttonConnectBluetooth.setText("Scanning");
////                break;
////            case isDisconnecting:
////                buttonConnectBluetooth.setText("isDisconnecting");
////                break;
//            default:
//                break;
//        }
    }

    @Override
    public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
    }

}