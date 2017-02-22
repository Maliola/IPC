package com.mahui.serializable.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.mahui.serializable.Book;
import com.mahui.serializable.IBookManager1;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2017/2/9.
 */

public class BookManagerService1 extends Service{
    //CopyOnWriteArrayList支持并发读写
    // AIDL在服务端的Binder线程池中执行，因此多个客户端同时连接时会有多线程访问问题，需要在AIDL中处理线程同步、CopyOnWriteArrayList来进行自动的现成同步
    private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<Book>();
    private Binder mBinder=new IBookManager1.Stub(){
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"Android"));
        mBookList.add(new Book(2,"IOS"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
