package com.example.poyecto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {

    public interface ItemExtractor<T> {
        String getTitle(T item);
        String getSubtitle(T item);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item, int position);
    }

    private List<T> datos;
    private ItemExtractor<T> extractor;
    private OnItemClickListener<T> listener;

    public GenericAdapter(List<T> datos, ItemExtractor<T> extractor, OnItemClickListener<T> listener) {
        this.datos = datos;
        this.extractor = extractor;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = datos.get(position);
        holder.txtPrincipal.setText(extractor.getTitle(item));
        holder.txtSecundario.setText(extractor.getSubtitle(item));
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) listener.onItemClick(item, position);
        });
    }

    @Override
    public int getItemCount() { return datos.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPrincipal, txtSecundario;
        public ViewHolder(View v) {
            super(v);
            txtPrincipal = v.findViewById(R.id.txtPrincipal);
            txtSecundario = v.findViewById(R.id.txtSecundario);
        }
    }
}