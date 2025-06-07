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
import android.text.Editable;
import android.text.TextWatcher;
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


public class ILLFragment extends Fragment {

    private ApiService api;
    private IllnessAdapter adapter;
    private List<Illness> illnesses = new ArrayList<>();

    public ILLFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_i_l_l, container, false);
        // Настраиваем RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.illnessList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Button addButton = rootView.findViewById(R.id.button10);
        // Создаем пустой список и адаптер
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "Stranger");
        if (!"Врач".equals(role))
            addButton.setVisibility(View.GONE);
        addButton.setOnClickListener(v -> AddItem());
        adapter = new IllnessAdapter(illnesses, role, new onIllnessActionListener(){
            @Override
            public void onDeleteClicked(Illness item) {
                // Создаём AlertDialog для подтверждения
                new AlertDialog.Builder(requireContext())
                        .setTitle("Подтверждение")
                        .setMessage("Вы уверены, что хотите удалить болезнь: " + item.getName() + "?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            Deletion(item);
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
        // Делаем асинхронный запрос к серверу
        api = ApiClient.getApiService();
        api.getIlnesses().enqueue(new Callback<List<Illness>>() {
            @Override
            public void onResponse(Call<List<Illness>> call, Response<List<Illness>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    illnesses.clear();
                    illnesses.addAll(response.body());
                    adapter.updateData(illnesses); // Обновляем адаптер
                } else {
                    // Обработка ошибки
                    Toast.makeText(requireContext(), "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Illness>> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        EditText editTextIllness = rootView.findViewById(R.id.editTextIllness);
        editTextIllness.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        return rootView;
    }


    private void Deletion(Illness item) {
        api.deleteIllness(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Удалено успешно", Toast.LENGTH_SHORT).show();
                    // Обновляем локальный список и адаптер
                    illnesses.removeIf(i -> i.getId().equals(item.getId()));
                    adapter.updateData(illnesses);
                } else {
                    Toast.makeText(requireContext(), "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AddItem(){
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_illnesses, null);

        // Находим поля
        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editdescription = dialogView.findViewById(R.id.edit_description);
        EditText edit_recommendations = dialogView.findViewById(R.id.edit_recommendations);
        // Показываем диалог
        new AlertDialog.Builder(requireContext())
                .setTitle("Добавить врача")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    // Создаем объект Timetable
                    Illness newItem = new Illness();
                    newItem.setId(null); // ID будет сгенерирован на сервере
                    newItem.setName(editName.getText().toString());
                    newItem.setDescription(editdescription.getText().toString());
                    newItem.setRecommendation(edit_recommendations.getText().toString());
                    // Отправка на сервер
                    api.createIllness(newItem).enqueue(new Callback<Illness>() {
                        @Override
                        public void onResponse(Call<Illness> call, Response<Illness> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(requireContext(), "Врач добавлен", Toast.LENGTH_SHORT).show();
                                illnesses.add(response.body());
                                adapter.updateData(illnesses);
                            } else {
                                Toast.makeText(requireContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Illness> call, Throwable t) {
                            Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}