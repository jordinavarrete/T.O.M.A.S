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
        TextView flightCode;
        TextView flightDeparture;
        TextView flightGate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flightCode = itemView.findViewById(R.id.flightCodeTextView);
            flightDeparture = itemView.findViewById(R.id.flightDepartureTextView);
            flightGate = itemView.findViewById(R.id.flightGateTextView);
        }
    }

    @NonNull
    @Override
    public EjemploAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flight, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EjemploAdapter.ViewHolder holder, int position) {
        Flight item = items.get(position);
        holder.flightCode.setText(item.getCode().get(0).getFlightNumber());
        // Posar funcio
        holder.flightDeparture.setText("Departure: " + "12:30");
        holder.flightGate.setText("Gate: " + item.getLocation().getGate());

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
