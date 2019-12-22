package com.example.note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_NOTE="create table C("
            +"id integer primary key autoincrement,"
            +"title text,"
            +"content text,"
            +"photoURI text,"
            +"imageURI text)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext=context;
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_NOTE);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_LONG).show();
    }
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
    }
}
