package com.example.rightcursovaya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Autorized extends Fragment {
    private ApiService api;

    private Button btnDelete;

    private Button btnVihod;

    private TextView tvLogin;
    private TextView tvRole;


    public Autorized() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autorized, container, false);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnVihod = view.findViewById(R.id.btnVihod);
        tvLogin = view.findViewById(R.id.tvLogin);
        tvRole = view.findViewById(R.id.tvRole);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("login", "Stranger");
        String role = sharedPreferences.getString("role", "Stranger");
        tvLogin.setText("Логин: " + login);
        tvRole.setText("Роль: " + role);
        btnVihod.setOnClickListener(v -> buttonVihod());
        btnDelete.setOnClickListener(v -> buttonDelete());
        return view;
    }

    private void buttonVihod() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_LONG).show();
        requireParentFragment()
                .getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.innerFragmentContainer, new No_autorized())
                .commit();
    }


    private void buttonDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удаление аккаунта")
                .setMessage("Вы уверены, что хотите удалить аккаунт? Это действие необратимо.")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    performAccountDeletion();
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performAccountDeletion() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("login", "Stranger");
        String password = sharedPreferences.getString("password", "defaultUsername");
        Client client = new Client(login, password);
        api = ApiClient.getApiService();
        Call<ResponseBody> call = api.delete(client);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String message = response.body().string();
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        requireParentFragment()
                                .getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.innerFragmentContainer, new No_autorized())
                                .commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Ошибка чтения ответа", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String message = response.errorBody().string();
                        Toast.makeText(requireContext(), "Ошибка: " + message, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}