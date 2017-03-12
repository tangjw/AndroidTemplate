package com.tjw.template.swipeback;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.tjw.template.R;
import com.tjw.template.bottomnav.BaseFragment;
import com.tjw.template.swipeback.main.MainFragment1;
import com.tjw.template.swipeback.main.MainFragment2;
import com.tjw.template.swipeback.main.MainFragment3;
import com.tjw.template.widget.bottomnav.BottomNavItemView;
import com.tjw.template.widget.bottomnav.BottomNavView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, BottomNavView.OnNavItemSelectedListener{
    
    private BottomNavView mBottomNav;
    private ViewPager mViewPager;
    
    private BottomNavItemView mMenuPhone;
    private BottomNavItemView mMenuPeople;
    private BottomNavItemView mMenuMine;
    private List<BaseFragment> mMainFragments;
    
    @Override
    protected void beforeSuperOnCreate() {
        super.beforeSuperOnCreate();
    }
    
    
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    
    
        mViewPager = (ViewPager) findViewById(R.id.container);
        initViewPager();
        mBottomNav = (BottomNavView) findViewById(R.id.bottomNav);
        initBottomNav();
    }
    
    private void initBottomNav() {
        mMenuPhone = (BottomNavItemView) mBottomNav.findViewById(R.id.menu_phone);
        mMenuPeople = (BottomNavItemView) mBottomNav.findViewById(R.id.menu_people);
        mMenuMine = (BottomNavItemView) mBottomNav.findViewById(R.id.menu_mine);
        mBottomNav.setOnNavItemSelectedListener(this);
    }
    
    private void initViewPager() {
        mMainFragments = new ArrayList<>();
        mMainFragments.add(MainFragment1.newInstance());
        mMainFragments.add(MainFragment2.newInstance());
        mMainFragments.add(MainFragment3.newInstance());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
    }
    
    @Override
    protected void setListener() {
    }
    
    @Override
    protected void loadData() {
        
    }
    
    @Override
    public void onClick(View v) {
    
    }
    
    @Override
    public boolean isSupportSwipeBack() {
        return false;
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
    
    
    
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position) {
            
            return mMainFragments.get(position);
        }
        
        @Override
        public int getCount() {
            // Show 3 total pages.
            return mMainFragments.size();
        }
    }
}
