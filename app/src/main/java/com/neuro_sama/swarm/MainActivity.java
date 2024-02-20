package com.neuro_sama.swarm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragmentslist;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    TextView textView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);

        fragmentslist = new ArrayList<Fragment>();
        fragmentslist.add(new Swarm1());
        fragmentslist.add(new Swarm1());
        fragmentslist.add(new Swarm1());

        /* viewpager2 adapter */
        FragmentStateAdapter adapter = new FragmentStateAdapter(MainActivity.this) {
            @Override
            public int getItemCount() {
                return fragmentslist.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentslist.get(position);
            }
        };

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    //tab.setCustomView(R.layout.iconfont_view);
                    tab.setText("Tab 1");
                    break;
                case 1:
                    tab.setText("Tab 2");
                    break;
                case 2:
                    tab.setText("Tab 3");
                    break;
            }
            //设置tab样式
        }).attach();//将tablayout与viewpager2绑定


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        textView = fragmentslist.get(0).getView().findViewById(R.id.hello);
//        textView.setText("hello");
//    }
}