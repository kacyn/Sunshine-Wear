package com.example.kacyn.watchfacetest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Kacyn on 12/12/2015.
 */
public class WatchFaceConfigActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void sendConfigUpdateMessage(int weatherIconRes, String highTemp, String lowTemp) {

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/sunshine_watchface");
        DataMap config = putDataMapRequest.getDataMap();

        config.putInt(getString(R.string.pref_weather_icon_key), weatherIconRes);
        config.putString(getString(R.string.pref_high_temp_key), highTemp);
        config.putString(getString(R.string.pref_low_temp_key), lowTemp);


//        config.putInt(getString(R.string.pref_weather_icon_key), mPrefs.getInt(getString(R.string.pref_weather_icon_key), 0));
//        config.putString(getString(R.string.pref_high_temp_key), mPrefs.getString(getString(R.string.pref_high_temp_key), "-"));
//        config.putString(getString(R.string.pref_low_temp_key), mPrefs.getString(getString(R.string.pref_low_temp_key), "-"));

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        //sendConfigUpdateMessage();
    }
}
