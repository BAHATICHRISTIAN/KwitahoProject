package bi.kwitaho.app.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import bi.kwitaho.app.databinding.ElementCarteAstuceBinding;

/** Carrousel horizontal de conseils. */
public class ConseilsHome extends RecyclerView.Adapter<ConseilsHome.SupportVue> {
    private final int[] astuces;
    private final Runnable onTipClick;

    public ConseilsHome(int[] astuces, Runnable onTipClick) {
        this.astuces = astuces;
        this.onTipClick = onTipClick;
    }

    @NonNull
    @Override
    public SupportVue onCreateViewHolder(@NonNull ViewGroup parent, int typeVue) {
        return new SupportVue(ElementCarteAstuceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SupportVue h, int position) {
        h.liaison.txtAstuce.setText(astuces[position]);
        // Un appui sur une carte ouvre la vraie section « Conseils »
        h.liaison.getRoot().setOnClickListener(v -> onTipClick.run());
    }

    @Override
    public int getItemCount() {
        return astuces.length;
    }

    static class SupportVue extends RecyclerView.ViewHolder {
        final ElementCarteAstuceBinding liaison;

        SupportVue(ElementCarteAstuceBinding liaison) {
            super(liaison.getRoot());
            this.liaison = liaison;
        }
    }
}
