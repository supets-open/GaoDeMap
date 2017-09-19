package com.supets.gaodeng;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.location.demo.R;

public class LocationActivity extends BaseLocationActivity {


    Button mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStart = findViewById(R.id.start);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setEnabled(false);

                if ("开始定位".equals(mStart.getText().toString())) {
                    startLocation();
                    updateText(true);
                } else {
                    updateText(false);
                    stopLocation();
                    view.setEnabled(true);
                }
            }
        });
    }

    public void updateText(boolean start) {
        mStart.setText(start ? "结束定位" : "开始定位");
    }

    @Override
    protected void locationSuccess(AMapLocation location) {
        TextView result = findViewById(R.id.result);

        if (null != location && (location.getErrorCode() == 0)) {
            result.setText(Utils.getLocationStr(location));
            stopLocation();
        } else {
            result.setText("定位失败");
        }

        mStart.setEnabled(true);
        updateText(true);
    }
}
