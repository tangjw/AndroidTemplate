package com.tjw.template.bottomnav;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tjw.template.R;

public class BottomNavActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    
    
    private SectionsPagerAdapter mSectionsPagerAdapter;
    
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private BottomNavigationView mBottomNav;
    private BottomNavigationItemView mMenuPhone;
    private BottomNavigationItemView mMenuPeople;
    private BottomNavigationItemView mMenuMine;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        
        initViewPager();
        
        mBottomNav = (BottomNavigationView) findViewById(R.id.bottomNav);
        
        initBottomNav();
    
    
    }
    
    /**
     * 设置 ViewPager
     */
    private void initViewPager() {
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
    }
    
    /**
     * 设置BottomNavigationView
     */
    private void initBottomNav() {
        
        mMenuPhone = (BottomNavigationItemView) mBottomNav.findViewById(R.id.menu_phone);
        mMenuPeople = (BottomNavigationItemView) mBottomNav.findViewById(R.id.menu_people);
        mMenuMine = (BottomNavigationItemView) mBottomNav.findViewById(R.id.menu_mine);
        
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_phone:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.menu_people:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.menu_mine:
                        mViewPager.setCurrentItem(2, false);
                        break;
                }
                
                
                return true;
            }
        });
    
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuPeople.showRedPoint(true);
            }
        }, 5000L);
        
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
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        
    }
    
    @Override
    public void onPageSelected(int position) {
        mBottomNav.getMenu().getItem(position).setChecked(true);
    }
    
    @Override
    public void onPageScrollStateChanged(int state) {
        
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
            View rootView = inflater.inflate(R.layout.fragment_bottom_nav, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }
        
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
