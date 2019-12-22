package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Add_note extends AppCompatActivity {
private MyDatabaseHelper dbHelper=new MyDatabaseHelper(this,"C.db",null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Button ok=(Button)findViewById(R.id.add_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText titleText=(EditText)findViewById(R.id.add_title);
                final EditText contentText=(EditText)findViewById(R.id.add_content);
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                db.execSQL("insert into C (title,content) values(?,?)",new String[]{titleText.getText().toString(),contentText.getText().toString()});
                Toast.makeText(Add_note.this,titleText.getText().toString()+"添加成功",Toast.LENGTH_SHORT).show();
                db.close();
                dbHelper.close();
            }
        });
    }
}
