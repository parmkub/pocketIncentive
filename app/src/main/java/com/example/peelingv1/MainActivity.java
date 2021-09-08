package com.example.peelingv1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    EditText _datePicker,_timePicker,_strartTimePicker,_endTimePicker,_edtSession,_edtJobCode,_edtScan,_edtConvey,_edtEmp;
    CheckBox _chkOT;
    private int mYear,mMonth,mDay, mHour, mMinute,mConvey,mcountPeeling,mCountPeelingAll;
    private Double mQtt;
    TextView _textCount,_sumQtty,_textAllRecode;
    StringBuilder stringBuilder;
    DBHelper mydb;
    String mSumQtty;
    ListView _listView;
    private String strDate ,strTime, strJobcode,strScan,strConvey,strSession,strTimeStart,strTimeEnd,strChkOT,strEmp,strDateTime;
    SimpleCursorAdapter adapter;
    Cursor myData;
    String[] Cmd = {"แก้ไข","ลบ"};
    CardView _cardViewAlldata;
    String location;
    private boolean mIsUploading = false;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, INTERNET}, PackageManager.PERMISSION_GRANTED);
        mydb =new DBHelper(this);
        mydb.getWritableDatabase();
        mcountPeeling = mydb.getPeelingCountAll();
        bindwiget();
        _textAllRecode.setText(String.format("%d",mcountPeeling));

        Intent logindata = getIntent();
        location = logindata.getStringExtra("location");

        ShowDataAll();

       _datePicker.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final Calendar c = Calendar.getInstance();
               mYear = c.get(Calendar.YEAR);
               mMonth = c.get(Calendar.MONTH);
               mDay = c.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                       new DatePickerDialog.OnDateSetListener() {
                           @Override
                           public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                               Date d = new Date(year,monthOfYear,dayOfMonth);
                               SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                               strDate = dateFormat.format(d);

                               _datePicker.setText((monthOfYear+1) + "/" +dayOfMonth +"/"+year);
                               //_datePicker.setText(strDate);
                           }
                       },mYear,mMonth,mDay);
               datePickerDialog.show();
           }
       });

       _timePicker.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
               final Calendar t = Calendar.getInstance();
               mHour = t.get(Calendar.HOUR_OF_DAY);
               mMinute = t.get(Calendar.MINUTE);


               TimePickerDialog timePickerDialog ;
               timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                       stringBuilder = new StringBuilder();
                       String am_pm;
                       am_pm = getTime(hour,minute);
                       stringBuilder.append(" ");
                       stringBuilder.append(am_pm);
                       strTime = stringBuilder.toString();
                       _timePicker.setText(strTime);
                   }
               },mHour,mMinute,false);
               timePickerDialog.setTitle("Select Time");
               timePickerDialog.show();
           }

       });

       _strartTimePicker.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final Calendar t = Calendar.getInstance();
               mHour = t.get(Calendar.HOUR_OF_DAY);
               mMinute = t.get(Calendar.MINUTE);
               TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                       _strartTimePicker.setText(hour + "." + minute);
                   }
               },mHour,mMinute,false);
               timePickerDialog.setTitle("Select Start Time");
               timePickerDialog.show();
           }
       });

       _endTimePicker.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final Calendar t = Calendar.getInstance();
               mHour = t.get(Calendar.HOUR_OF_DAY);
               mMinute = t.get(Calendar.MINUTE);
               TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                       _endTimePicker.setText(hour + "." + minute);
                   }
               },mHour,mMinute,false);
               timePickerDialog.setTitle("Select End Time");
               timePickerDialog.show();
           }
       });

       _edtScan.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               LocalDateTime tTime = LocalDateTime.now();
               DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
               String tTimeFormat = tTime.format(format);

               strDate = _datePicker.getText().toString();
               strTime = _timePicker.getText().toString();
               strConvey = _edtConvey.getText().toString();
               strSession = _edtSession.getText().toString();
               strJobcode = _edtJobCode.getText().toString();
               strScan = _edtScan.getText().toString();
               strEmp = _edtEmp.getText().toString();
               strTimeStart = _strartTimePicker.getText().toString();
               strTimeEnd = _endTimePicker.getText().toString();
               strChkOT = chkOT();

               if(!_edtScan.getText().toString().contains(".") && _edtEmp.getText().toString().length() == 0 && charSequence.length() == 6 ){
                   _edtEmp.setText(strScan);
                   strEmp = _edtEmp.getText().toString();
                   mSumQtty = mydb.getQttyValude(strEmp);
                   mcountPeeling = mydb.getPeelingCount(strEmp);
                   _textCount.setText(String.format("%d",mcountPeeling));
                   _sumQtty.setText(mSumQtty);
                   _edtScan.setText("");
                   ShowDataEmp(strEmp);
                   beefEmp();


               }else if(!_edtScan.getText().toString().contains(".") && _edtEmp.getText().toString().length() == 6 & charSequence.length() == 6){
                   _edtEmp.setText(strScan);
                   strEmp = _edtEmp.getText().toString();
                   mSumQtty = mydb.getQttyValude(strEmp);
                   mcountPeeling = mydb.getPeelingCount(strEmp);
                   mCountPeelingAll = mydb.getPeelingCountAll();
                   _textAllRecode.setText(String.format("%d",mCountPeelingAll));
                   _textCount.setText(String.format("%d",mcountPeeling));
                   _sumQtty.setText(mSumQtty);
                   _edtScan.setText("");
                   ShowDataEmp(strEmp);
                   beefEmp();

               }else if(_edtScan.getText().toString().contains(".") && _edtEmp.getText().toString().length() !=0 && _edtScan.getText().toString().length() == 6 ){

                   if(strDate.isEmpty() || strTime.isEmpty() || strConvey.isEmpty() || strSession.isEmpty() || strJobcode.isEmpty() || strTimeStart.isEmpty() || strTimeEnd.isEmpty()){
                       Toast.makeText(MainActivity.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();

                   }else {
                       strDateTime = strDate+ " " +strTime;
                       mConvey = Integer.valueOf(strConvey);
                       mQtt = Double.parseDouble(strScan);
                       long mInsert =  mydb.InserData(strDateTime,mConvey,strJobcode,strEmp,mQtt,"0",tTimeFormat,strTimeStart,strTimeEnd,strSession,tTimeFormat,strChkOT);
                       if(mInsert > 0){
                           mSumQtty = mydb.getQttyValude(strEmp);
                           mcountPeeling = mydb.getPeelingCount(strEmp);
                           mCountPeelingAll = mydb.getPeelingCountAll();
                           _textCount.setText(String.format("%d",mcountPeeling));
                           _textAllRecode.setText(String.format("%d",mCountPeelingAll));
                           _sumQtty.setText(mSumQtty);
                           ShowDataEmp(strEmp);
                           Toast.makeText(MainActivity.this, "Inser Data Success", Toast.LENGTH_SHORT).show();
                       }else {
                           Toast.makeText(MainActivity.this, "Inser Data Failed", Toast.LENGTH_SHORT).show();
                       }

                   }
                   beefScan();
                   _edtScan.setText("");

               }

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setTitle("ออกจากระบบ ?")
                .setMessage("คุณแน่ใจหรือ")
                .setNegativeButton(R.string.Cancel,null)
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    //Method สำหรับจัด Format ใหักับเวลา
    public String getTime(int hr, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hr);
        cal.set(Calendar.MINUTE,min);
        Format formatter;
        formatter = new SimpleDateFormat("h:mm:ss a");
        return formatter.format(cal.getTime());
    }

    public  void ShowDataAll(){
        myData = mydb.getDataAll();
        AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);

        View headerView = getLayoutInflater().inflate(R.layout.listview_header, null,true);
        if(_listView.getHeaderViewsCount()>0){
            _listView.removeHeaderView(headerView);
        }else {
            _listView.addHeaderView(headerView);
        }
        adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.activity_column,myData
                ,new String[]{"_id","convey_bar","job_no","qtty"}
                ,new int[]{R.id.noNumber,R.id.colConvey,R.id.colJob,R.id.colQtty});

        _listView.setAdapter(adapter);



    }


    public void ShowDataEmp(String empCode){
        myData = mydb.getEmpData(empCode);
        AlertDialog.Builder viewDetail = new AlertDialog.Builder(this);

        View headerView = getLayoutInflater().inflate(R.layout.listview_header, null,true);
        if(_listView.getHeaderViewsCount()>0){
            _listView.removeHeaderView(headerView);
        }else {
            _listView.addHeaderView(headerView);
        }

        adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.activity_column,myData
                ,new String[]{"_id","convey_bar","job_no","qtty"}
                ,new int[]{R.id.noNumber,R.id.colConvey,R.id.colJob,R.id.colQtty});

        _listView.setAdapter(adapter);


        registerForContextMenu(_listView);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> myAdapter, View v, int position, long mylng) {
                String peelingID = myData.getString(myData.getColumnIndex("id"));
                String convey = myData.getString(myData.getColumnIndex("convey_bar"));
                String JobID = myData.getString(myData.getColumnIndex("job_no"));
                String EmpCode = myData.getString(myData.getColumnIndex("emp_code"));
                String Qtty = myData.getString(myData.getColumnIndex("qtty"));

                viewDetail.setTitle("Peeling Detail");
                viewDetail.setIcon(android.R.drawable.star_big_on);
                viewDetail.setMessage("รหัสพนักงาน : "+EmpCode+ "\n"
                + "รอบที่ : " + convey + "\n"
                + "Job ID : " + JobID + "\n"
                + "จำนวนเงิน : " + Qtty);

                viewDetail.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                viewDetail.show();

            }
        });



    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderIcon(android.R.drawable.btn_star_big_on);
        menu.setHeaderTitle("เลือกหัวข้อที่ต้องการ");
        String[] menuItem = Cmd;
        for (int i = 0; i< menuItem.length; i++){
            menu.add(Menu.NONE,i,i,menuItem[i]);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int menuItemIndex = item.getItemId();
        String[] menuItems = Cmd;
        String CmdName = menuItems[menuItemIndex];
        String PeelingID = myData.getString(myData.getColumnIndex("id"));

        if("แก้ไข".equals(CmdName)){

            Toast.makeText(MainActivity.this, "Edit Command (Peeling ID = "+ PeelingID + ")", Toast.LENGTH_SHORT).show();
        }else if("ลบ".equals(CmdName)){

            Toast.makeText(MainActivity.this, "Delete Command (Peeling ID = "+ PeelingID + ")", Toast.LENGTH_SHORT).show();
            mydb.DeleteData(PeelingID);
            strEmp = _edtEmp.getText().toString();

            mSumQtty = mydb.getQttyValude(strEmp);
            mcountPeeling = mydb.getPeelingCount(strEmp);
            mCountPeelingAll = mydb.getPeelingCountAll();

            _sumQtty.setText(mSumQtty);
            _textCount.setText(String.format("%d",mcountPeeling));
            _textAllRecode.setText(String.format("%d",mCountPeelingAll));
            _edtScan.setText("");
            ShowDataEmp(strEmp);

        }
        return  true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        AlertDialog.Builder viewManu = new AlertDialog.Builder(this);


        if(id == R.id.action_deleteAll){
            viewManu.setTitle("ลบข้อมูล")
                    .setIcon(R.drawable.ic_delete_24)
                    .setMessage("ต้องการลบข้อมูลทั้งหมดใช่หรือไม่")
                    .setNegativeButton(android.R.string.cancel,null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(mydb.DeleteDataAll() == "OK"){
                                mcountPeeling = mydb.getPeelingCountAll();
                                _textAllRecode.setText(String.format("%d",mcountPeeling));
                                _sumQtty.setText("0");
                                ShowDataEmp("000000");
                                Toast.makeText(MainActivity.this, "ลบข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(MainActivity.this, "ลบข้อมูลไม่สำเร็จ", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
            viewManu.show();


            return true;
        }else if(id == R.id.action_exportData){

            viewManu.setTitle("ส่งออกข้อมูล")
                        .setIcon(R.drawable.ic_export)
                        .setMessage("ต้องการส่งออข้อมูลใช้หรือไม่")
                        .setNegativeButton(android.R.string.cancel,null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mydb.exportDatabase();
                                Toast.makeText(MainActivity.this, "ส่งข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();
                                uploadFile(location);
                            }
                        });
            viewManu.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public String chkOT(){
        boolean isCheckedOT = _chkOT.isChecked();
        if(isCheckedOT){
            strChkOT = "N";
        }else if(! isCheckedOT){
            strChkOT = "O";
        }
        return strChkOT;
    }

    public void bindwiget(){

        _datePicker = findViewById(R.id.date);
        _timePicker = findViewById(R.id.time);
        _strartTimePicker = findViewById(R.id.edtStratTime);
        _endTimePicker = findViewById(R.id.edtEndTime);
        _edtScan = findViewById(R.id.edtScan);
        _edtEmp = findViewById(R.id.edtEmp);
        _edtJobCode = findViewById(R.id.edtJobCode);
        _edtConvey = findViewById(R.id.edtConvey);
        _edtSession = findViewById(R.id.edtSession);
        _chkOT = findViewById(R.id.chkOT);
        _textCount = findViewById(R.id.textCountPeeling);
        _sumQtty = findViewById(R.id.textSumQtty);
        _listView = findViewById(R.id.listView);
        _textAllRecode = findViewById(R.id.textAllRecode);
        _cardViewAlldata = findViewById(R.id.cardViewAll);

    }
    public void beefScan(){
        final MediaPlayer beefScan = MediaPlayer.create(MainActivity.this,R.raw.beef_scan);
        beefScan.start();
    }
    public void beefEmp(){
        final MediaPlayer beefScan = MediaPlayer.create(MainActivity.this,R.raw.multimedia_notify);
        beefScan.start();
    }

    public void  uploadFile(String location){
        String Uri ="http://61.7.142.47:8086/peeling/uploadExdata.php?section="+location;

        final Notification.Builder notifBuilder = new Notification.Builder(getBaseContext()).setSmallIcon(R.mipmap.ic_launcher);
        final int id = 1122;
        final NotificationManager notifMan =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mIsUploading = true;
        Ion.with(this).load(Uri)
                .uploadProgress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        notifBuilder.setProgress((int)total,(int)downloaded,false);

                        Notification notif = notifBuilder.build();
                        notifMan.notify(id,notif);
                    }
                })
                .setMultipartFile("upload_file",new File(mydb.getPathFile()))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        notifBuilder.setProgress(100, 100, false);
                        notifBuilder.setContentText(result);
                        Notification notif = notifBuilder.build();
                        notifMan.notify(id,notif);
                        mIsUploading = false;
                        Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
                    }
                });
    }



}