package vatsalchavda.mobileusagerestriction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {

    private DatabaseReference mDatabase;

    public static final String DATABASE_NAME = "Speed_Pattern";
    public static final String TABLE_NAME = "Driver_Details";
    public static final String userUID = "UserID";
    public static final String START_LATITUDE = "start_Latitude";
    public static final String START_LONGITUDE = "start_Longitude";
    public static final String END_LATITUDE = "end_Latitude";
    public static final String END_LONGITUDE = "end_Longitude";
    public static final String START_TIME = "start_Time";
    public static final String END_TIME = "end_Time";
    public static final String DISTANCE = "DISTANCE";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, 1);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "START_LATITUDE DOUBLE, END_LATITUDE DOUBLE, START_LONGITUDE DOUBLE, END_LONGITUDE DOUBLE," +
                "START_TIME TEXT, END_TIME TEXT, DISTANCE DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUserData(double startLatitude, double stopLatitude, double startLongitude, double stopLongitude,
                                  String startTime, String stopTime, double distance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
       // contentValues.put(userUID,UID);
        contentValues.put(START_LATITUDE,startLatitude);
        contentValues.put(END_LATITUDE,stopLatitude);
        contentValues.put(START_LONGITUDE,startLongitude);
        contentValues.put(END_LONGITUDE,stopLongitude);
        contentValues.put(START_TIME,startTime);
        contentValues.put(END_TIME,stopTime);
        contentValues.put(DISTANCE,distance);
        long result = db.insert(TABLE_NAME,null, contentValues);

        //Insert data in Firebase Database
        String accountName = LoginActivity.accountName;
        mDatabase.child(accountName).child("StartLatitude").setValue(startLatitude);
        mDatabase.child(accountName).child("StopLatitude").setValue(stopLatitude);
        mDatabase.child(accountName).child("StartLongitude").setValue(startLongitude);
        mDatabase.child(accountName).child("stopLongitude").setValue(stopLongitude);
        mDatabase.child(accountName).child("startTime").setValue(startTime);
        mDatabase.child(accountName).child("stopTime").setValue(stopTime);
        mDatabase.child(accountName).child("distance").setValue(distance);

        return result != -1;
    }

    public Cursor allData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Driver_Details",null);
        return cursor;
    }

}
