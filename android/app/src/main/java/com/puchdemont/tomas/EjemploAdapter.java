package com.puchdemont.tomas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EjemploAdapter extends RecyclerView.Adapter<EjemploAdapter.ViewHolder> {

    private List<String> items;

    public EjemploAdapter(List<String> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView texto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(android.R.id.text1);
        }
    }

    @NonNull
    @Override
    public EjemploAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EjemploAdapter.ViewHolder holder, int position) {
        holder.texto.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
