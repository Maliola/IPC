package com.mahui.serializable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mahui.serializable.service.BookManagerService1;

import java.util.List;

/**
 * Created by Administrator on 2017/2/10.
 */

public class BookManagerActivity1 extends Activity{
    private static final String TAG="BookManagerActivity1";

    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            IBookManager1 bookManager=IBookManager1.Stub.asInterface(service);
            try {
                List<Book> list=bookManager.getBookList();
                Log.e(TAG,"query book list,list type:"+list.getClass());
                Book newBook=new Book(3,"Android开发艺术探索");
                bookManager.addBook(newBook);
                Log.e(TAG,"add book:"+newBook);
                List<Book> newlist=bookManager.getBookList();
                Log.e(TAG,"query book list:"+newlist.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG,"bind died");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Intent intent=new Intent(this, BookManagerService1.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
