package who.euro;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class EasyWHOActivity extends Activity {
	
	public static final String PREFS_NAME = "easyPrefsFile";
	public static String currentDateString;
	    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button myButtonIn = (Button) findViewById(R.id.button1);
        Button myButtonOut = (Button) findViewById(R.id.button2);
//        TextView textOutput2 = (TextView) findViewById(R.id.textOutput2);
                
        SharedPreferences mySharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        boolean mylastInOrOut = mySharedPreferences.getBoolean("lastInOrOut", false);
        if (mylastInOrOut==false) {
        	myButtonOut.setEnabled(false);
        } else {
        	myButtonIn.setEnabled(false);
        }
        
    }
    
    public void checkIn (View view) {
    	
    	Button myButtonIn = (Button) findViewById(R.id.button1);
        Button myButtonOut = (Button) findViewById(R.id.button2);
        TextView textOutput1 = (TextView) findViewById(R.id.textOutput1);
        TextView textOutput2 = (TextView) findViewById(R.id.textOutput2);
    	
  		String myInfo = insertRecord(0, 0);
  		myButtonIn.setEnabled(false);
  		myButtonOut.setEnabled(true);
  		
  		textOutput1.setText(myInfo);
  		textOutput2.setText("");
  		
  		SharedPreferences mySharedPreferences = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = mySharedPreferences.edit();
	    editor.putBoolean("lastInOrOut", true);
	    editor.putString("lastInTS", currentDateString);
	    editor.commit();
	    
    }
    
  	public void checkOut (View view) throws ParseException {
  		
  	  	Button myButtonIn = (Button) findViewById(R.id.button1);
        Button myButtonOut = (Button) findViewById(R.id.button2);
        TextView textOutput1 = (TextView) findViewById(R.id.textOutput1);
        TextView textOutput2 = (TextView) findViewById(R.id.textOutput2);
  		
  		String myInfo = insertRecord(1, 0);
  		myButtonIn.setEnabled(true);
  		myButtonOut.setEnabled(false);
  		
  		String myWorkTimeInfo = calcWorkTime();
  		textOutput1.setText(myInfo);
		textOutput2.setText(myWorkTimeInfo);
  		
  		SharedPreferences mySharedPreferences = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = mySharedPreferences.edit();
	    editor.putBoolean("lastInOrOut", false);
	    editor.commit();
	    
  	}
    
  	public void seeRecords (View view) {
  		
  		Intent myIntent = new Intent(EasyWHOActivity.this, EasyListActivity.class);
  		EasyWHOActivity.this.startActivity(myIntent);
 
   	}
  	
  	private String insertRecord (int inORout, int autorecorded) {
  		
  		int myinORout = inORout;
  		int myAutorecorded = autorecorded;

  		EasyDB easyDBobject = new EasyDB(this);
	    SQLiteDatabase db1 = easyDBobject.getWritableDatabase();
	    ContentValues myValues = new ContentValues();
	    myValues.putNull("_id");
	    myValues.put("inORout", myinORout);
	    
	    //Get new time and round it
	    Date myDateAround =  new java.util.Date();
	    int myMinutes = myDateAround.getMinutes();
	    int mod = myMinutes % 10;
//	    Log.i("EasyWHO-tag", "mod: "+Integer.toString(mod));
	    int modT;
	    if (mod<=5) {
	    	modT=0;
	    } else {
	    	modT=10;
	    }
	    myDateAround.setMinutes(myMinutes-mod+modT);
	    
	    //testing in the log
//	    int myNewMinutes = myDateAround.getMinutes();
//	    Log.i("EasyWHO-tag", "myNewMinutes: "+Integer.toString(myNewMinutes));
	    
//	    Date myDate = new java.util.Date();
	    CharSequence cs = DateFormat.format("E_dd-MM-yyyy_kk:mm", myDateAround);
	    currentDateString = (String)cs;
  	    
	    myValues.put("timestampInfo", currentDateString);
	    myValues.put("autoRecorded", myAutorecorded);
	    db1.insert("easydata", null, myValues);
	    
//This is a trick for emptying the db
//    	db1.delete("easyData", null, null);
	    
	    db1.close();
	    
	    String myInfoMessageIN = "You have now checked in at:\n"+currentDateString;
	    String myInfoMessageOUT = "You have now checked out at:\n"+currentDateString;
	    String myInfoMessageERROR = "! There was a problem checking in or out...";
	    
	    if (myinORout == 0) {
	    	return myInfoMessageIN;
	    	} else if (myinORout == 1) {
	    		return myInfoMessageOUT;
	    	} else {
	    		return myInfoMessageERROR;
	    	}
  	
  	}
  	
  	private String calcWorkTime() throws ParseException {
  		
  		//get the lastInTS from preferences
  		SharedPreferences mySharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        String myLastInTS = mySharedPreferences.getString("lastInTS", null);
       
        //get a long for lastInTS
        SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("E_dd-MM-yyyy_kk:mm"); 
        Date lastInTSdate = mySimpleDateFormat.parse(myLastInTS);
        long lastInTSlong = lastInTSdate.getTime();
        
        //get a long for currentDateString
        Date currentDateStringDate = mySimpleDateFormat.parse(currentDateString);
        long currentDateStringLong = currentDateStringDate.getTime();
        
        //get result in milliseconds
        long diff = currentDateStringLong-lastInTSlong;   
        
        //milliseconds to hours, minutes, seconds
        int timeInSeconds = (int)diff / 1000;
        int hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        
        //return string
        String resultString = "You have been working for:\n"+hours+" hour/s and "+minutes+" minute/s";
        return resultString;
  	}

    
}

//TODO
//1. Make it possible to edit records
//2. Make dialogue boxes for "Are u sure?"
//3. Make tabs
//4. Implement proximity alert
//5. Make it efficient
//6. Make the app generic
//7. Find a way to keep the database under a max size