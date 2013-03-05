package who.euro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class EasyDB extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "easy.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "CREATE TABLE easydata (_id INTEGER PRIMARY KEY AUTOINCREMENT, inORout INT, timestampInfo TEXT, autoRecorded INT);";
        
    public EasyDB (Context ctx) {
    	super (ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(DATABASE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}