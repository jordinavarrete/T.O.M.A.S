package com.puchdemont.tomas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EjemploAdapter extends RecyclerView.Adapter<EjemploAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Flight item);
    }

    private List<Flight> items;
    private OnItemClickListener listener;


    public EjemploAdapter(List<Flight> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
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
        Flight item = items.get(position);
        holder.texto.setText(item.getCode().get(0).getFlightNumber());

        // Configurar el listener para manejar el clic en cada elemento
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
