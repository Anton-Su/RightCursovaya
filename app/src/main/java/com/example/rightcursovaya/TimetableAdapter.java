package com.example.rightcursovaya;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {
    private List<Timetable> doctors;

    private String role;
    private OnDoctorActionListener listener;
    public TimetableAdapter(List<Timetable> doctors, String role, OnDoctorActionListener listener) {
        this.doctors = doctors;
        this.role = role;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimetableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableAdapter.ViewHolder holder, int position) {
        Timetable item = doctors.get(position);
        holder.tvFullName.setText(item.getSurname() + " " + item.getName() + " " + item.getPatronymic());
        holder.tvBirthday.setText("День рождения: " + item.getBirthday_date().toString());
        holder.tvSpecialization.setText("Специализация: " + item.getSpecialization());
        holder.tvVacation.setText("Отпуск: " + item.getStart_vacation_date().toString() + " — " + item.getEnd_vacation_date().toString());
        if ("Stranger".equals(role))
            holder.complainButton.setVisibility(View.GONE);
        if (!"Администратор".equals(role))
            holder.editButton.setVisibility(View.GONE);
        // Обработка нажатий
        holder.complainButton.setOnClickListener(v -> listener.onComplainClicked(item));
        holder.editButton.setOnClickListener(v -> listener.onEditClicked(item));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName;
        TextView tvBirthday;

        TextView tvSpecialization;
        TextView tvVacation;

        Button editButton;
        Button complainButton;

        ViewHolder(View view) {
            super(view);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvBirthday = itemView.findViewById(R.id.tvBirthday);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            tvVacation = itemView.findViewById(R.id.tvVacationDates);
            editButton = itemView.findViewById(R.id.btnEdit);
            complainButton = itemView.findViewById(R.id.btnReport);
        }
    }
}
