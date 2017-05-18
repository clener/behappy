package whitewire.behappy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.inset;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "spinnerDates";
    private static final String TABLE_NAME = "labels";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_MOOD = "mood";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_MOOD + " MOOD,"
                + COLUMN_MESSAGE + " TEXT," + COLUMN_DAY + " DAY," + COLUMN_MONTH
                + " MONTH," + COLUMN_YEAR + " YEAR)";
        db.execSQL(CREATE_ITEM_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertLabel(String message, int day, int month, int year, int mood) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MOOD, mood);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_YEAR, year);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);//tableName, nullColumnHack, ContentValues
        db.close(); // Closing database connection
    }

    public ArrayList<Message> getMessage(int day, int month, int year) {
        ArrayList<Message> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // to use after this query.
        String[] projection = {
                DatabaseHandler.COLUMN_ID,
                DatabaseHandler.COLUMN_MOOD,
                DatabaseHandler.COLUMN_MESSAGE,
                DatabaseHandler.COLUMN_DAY,
                DatabaseHandler.COLUMN_MONTH,
                DatabaseHandler.COLUMN_YEAR
        };

        String[] selectionArgs = {Integer.toString(day), Integer.toString(month),
                Integer.toString(year)};

        String selectQuery;
        Cursor cursor;
        boolean check = false;

        if (day == -1) {
            if (month == -1) {
                if (year == -1) {
                    selectQuery = "SELECT * FROM " + TABLE_NAME;
                } else {
                    selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE month >= 1 AND month <= 12 " +
                    "AND day >= 1 AND day <=31 AND year == " + year;
                }
            } else if (month != -1 && year == -1){
                selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE day >= 1 AND day <=31 AND month == " +
                        month + " AND year >= 2015";
            } else {
                selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE day >= 1 AND day <=31 AND month == " +
                month + " AND year == " + year;
            }
        } else if (month == -1) {
            if (year == -1) {
                selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE month >= 1 AND month <= 12 " +
                        "AND year >= 2015 AND year <=2020 AND day == " + day;
            } else {
                selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE day >= 1 AND day <=31 AND day == " +
                day + " AND year == " + year;
            }
        } else if (year == -1) {
            selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE year >= 2015 AND year <=2020 AND day == " +
                    day + " AND month == " + month;
        } else {
            selectQuery = DatabaseHandler.COLUMN_DAY + "=?" + " AND " +
                    DatabaseHandler.COLUMN_MONTH + "=?" + " AND " +
                    DatabaseHandler.COLUMN_YEAR + "=?";
            check = true;
        }

        if (check == true) {
            cursor = db.query(
                DatabaseHandler.TABLE_NAME,
                projection,                               // The columns to return
                selectQuery,                              // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null);
        } else {
            cursor = db.rawQuery(selectQuery, null);
        }

        while (cursor.moveToNext()) {
            String message = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_MESSAGE));
            String mood = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_MOOD));
            String dDay = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_DAY));
            String dMonth= cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_MONTH));
            String dYear = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_YEAR));
            list.add(new Message(message, Integer.parseInt(mood), Integer.parseInt(dDay), Integer.parseInt(dMonth),
                    Integer.parseInt(dYear)));
        }
        // closing connection
        cursor.close();
        db.close();
        // returning labels
        return list;
    }

    public ArrayList<Message> clearDatabase() {
        String clearDBQuery = "DELETE FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(clearDBQuery);
        ArrayList<Message> list = new ArrayList<>();
        return list;
    }
}
