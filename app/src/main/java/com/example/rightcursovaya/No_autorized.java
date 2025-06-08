package com.example.rightcursovaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class No_autorized extends Fragment {
    public No_autorized() {
        // Required empty public constructor
    }

    private ApiService api;

    private Spinner spinnerRole;
    private Spinner spinnerAction;
    private EditText etSpecialCode;
    private Button btnLogin;
    private Button btnReg;

    private EditText etLogin;
    private EditText etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_autorized, container, false);

        // Инициализация ApiService
        api = ApiClient.getApiService();

        spinnerRole = view.findViewById(R.id.spinnerRole);
        spinnerAction = view.findViewById(R.id.spinnerAction);
        etSpecialCode = view.findViewById(R.id.etSpecialCode);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnReg = view.findViewById(R.id.btnReg);
        etLogin = view.findViewById(R.id.tvLogin);
        etPassword = view.findViewById(R.id.etPassword);

        // Настройка Spinner Role
        String[] roles = getResources().getStringArray(R.array.roles);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setSelection(2);

        // Настройка Spinner Action
        String[] action_registration = getResources().getStringArray(R.array.registration_or_autorization);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, action_registration);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAction.setAdapter(spinnerAdapter);
        spinnerAction.setSelection(1);

        // Обработка выбора в spinnerAction
        spinnerAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                String selectedAction = parent.getItemAtPosition(position).toString();
                if (selectedAction.equals("Авторизация")) {
                    etSpecialCode.setVisibility(View.GONE);
                    etSpecialCode.setText("");
                    btnReg.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    spinnerRole.setVisibility(View.GONE);
                }
                else {
                    etSpecialCode.setVisibility(View.VISIBLE);
                    spinnerRole.setVisibility(View.VISIBLE);
                    btnReg.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Обработчики кнопок
        btnReg.setOnClickListener(v -> buttonRegister());
        btnLogin.setOnClickListener(v -> buttonLogin());
        View rootLayout = view.findViewById(R.id.rootLayout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void buttonRegister() {
        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        String role = spinnerRole.getSelectedItem().toString();
        String specialCode = etSpecialCode.getText().toString();
        Client client = new Client(login, password, role, specialCode);
        Call<ResponseBody> call = api.register(client);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String message = response.body().string();
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Ошибка чтения ответа", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String message = response.errorBody().string();
                        Toast.makeText(requireContext(), "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buttonLogin() {
        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        Client client = new Client(login, password);
        Call<ResponseBody> call = api.login(client);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String role = response.body().string();
                        String message = "Успешный вход как «" + role + "»";
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("login", login);
                        editor.putString("password", password);
                        editor.putString("role", role);
                        editor.apply();
                        requireParentFragment()
                                .getChildFragmentManager()
                                .beginTransaction()
                                .replace(R.id.innerFragmentContainer, new Autorized())
                                .commit();


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Ошибка чтения ответа", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String message = response.errorBody().string();
                        Toast.makeText(requireContext(), "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}