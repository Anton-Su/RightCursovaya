package com.example.rightcursovaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import java.util.List;


public class RegFragment extends Fragment {

    private Button btnSave;
    public RegFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout фрагмента (создай layout fragment_main.xml с твоим UI)
        View view = inflater.inflate(R.layout.fragment_reg, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "Stranger");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if ("Stranger".equals(role))
            transaction.replace(R.id.innerFragmentContainer, new No_autorized()); // или NotLoggedFragment()
        else
            transaction.replace(R.id.innerFragmentContainer, new Autorized());
        transaction.commit();
        View rootLayout = view.findViewById(R.id.rootLayout);
        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> SaveToSqLite());
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        return view;
    }


    private void SaveToSqLite(){
        SQLiteHelper db = new SQLiteHelper(requireContext());
        ApiService api = ApiClient.getApiService();
        api.getIlnesses().enqueue(new Callback<List<Illness>>() {
            @Override
            public void onResponse(Call<List<Illness>> call, Response<List<Illness>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Illness> illnesses = response.body();
                    for (Illness ill : illnesses) {
                        db.insertIllness(ill);
                    }
                    Toast.makeText(requireContext(), "Импорт болезней завершён", Toast.LENGTH_LONG).show();

                } else {
                    // Обработка ошибки
                    Toast.makeText(requireContext(), "Ошибка получения данных", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Illness>> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


