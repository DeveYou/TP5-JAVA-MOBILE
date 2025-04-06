package com.example.volley;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.volley.adapter.StudentAdapter;
import com.example.volley.beans.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ListStudentsActivity extends AppCompatActivity {

    private RecyclerView studentList;
    private Toolbar toolbar;
    private StudentAdapter studentAdapter = null;
    private List<Student> students;
    private static final String loadUrl = "http://192.168.100.170/school/src/api/loadStudent.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_students);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFC107")));
            getSupportActionBar().setTitle("Students list");
        }

        students = new ArrayList<>();

        studentList = findViewById(R.id.studentList);
        studentList.setLayoutManager(new LinearLayoutManager(this));
        fetchStudents();

    }

    private void fetchStudents(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d("ListStudent", "fetched !!!!!");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, loadUrl, null,
                response -> {
                    Log.d("Volley", "Response: " + response.toString());
                    students.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Student student = new Student();
                            student.setId(obj.getInt("id"));
                            student.setFirstName(obj.getString("firstName"));
                            student.setLastName(obj.getString("lastName"));
                            student.setCity(obj.getString("city"));
                            student.setGender(obj.getString("gender"));
                            student.setDateOfBirth(Date.valueOf(obj.getString("dateOfBirth")));
                            student.setImage(
                                    obj.isNull("image") ? null : Base64.decode(obj.getString("image"), Base64.DEFAULT)
                            );

                            students.add(student);
                        } catch (JSONException e) {
                            Log.e("Parse error", e.toString());
                        }
                    }
                    studentAdapter = new StudentAdapter(students, ListStudentsActivity.this);
                    studentList.setAdapter(studentAdapter);
                    studentAdapter.notifyDataSetChanged();
                },
                error -> Log.e("Volley error", error.toString())
        );

        // Set a longer timeout to 30 sec
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(studentAdapter != null)
                    studentAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_student){
            Intent intent = new Intent(ListStudentsActivity.this, AddStudentActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}