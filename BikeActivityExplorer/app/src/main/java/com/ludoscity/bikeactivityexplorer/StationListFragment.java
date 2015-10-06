package com.ludoscity.bikeactivityexplorer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStationListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public static final String STATION_LIST_ITEM_CLICK_PATH = "station_list_item_click";
    public static final String STATION_LIST_ITEM_CLICK_STATION_NAME_PARAM = "station_list_item_click_station_name_param";
    public static final String STATION_LIST_ITEM_CLICK_STATION_POS_LAT_PARAM = "station_list_item_click_station_pos_lat_param";
    public static final String STATION_LIST_ITEM_CLICK_STATION_POS_LNG_PARAM = "station_list_item_click_station_pos_lng_param";

    public static final String STATION_LIST_FRAG_ONRESUME_PATH = "station_list_frag_onresume";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ///////////////////////////////////////////////////
    private StationListViewAdapter mStationListViewAdapter;
    private TextView mBikesOrParkingColumn;
    private ListView mStationListView;

    private OnStationListFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StationListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StationListFragment newInstance(String param1, String param2) {
        StationListFragment fragment = new StationListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView =  inflater.inflate(R.layout.fragment_station_list, container, false);
        mStationListView = (ListView) inflatedView.findViewById(com.ludoscity.bikeactivityexplorer.R.id.stationListView);
        mStationListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mStationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri.Builder builder = new Uri.Builder();

                builder.appendPath(STATION_LIST_ITEM_CLICK_PATH);

                StationItem station = ((StationItem) adapterView.getAdapter().getItem(i));

                builder.appendQueryParameter(STATION_LIST_ITEM_CLICK_STATION_NAME_PARAM,
                        station.getName());

                builder.appendQueryParameter(STATION_LIST_ITEM_CLICK_STATION_POS_LAT_PARAM,
                        String.valueOf(station.getPosition().latitude));
                builder.appendQueryParameter(STATION_LIST_ITEM_CLICK_STATION_POS_LNG_PARAM,
                        String.valueOf(station.getPosition().longitude));

                if (mListener != null) {
                    mListener.onStationListFragmentInteraction(builder.build());
                }
            }
        });

        mBikesOrParkingColumn = (TextView) inflatedView.findViewById(com.ludoscity.bikeactivityexplorer.R.id.bikesOrParkingColumn);

        return inflatedView;
    }

    @Override
    public void onResume(){

        super.onResume();

        Uri.Builder builder = new Uri.Builder();
        builder.appendPath(STATION_LIST_FRAG_ONRESUME_PATH);

        if (mListener != null){
            mListener.onStationListFragmentInteraction(builder.build());
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStationListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setupUI(StationsNetwork stationsNetwork, LatLng currentUserLatLng, boolean lookingForBike) {

        if (stationsNetwork != null) {
            mStationListViewAdapter = new StationListViewAdapter(getActivity().getApplicationContext(), stationsNetwork, currentUserLatLng, true);
            mStationListView.setAdapter(mStationListViewAdapter);
            lookingForBikes(lookingForBike);
        }
    }

    public void setCurrentUserLatLng(LatLng currentUserLatLng) {
        if (null != mStationListViewAdapter)
            mStationListViewAdapter.setCurrentUserLatLng(currentUserLatLng);
    }

    public void highlightStationFromName(String stationName) {

        int i = mStationListViewAdapter.getPositionInList(stationName);
        mStationListView.setItemChecked(i, true);

        mStationListView.smoothScrollToPositionFromTop(i, 0, 300);
    }

    public StationItem getHighligthedStation(){

        StationItem toReturn = null;

        if (mStationListView.getCheckedItemPosition() != AdapterView.INVALID_POSITION){
            toReturn = (StationItem) mStationListViewAdapter.getItem(mStationListView.getCheckedItemPosition());
        }

        return toReturn;
    }

    public void lookingForBikes(boolean lookingForBike) {

        mStationListViewAdapter.lookingForBikesNotify(lookingForBike);

        if (lookingForBike)
            mBikesOrParkingColumn.setText(R.string.bikes);
        else
            mBikesOrParkingColumn.setText(R.string.parking);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStationListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onStationListFragmentInteraction(Uri uri);
    }

}
