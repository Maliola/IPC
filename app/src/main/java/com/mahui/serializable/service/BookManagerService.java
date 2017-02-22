package com.mahui.serializable.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
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

public class BookManagerService extends Service{
    private static final String TAG="BMS";
    private AtomicBoolean mIsServiceDestoryed=new AtomicBoolean(false);
    //CopyOnWriteArrayList支持并发读写
    // AIDL在服务端的Binder线程池中执行，因此多个客户端同时连接时会有多线程访问问题，需要在AIDL中处理线程同步、CopyOnWriteArrayList来进行自动的现成同步
    private CopyOnWriteArrayList<Book> mBookList=new CopyOnWriteArrayList<Book>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList=new RemoteCallbackList<IOnNewBookArrivedListener>();
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
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
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
        int check=checkCallingOrSelfPermission("com.mahui.serializable.ACCESS_BOOK_SERVICE");
        if(check== PackageManager.PERMISSION_DENIED){
            return null;
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }
    private void onNewBookArrived(Book book) throws RemoteException{
        mBookList.add(book);
        final int N=mListenerList.beginBroadcast();
        for(int i=0;i<N;i++){
            IOnNewBookArrivedListener listener=mListenerList.getBroadcastItem(i);
            Log.e(TAG,"onNewBookArrived,notify listener:"+listener);
            if(listener!=null){
                listener.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
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
