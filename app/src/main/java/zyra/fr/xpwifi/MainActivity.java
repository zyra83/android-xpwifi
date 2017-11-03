package zyra.fr.xpwifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {


    WifiManager wifi;
    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<ScanResult> results;

    String ITEM_KEY = "key";
    /**
     * Stocke les résultats
     */
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStatus = (TextView) findViewById(R.id.textStatus);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.list);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        this.adapter = new SimpleAdapter(MainActivity.this, arraylist, R.layout.row, new String[]{ITEM_KEY}, new int[]{R.id.list_value});
        lv.setAdapter(this.adapter);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                arraylist.clear();
                Log.i("JPP", "Résultats reçus");
                results = wifi.getScanResults();
                size = results.size();


                Toast.makeText(MainActivity.this, "Scanned...." + size, Toast.LENGTH_SHORT).show();


                try {
                    size = size - 1;
                    while (size >= 0) {
                        HashMap<String, String> item = new HashMap<String, String>();
                        //item.put(ITEM_KEY, results.get(size).SSID + " <> " + results.get(size).BSSID + " <> " + results.get(size).capabilities);
                        item.put(ITEM_KEY, results.get(size).SSID + " <> " + results.get(size).BSSID + " <> " + results.get(size).level);

                        arraylist.add(item);
                        size--;
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    Log.e("Suu", e.getMessage(), e);


                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("JPP", "JPP request permission results");
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void onClick(View view) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("JPP", "JPP version récente");
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        } else {
            Log.i("JPP", "JPP version pas récente");
            //do something, permission was previously granted; or legacy device
        }


        arraylist.clear();
        wifi.startScan();

        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();

    }


}
