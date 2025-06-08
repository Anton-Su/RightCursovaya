package com.example.rightcursovaya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TimetableFragment extends Fragment {
    private ApiService api;
    private TimetableAdapter adapter;
    private List<Timetable> doctorList = new ArrayList<>();
    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        // Настраиваем RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Button addButton = rootView.findViewById(R.id.button5);
        // Создаем пустой список и адаптер
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "Stranger");
        if (!"Администратор".equals(role))
            addButton.setVisibility(View.GONE);
        addButton.setOnClickListener(v -> AddItem());
        adapter = new TimetableAdapter(doctorList, role, new OnDoctorActionListener(){

            @Override
            public void onComplainClicked(Timetable item) {
                LayoutInflater inflater = LayoutInflater.from(requireContext());
                View complainView = inflater.inflate(R.layout.dialog_edit_complains, null);
                EditText input = complainView.findViewById(R.id.edit_description);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Жалоба на врача: " + item.getName() + " " + item.getSurname() + " " + item.getPatronymic())
                        .setView(complainView)
                        .setPositiveButton("Отправить", (dialog, which) -> {
                            String complaintText = input.getText().toString().trim();
                            if (!complaintText.isEmpty()) {
                                // Отправляем жалобу на сервер
                                ComplainDo(item.getId(), complaintText);
                            } else {
                                Toast.makeText(requireContext(), "Введите текст жалобы", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            }
            @Override
            public void onEditClicked(Timetable item) {
                openEditDialog(item);
            }
        });
        recyclerView.setAdapter(adapter);
        // Делаем асинхронный запрос к серверу
        api = ApiClient.getApiService();
        api.getDoctors().enqueue(new Callback<List<Timetable>>() {
            @Override
            public void onResponse(Call<List<Timetable>> call, Response<List<Timetable>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doctorList.clear();
                    doctorList.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Обновляем адаптер
                } else {
                    // Обработка ошибки
                    Toast.makeText(requireContext(), "Ошибка получения данных", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Timetable>> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        return rootView;
    }

    private void ComplainDo(Long id_doctor, String complaintText) {

        Complain complain = new Complain(null, id_doctor, complaintText);
        api.createComplain(complain).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Жалоба отправлена", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), "Ошибка отправки жалобы", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openEditDialog(Timetable item) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_doctor, null);

        // Находим поля
        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editSurname = dialogView.findViewById(R.id.edit_description);
        EditText editPatronymic = dialogView.findViewById(R.id.edit_recommendations);
        EditText editBirthday = dialogView.findViewById(R.id.edit_birthday);
        EditText editSpecialization = dialogView.findViewById(R.id.edit_specialization);
        EditText editVacationStart = dialogView.findViewById(R.id.edit_vacation_start);
        EditText editVacationEnd = dialogView.findViewById(R.id.edit_vacation_end);

        // Подставляем текущие значения
        editName.setText(item.getName());
        editSurname.setText(item.getSurname());
        editPatronymic.setText(item.getPatronymic());
        editBirthday.setText(item.getBirthday_date());
        editSpecialization.setText(item.getSpecialization());
        editVacationStart.setText(item.getStart_vacation_date());
        editVacationEnd.setText(item.getEnd_vacation_date());

        // Создаём диалог
        new AlertDialog.Builder(requireContext())
                .setTitle("Редактировать врача")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    // Обновляем объект
                    item.setName(editName.getText().toString());
                    item.setSurname(editSurname.getText().toString());
                    item.setPatronymic(editPatronymic.getText().toString());
                    item.setBirthday_date(editBirthday.getText().toString());
                    item.setSpecialization(editSpecialization.getText().toString());
                    item.setStart_vacation_date(editVacationStart.getText().toString());
                    item.setEnd_vacation_date(editVacationEnd.getText().toString());
                    // Отправка на сервер
                    api.updateItem(item.getId(), item).enqueue(new Callback<Timetable>() {
                        @Override
                        public void onResponse(Call<Timetable> call, Response<Timetable> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Успешно обновлено", Toast.LENGTH_LONG).show();
                                adapter.notifyDataSetChanged(); // обновляем список
                            } else {
                                Toast.makeText(requireContext(), "Ошибка обновления", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Timetable> call, Throwable t) {
                            Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
    private void AddItem(){
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_doctor, null);
        // Находим поля
        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editSurname = dialogView.findViewById(R.id.edit_description);
        EditText editPatronymic = dialogView.findViewById(R.id.edit_recommendations);
        EditText editBirthday = dialogView.findViewById(R.id.edit_birthday);
        EditText editSpecialization = dialogView.findViewById(R.id.edit_specialization);
        EditText editVacationStart = dialogView.findViewById(R.id.edit_vacation_start);
        EditText editVacationEnd = dialogView.findViewById(R.id.edit_vacation_end);
        // Показываем диалог
        new AlertDialog.Builder(requireContext())
                .setTitle("Добавить врача")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    // Создаем объект Timetable
                    Timetable newItem = new Timetable();
                    newItem.setId(null); // ID будет сгенерирован на сервере
                    newItem.setName(editName.getText().toString());
                    newItem.setSurname(editSurname.getText().toString());
                    newItem.setPatronymic(editPatronymic.getText().toString());
                    newItem.setBirthday_date(editBirthday.getText().toString());
                    newItem.setSpecialization(editSpecialization.getText().toString());
                    newItem.setStart_vacation_date(editVacationStart.getText().toString());
                    newItem.setEnd_vacation_date(editVacationEnd.getText().toString());
                    // Отправка на сервер
                    api.createItem(newItem).enqueue(new Callback<Timetable>() {
                        @Override
                        public void onResponse(Call<Timetable> call, Response<Timetable> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(requireContext(), "Врач добавлен", Toast.LENGTH_LONG).show();
                                doctorList.add(response.body());
                                adapter.notifyItemInserted(doctorList.size() - 1);
                            } else {
                                Toast.makeText(requireContext(), "Ошибка добавления", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Timetable> call, Throwable t) {
                            Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}