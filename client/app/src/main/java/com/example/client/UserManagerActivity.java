package com.example.client;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UserManagerActivity extends AppCompatActivity {
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView imageView;

    private ListView userOptions;
    private Button logOffbtn;
    private String[] options= {"更换头像", "更改名称"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_manager);
        imageView=(ImageView)findViewById(R.id.imageView);

        userOptions=(ListView)findViewById(R.id.user_options);
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(this,R.layout.layout_user_manager_options,R.id.option,options);
        userOptions.setAdapter(adapter);
        userOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://选择本地照片
                        showChosePicDialog();
                        break;
                    case 1://拍照
                        break;
                }

            }
        });
        logOffbtn=(Button)findViewById(R.id.logoff);
        logOffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp=getSharedPreferences("user-info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.clear();
                editor.commit();

                Intent intent=new Intent();
                intent.setClass(UserManagerActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showChosePicDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items={"选择本地图片","拍照"};
        builder.setNegativeButton("取消",null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case CHOOSE_PICTURE://选择本地照片
                        if(ContextCompat.checkSelfPermission(UserManagerActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(UserManagerActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
                        }else {
                            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent,CHOOSE_PICTURE);
                        }
                        break;
                    case TAKE_PICTURE://拍照
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case TAKE_PICTURE:
                    //startPhotoZoom(tempUri);
                    //sdk版本在19之上
//                    if (Build.VERSION.SDK_INT >= 19) {
//                        handleImageOnKitKat(data);
//                    }
                    break;
                case CHOOSE_PICTURE:
                    //startPhotoZoom(data.getData());
                    //sdk版本在19之上
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    if (data!=null){
                        setImageToView(data);
                    }
                    break;
            }
        }
    }

    @RequiresApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docID=DocumentsContract.getDocumentId(uri);
            // 如果是document类型的Uri，则通过document id处理
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docID.split(":")[1];
                // 解析出数字格式的id
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"),Long.valueOf(docID));
                imagePath=getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath=getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri externalContentUri, String selection) {
        String path=null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor=getContentResolver().query(externalContentUri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //保存剪裁之后图片得数据
    private void setImageToView(Intent data) {
        Bundle extras=data.getExtras();
        if (extras!=null){
            Bitmap photo=extras.getParcelable("data");
            imageView.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap photo) {

    }

    //剪裁图片
    private void startPhotoZoom(Uri tempUri) {
        if (tempUri==null){
            Log.i("tag","The uri is not exist");
        }
        this.tempUri=tempUri;
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(tempUri,"image/*");
        //剪裁
        intent.putExtra("crop","true");
        //宽高比例
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        //剪裁图片得宽高
        intent.putExtra("outputX",150);
        intent.putExtra("outputY",150);
        intent.putExtra("return-data",true);
        startActivityForResult(intent,CROP_SMALL_PICTURE);
    }
}
