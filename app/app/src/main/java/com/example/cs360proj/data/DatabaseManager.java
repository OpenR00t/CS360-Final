package com.example.cs360proj.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager instance;
    private static final String DATABASE_NAME = "data.db";
    private static final int VERSION = 2;

    // Get an instance of DatabaseManager (if it does not exist yet) and return it
    public static DatabaseManager getInstance(Context context){
        if (instance == null){
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    private DatabaseManager(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Create the tables for the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserTable.TABLE + " (" +
                UserTable.COL_ID + " integer primary key autoincrement, " +
                UserTable.COL_USERNAME + " text, " +
                UserTable.COL_PASSWORD + " text)");

        db.execSQL("CREATE TABLE " + InventoryTable.TABLE + " (" +
                InventoryTable.COL_ID + " integer primary key autoincrement, " +
                InventoryTable.COL_NAME + " text, " +
                InventoryTable.COL_QTY + " text, " +
                InventoryTable.COL_LOC + " text)");
    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int il) {
        //not yet implemented
    }

    // Authenticate user with given username and password
    public boolean authenticate(String username, String password){
        boolean isAuthenticated = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + UserTable.TABLE +
                " WHERE " + UserTable.COL_USERNAME + " = ? AND " +
                UserTable.COL_PASSWORD + " = ? ";

        Cursor cursor = db.rawQuery(sql, new String[]{username, password});

        if(cursor.moveToFirst()){
            isAuthenticated = true;
        }

        return isAuthenticated;
    }

    // Check if a user exists with given username
    public boolean userExists(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + UserTable.TABLE +
                " WHERE " + UserTable.COL_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{username});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Add a user with given username and password to the database
    public void addUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, username);
        values.put(UserTable.COL_PASSWORD, password);

        db.insert(UserTable.TABLE, null, values);
    }

    // Add an inventory item with given name, quantity, and location to the database
    public void addInventoryItem(String name, String quantity, String location) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_NAME, name);
        values.put(InventoryTable.COL_QTY, quantity);
        values.put(InventoryTable.COL_LOC, location);

        db.insert(InventoryTable.TABLE, null, values);
    }

    // Get all inventory items from the database
    public Cursor getAllInventoryItems() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(InventoryTable.TABLE,
                new String[]{InventoryTable.COL_ID, InventoryTable.COL_NAME, InventoryTable.COL_QTY, InventoryTable.COL_LOC},
                null, null, null, null, null);
    }

    //Get specific inventory item from the database
    public Cursor getInventoryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(InventoryTable.TABLE,
                new String[]{InventoryTable.COL_ID, InventoryTable.COL_NAME, InventoryTable.COL_QTY, InventoryTable.COL_LOC},
                InventoryTable.COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    //Update database entry
    public void updateInventoryItem(long id, String name, String quantity, String location) {
        SQLiteDatabase db = getWritableDatabase();

        //Object with new values to be updated
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COL_NAME, name);
        values.put(InventoryTable.COL_QTY, quantity);
        values.put(InventoryTable.COL_LOC, location);

        //write updates to inventory item with matching ID
        db.update(InventoryTable.TABLE, values, InventoryTable.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteInventoryItem(long id) {
        //Get writable database instance
        SQLiteDatabase db = getWritableDatabase();

        //Delete entry with matching ID
        db.delete(InventoryTable.TABLE, InventoryTable.COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public static String getInventoryTableName() {
        return InventoryTable.TABLE;
    }

    public static String getInventoryColId() {
        return InventoryTable.COL_ID;
    }

    public static String getInventoryColName() {
        return InventoryTable.COL_NAME;
    }

    public static String getInventoryColQty() {
        return InventoryTable.COL_QTY;
    }

    public static String getInventoryColLoc() {
        return InventoryTable.COL_LOC;
    }


    private static final class UserTable{
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    private static final class InventoryTable{
        private static final String TABLE = "inventory";
        private static final String COL_ID = "_id";
        private static final String COL_NAME= "name";
        private static final String COL_QTY = "quantity";
        private static final String COL_LOC = "location";
    }
}
