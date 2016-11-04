package com.anbillon.nougatloading;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.anbillon.nougatboot.NougatBootDrawable;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NougatBootDrawable drawable = new NougatBootDrawable();
        ImageView a = (ImageView)findViewById(R.id.imageView);
        ToggleButton t = (ToggleButton)findViewById(R.id.toggle);
        a.setImageDrawable(drawable);
        t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (drawable.isRunning())
                        drawable.resume();
                    else drawable.start();
                }else drawable.pause();
            }
        });

    }
}
