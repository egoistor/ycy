package com.example.ycy.activity;


import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;

import com.example.ycy.R;
import com.example.ycy.adapter.MyFragmentPagerAdapter;
import com.example.ycy.fragment.FirstFragment;
import com.example.ycy.fragment.SecondFragment;
import com.example.ycy.fragment.ThirdFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String[] titleText = new String[]{"个人", "社区", "我的"};
    int[] incons = new int[]{R.drawable.main_bottom_tab_1, R.drawable.main_bottom_tab_2, R.drawable.main_bottom_tab_3};
    private ArrayList<ImageView> tabImageViews = new ArrayList<>();
    private ArrayList<TextView> tabTextViews = new ArrayList<>();
    private TabLayout mTabLayout;
    private MyFragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private static final int tabSelectedColor = Color.parseColor("#FF4500");
    private static final int tabUnsSelectedColor = Color.parseColor("#000000");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    private void init() {
        Fragment[] fragments = {new FirstFragment(), new SecondFragment(), new ThirdFragment()};
        //初始化viewPager
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager = findViewById(R.id.content_viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private Handler handler = new Handler();
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                changeTabColor(i,v);
            }

            @Override
            public void onPageSelected(final int i) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeTabColor(i);
                    }
                },100);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setOffscreenPageLimit(3);


        //初始化底部导航
        initTab();

    }

    private void initTab() {
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < titleText.length; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
            TextView textView = v.findViewById(R.id.tab_text);
            ImageView imageView = v.findViewById(R.id.tab_icon);
            textView.setText(titleText[i]);
            imageView.setImageDrawable(getResources().getDrawable(incons[i]));
            tabImageViews.add(imageView);
            tabTextViews.add(textView);
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(v);
        }
    }

    private void changeTabColor(int index) {
        for (int i = 0; i < tabImageViews.size();i++){
            if(i == index){
                tabTextViews.get(i).setTextColor(tabSelectedColor);
                tabImageViews.get(i).setColorFilter(tabSelectedColor);
            }else {
                tabTextViews.get(i).setTextColor(tabUnsSelectedColor);
                tabImageViews.get(i).setColorFilter(tabUnsSelectedColor);
            }
        }
    }

    private void changeTabColor(int position, float positionOffset){
        ImageView imageViewFrom;
        TextView textViewFrom;
        ImageView imageViewTo;
        TextView textViewTo;
        int unselectedcolor = (int) argbEvaluator.evaluate(positionOffset, tabSelectedColor, tabUnsSelectedColor);
        int selectedcolor = (int) argbEvaluator.evaluate(positionOffset, tabUnsSelectedColor, tabSelectedColor);
        imageViewFrom = tabImageViews.get(position);
        textViewFrom = tabTextViews.get(position);
        if(position != tabImageViews.size() - 1){
            imageViewTo = tabImageViews.get((position + 1));
            textViewTo = tabTextViews.get((position + 1));
        }else {
            textViewTo = null;
            imageViewTo = null;
        }


        if (imageViewTo != null){
            imageViewTo.setColorFilter(selectedcolor);
        }
        if (textViewTo != null){
            textViewTo.setTextColor(selectedcolor);
        }
        if (imageViewFrom != null){
            imageViewFrom.setColorFilter(unselectedcolor);
        }
        if (textViewFrom != null){
            textViewFrom.setTextColor(unselectedcolor);
        }


//        Button button = findViewById(R.id.begin);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, EditActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
