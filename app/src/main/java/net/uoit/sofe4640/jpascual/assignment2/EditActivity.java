package net.uoit.sofe4640.jpascual.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.uoit.sofe4640.jpascual.assignment2.databinding.ActivityEditBinding;

import java.io.IOException;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityEditBinding binding;

    private AssignmentDatabaseHelper databaseHelper;

    private AppLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Grab the database helper from our AssignmentApplication instance.
        databaseHelper = ((AssignmentApplication)getApplication()).getDatabaseHelper();

        // Grab the ID from the Intent. If it doesn't exist, use -1 as default.
        int id = getIntent().getIntExtra("id", -1);

        // Check if we did not extract an ID.
        if (id == -1) {
            // Create a new AppLocation instance.
            location = new AppLocation();
        } else {
            // Get the AppLocation from the database.
            location = databaseHelper.getAppLocationById(id);

            // Set the EditText content to the address.
            binding.editTextAddress.setText(location.address);
        }

        // Save the details to the AppLocation if the save button is pressed and close the Activity.
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new Geocoder instance.
                Geocoder geocoder = new Geocoder(EditActivity.this);

                // Look up the coordinates for this address.
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(binding.editTextAddress.getText().toString(), 1);
                } catch (IOException e) {
                    showLookupErrorAlert();
                    return;
                }

                // If there are no results, show the error.
                if (addresses.size() < 1) {
                    showLookupErrorAlert();
                    return;
                }

                // Get the result.
                Address address = addresses.get(0);

                // Copy the details to the AppLocation instance.
                location.address = address.getAddressLine(0);
                location.latitude = address.getLatitude();
                location.longitude = address.getLongitude();

                // Update the database.
                databaseHelper.addOrUpdateAppLocation(location);

                // Show an alert with the found full address and the coordinates.
                String message = location.address + " is located at the coordinates (" + location.latitude + ", " + location.longitude + ").";

                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setTitle(R.string.alert_normal_lookup_success_title)
                        .setMessage(message)
                        .setPositiveButton(R.string.alert_normal_lookup_success_button_dismiss, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Dismiss the dialog and finish this Activity.
                                dialog.dismiss();
                                finish();
                            }
                        });

                builder.show();
            }
        });

        // Close the Activity if the cancel button is pressed.
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Shows an error alert saying that the lookup failed.
    private void showLookupErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_error_lookup_fail_title)
                .setMessage(R.string.alert_error_lookup_fail_message)
                .setPositiveButton(R.string.alert_error_lookup_fail_button_dismiss, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }
}