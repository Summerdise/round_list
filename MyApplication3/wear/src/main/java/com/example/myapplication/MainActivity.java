package com.example.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WearableActivity {

    private WearableRecyclerView recyclerView;
    private List<Data> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_launcher_view);

        // Enables Always-on
        setAmbientEnabled();
        dataList.add(new Data(R.drawable.music_online, "在线音乐"));
        dataList.add(new Data(R.drawable.music_base, "music"));
        dataList.add(new Data(R.drawable.music_qq, "QQ 音乐"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        dataList.add(new Data(R.drawable.camera, "camera"));
        Adapter adapter = new Adapter(dataList, this);

        //环形列表设置
        recyclerView.setEdgeItemsCenteringEnabled(true);
        //环形显示的layoutManager设置
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this,customScrollingLayoutCallback));

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(MainActivity.this,OnlineMediaActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this,MediaPlayerActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        String PACKAGE_NAME="com.tencent.qqmusicwatch";
                        String CLASS_NAME="com.tencent.qqmusicwatch.ui.PlayMainActivity";
                        ComponentName cn = new ComponentName(PACKAGE_NAME, CLASS_NAME);
                        intent.setComponent(cn);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);

        //环形手势 允许环形滑动
        recyclerView.setCircularScrollingGestureEnabled(true);
        recyclerView.setBezelFraction(0.5f);
        recyclerView.setScrollDegreesPerScreen(90);
    }
}

class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
    /** How much should we scale the icon at most. */
    private static final float MAX_ICON_PROGRESS = 0.65f;

    private float progressToCenter;

    @Override
    public void onLayoutFinished(View child, RecyclerView parent) {

        // Figure out % progress from top to bottom
        float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
        float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

        // Normalize for center
        progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
        // Adjust to the maximum scale
        progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

        child.setScaleX(1 - progressToCenter);
        child.setScaleY(1 - progressToCenter);
    }
}
