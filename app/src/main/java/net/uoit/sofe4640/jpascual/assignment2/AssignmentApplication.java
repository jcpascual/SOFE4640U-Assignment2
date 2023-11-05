package net.uoit.sofe4640.jpascual.assignment2;


import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class AssignmentApplication extends Application {
    // Our database helper. We store it here as the Application instance can be accessed from all contexts.
    private AssignmentDatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        databaseHelper = new AssignmentDatabaseHelper(this);

        // DEBUG: Delete the database contents.
        // databaseHelper.recreateDb();

        // Check if there's anything in the database. If there isn't, import the initial data.
        if (databaseHelper.getAppLocations().size() == 0) {
            // Create a new Geocoder instance.
            Geocoder geocoder = new Geocoder(this);

            // Open the file containing the coordinate pairs.
            InputStream inputStream = getResources().openRawResource(R.raw.locations);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Loop over every line in the file.
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    // Split the line by the comma character.
                    String[] splitLine = line.split(",");

                    // Parse the latitude and longitude values from their respective Strings.
                    double latitude = Double.valueOf(splitLine[1]);
                    double longitude = Double.valueOf(splitLine[2]);

                    // Look up the address for these coordinates.
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    // Check if the look up succeeded.
                    if (addresses.size() < 1) {
                        Log.e("AssignmentApplication", "failed to look up address for " + line);
                        continue;
                    }

                    // Get the returned Address instance.
                    Address address = addresses.get(0);

                    // Create a new AppLocation instance for this address and coordinates.
                    AppLocation location = new AppLocation();
                    location.address = address.getAddressLine(0);
                    location.latitude = latitude;
                    location.longitude = longitude;

                    // Store it in the database.
                    databaseHelper.addOrUpdateAppLocation(location);
                }

                reader.close();
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Log.d("AssignmentApplication", "imported " + databaseHelper.getAppLocations().size() + " locations");
        }
    }

    public AssignmentDatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}

