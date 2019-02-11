package com.example.shanm.mobioticstask.mydatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {

    Context context;
    public static final int VERSION = 1;
    public static final String DBNAME= "MYDATABASE";
    public static final String TABLENAME= "MYTABLE";
    public static final String COL1= "id";
    public static final String SEEkTime= "time";


    public MyDatabase(Context context) {
        super(context, DBNAME, null, VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String qry= "create table "+TABLENAME +"(" +COL1+" text primary key," +SEEkTime+ " text)";
        sqLiteDatabase.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
