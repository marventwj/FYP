package com.marven.fyp.memorytraining;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainPage extends BaseActivity {
    private Button buttonConnectBluetooth;
    private Button buttonSelectGame;
    ProgressDialog progress;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        i = new Intent (this, SelectGame.class);

        buttonSelectGame = (Button) findViewById(R.id.buttonSelectGame);
        buttonSelectGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (getConnectionState() == connectionStateEnum.isConnected)
                    startActivity(i);
                else
                    Toast.makeText(getBaseContext(), "Connect Bluetooth First!",
                            Toast.LENGTH_LONG).show();
            }
        });

        buttonConnectBluetooth = (Button) findViewById(R.id.buttonConnectBluetooth);					//initial the button for scanning the BLE device
        buttonConnectBluetooth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                buttonScanOnClickProcess();										//Connect to board process
                progress = new ProgressDialog(MainPage.this);
                progress.setMessage("Connecting To The Board...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

            }
        });
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                buttonConnectBluetooth.setText("Disconnect Board");
                progress.dismiss();
                Toast.makeText(this, "Connection Successful!",
                        Toast.LENGTH_LONG).show();
                break;
            case isToScan:
                buttonConnectBluetooth.setText("Connect To Board");
                progress.dismiss();
                break;
//            case isConnecting:
//                buttonConnectBluetooth.setText("Connecting");
//                break;
//            case isScanning:
//                buttonConnectBluetooth.setText("Scanning");
//                break;
//            case isDisconnecting:
//                buttonConnectBluetooth.setText("isDisconnecting");
//                break;
            default:
                break;
        }
    }

}