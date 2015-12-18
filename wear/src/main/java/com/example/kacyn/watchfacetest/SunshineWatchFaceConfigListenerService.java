package com.example.kacyn.watchfacetest;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kacyn on 12/13/2015.
 */

public class SunshineWatchFaceConfigListenerService extends WearableListenerService implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private static final String DATA_ITEM_RECEIVED_PATH="/sunshine_watchface";

    @Override
    public void onCreate(){
        super.onCreate();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents){

        final List<DataEvent>events=FreezableUtils.freezeIterable(dataEvents);

        if(mGoogleApiClient==null){
            mGoogleApiClient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(Wearable.API).build();
        }

        if(!mGoogleApiClient.isConnected()){
            ConnectionResult connectionResult=
            mGoogleApiClient.blockingConnect(30,TimeUnit.SECONDS);

            if(!connectionResult.isSuccess()){
                return;
            }
        }

        // Loop through the events and send a message
        // to the node that created the data item.
        for(DataEvent event:events) {

            DataMap dm = DataMap.fromByteArray(event.getDataItem().getData());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(getString(R.string.pref_weather_icon_key), dm.getString(getString(R.string.pref_weather_icon_key), ""));
            editor.putString(getString(R.string.pref_high_temp_key), dm.getString(getString(R.string.pref_high_temp_key), "-"));
            editor.putString(getString(R.string.pref_low_temp_key), dm.getString(getString(R.string.pref_low_temp_key), "-"));
            editor.apply();

            Uri uri = event.getDataItem().getUri();

            // Get the node id from the host value of the URI
            String nodeId = uri.getHost();
            // Set the data of the message to be the bytes of the URI
            byte[] payload = uri.toString().getBytes();

            // Send the RPC
            Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH, payload);
        }
    }

    @Override
    public void onConnected(Bundle bundle){
    }

    @Override
    public void onConnectionSuspended(int i){
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
