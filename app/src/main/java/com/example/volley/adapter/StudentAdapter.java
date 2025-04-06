package com.example.volley.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.volley.R;
import com.example.volley.beans.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> implements Filterable {

    private List<Student> students;
    private List<Student> studentsFilter;
    private NewFilter filter;
    private Context context;

    public StudentAdapter(List<Student> students, Context context){
        this.students = students;
        this.context = context;
        studentsFilter = new ArrayList<>();
        studentsFilter.addAll(students);
        filter = new NewFilter(this);
    }

    @Override
    public NewFilter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_student_card, parent, false);
        final StudentViewHolder holder = new StudentViewHolder(view);

        holder.itemView.setOnClickListener(v -> {
            View updateStudentPopup = LayoutInflater.from(context).inflate(R.layout.activity_student_popup, null, false);
            final ImageView img = updateStudentPopup.findViewById(R.id.img);
            final TextView id = updateStudentPopup.findViewById(R.id.id);
            final TextView first_name = updateStudentPopup.findViewById(R.id.first_name);
            final TextView last_name = updateStudentPopup.findViewById(R.id.last_name);
            final Spinner city = updateStudentPopup.findViewById(R.id.city);
            final RadioGroup gender = updateStudentPopup.findViewById(R.id.gender);
            final DatePicker date_of_birth = updateStudentPopup.findViewById(R.id.date_of_birth);

            Student selectedStudent = studentsFilter.get(holder.getAdapterPosition());

            // Set image
            if (selectedStudent.getImage() != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(selectedStudent.getImage())
                        .into(img);
            }

            // Set text fields
            id.setText(String.valueOf(selectedStudent.getId()));
            first_name.setText(selectedStudent.getFirstName());
            last_name.setText(selectedStudent.getLastName());

            // Set city
            for (int i = 0; i < city.getCount(); i++) {
                if (city.getItemAtPosition(i).toString().equalsIgnoreCase(selectedStudent.getCity())) {
                    city.setSelection(i);
                    break;
                }
            }

            // Set gender
            if ("M".equalsIgnoreCase(selectedStudent.getGender())) {
                gender.check(R.id.male);
            } else if ("F".equalsIgnoreCase(selectedStudent.getGender())) {
                gender.check(R.id.female);
            }

            // Set date
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedStudent.getDateOfBirth());
            date_of_birth.updateDate(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(updateStudentPopup)
                    .setPositiveButton("Save", (dialogInterface, which) -> {
                        // Save logic can be added here
                        notifyItemChanged(holder.getAdapterPosition());
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.show();
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentViewHolder holder, int position) {
        Student student = studentsFilter.get(position);

        // Load image
        Glide.with(context)
                .asBitmap()
                .load(student.getImage())
                .apply(new RequestOptions().override(100, 100))
                .into(holder.img);

        // Bind all attributes
        holder.id.setText(String.valueOf(student.getId()));
        holder.first_name.setText(student.getFirstName());
        holder.last_name.setText(student.getLastName());
        holder.city.setText(student.getCity());
        holder.gender.setText(student.getGender());
        holder.date_of_birth.setText(student.getDateOfBirth().toString());
    }

    @Override
    public int getItemCount() {
        return studentsFilter.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView id, first_name, last_name, city, gender, date_of_birth;
        ImageView img;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.id = itemView.findViewById(R.id.id);
            this.first_name = itemView.findViewById(R.id.first_name);
            this.last_name = itemView.findViewById(R.id.last_name);
            this.city = itemView.findViewById(R.id.city);
            this.gender = itemView.findViewById(R.id.gender);
            this.date_of_birth = itemView.findViewById(R.id.data_of_birth);
            this.img = itemView.findViewById(R.id.img);
        }
    }

    public class NewFilter extends Filter {
        public RecyclerView.Adapter myAdapter;

        public NewFilter(RecyclerView.Adapter myAdapter) {
            super();
            this.myAdapter = myAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            studentsFilter.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                studentsFilter.addAll(students);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (Student student : students) {
                    if (student.getFirstName().toLowerCase().startsWith(filterPattern)
                            || student.getLastName().toLowerCase().startsWith(filterPattern)) {
                        studentsFilter.add(student);
                    }
                }
            }

            results.values = studentsFilter;
            results.count = studentsFilter.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            studentsFilter = (List<Student>) results.values;
            this.myAdapter.notifyDataSetChanged();
        }
    }
}
