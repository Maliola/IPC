package com.mahui.serializalbelocalfile;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Administrator on 2017/2/16.
 */

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //序列化
        User user=new User("张三","男");
        File file =new File(getSDPath()+"/test");
        try {
            ObjectOutputStream objctOutputStream=new ObjectOutputStream(new FileOutputStream(file));
            objctOutputStream.writeObject(user);
            objctOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //反序列化
        try {
            ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(file));
            User newuser= (User) objectInputStream.readObject();
            objectInputStream.close();
            Log.e("-------------",newuser.getName()+"------"+newuser.getSex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getSDPath() {
        File sdDir=null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在*/
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        } else {
            Toast.makeText(MainActivity.this,"没有sd卡",Toast.LENGTH_SHORT).show();
        }
        return sdDir.toString();
    }
}
