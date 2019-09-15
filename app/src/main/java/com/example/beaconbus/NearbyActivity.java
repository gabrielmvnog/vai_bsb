package com.example.beaconbus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class NearbyActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager beaconManager;
    protected static final String TAG = "BeaconsActivity";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("NearbyBeacons", null, null, null);

        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for(Beacon oneBeacon : beacons) {
                    if (oneBeacon.getDistance() < 0.3){

                        try {
                            beaconManager.stopMonitoringBeaconsInRegion(region);
                            beaconManager.unbind(NearbyActivity.this);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(NearbyActivity.this);

                        builder.setTitle("Pagamento");
                        builder.setMessage("Você aceita realizar o pagamento?");

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                RequestQueue queue = Volley.newRequestQueue(NearbyActivity.this);
                                String url ="http://ec2-18-191-91-33.us-east-2.compute.amazonaws.com:5000/api/pay_bus";
                                JSONObject jsonBody = new JSONObject();

                                try {
                                    jsonBody.put("payload", "1");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                final String requestBody = jsonBody.toString();

                                // Request a string response from the provided URL.
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Intent intent = new Intent(NearbyActivity.this, MainActivity.class);
                                                startActivity(intent);

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("VOLLEY", error.toString());
                                    }
                                }) {
                                    @Override
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }

                                    @Override
                                    public byte[] getBody() throws AuthFailureError {
                                        try {
                                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                                        } catch (UnsupportedEncodingException uee) {
                                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                            return null;
                                        }
                                    }

                                    @Override
                                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                        String responseString = "";
                                        if (response != null) {
                                            responseString = String.valueOf(response.statusCode);
                                            // can get more details such as response.headers
                                        }
                                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                                    }
                                };

                                // Add the request to the RequestQueue.
                                queue.add(stringRequest);

                            }
                        });

                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(NearbyActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });


        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}
