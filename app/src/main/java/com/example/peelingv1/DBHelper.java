package com.example.peelingv1;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Locale;

import androidx.annotation.Nullable;


public class DBHelper extends SQLiteOpenHelper {
    public static final String  DATABASE_NAME = "db_barcode.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "peeling";
    private Resources mResources;
    private String empCode;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mResources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PEELING_TABLE = "CREATE TABLE "+TABLE+"(\n" +
                "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`date_bar`\tTEXT,\n" +
                "\t`convey_bar`\tINTEGER,\n" +
                "\t`job_no`\tTEXT,\n" +
                "\t`emp_code`\tTEXT,\n" +
                "\t`qtty`\tNUMERIC,\n" +
                "\t`status`\tTEXT,\n" +
                "\t`time`\tTEXT,\n" +
                "\t`start_time`\tTEXT,\n" +
                "\t`end_time`\tTEXT,\n" +
                "\t`session`\tTEXT,\n" +
                "\t`update_time`\tTEXT,\n" +
                "\t`OT`\tTEXT\n" +
                ");";
        db.execSQL(CREATE_PEELING_TABLE);
        Log.d("Create table","Create Table Successfully");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String DROP_PEELING_TABLE = "DROP TABLE IF EXISTS peeling";

        db.execSQL(DROP_PEELING_TABLE);
        onCreate(db);

    }

    //Insert Data
    public  long InserData(String date_bar,int convey_bar,String job_no,String emp_code,
                           double qtty,String status, String time, String start_time,String end_time,String session,String update_time,String ot) {
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            ContentValues Val = new ContentValues();
            Val.put("date_bar",date_bar);
            Val.put("convey_bar",convey_bar);
            Val.put("job_no",job_no);
            Val.put("emp_code",emp_code);
            Val.put("qtty",qtty);
            Val.put("status",status);
            Val.put("time",time);
            Val.put("start_time",start_time);
            Val.put("end_time",end_time);
            Val.put("session",session);
            Val.put("update_time",update_time);
            Val.put("OT",ot);

            long rows = db.insert(TABLE,null,Val);

            db.close();
            return  rows;
        } catch (Exception e) {
            return -1;
        }

    }
    public String DeleteDataAll(){
        String deleteST;
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE);
            deleteST = "OK";
            return deleteST;
        }catch (Exception e){
            return e.toString();
        }

    }

    public void DeleteData(String id){
        SQLiteDatabase db = getWritableDatabase();
       db.execSQL("DELETE FROM " + TABLE + " WHERE id = " + id);

    }

    public int getPeelingCountAll(){
        int result = 0;
        String query = "SELECT * FROM " + TABLE ;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        result = cursor.getCount();
        db.close();
        return result;
    }
    public int getPeelingCount(String empCode){
        int result = 0;
        String query = "SELECT * FROM " + TABLE + " WHERE emp_code = " +empCode ;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        result = cursor.getCount();
        db.close();

        return result;
    }
    public String getQttyValude(String empCode){
        String result;
        String query = "SELECT sum(qtty) SUMQTTY FROM " + TABLE + " WHERE emp_code = " +empCode ;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        result = cursor.getString(0);
        db.close();
        return result;
    }

    public Cursor getDataAll(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            //String query = " SELECT id As _id, * FROM " + TABLE + " WHERE emp_code = " + empCode + " ORDER BY id";
            String query = "SELECT id _id,* from peeling";
            Cursor cursor = db.rawQuery(query,null);

            return cursor;

        }catch (Exception e){
            return null;
        }
    }

    public Cursor getEmpData(String empCode){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            //String query = " SELECT id As _id, * FROM " + TABLE + " WHERE emp_code = " + empCode + " ORDER BY id";
            String query = "SELECT  (SELECT count(*) from peeling b where a.id >= b.id and b.emp_code = " + empCode + ") as _id, *   from peeling a WHERE a.emp_code = " +empCode;
            Cursor cursor = db.rawQuery(query,null);

            return cursor;

        }catch (Exception e){
            return null;
        }
    }

    public  String getPathFile(){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String patch ;
        File filePtch = new File(dir,"Exdata.txt");
        patch = filePtch.getPath();
        return patch;
    }

    //export Data to CSV
    public boolean exportDatabase() {

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         * เริ่มแรกตรวจสอบพื้นที่เก็บข้อมูลว่ามีหรือไม่ในที่นี้ไม่่ใช้เฉพาะ SD การ์ด
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }
        else {
            //ใช้ Folder Download ในการ Save File .CSV
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                file = new File(exportDir, "Exdata.txt");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));

                /**This is our database connector class that reads the data from the database.
                 * The code of this class is omitted for brevity.
                 */
                SQLiteDatabase db = this.getReadableDatabase(); //open the database for reading

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */
                Cursor curCSV = db.rawQuery("select * from peeling", null);
                //Write the name of the table and the name of the columns (comma separated values) in the .csv file.
                //printWriter.println("FIRST TABLE OF THE DATABASE");
                //printWriter.println("ID,DATE_TIME,Convey_bar,JOB_NO,EMP_CODE,QTTY,START_TIME,ENT_TIME,SSESION,T_TIME");
                while(curCSV.moveToNext())
                {
//
                    int id = curCSV.getInt(curCSV.getColumnIndex("id"));
                    String date_bar = curCSV.getString(curCSV.getColumnIndex("date_bar"));
                    int convey_bar = curCSV.getInt(curCSV.getColumnIndex("convey_bar"));
                    String job_no = curCSV.getString(curCSV.getColumnIndex("job_no"));
                    String emp_code = curCSV.getString(curCSV.getColumnIndex("emp_code"));
                    String qtty = curCSV.getString(curCSV.getColumnIndex("qtty"));
                    String start_time = curCSV.getString(curCSV.getColumnIndex("start_time"));
                    String end_time = curCSV.getString(curCSV.getColumnIndex("end_time"));
                    String session = curCSV.getString(curCSV.getColumnIndex("session"));
                    String time = curCSV.getString(curCSV.getColumnIndex("time"));
                    String OT = curCSV.getString(curCSV.getColumnIndex("OT"));

                    /**Create the line to write in the .csv file.
                     * We need a String where values are comma separated.
                     * The field date (Long) is formatted in a readable text. The amount field
                     * is converted into String.
                     */
                    String record = id + "," + date_bar + "," + convey_bar + "," + job_no + "," + emp_code + "," + qtty + "," + start_time + "," +
                            "" + end_time + "," + session + "," + time + ","+","+ OT ;
                    printWriter.println(record); //write the record in the .csv file
                }

                curCSV.close();
                db.close();
            }

            catch(Exception exc) {
                //if there are any exceptions, return false
                return false;
            }
            finally {
                if(printWriter != null) printWriter.close();
            }

            //If there are no errors, return true.
            return true;
        }

    }
    //getDataAll
//    public Cursor getDataAll(){
//        String sql = "SELECT * FROM " + TABLE + " ORDER BY id;";
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor res = null;
//        if(db !=null){
//            res = db.rawQuery(sql,null);
//        }
//        return res;
//    }
//    //getData from Emp
//    public Cursor getDataEmp(String _empCode){
//        empCode = _empCode;
//        String sql = "SELECT * FROM peeling WHERE emp_code ="+ empCode; //+ _empCode;
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor res = null;
//        if(db !=null){
//            res = db.rawQuery(sql,null);
//        }
//        return res;
//    }
//    public Cursor getSumQtty(String _empCode){
//        empCode = _empCode;
//        String sql = "SELECT sum(qtty) QTTY FROM peeling WHERE emp_code ="+ empCode;
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor res = null;
//        if(db !=null){
//            res = db.rawQuery(sql,null);
//        }
//        return res ;
//    }


}
