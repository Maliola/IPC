// IBookManager1.aidl
package com.mahui.serializable;

// Declare any non-default types here with import statements
import com.mahui.serializable.Book;
interface IBookManager1 {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    List<Book> getBookList();
    void addBook(in Book book);
}
