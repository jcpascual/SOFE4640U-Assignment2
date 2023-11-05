package net.uoit.sofe4640.jpascual.assignment2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppLocationAdapter extends RecyclerView.Adapter<AppLocationAdapter.AppLocationViewHolder> {
    private final AssignmentDatabaseHelper databaseHelper;

    private List<AppLocation> locations;

    public static class AppLocationViewHolder extends RecyclerView.ViewHolder {
        public final View parentView;
        public final TextView textView_address;
        public final ImageButton imageButton_edit;
        public final ImageButton imageButton_delete;

        public AppLocationViewHolder(View view) {
            super(view);

            parentView = view;
            textView_address = view.findViewById(R.id.textView_address);
            imageButton_edit = view.findViewById(R.id.imageButton_edit);
            imageButton_delete = view.findViewById(R.id.imageButton_delete);
        }
    }

    public AppLocationAdapter(AssignmentDatabaseHelper helper) {
        super();

        databaseHelper = helper;
        locations = databaseHelper.getAppLocations();
    }

    @NonNull
    @Override
    public AppLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_location, parent, false);
        return new AppLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppLocationViewHolder holder, int position) {
        // Get the AppLocation at this position.
        AppLocation location = locations.get(position);

        // Set the TextView's content to the address.
        holder.textView_address.setText(location.address);

        // Set an listener for the edit button being pressed.
        holder.imageButton_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the AppLocation at this position. We can't use position here as it may not be accurate.
                AppLocation location = locations.get(holder.getAdapterPosition());

                // Create an Intent that opens the EditActivity with the current AppLocation's ID.
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra("id", location.id);

                // Start the Activity.
                Activity activity = (Activity)v.getContext();
                activity.startActivityForResult(intent, MainActivity.REQUEST_EDIT);
            }
        });

        // Set an listener for the delete button being pressed.
        holder.imageButton_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the AppLocation at this position. We can't use position here as it may not be accurate.
                AppLocation location = locations.get(holder.getAdapterPosition());

                // Delete this AppLocation.
                databaseHelper.deleteAppLocation(location.id);

                // Refresh the data in this view.
                refreshList();
            }
        });

        // Set an listener for this view being pressed.
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the AppLocation at this position. We can't use position here as it may not be accurate.
                AppLocation location = locations.get(holder.getAdapterPosition());

                // Show an alert with this AppLocation's address, longitude, and latitude.
                String message = location.address + " is located at the coordinates (" + location.latitude + ", " + location.longitude + ").";

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.alert_location_title)
                        .setMessage(message)
                        .setPositiveButton(R.string.alert_location_button_dismiss, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void refreshList() {
        // Refetch the AppLocations list from the database.
        locations = databaseHelper.getAppLocations();

        // Notify the RecyclerView code that the dataset has changed.
        notifyDataSetChanged();
    }
}
