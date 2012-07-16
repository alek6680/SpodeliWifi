package com.swifi.al;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Iterator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.database.DatabaseUtils.InsertHelper;

public class DatabaseAccessPoints extends SQLiteOpenHelper {

	private static int DATABASE_VERSION = 2;
	private static String DATABASE_NAME = "AccesPoints.db";
	private static String D_TABLE_APS = "accesspoints";
	private static String KEY_ID = "_id";
	private static String KEY_AP_NAME = "apname";
	private static String KEY_BSSID = "bssid";
	private static String KEY_PASS = "pass";
	private long mLongInserted;
	private boolean hasTables;

	private static final String D_TABLE_CREATE = "CREATE TABLE " + D_TABLE_APS
			+ "(" + KEY_ID + " INTEGER," + KEY_AP_NAME + " TEXT," + KEY_BSSID
			+ " TEXT," + KEY_PASS + " TEXT); ";

	public DatabaseAccessPoints(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DatabaseAccessPoints(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(D_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + D_TABLE_APS);
		onCreate(db);
	}

	public void addAccessPoints(AccessPoint tmp) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_AP_NAME, tmp.getmAPName());
		values.put(KEY_BSSID, tmp.getmBssid());
		values.put(KEY_PASS, tmp.getmPassword());
		// mLongInserted =
		db.insert(D_TABLE_APS, null, values);

		// db.close();
	}

	public String getLastAccessPointId() {
		String lastID = "";
		SQLiteDatabase db = this.getWritableDatabase();
		// order by _id DESC limit 1
		int lastId = 1;
		String query = "SELECT _id from accesspoints ORDER BY _id DESC LIMIT 1";
		// Cursor c = db.query(D_TABLE_APS, new String[] {KEY_AP_NAME},
		// null, null, null, null, null);
		Cursor c = db.rawQuery(query, null);
		if (c.moveToLast()) {

			// n=c.getCount();
			lastId = c.getInt(0);
			try {
				// lastId = c.getInt(0); // The 0 is the column index, we only
				// have
				// 1 column, so the index is 0
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("index= " + c.getInt(0) + " " + c.getCount()
					+ " rows " + c.getColumnCount() + " colsumns");
		}
		c.close();
		lastID = String.valueOf(lastId);
		return lastID;
	}

	public void deleteAP(AccessPoint accesspoint) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(D_TABLE_APS, KEY_PASS + "=?",
				new String[] { accesspoint.getmPassword() });
		db.close();
	}

	public boolean check() {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery("SELECT * FROM  accesspoints", null);

		if (cursor.getCount() == 0) {
			hasTables = false;
		}
		if (cursor.getCount() > 0) {
			hasTables = true;
		}
		cursor.close();

		return hasTables;
	}

	public void addAllAP(ArrayList<AccessPoint> tmp) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.setLockingEnabled(false);
			ContentValues values = new ContentValues();
			AccessPoint a = new AccessPoint();
			for (int i = 0; i < tmp.size(); i++) {
				a = tmp.get(i);
				values.put(KEY_ID, a.getmId());
				values.put(KEY_AP_NAME, a.getmAPName());
				values.put(KEY_BSSID, a.getmBssid());
				values.put(KEY_PASS, a.getmPassword());
				// mLongInserted =
				db.insert(D_TABLE_APS, null, values);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		db.close();
	}

	public void addAllAccessPoints(ArrayList<AccessPoint> tmp) {
		SQLiteDatabase db = this.getWritableDatabase();
		InsertHelper ih = new InsertHelper(db, D_TABLE_APS);
		// Get the numeric indexes for each of the columns that we're updating
		final int id = ih.getColumnIndex(KEY_ID);
		final int apname = ih.getColumnIndex(KEY_AP_NAME);
		final int bssid = ih.getColumnIndex(KEY_BSSID);
		final int pass = ih.getColumnIndex(KEY_PASS);

		try {
			db.beginTransaction();
			for (AccessPoint accessp : tmp) {
				ih.prepareForInsert();

				ih.bind(id, accessp.getmId());
				ih.bind(apname, accessp.getmAPName());
				ih.bind(bssid, accessp.getmBssid());
				ih.bind(pass, accessp.getmPassword());

				ih.execute();
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public ArrayList<AccessPoint> getAllAps() {
		ArrayList<AccessPoint> apsList = new ArrayList<AccessPoint>();
		SQLiteDatabase db = this.getWritableDatabase();
		String selectquery = "SELECT * FROM " + D_TABLE_APS + ";";
		Cursor cursor = db.rawQuery(selectquery, null);
		if (cursor.moveToFirst()) {
			do {
				AccessPoint access = new AccessPoint();
				access.setmAPName(cursor.getString(1));
				access.setmBssid(cursor.getString(2));
				access.setmPassword(cursor.getString(3));
				apsList.add(access);
			} while (cursor.moveToNext());
		}
		cursor.deactivate();
		cursor.close();
		return apsList;

	}
}
