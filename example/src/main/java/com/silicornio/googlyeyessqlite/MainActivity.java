package com.silicornio.googlyeyessqlite;

import android.app.Activity;
import android.os.Bundle;

import com.silicornio.geormlite.general.GEL;
import com.silicornio.googlyeyessqlite.model.Item;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        GEL.showLogs = true;
        DBManager dbManager = DBManager.getInstance(this);
        dbManager.getFirst(Item.class);

    }

}
