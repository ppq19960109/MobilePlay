package com.mobileplay.gen;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

public class App extends Application {

    private DaoSession daoSession;
    private Database db;
    @Override
    public void onCreate() {
        super.onCreate();

        // regular SQLite database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        db = helper.getWritableDb();

        // encrypted SQLCipher database
        // note: you need to add SQLCipher to your dependencies, check the build.gradle file
        // DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db-encrypted");
        // Database db = helper.getEncryptedWritableDb("encryption-key");

        daoSession = new DaoMaster(db).newSession();
    }
    public Database getDb(){
        return db;
    }
    public DaoSession getDaoSession() {
        return daoSession;
    }
}
