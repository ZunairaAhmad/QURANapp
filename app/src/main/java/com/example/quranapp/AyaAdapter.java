package com.example.quranapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AyaAdapter extends RecyclerView.Adapter<AyaAdapter.AyaViewHolder> {
    private List<Aya> ayas;
    private int selectedTranslation;
    public AyaAdapter(List<Aya> ayas, int selectedTranslation) {
        this.ayas = ayas;
        this.selectedTranslation = selectedTranslation;
    }

    public void setSelectedTranslation(int selectedTranslation) {
        this.selectedTranslation = selectedTranslation;
    }

    @NonNull
    @Override
    public AyaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translation_item, parent, false);
        return new AyaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AyaViewHolder holder, int position) {
        Aya aya = ayas.get(position);

        String translation = aya.urduTranslation;

        switch (selectedTranslation) {
            case 0:
                translation = aya.urduTranslation;
                break;
            case 1:
                translation = aya.englishTranslation;
                break;
            case 2:
                translation = aya.sindhiTranslation;
                break;
            case 3:
                translation = aya.hindiTranslation;
                break;
            case 4:
                translation = aya.pushtoTranslation;
                break;
        }

        String text = aya.text + "\n" + translation;
        holder.textViewTranslation.setText(text);
    }


    @Override
    public int getItemCount() {

        return ayas.size();
    }

    public static class AyaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTranslation;

        public AyaViewHolder(View itemView) {
            super(itemView);
            textViewTranslation = itemView.findViewById(R.id.textViewTranslation);
        }
    }
}

