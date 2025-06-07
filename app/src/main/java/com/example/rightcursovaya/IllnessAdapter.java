package com.example.rightcursovaya;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IllnessAdapter extends RecyclerView.Adapter<IllnessAdapter.ViewHolder> {
    private List<Illness> illnesses; // текущий отфильтрованный список
    private List<Illness> fullList; // текущий неотфильтрованный список


    private String role;
    private onIllnessActionListener listener;
    public IllnessAdapter(List<Illness> illnesses, String role, onIllnessActionListener listener) {
        this.illnesses = new ArrayList<>(illnesses);
        this.role = role;
        this.listener = listener;
        this.fullList = new ArrayList<>(illnesses);
    }

    @NonNull
    @Override
    public IllnessAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.illness_item, parent, false);
        return new IllnessAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IllnessAdapter.ViewHolder holder, int position) {
        Illness item = illnesses.get(position);
        holder.tvFullIllnessName.setText(item.getName());
        holder.tvdescription.setText("Описание: " + item.getDescription().toString());
        holder.tvRecommendations.setText("Рекомендации: " + item.getRecommendation());
        if (!"Врач".equals(role))
            holder.deleteButton.setVisibility(View.GONE);
        // Обработка нажатий
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClicked(item));
    }


    public void filter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        illnesses = new ArrayList<>();
        if (query.isEmpty()) {
            illnesses.addAll(fullList);
        } else {
            for (Illness ill: fullList) {
                if (ill.getName().toLowerCase(Locale.getDefault()).startsWith(query)) {
                    illnesses.add(ill);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<Illness> illnesses) {
        fullList.clear();
        fullList.addAll(illnesses);
        filter(""); // Показываем всё по умолчанию
    }

    @Override
    public int getItemCount() {
        return illnesses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullIllnessName;

        TextView tvdescription;
        TextView tvRecommendations;

        Button deleteButton;

        ViewHolder(View view) {
            super(view);
            tvFullIllnessName = itemView.findViewById(R.id.tvFullIllnessName);
            tvdescription = itemView.findViewById(R.id.tvDescription);
            tvRecommendations = itemView.findViewById(R.id.tvRecommendations);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
