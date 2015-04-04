package com.udem.ift2906.bixitracksexplorer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.udem.ift2906.bixitracksexplorer.BixiAPI.BixiAPI;


public class NearbyFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap nearbyMap;
    private BixiAPI bixiApiInstance;

    private Context mContext;
    private OnFragmentInteractionListener mListener;

    private String testText;
    private TextView textView;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static NearbyFragment newInstance(int sectionNumber){
        NearbyFragment fragment = new NearbyFragment();
        Bundle args= new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        //TODO string
        Toast.makeText(mContext, "Trying download...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View inflatedView = layoutInflater.inflate(R.layout.fragment_nearby, viewGroup, false);

        ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapNearby)).getMapAsync(this);
        textView = (TextView) inflatedView.findViewById(R.id.textView);
        return inflatedView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nearbyMap = googleMap;

        nearbyMap.setMyLocationEnabled(true);
        nearbyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.5086699,-73.5539925),10));
        new DownloadWebTask().execute();
    }

    //Pour interaction avec mainActivity
    public interface OnFragmentInteractionListener {
        public void onNearbyFragmentInteraction();
    }


    public class DownloadWebTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            bixiApiInstance= new BixiAPI(mContext);
            testText = bixiApiInstance.getJSonDataFromSharedPref();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textView.setText(testText);
            //TODO R.string
            Toast.makeText(mContext, "Download Successful!", Toast.LENGTH_SHORT).show();

            bixiApiInstance.getBixiNetwork().network.setUpMarkers();
            bixiApiInstance.getBixiNetwork().network.addMarkersToMap(nearbyMap);
        }
    }

    public Context getContext(){return mContext;}
}