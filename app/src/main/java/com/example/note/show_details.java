package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class show_details extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private String Scontent;
    private String ititle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        Intent intent=getIntent();
        ititle=intent.getStringExtra("title");
        dbHelper=new MyDatabaseHelper(this,"C.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from C where title=?",new String[]{ititle});
        cursor.moveToFirst();
        String Stitle=cursor.getString(cursor.getColumnIndex("title"));
        Scontent=cursor.getString(cursor.getColumnIndex("content"));
        cursor.close();
        db.close();
        final TextView title=(TextView)findViewById(R.id.title);
        title.setText(Stitle);
        final TextView content=(TextView)findViewById(R.id.content);
        content.setText(Scontent);
        title.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("选择操作");
                contextMenu.add(0,0,0,"修改标题");
            }
        });
        //对content进行修改
        content.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("选择操作");
                contextMenu.add(0,1,0,"修改内容");
            }
        });
        photo();
        image();
    }
    //对title进行修改
    public boolean onContextItemSelected(MenuItem item){
        SQLiteDatabase db= dbHelper.getWritableDatabase();
        switch (item.getItemId()){
            case 0:
                AlertDialog.Builder t_dialog=new AlertDialog.Builder(this);
                final View edit_title= LayoutInflater.from(show_details.this).inflate(R.layout.edit_title,null);
                t_dialog.setTitle("修改标题");
                t_dialog.setView(edit_title);
                final EditText title_text=edit_title.findViewById(R.id.edit_title);
                dbHelper=new MyDatabaseHelper(this,"C.db",null,1);
                title_text.setText(ititle);
                t_dialog.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String f_title=title_text.getText().toString();
                        SQLiteDatabase db= dbHelper.getWritableDatabase();
                        db.execSQL("update C set title=? where title=?",new String[]{f_title,ititle});
                        db.close();
                        Toast.makeText(show_details.this,"修改成功",Toast.LENGTH_SHORT).show();
                    }
                });
                t_dialog.setNegativeButton("取消",null);
                t_dialog.show();
                return true;
            //对内容进行修改
            case 1:
                AlertDialog.Builder c_dialog=new AlertDialog.Builder(this);
                final View edit_content=LayoutInflater.from(show_details.this).inflate(R.layout.edit_content,null);
                c_dialog.setTitle("修改内容");
                c_dialog.setView(edit_content);
                final EditText content_text=edit_content.findViewById(R.id.edit_content);
               content_text.setText(Scontent);
                c_dialog.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String f_content=content_text.getText().toString();
                        SQLiteDatabase db= dbHelper.getWritableDatabase();
                        db.execSQL("update C set content=? where content=?",new String[]{f_content,Scontent});
                        db.close();
                        Toast.makeText(show_details.this,"修改成功",Toast.LENGTH_SHORT).show();
                    }
                });
                c_dialog.setNegativeButton("取消",null);
                c_dialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private ImageView show_photo;
    private Button BtnTakePhoto;
    private Button save_photo;
    private Button show_photos;
    private ImageView show_photo1;
    private ImageView show_photo2;
    private ImageView show_photo3;
    private ImageView show_photo4;
    private ImageView show_photo5;
    private static final int CAMERA_REQUEST_CODE = 1;
    Uri imageUri;
    Bitmap bm;
    private void photo(){
        show_photo=(ImageView)findViewById(R.id.show_photo);
        BtnTakePhoto=(Button)findViewById(R.id.btn_take_photo);
        save_photo=(Button)findViewById(R.id.save_photo);
        show_photos=(Button)findViewById(R.id.show_photos);
        show_photo1=(ImageView)findViewById(R.id.show_photo1);
        show_photo2=(ImageView)findViewById(R.id.show_photo2);
        show_photo3=(ImageView)findViewById(R.id.show_photo3);
        show_photo4=(ImageView)findViewById(R.id.show_photo4);
        show_photo5=(ImageView)findViewById(R.id.show_photo5);
        BtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photo_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photo_intent,CAMERA_REQUEST_CODE);
            }
        });
        save_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePhoto();
                savePhotoSQL();
            }
        });
        show_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhoto();
            }
        });
    }
    @Override
//显示拍后的图片
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bm = extras.getParcelable("data");
                    show_photo.setImageBitmap(bm);
                }
            }
        }
    }
    private void savePhoto(){
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
        try{
            OutputStream os=getContentResolver().openOutputStream(imageUri);
            bm.compress(Bitmap.CompressFormat.JPEG,90,os);
           // Toast.makeText(this,"保存："+imageUri.toString(),Toast.LENGTH_LONG).show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void savePhotoSQL(){
        dbHelper=new MyDatabaseHelper(this,"C.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        String P=null;
        P=getImagePath(imageUri,null);
        db.execSQL("insert into C (content,photoURI) values(?,?)",new String[]{Scontent,P});
        Toast.makeText(this,"保存："+imageUri.toString(),Toast.LENGTH_LONG).show();
    } //显示保存的图片
    private void showPhoto(){
        List<Bitmap> photoList=new ArrayList<>();
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from C where content=?",new String[]{Scontent});
        if(cursor.moveToFirst()){
            do{
                String path=cursor.getString(cursor.getColumnIndex("photoURI"));
                Bitmap bitmap= BitmapFactory.decodeFile(path);
                photoList.add(bitmap);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        if(photoList.size()==2) {
            show_photo1.setImageBitmap(photoList.get(1));
        }else if(photoList.size()==3) {
            show_photo1.setImageBitmap(photoList.get(1));
            show_photo2.setImageBitmap(photoList.get(2));
        }else if(photoList.size()==4) {
            show_photo1.setImageBitmap(photoList.get(1));
            show_photo2.setImageBitmap(photoList.get(2));
            show_photo3.setImageBitmap(photoList.get(3));
        }else if(photoList.size()==5){
            show_photo1.setImageBitmap(photoList.get(1));
            show_photo2.setImageBitmap(photoList.get(2));
            show_photo3.setImageBitmap(photoList.get(3));
            show_photo4.setImageBitmap(photoList.get(4));
        }else{
            show_photo1.setImageBitmap(photoList.get(1));
            show_photo2.setImageBitmap(photoList.get(2));
            show_photo3.setImageBitmap(photoList.get(3));
            show_photo4.setImageBitmap(photoList.get(4));
            show_photo5.setImageBitmap(photoList.get(5));
        }
    }

    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private ImageView iv;
    Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private Button save_image;
    private Button show_images;
    private ImageView show_image1;
    private ImageView show_image2;
    private ImageView show_image3;
    private ImageView show_image4;
    private ImageView show_image5;
    Uri Uri;
    public void image(){
        Button reset=(Button)findViewById(R.id.reset);
        //show_images=(Button)findViewById(R.id.show_images);
        show_image1=(ImageView)findViewById(R.id.show_image1);
        show_image2=(ImageView)findViewById(R.id.show_image2);
        show_image3=(ImageView)findViewById(R.id.show_image3);
        show_image4=(ImageView)findViewById(R.id.show_image4);
        show_image5=(ImageView)findViewById(R.id.show_image5);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.drawColor(Color.GRAY);
                canvas.drawBitmap(baseBitmap, new Matrix(), paint);
                iv.setImageBitmap(baseBitmap);
            }
        });
        this.iv = (ImageView) this.findViewById(R.id.iv);
        // 创建一张空白图片
        baseBitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
        // 创建一张画布
        canvas = new Canvas(baseBitmap);
        // 画布背景为灰色
        canvas.drawColor(Color.GRAY);
        // 创建画笔
        paint = new Paint();
        // 画笔颜色为红色
        paint.setColor(Color.RED);
        // 宽度5个像素
        paint.setStrokeWidth(5);
        // 先将灰色背景画上
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);
        iv.setImageBitmap(baseBitmap);

        iv.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取手按下时的坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取手移动后的坐标
                        int stopX = (int) event.getX();
                        int stopY = (int) event.getY();
                        // 在开始和结束坐标间画一条线
                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        // 实时更新开始坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        iv.setImageBitmap(baseBitmap);
                        break;
                }
                return true;
            }
        });
        save_image=(Button)findViewById(R.id.save_image);
        save_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
                saveImageSQL();
            }
        });
        show_images=(Button)findViewById(R.id.show_images);
        show_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage();
            }
        });
    }
    private void saveImage(){
        Uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
        try{
            OutputStream os=getContentResolver().openOutputStream(Uri);
           baseBitmap.compress(Bitmap.CompressFormat.JPEG,90,os);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void saveImageSQL(){
        dbHelper=new MyDatabaseHelper(this,"C.db",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        String P=null;
        P=getImagePath(Uri,null);
        db.execSQL("insert into C (content,imageURI) values(?,?)",new String[]{Scontent,P});
        Toast.makeText(this,"保存："+Uri.toString(),Toast.LENGTH_LONG).show();
    } //显示保存的图片
    private void showImage(){
        List<Bitmap> photoList=new ArrayList<>();
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from C where content=?",new String[]{Scontent});
        if(cursor.moveToFirst()){
            do{
                String path=cursor.getString(cursor.getColumnIndex("imageURI"));
                Bitmap bitmap= BitmapFactory.decodeFile(path);
                photoList.add(bitmap);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        if(photoList.size()==7) {
            show_image1.setImageBitmap(photoList.get(6));
        }else if(photoList.size()==8) {
            show_image1.setImageBitmap(photoList.get(6));
            show_image2.setImageBitmap(photoList.get(7));
        }else if(photoList.size()==9) {
            show_image1.setImageBitmap(photoList.get(6));
            show_image2.setImageBitmap(photoList.get(7));
            show_image3.setImageBitmap(photoList.get(8));
        }else if(photoList.size()==10){
            show_image1.setImageBitmap(photoList.get(6));
            show_image2.setImageBitmap(photoList.get(7));
            show_image3.setImageBitmap(photoList.get(8));
            show_image4.setImageBitmap(photoList.get(9));
        }else{
            show_image1.setImageBitmap(photoList.get(6));
            show_image2.setImageBitmap(photoList.get(7));
            show_image3.setImageBitmap(photoList.get(8));
            show_image4.setImageBitmap(photoList.get(9));
            show_image5.setImageBitmap(photoList.get(10));
        }
    }
}
