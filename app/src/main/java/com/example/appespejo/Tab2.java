package com.example.appespejo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;

//import com.spotify.sdk.android.auth;


import androidx.fragment.app.Fragment;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class Tab2 extends Fragment {
    
    private static final String CLIENT_ID = "d6720cc30e3c48b283910c2040f81244";
    private static final String REDIRECT_URI = "SpotifyTestApp://authenticationResponse";
    private SpotifyAppRemote mSpotifyAppRemote;
    final int REQUEST_CODE = 1337;
    ImageView album, pause, back, next, play;


    public Tab2(){
        // require a empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab2, container, false);

        album = v.findViewById(R.id.album);
        pause = v.findViewById(R.id.pausePlay);
        back = v.findViewById(R.id.back);
        next = v.findViewById(R.id.next);
        play = v.findViewById(R.id.play);
        play.setVisibility(View.GONE);

        allClicks();

        // Request code will be used to verify if result comes from the login activity.
        // Can be set to any integer.


        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        builder.setShowDialog(true);

        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);

        return v;
    }

    private void allClicks() {


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyAppRemote.getPlayerApi().pause();
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyAppRemote.getPlayerApi().resume();
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // We will start writing our code here.

        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("Demo", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("Demo", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here

                        Log.d("Demo", "No se ha connectado con el spotify");
                    }
                });
    }


    private void connected() {
        // Then we will write some more code here.
        // Play a playlist

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("Demo", track.name + " by " + track.artist.name);

                        mSpotifyAppRemote.getImagesApi()
                                .getImage(track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(new CallResult.ResultCallback<Bitmap>(){

                                    @Override public void onResult(Bitmap bitmap) {
                                        album.setImageBitmap(bitmap);
                                    }
                                });



                    }
                    else{
                        Log.d("Demo", "No ha pillado el track");
                    }
                });
    }


    @Override
    public void onStop() {
        super.onStop();

//        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        mSpotifyAppRemote.getPlayerApi().pause();
        Log.d("Demo", "onStop");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        mSpotifyAppRemote.getPlayerApi().pause();
        Log.d("Demo", "onDestroy");

    }
}
