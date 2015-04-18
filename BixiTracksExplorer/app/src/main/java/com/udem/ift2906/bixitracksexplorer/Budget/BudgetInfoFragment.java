package com.udem.ift2906.bixitracksexplorer.Budget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.udem.ift2906.bixitracksexplorer.R;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class BudgetInfoFragment extends ListFragment {

    private static final String ARG_TIMEPERIOD = "timePeriod";
    private static final String ARG_INFOTYPE = "infoType";
    private static final String ARG_ITEMLIST = "itemList";
    public static final String BUDGETINFOITEM_SORT_CHANGED_PATH = "budgetinfoitem_sort_changed";
    public static final String SORT_CHANGED_SUBTITLE_PARAM = "new_sort_subtitle";
    public static final String BUDGETINFOITEM_CLICK_PATH = "budgetinfoitem_click";
    public static final String CLICK_TRACKID_PARAM = "track_id";

    private String mInfoType;
    private String mTimePeriod;

    private boolean mSortOrderHighToLow = true;
    private int mCurrentSortCriteria = BudgetInfoListViewAdapter.SORT_CRITERIA_COST;

    private ArrayList<BudgetInfoItem> mBudgetInfoItems = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    private ImageView mAnimatedrowBitmapHolderImageView;

    public static BudgetInfoFragment newInstance(String infoType, String timePeriod, ArrayList<BudgetInfoItem> itemList) {
        BudgetInfoFragment fragment = new BudgetInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INFOTYPE, infoType);
        args.putString(ARG_TIMEPERIOD, timePeriod);
        args.putParcelableArrayList(ARG_ITEMLIST, itemList);
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BudgetInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mInfoType = getArguments().getString(ARG_INFOTYPE);
            mTimePeriod = getArguments().getString(ARG_TIMEPERIOD);
            mBudgetInfoItems = getArguments().getParcelableArrayList(ARG_ITEMLIST);
        }


        setListAdapter(new BudgetInfoListViewAdapter(getActivity(), mBudgetInfoItems));
        // TODO: Change Adapter to display your content
        //setListAdapter(new ArrayAdapter<>(getActivity(),
        //        android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case android.R.id.home:
                //called when the up affordance/carat in actionbar is pressed
                getActivity().onBackPressed();
                return true;

            case R.id.budgetInfoSortOrder:

                ((BudgetInfoListViewAdapter)getListAdapter()).reverseSortOrderAndNotify();
                if(mSortOrderHighToLow){
                    item.setIcon(R.drawable.ic_action_sort_low_to_high);
                }
                else{
                    item.setIcon(R.drawable.ic_action_sort_high_to_low);
                }
                mSortOrderHighToLow = !mSortOrderHighToLow;
                return true;

            case R.id.budgetInfoSortCriteria:
                if (mCurrentSortCriteria == BudgetInfoListViewAdapter.SORT_CRITERIA_COST){
                    item.setIcon(R.drawable.ic_action_duration);
                    mCurrentSortCriteria = BudgetInfoListViewAdapter.SORT_CRITERIA_DURATION;
                    ((BudgetInfoListViewAdapter)getListAdapter()).sortTracksByDurationAndNotify(mSortOrderHighToLow);
                }
                else if (mCurrentSortCriteria == BudgetInfoListViewAdapter.SORT_CRITERIA_DURATION){
                    item.setIcon(R.drawable.ic_action_date);
                    mCurrentSortCriteria = BudgetInfoListViewAdapter.SORT_CRITERIA_DATE;
                    ((BudgetInfoListViewAdapter)getListAdapter()).sortTracksByDateAndNotify(mSortOrderHighToLow);
                }
                else if (mCurrentSortCriteria == BudgetInfoListViewAdapter.SORT_CRITERIA_DATE){
                    item.setIcon(R.drawable.ic_action_cost);
                    mCurrentSortCriteria = BudgetInfoListViewAdapter.SORT_CRITERIA_COST;
                    ((BudgetInfoListViewAdapter)getListAdapter()).sortTracksByCostAndNotify(mSortOrderHighToLow);
                }

                notifySortCriteriaChangeToActivity();
                return true;

        }

        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_budget_info, menu);

        MenuItem tmpItem = menu.findItem(R.id.budgetInfoSortOrder);
        if(mSortOrderHighToLow){
            tmpItem.setIcon(R.drawable.ic_action_sort_high_to_low);
        }
        else {
            tmpItem.setIcon(R.drawable.ic_action_sort_low_to_high);
        }

        tmpItem = menu.findItem(R.id.budgetInfoSortCriteria);

        switch (mCurrentSortCriteria){
            case BudgetInfoListViewAdapter.SORT_CRITERIA_COST:
                tmpItem.setIcon(R.drawable.ic_action_cost);
                break;
            case BudgetInfoListViewAdapter.SORT_CRITERIA_DURATION:
                tmpItem.setIcon(R.drawable.ic_action_duration);
                break;
            case BudgetInfoListViewAdapter.SORT_CRITERIA_DATE:
                tmpItem.setIcon(R.drawable.ic_action_date);
                break;
        }

        notifySortCriteriaChangeToActivity();
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        // update the actionbar to show the up carat/affordance
        ((ActionBarActivity)activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        notifySortCriteriaChangeToActivity();

        mAnimatedrowBitmapHolderImageView = (ImageView)activity.findViewById(R.id.animatedrow_bitmap_holder_imageview);
    }

    private void notifySortCriteriaChangeToActivity(){
        Uri.Builder builder = new Uri.Builder();
        builder.appendPath(BUDGETINFOITEM_SORT_CHANGED_PATH);
        String subtitle = mTimePeriod + " - ";

        switch (mCurrentSortCriteria){
            case BudgetInfoListViewAdapter.SORT_CRITERIA_COST:
                subtitle += getString(R.string.budgetinfo_subtitle_by_cost);
                break;
            case BudgetInfoListViewAdapter.SORT_CRITERIA_DURATION:
                subtitle += getString(R.string.budgetinfo_subtitle_by_duration);
                break;
            case BudgetInfoListViewAdapter.SORT_CRITERIA_DATE:
                subtitle += getString(R.string.budgetinfo_subtitle_by_date);
                break;
        }

        builder.appendQueryParameter(SORT_CHANGED_SUBTITLE_PARAM, subtitle);

        if (mListener != null){
            mListener.onBudgetInfoFragmentInteraction(builder.build(), null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mAnimatedrowBitmapHolderImageView = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        getListView().setItemChecked(position, true);

        //Let's animate !!
        //Render the View representing the clicked row in a Bitmap
        //BitmapFactory.decodeResource(R.layout.budgetinfolist_item);
        Bitmap viewCapture;

        v.setDrawingCacheEnabled(true);
        viewCapture = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        Pair<Integer,Integer> targetAbsoluteXY = calculateRowAnimTarget(v);

        mAnimatedrowBitmapHolderImageView.setImageBitmap(viewCapture);

        //... set initial position...
        mAnimatedrowBitmapHolderImageView.setX(v.getX());
        mAnimatedrowBitmapHolderImageView.setY(v.getY());

        //Setup animation
        mAnimatedrowBitmapHolderImageView.animate().x(targetAbsoluteXY.first)
                .y(targetAbsoluteXY.second)
                .setDuration(1000)
                .setInterpolator(new LinearInterpolator());

        //Make Bitmap holder ImageView visible
        mAnimatedrowBitmapHolderImageView.setVisibility(View.VISIBLE);

        //Animate !
        mAnimatedrowBitmapHolderImageView.animate().start();

        if (mListener != null){
            Uri.Builder builder = new Uri.Builder();

            builder.appendPath(BUDGETINFOITEM_CLICK_PATH)
                    .appendQueryParameter(CLICK_TRACKID_PARAM, mBudgetInfoItems.get(position).getIDAsString());

            //Bitmap implements Parcelable. We pass it around to BudgetTrackDetailsFragment
            mListener.onBudgetInfoFragmentInteraction(builder.build(), viewCapture);
        }
    }

    //Return X and Y of row line View target position on screen
    private Pair<Integer, Integer> calculateRowAnimTarget(View rowView){

        //calculate the position of the row as well as the target animation position (at bottom of screen)
        //matching the next fragment layout
        // find out where the clicked view sits in relationship to the
        // parent container

        //Retrieve data required for calculation
        //int rowTop = rowView.getTop() + getListView().getTop();
        //int rowLeft = rowView.getLeft() + getListView().getLeft();

        int listViewBottom = getListView().getBottom();
        int listViewRight = getListView().getRight();

        int YTarget = listViewBottom - rowView.getHeight();


        // First figure out if we're on a one of two fragments configuration
        FrameLayout endFragmentContainer = (FrameLayout)getActivity().findViewById(R.id.end_fragment_container);

        if (endFragmentContainer != null){
            //two fragments configuration
            return new Pair<>(listViewRight, YTarget);
        }
        else{

            return new Pair<>(0, YTarget);
            //int absoluteAnimationTargetPx = (listViewBottom - rowTop) - rowView.getHeight();

        }
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
    public interface OnFragmentInteractionListener {
        public void onBudgetInfoFragmentInteraction(Uri _uri, Bitmap _infoListRowBitmapRender);
    }

}
