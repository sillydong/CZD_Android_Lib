package czd.lib.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import czd.lib.application.ApplicationUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SQLiteUtil {

	public static final String SQLite_MASTER_TABLE = "sqlite_master";
	private static SQLiteUtil mInstance = new SQLiteUtil();

	/**
	 *
	 */
	private SQLiteUtil() {
	}

	/**
	 * @return instance of SQLiteUtil
	 */
	public static SQLiteUtil getInstance() {
		return SQLiteUtil.mInstance;
	}

	/**
	 * open or create a database with the given dbName:
	 * /data/data/packagename/databases/databasefilename
	 * /sdcard/databasefilename
	 *
	 * @param dbName
	 *
	 * @return SQLiteDatabase
	 */
	private SQLiteDatabase openDB(String dbName) {
		if (!dbName.contains("/"))
		{
			dbName = ApplicationUtil.application_context.getDatabasePath(dbName).getAbsolutePath();
		}
		File file = new File(dbName);
		if (file.exists())
		{
			return SQLiteDatabase.openDatabase(dbName, null, SQLiteDatabase.OPEN_READWRITE);
		}
		else
		{
			File datafiledir = new File(file.getParent());
			if (!datafiledir.exists())
			{
				datafiledir.mkdirs();
			}
			return SQLiteDatabase.openOrCreateDatabase(dbName, null);
		}
	}

	/**
	 * close database
	 *
	 * @param db
	 */
	private void closeDB(SQLiteDatabase db) {
		db.close();
	}

	/**
	 * delete database
	 *
	 * @param dbName
	 *
	 * @return boolean
	 */
	public boolean deleteDB(String dbName) {
		File file = new File(dbName);
		return !file.exists() || file.delete();
	}

	/**
	 * execute sql command
	 *
	 * @param dbName
	 * @param sql
	 */
	public void execQuery(String dbName, String sql) {
		SQLiteDatabase db = openDB(dbName);
		db.execSQL(sql);
		closeDB(db);
	}

	/**
	 * execute sql command and return Cursor
	 *
	 * @param dbName
	 * @param tableName
	 * @param condStr
	 *
	 * @return Cursor
	 */
	public Cursor openQuery(String dbName, String sql) {
		SQLiteDatabase db = openDB(dbName);
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		closeDB(db);
		return cursor;
	}

	public Cursor openQuery(String dbName, String tableName, String condStr) {
		SQLiteDatabase db = openDB(dbName);
		Cursor cursor = db.query(tableName, null, condStr, null, null, null, null);
		cursor.moveToFirst();
		closeDB(db);
		return cursor;
	}

	public int getRowsCount(Cursor cursor) {
		return cursor.getCount();
	}

	public int getColumnsCount(Cursor cursor) {
		return cursor.getColumnCount();
	}

	public String getColumnNameBy(Cursor cursor, int index) {
		return cursor.getColumnName(index);
	}

	public boolean isBOF(Cursor cursor) {
		return cursor.isBeforeFirst();
	}

	public boolean isEOF(Cursor cursor) {
		return cursor.isAfterLast();
	}

	public boolean moveNext(Cursor cursor) {
		return cursor.moveToNext();
	}

	public String getField(Cursor cursor, int index) {
		return cursor.getString(index);
	}

	public void closeQuery(Cursor cursor) {
		cursor.close();
	}

	public boolean isTableExists(String dbName, String tableName) {
		Cursor cursor = openQuery(dbName, SQLiteUtil.SQLite_MASTER_TABLE, "(tbl_name='" + tableName + "')");
		int recordCount = cursor.getCount();
		cursor.close();
		return (recordCount > 0);
	}

	public boolean isDatabaseExists(String dbName) {
		return new File(dbName).exists();
	}

	public static void copyDb(Context context, String dbName, int resourceId) {
		try
		{
			String databaseFilename = context.getDatabasePath(dbName).getPath();
			if (!(new File(databaseFilename)).exists())
			{
				InputStream is = context.getResources().openRawResource(resourceId);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
