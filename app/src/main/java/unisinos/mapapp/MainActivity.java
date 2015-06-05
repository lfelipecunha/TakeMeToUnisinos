package unisinos.mapapp;

import java.util.ArrayList;
import java.util.Locale;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    private Station currentStation;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(40, 96, 144)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.rgb(32, 77, 116)));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


        @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment;
            if (sectionNumber == 2) {
                fragment = new MapsFragment();
            } else {
                fragment = new PlaceholderFragment();
            }
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView;
            MainActivity activity = (MainActivity) getActivity();
            switch ((int)getArguments().get(ARG_SECTION_NUMBER)) {
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_train, container, false);
                    activity.loadSchedule(rootView, R.id.train_table, R.id.train_title, activity.currentStation);
                    break;
                case 4:
                    rootView = inflater.inflate(R.layout.fragment_bus, container, false);
                    activity.loadSchedule(rootView, R.id.bus_table, R.id.bus_title, new BusStation("BUS", 0, 0, 0, null));
                    break;
                case 1:
                default:
                    rootView = inflater.inflate(R.layout.fragment_start, container, false);
                    String array_spinner[] = {"Sector A", "Sector B", "Sector C", "Sector D",
                            "Sector E", "Sector F", "Sector G", "Sector H", "Sector I", "Sector J",
                            "Sector K", "Sector L", "Sector M"};
                    Spinner s = (Spinner) rootView.findViewById(R.id.spinner);
                    ArrayAdapter adapter = new ArrayAdapter(rootView.getContext(),
                            android.R.layout.simple_spinner_item, array_spinner);
                    s.setAdapter(adapter);
            }

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

        }
    }



    public void startNavigation(View v) {
        EventsManager.getInstance().start();
    }

    public void loadSchedule(View v, int tableId, int titleId, Station station) {
        Log.e("MAIN", "Table ID: "+ tableId + "Title ID: " + titleId);
        if (station == null) {
            Log.e("MAIN", "STATION IS NULL");
            return;
        }
        TableLayout tableLayout = (TableLayout)v.findViewById(tableId);
        TextView title = (TextView)v.findViewById(titleId);
        if (tableLayout == null) {
            Log.e("MAIN", "TABLE IS NULL");
            return;
        }

        if (title == null ) {
            Log.e("MAIN", "TITLE IS NULL");
            return;
        }
        title.setText("Schedule of " + station.getName() + " Station");
        ArrayList<MyTime> list = station.getTimeSchedule();
        int i = 0;
        TableRow aux = null;

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        params.weight = 1;

        for (MyTime t : list) {
            if (i%3==0) {
                if (aux != null) {
                    tableLayout.addView(aux);
                }
                aux = new TableRow(this);
            }
            TextView hour = new TextView(this);
            hour.setText(t.toString());
            hour.setLayoutParams(params);

            aux.addView(hour);
            i++;
        }
    }

    public void setStation(Station s) {
        currentStation = s;
    }


}
