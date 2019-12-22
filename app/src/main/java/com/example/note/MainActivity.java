package com.example.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private MyDatabaseHelper dbHelper;
private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String>titles=new ArrayList<>();
        dbHelper=new MyDatabaseHelper(this,"C.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("C",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String title=cursor.getString(cursor.getColumnIndex("title"));
                titles.add(title);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        final ArrayAdapter<String>adapter=new ArrayAdapter<String>(
                MainActivity.this,android.R.layout.simple_list_item_1,titles
        );
        final ListView listView=(ListView)findViewById(R.id.lis_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,show_details.class);
                title=(String)adapter.getItem(i);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                Intent intent=new Intent(MainActivity.this,Add_note.class);
                startActivity(intent);
                break;
            case R.id.web:
                Intent web=new Intent(MainActivity.this, com.example.note.web.class);
                startActivity(web);
                break;
            case R.id.help:
                Toast.makeText(this,"Somethings can help you",Toast.LENGTH_LONG).show();
                break;
            case R.id.quit:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
        return true;
    }
}
