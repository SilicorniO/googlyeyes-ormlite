package com.silicornio.googlyeyessqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.silicornio.geormlite.GeOrmLiteManager;
import com.silicornio.googlyeyessqlite.model.Item;

public class DBManager extends GeOrmLiteManager {

    public static final String DATABASE_NAME = "GeOrmLiteExample";
    private static final int DATABASE_VERSION = 1;

    /** Instance of DBManager **/
    private static DBManager mInstance;

    private DBManager(Context context){
        super(context);
    }

    public static DBManager getInstance(Context context){
        if(mInstance==null){
            mInstance = new DBManager(context);
        }
        return mInstance;
    }

    @Override
    public Class[] getClasses() {
        return new Class[]{
                Item.class
        };
    }

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    @Override
    public void onGeUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
