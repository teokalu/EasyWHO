package who.euro;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class EasyListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_example);
				
		EasyDB easyDBobject = new EasyDB(this);
		SQLiteDatabase db1 = easyDBobject.getReadableDatabase();
		String [] myColumns = {"_id", "inORout", "timestampInfo", "autoRecorded"};
		Cursor myCursor = db1.query("easydata", myColumns, null, null, null, null, null);
		int[] views = new int[] {R.id.ena, R.id.dio, R.id.tria, R.id.tesera};
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_example_entry, myCursor, myColumns, views);
		this.setListAdapter(adapter);
	
		
	}
}
