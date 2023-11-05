package net.uoit.sofe4640.jpascual.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "assignment_app";
    private static final int DATABASE_SCHEMA_VERSION = 1;


    private static final String LOCATION_TABLE_NAME = "location";

    public AssignmentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the location table with four columns: the ID (auto incrementing), an address, its latitude, and its longitude.
        db.execSQL("CREATE TABLE " + LOCATION_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, address TEXT, latitude REAL, longitude REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int prev, int next) {
        // Database schema format conversion (migration).
        throw new UnsupportedOperationException("Invalid database schema migration from format " + prev + " to " + next);
    }

    public void recreateDb() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE " + LOCATION_TABLE_NAME + ";");

        onCreate(db);

        db.close();
    }

    public void addOrUpdateAppLocation(AppLocation location) {
        SQLiteDatabase db = getWritableDatabase();

        // Create the ContentValues.
        ContentValues values = new ContentValues();
        values.put("address", location.address);
        values.put("latitude", location.latitude);
        values.put("longitude", location.longitude);

        // If the ID is not -1, then this Location has already been inserted into the database.
        if (location.id != -1) {
            values.put("id", location.id);

            // Update the existing row.
            db.update(LOCATION_TABLE_NAME, values, "id = ?", new String[] { String.valueOf(location.id) });
        } else {
            values.putNull("id");

            // Insert a new row into the database.
            db.insertOrThrow(LOCATION_TABLE_NAME, null, values);
        }

        db.close();
    }

    private AppLocation getAppLocationFromCursor(Cursor cursor) {
        // Construct the AppLocation instance using the row data.
        AppLocation location = new AppLocation();
        location.id = cursor.getInt(0);
        location.address = cursor.getString(1);
        location.latitude = cursor.getDouble(2);
        location.longitude = cursor.getDouble(3);

        return location;
    }

    public AppLocation getAppLocationById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        // Query the database for the row with the specified ID.
        Cursor cursor = db.query(LOCATION_TABLE_NAME, new String[] { "id", "address", "latitude", "longitude" }, "id = ?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();

        // Read the data.
        AppLocation location = getAppLocationFromCursor(cursor);

        cursor.close();
        db.close();

        return location;
    }

    public AppLocation getAppLocationByAddress(String address) {
        SQLiteDatabase db = getReadableDatabase();

        // Query the database for the row with the specified ID.
        Cursor cursor = db.query(LOCATION_TABLE_NAME, new String[] { "id", "address", "latitude", "longitude" }, "address = ?", new String[] { address }, null, null, null, null);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();

        // Read the data.
        AppLocation location = getAppLocationFromCursor(cursor);

        cursor.close();
        db.close();

        return location;
    }

    public List<AppLocation> getAppLocations() {
        SQLiteDatabase db = getReadableDatabase();

        // Select every row from the database.
        Cursor cursor = db.rawQuery("SELECT * FROM " + LOCATION_TABLE_NAME, null);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();

        ArrayList<AppLocation> locations = new ArrayList<>();

        // Create AppLocation instances using the row data.
        while (!cursor.isAfterLast()) {
            AppLocation location = getAppLocationFromCursor(cursor);
            locations.add(location);

            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return locations;
    }

    public void deleteAppLocation(int id) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete any rows with this note's ID.
        db.delete(LOCATION_TABLE_NAME, "id = ?", new String[] { String.valueOf(id) });

        db.close();
    }
}
