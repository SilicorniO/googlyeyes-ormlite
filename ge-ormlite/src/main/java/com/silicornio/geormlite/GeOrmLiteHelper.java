package com.silicornio.geormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.silicornio.geormlite.general.GEL;

public class GeOrmLiteHelper extends OrmLiteSqliteOpenHelper {

    /** Reference to manager **/
    private GeOrmLiteManager mManager;

    public GeOrmLiteHelper(Context context) {
        super(context, GeOrmLiteManager.databaseName, null, GeOrmLiteManager.databaseVersion);
    }

    public void setManager(GeOrmLiteManager mManager) {
        this.mManager = mManager;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            for(Class klass : GeOrmLiteManager.classes){
                TableUtils.createTable(connectionSource, klass);
            }

        } catch (Exception e) {
            GEL.e("Exception creating database: " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        mManager.onGeDbUpgrade(database, connectionSource, oldVersion, newVersion);
    }


}
