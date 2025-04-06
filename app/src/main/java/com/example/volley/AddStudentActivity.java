package com.example.volley;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.volley.util.Convert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView img;
    private AppCompatButton load_image;
    private EditText first_name, last_name;
    private Spinner city;
    private RadioButton male, female;
    private DatePicker date_of_birth;
    private AppCompatButton add_student;

    private Bitmap selected_image;
    private String selected_city = "";

    private RequestQueue requestQueue;
    private final String insertUrl = "http://192.168.100.170/school/src/api/createStudent.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFC107")));
            getSupportActionBar().setTitle("Add student");
        }

        // Init views
        img = findViewById(R.id.img);
        load_image = findViewById(R.id.load_image);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        city = findViewById(R.id.city);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        date_of_birth = findViewById(R.id.date_of_birth);
        add_student = findViewById(R.id.add_student);

        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.cities)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_city = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_city = "";
            }
        });

        load_image.setOnClickListener(v -> openImageChooser());

        add_student.setOnClickListener(v -> {
            if (!validateInputs()) return;

            String gender = male.isChecked() ? "Male" : "Female";

            // Date formatting
            String dateOfBirth = String.format(
                    "%04d-%02d-%02d",
                    date_of_birth.getYear(),
                    date_of_birth.getMonth() + 1,
                    date_of_birth.getDayOfMonth()
            );

            // Create JSON payload
            JSONObject postData = new JSONObject();
            try {
                postData.put("firstName", first_name.getText().toString().trim());
                postData.put("lastName", last_name.getText().toString().trim());
                postData.put("city", selected_city);
                postData.put("gender", gender);
                postData.put("dateOfBirth", dateOfBirth);

                // Compress image before converting to Base64
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                if(selected_image != null) {
                    selected_image.compress(Bitmap.CompressFormat.JPEG, 50, byteStream);
                    postData.put("image", Base64.encodeToString(byteStream.toByteArray(), Base64.DEFAULT));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(AddStudentActivity.this, "Failed to build data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Initialize Volley request queue
            requestQueue = Volley.newRequestQueue(AddStudentActivity.this);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    insertUrl,
                    postData,
                    response -> {
                        try {
                            Log.d("Response", response.toString());
                            if (response.getBoolean("success")) {
                                Toast.makeText(AddStudentActivity.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                                clear();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddStudentActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("VolleyError", "Error: " + error.getMessage());
                        if (error.networkResponse != null) {
                            Log.e("VolleyError", "Status code: " + error.networkResponse.statusCode);
                        }

                        if (error instanceof TimeoutError) {
                            Toast.makeText(AddStudentActivity.this, "Server timeout. Check your network.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddStudentActivity.this, "Error adding student: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(request);
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                        selected_image = ImageDecoder.decodeBitmap(source);
                    } else {
                        selected_image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    }
                    img.setImageBitmap(selected_image);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean validateInputs() {
        if (first_name.getText().toString().isEmpty() || last_name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!male.isChecked() && !female.isChecked()) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void clear() {
        first_name.setText("");
        last_name.setText("");
        img.setImageResource(R.drawable.alumni);
        male.setChecked(false);
        female.setChecked(false);
        selected_image = null;
    }
}
