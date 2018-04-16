package com.cmsc190.ics.uplbnb;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import android.widget.TextView;
import android.widget.Toast;

public class Establishment_Drilldown extends AppCompatActivity {
    Intent i;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment__drilldown);
        i = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_establishment__drilldown, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_establishment__drilldown, container, false);
        /*    TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            return rootView;
        }
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
            Establishment_Item e = (Establishment_Item)i.getSerializableExtra("establishment");;
            switch(position){
                case 0: //overview
                    if(e.getEstablishmentType() == 1){
                        Toast.makeText(getApplicationContext(),e.getEstablishmentName(),Toast.LENGTH_SHORT);
                        Apartment_Drilldown a1 = new Apartment_Drilldown();
                        Bundle args = new Bundle();
                        args.putString("id",e.getId());
                        a1.setArguments(args);
                        return a1;
                    }
                    else if(e.getEstablishmentType() == 0){
                        Dormitory_Drilldown d1 = new Dormitory_Drilldown();
                        Bundle args2 = new Bundle();
                        Toast.makeText(getApplicationContext(),e.getId(),Toast.LENGTH_SHORT);
                        args2.putString("id2",e.getId());
                        d1.setArguments(args2);
                        return d1;
                    }
                    return null;

                case 1: //units
                    Units_List u1 = new Units_List();
                    Bundle args3 = new Bundle();
                    args3.putString("establishmentId",e.getId());
                    args3.putInt("establishmentType",e.getEstablishmentType());
                    args3.putString("establishmentContact",e.getContactNumber1());
                    u1.setArguments(args3);
                    return u1;

                case 2:
                    Reviews_List r1 = new Reviews_List();
                    Bundle args4 = new Bundle();
                    args4.putString("establishmentId",e.getId());
                    args4.putString("establishmentName",e.getEstablishmentName());
                    args4.putInt("establishmentType",e.getEstablishmentType());
                    args4.putFloat("establishmentRating",e.getRating());
                    r1.setArguments(args4);
                    return r1;
                case 3:
                    Photo_List p1 = new Photo_List();
                    Bundle args5 = new Bundle();
                    args5.putString("establishmentId",e.getId());
                    p1.setArguments(args5);
                    return p1;

            }
            return null;
        }

        @Override
        public int getCount() {

            // Show 3 total pages.
            return 4;
        }


    }
}
