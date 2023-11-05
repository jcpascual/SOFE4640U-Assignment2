package net.uoit.sofe4640.jpascual.assignment2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.uoit.sofe4640.jpascual.assignment2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_EDIT = 1;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private AppLocationAdapter locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Grab the helper from our AssignmentApplication instance.
        AssignmentDatabaseHelper helper = ((AssignmentApplication)getApplication()).getDatabaseHelper();

        // Create a new AppLocationAdapter.
        locationAdapter = new AppLocationAdapter(helper);

        // Set up the RecyclerView.
        binding.recyclerView.setAdapter(locationAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Open the EditActivity class with a blank Intent when the plus button is pressed.
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent, REQUEST_EDIT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Ignore any requests that aren't ours.
        if (requestCode != REQUEST_EDIT) {
            return;
        }

        // Refresh the RecyclerView's adapter.
        locationAdapter.refreshList();
    }
}