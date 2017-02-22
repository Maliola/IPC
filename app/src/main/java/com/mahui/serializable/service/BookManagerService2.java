package com.mahui.serializable.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mahui.serializable.Book;
import com.mahui.serializable.IBookManager;
import com.mahui.serializable.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/2/9.
 */

public class BookManagerService2 extends Service{
    private static final String TAG="BMS";
    private AtomicBoolean mIsServiceDestoryed=new AtomicBoolean(false);
    //CopyOnWriteArrayList支持并发读写
    // AIDL在服务端的Binder线程池中执行，因此多个客户端同时连接时会有多线程访问问题，需要在AIDL中处理线程同步、CopyOnWriteArrayList来进行自动的现成同步
    private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<Book>();
    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList=new CopyOnWriteArrayList<IOnNewBookArrivedListener>();
    private Binder mBinder=new IBookManager.Stub(){
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if(!mListenerList.contains(listener)){
                mListenerList.add(listener);
            }else{
                Log.e(TAG,"already exists");
            }
            Log.e(TAG,"registerListener,size:"+mListenerList.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if(mListenerList.contains(listener)){
                mListenerList.remove(listener);
                Log.e(TAG,"unregister listener succeed");
            }else{
                Log.e(TAG,"not found,can not unregister");
            }
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
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }
    private void onNewBookArrived(Book book) throws RemoteException{
        mBookList.add(book);
        Log.e(TAG,"onNewBookArrived,notify listeners:"+mListenerList.size());
        for(int i=0;i<mListenerList.size();i++){
            IOnNewBookArrivedListener listener=mListenerList.get(i);
            Log.e(TAG,"onNewBookArrived,notify listener:"+listener);
            listener.onNewBookArrived(book);
        }
    }
    public class ServiceWorker implements Runnable{
        @Override
        public void run() {
            while(!mIsServiceDestoryed.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId=mBookList.size()+1;
                Book newBook=new Book(bookId,"我是新书");
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
