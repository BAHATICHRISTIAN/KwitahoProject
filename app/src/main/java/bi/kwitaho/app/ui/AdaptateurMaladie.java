package bi.kwitaho.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import bi.kwitaho.app.data.model.Maladie;
import bi.kwitaho.app.databinding.ElementMaladieBinding;

public class AdaptateurMaladie extends ListAdapter<Maladie, AdaptateurMaladie.SupportVue> {
    public interface Ecouteur {
        void auClic(Maladie maladie);
    }

    private static final DiffUtil.ItemCallback<Maladie> COMPARATEUR = new DiffUtil.ItemCallback<Maladie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Maladie ancien, @NonNull Maladie nouveau) {
            return ancien.code.equals(nouveau.code);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Maladie ancien, @NonNull Maladie nouveau) {
            return ancien.code.equals(nouveau.code);
        }
    };

    private final Ecouteur ecouteur;

    public AdaptateurMaladie(Ecouteur ecouteur) {
        super(COMPARATEUR);
        this.ecouteur = ecouteur;
    }

    @NonNull
    @Override
    public SupportVue onCreateViewHolder(@NonNull ViewGroup parent, int typeVue) {
        return new SupportVue(ElementMaladieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SupportVue h, int position) {
        Maladie d = getItem(position);
        Context c = h.liaison.getRoot().getContext();
        h.liaison.imgMaladie.setImageResource(d.imageRes);
        h.liaison.txtNom.setText(d.nomRes);
        String code = d.saine ? "" : d.code + " • ";
        h.liaison.txtSous.setText(code + c.getString(d.natureRes));
        h.liaison.getRoot().setOnClickListener(v -> ecouteur.auClic(d));
    }

    static class SupportVue extends RecyclerView.ViewHolder {
        final ElementMaladieBinding liaison;

        SupportVue(ElementMaladieBinding liaison) {
            super(liaison.getRoot());
            this.liaison = liaison;
        }
    }
}
