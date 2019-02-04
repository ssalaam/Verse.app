package com.veed.oduchantingapp.DatabaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.veed.oduchantingapp.Objects.Chant;
import com.veed.oduchantingapp.Utils.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 24;

    // Database Name
    private static final String DATABASE_NAME = "User_data";

    //Chant table
    public static final String TABLE_CHANTS = "table_chants";
    public static final String CHANT_NAME = "chant_name";
    public static final String CHANT_COVER = "chant_cover";
    public static final String CHANT_ID= "chant_id";

    //File table
    public static final String TABLE_CHANT_LINES = "table_chant_lines";
    public static final String CHANT_LINE_FILE_NAME = "chant_line_file_name";
    public static final String CHANT_LINE_TEXT = "chant_line_text";
    public static final String CHANT_LINE_CHANT_ID = "chant_line_chant_id";
    public static final String CHANT_LINE_ORDER = "chant_line_order";


    //Tags table
    public static final String TABLE_TAGS = "table_tags";
    public static final String TAG_TEXT = "tag_text";
    public static final String TAG_CHANT_ID = "tag_chant_id";

    public Context mContext;

    private static DatabaseHandler sInstance;

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_CHANTS = "CREATE TABLE " + TABLE_CHANTS + "("
                + CHANT_NAME + " TEXT , "
                + CHANT_COVER + " INTEGER , " +
                CHANT_ID +" TEXT PRIMARY KEY)";


        String CREATE_TABLE_TAGS = "CREATE TABLE " + TABLE_TAGS + "("
                + TAG_TEXT + " TEXT , " +
                TAG_CHANT_ID +" TEXT)";

        String CREATE_TABLE_CHANT_FILES = "CREATE TABLE " + TABLE_CHANT_LINES + "("
                + CHANT_LINE_CHANT_ID + " TEXT , " +
                CHANT_LINE_FILE_NAME + " TEXT , " +
                CHANT_LINE_TEXT + " TEXT , " +
                CHANT_LINE_ORDER +" INTEGER)";


        db.execSQL(CREATE_TABLE_CHANT_FILES);
        db.execSQL(CREATE_TABLE_TAGS);
        db.execSQL(CREATE_TABLE_CHANTS);
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANT_LINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        onCreate(db);
    }


    public void saveChant(Chant chant){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CHANT_NAME, chant.name);
        values.put(CHANT_COVER, chant.cover);
        values.put(CHANT_ID, chant.id);
        db.insert(TABLE_CHANTS, null, values);


        ContentValues values3 = new ContentValues();

        for(int i = 0; i < chant.tags.size(); i++){
            values3.put(TAG_TEXT, chant.tags.get(i));
            values3.put(TAG_CHANT_ID, chant.id);
            db.insert(TABLE_TAGS, null, values3);
        }

    }


    public void saveChantLines(Chant chant){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values2 = new ContentValues();
        for(int i = 0; i < chant.files.size(); i++){
            values2.put(CHANT_LINE_FILE_NAME, chant.files.get(i).filename);
            values2.put(CHANT_LINE_ORDER, i);
            values2.put(CHANT_LINE_CHANT_ID, chant.id);
            values2.put(CHANT_LINE_TEXT, chant.files.get(i).text);
            db.insert(TABLE_CHANT_LINES, null, values2);
        }

    }


    public void updateChant(Chant chant) {

        SQLiteDatabase db = this.getWritableDatabase();

        //Update Chant Name
        ContentValues values = new ContentValues();
        values.put(CHANT_NAME, chant.name);
        values.put(CHANT_COVER, chant.cover);

        String whereClause = CHANT_ID +"=?";
        String[]whereArgs = new String[] {chant.id};

        db.update(
                TABLE_CHANTS,
                values,
                whereClause,
                whereArgs);


        //Update Tags *************

        String whereClause1 = TAG_CHANT_ID +"=?";
        String[]whereArgs1 = new String[] {chant.id};

        db.beginTransaction();
        db.delete(TABLE_TAGS, whereClause1, whereArgs1);
        db.setTransactionSuccessful();
        db.endTransaction();

        ContentValues values3 = new ContentValues();

        for(int i = 0; i < chant.tags.size(); i++){
            values3.put(TAG_TEXT, chant.tags.get(i));
            values3.put(TAG_CHANT_ID, chant.id);
            db.insert(TABLE_TAGS, null, values3);
        }

        //*************************



        //Update Chant Lines
        String whereClause2 = CHANT_LINE_CHANT_ID +"=?";
        String[]whereArgs2 = new String[] {chant.id};

        db.beginTransaction();
        db.delete(TABLE_CHANT_LINES, whereClause2, whereArgs2);
        db.setTransactionSuccessful();
        db.endTransaction();

        saveChantLines(chant);

    }




    public List<Chant> getAllChants(){

        List<Chant> chants = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CHANTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Chant chant = new Chant();
                chant.name = cursor.getString(0);
                chant.cover = cursor.getInt(1);
                chant.id = cursor.getString(2);

                chant.tags = getAllTagsOfAChant(chant.id);

                chants.add(chant);
            } while (cursor.moveToNext());
        }
        cursor.close();

        for(int i = 0; i < chants.size(); i++){
            Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_CHANT_LINES + " WHERE " + CHANT_LINE_CHANT_ID + " = '" + chants.get(i).id + "' ORDER BY " + CHANT_LINE_ORDER + " ASC" , null);
            if (cursor2.moveToFirst()) {
                do {
                    chants.get(i).files.add(new MyFile(cursor2.getString(1),cursor2.getString(2))); //?????
                } while (cursor2.moveToNext());
            }
            cursor2.close();
        }

        Log.d("getAllChants", "Returned " + chants.size() + " chants");
        return chants;
    }

    public boolean isChantUnique(String chant_name){
        String selectQuery = "SELECT * FROM " + TABLE_CHANTS + " WHERE " + CHANT_NAME + " = '" + chant_name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean result = !cursor.moveToFirst();
        cursor.close();
        return result;

    }

    public void deleteChant(Chant chant){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = CHANT_ID +"=?";
        String[]whereArgs = new String[] {chant.id};

        db.beginTransaction();
        db.delete(TABLE_CHANTS, whereClause, whereArgs);
        db.setTransactionSuccessful();
        db.endTransaction();

        whereClause = CHANT_LINE_CHANT_ID +"=?";
        whereArgs = new String[] {chant.id};
        db.beginTransaction();
        db.delete(TABLE_CHANT_LINES, whereClause, whereArgs);
        db.setTransactionSuccessful();
        db.endTransaction();

        for(int i = 0; i < chant.files.size(); i++){
            File file = new File(chant.files.get(i).filename);
            file.delete();
        }
    }



    public List<String> getAllTags()
    {
        List<String> tags = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TAGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                tags.add(cursor.getString(0)); //?????
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tags;
    }

    public List<String> getAllTagsOfAChant(String id){
        ArrayList<String> tags = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TAGS, null, TAG_CHANT_ID + "=?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                tags.add(cursor.getString(0)); //?????
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tags;
    }



    public void addTag(String text, String chant_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TAG_TEXT, text);
        values.put(TAG_CHANT_ID, chant_id);
        db.insert(TABLE_TAGS, null, values);
        // Closing database connection
    }


    public void deleteChant(String filename){

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = CHANT_LINE_FILE_NAME +"=?";
        String[]whereArgs = new String[] {filename};

        db.beginTransaction();
        db.delete(TABLE_CHANT_LINES, whereClause, whereArgs);
        db.setTransactionSuccessful();
        db.endTransaction();

        File file = new File(filename);
        file.delete();

    }



    public void updateLineText(String filename, String line_text){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(CHANT_LINE_TEXT, line_text);
        String[] args = new String[]{filename};
        db.update(TABLE_CHANT_LINES, newValues, CHANT_LINE_FILE_NAME + "=?", args);
    }

    public String getNewFileNameForChant(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_CHANT_LINES + " WHERE " + CHANT_LINE_CHANT_ID + " = '" + id + "' ORDER BY " + CHANT_LINE_ORDER + " ASC", null);

        int last_index = 0;
        if(cursor2.moveToLast()){
            cursor2.moveToLast();
            last_index = cursor2.getInt(3) + 1;
        }
        cursor2.close();
        return mContext.getFilesDir() + "/oduapp" + id + last_index + ".3gp";
    }

    public void addNewLine(String id, String filename, String text) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_CHANT_LINES + " WHERE " + CHANT_LINE_CHANT_ID + " = '" + id + "' ORDER BY " + CHANT_LINE_ORDER + " ASC", null);

        int last_index = 0;
        if(cursor2.moveToLast()) {
            cursor2.moveToLast();
            last_index = cursor2.getInt(3) + 1;
            cursor2.close();

            ContentValues insertValues = new ContentValues();
            insertValues.put(CHANT_LINE_CHANT_ID, id);
            insertValues.put(CHANT_LINE_TEXT, text);
            insertValues.put(CHANT_LINE_FILE_NAME, filename);
            insertValues.put(CHANT_LINE_ORDER, last_index);
            db.insert(TABLE_CHANT_LINES, null, insertValues);

        }else{

            ContentValues insertValues = new ContentValues();
            insertValues.put(CHANT_LINE_CHANT_ID, id);
            insertValues.put(CHANT_LINE_TEXT, text);
            insertValues.put(CHANT_LINE_FILE_NAME, filename);
            insertValues.put(CHANT_LINE_ORDER, last_index);
            db.insert(TABLE_CHANT_LINES, null, insertValues);

        }

    }
}
