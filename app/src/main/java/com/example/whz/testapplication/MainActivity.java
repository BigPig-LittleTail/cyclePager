package com.example.whz.testapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyBetterPager mMyPager;
    private ArrayList<View> test = new ArrayList<>();
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMyPager = findViewById(R.id.myViewPager);

        listView = new ListView(mMyPager.getContext());

        ArrayList<Integer> test1 = new ArrayList<>();
        for(int i = 0;i<20;i++){
            test1.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, test1);

        listView.setAdapter(adapter);

        View view0;
        View view1;
        View view2;
        View view3;
        View view4;
        view0 = new View(mMyPager.getContext());
        view0.setBackgroundColor(Color.RED);
        view1 = new View(mMyPager.getContext());
        view1.setBackgroundColor(Color.BLACK);
        view2 = new View(mMyPager.getContext());
        view2.setBackgroundColor(Color.BLUE);
        view3 = new View(mMyPager.getContext());
        view3.setBackgroundColor(Color.YELLOW);
        view4 = new View(mMyPager.getContext());
        view4.setBackgroundColor(Color.GREEN);
        test.add(view0);
        test.add(view1);
        test.add(listView);
        test.add(view2);
        test.add(view3);
        test.add(view4);


        mMyPager.setViewList(test);


        //myViewPager = findViewById(R.id.myViewPager);
    }
}

//public class MainActivity extends AppCompatActivity {
//    ViewPager mvpGoods;
//    ArrayList<Integer>mGoodsList;
//    ArrayList<ImageView> mivGoodsList;
//    GoodsAdapter mAdapter;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.testviewpager);
//        initView();
//    }
//    private void initView(){
//        //准备数据源
//        mGoodsList=new ArrayList<>();
//        mGoodsList.add(R.drawable.ic_launcher_background);
////        mGoodsList.add(R.drawable.ic_launcher_foreground);
////        mGoodsList.add(R.drawable.ic_launcher_background);
////        mGoodsList.add(R.drawable.ic_launcher_foreground);
//
//        mivGoodsList=new ArrayList<>();
//        for(int i=0;i<mGoodsList.size();i++){
//            ImageView iv=new ImageView(this);
//            iv.setImageResource(mGoodsList.get(i));
//            mivGoodsList.add(iv);
//        }
//        mAdapter=new GoodsAdapter(this,mivGoodsList);
//        mvpGoods= (ViewPager) findViewById(R.id.viewTest);
//        mvpGoods.setAdapter(mAdapter);
//    }
//    //准备适配器类
//    class GoodsAdapter extends PagerAdapter {
//        Context context;
//        ArrayList<ImageView> ivGoodsList;
//        public GoodsAdapter(Context context,ArrayList<ImageView>ivGoodsList){
//            this.context=context;
//            this.ivGoodsList=ivGoodsList;
//        }
//
//        @Override
//        public int getCount() {
//            return Integer.MAX_VALUE;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view==object;
//        }
//
//        @Override
//        public ImageView instantiateItem(ViewGroup container, int position) {
//            ImageView imageView=ivGoodsList.get(position%ivGoodsList.size());
//            container.addView(imageView);
//            return imageView;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            ImageView imageView=(ImageView)object;
//            container.removeView(imageView);
//        }
//    }
//}
