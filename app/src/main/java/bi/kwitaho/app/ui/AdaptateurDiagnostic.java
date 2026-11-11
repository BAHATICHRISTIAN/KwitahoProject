package bi.kwitaho.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;

import bi.kwitaho.app.R;
import bi.kwitaho.app.data.db.EntiteDiagnostic;
import bi.kwitaho.app.data.model.NiveauConfiance;
import bi.kwitaho.app.data.model.Maladie;
import bi.kwitaho.app.data.model.CatalogueMaladies;
import bi.kwitaho.app.databinding.ElementDiagnosticBinding;
import bi.kwitaho.app.util.FormatTemps;

public class AdaptateurDiagnostic extends ListAdapter<EntiteDiagnostic, AdaptateurDiagnostic.SupportVue> {
    public interface Ecouteur {
        void auClic(EntiteDiagnostic entite);

        void auSuppression(EntiteDiagnostic entite);
    }

    private static final DiffUtil.ItemCallback<EntiteDiagnostic> COMPARATEUR = new DiffUtil.ItemCallback<EntiteDiagnostic>() {
        @Override
        public boolean areItemsTheSame(@NonNull EntiteDiagnostic ancien, @NonNull EntiteDiagnostic nouveau) {
            return ancien.id == nouveau.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EntiteDiagnostic ancien, @NonNull EntiteDiagnostic nouveau) {
            return ancien.cheminsImages.equals(nouveau.cheminsImages) && ancien.codeMaladie.equals(nouveau.codeMaladie)
                    && ancien.confiance == nouveau.confiance && ancien.timestamp == nouveau.timestamp && ancien.demo == nouveau.demo;
        }
    };

    private final Ecouteur ecouteur;
    private final boolean afficherSuppression;

    public AdaptateurDiagnostic(Ecouteur ecouteur, boolean afficherSuppression) {
        super(COMPARATEUR);
        this.ecouteur = ecouteur;
        this.afficherSuppression = afficherSuppression;
    }

    @NonNull
    @Override
    public SupportVue onCreateViewHolder(@NonNull ViewGroup parent, int typeVue) {
        return new SupportVue(ElementDiagnosticBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SupportVue h, int position) {
        EntiteDiagnostic e = getItem(position);
        Context c = h.liaison.getRoot().getContext();
        Maladie d = CatalogueMaladies.parCode(e.codeMaladie);
        NiveauConfiance niveau = NiveauConfiance.depuis(e.confiance);

        h.liaison.txtTitre.setText(niveau == NiveauConfiance.INCERTAIN ? c.getString(R.string.diagnostic_incertain)
                : c.getString(d.nomRes));
        h.liaison.txtDate.setText(FormatTemps.formater(c, e.timestamp));
        h.liaison.txtPastille.setVisibility(niveau != NiveauConfiance.INCERTAIN && !d.saine ? View.VISIBLE : View.GONE);
        h.liaison.txtDemo.setVisibility(e.demo ? View.VISIBLE : View.GONE);
        h.liaison.btnSupprimer.setVisibility(afficherSuppression ? View.VISIBLE : View.GONE);
        h.liaison.imgChevron.setVisibility(afficherSuppression ? View.GONE : View.VISIBLE);

        String chemin = e.premierChemin();
        Glide.with(h.liaison.imgMiniature)
                .load(chemin == null ? null : new File(chemin))
                .centerCrop()
                .override(160, 160)
                .error(R.drawable.img_feuille_saine)
                .into(h.liaison.imgMiniature);

        h.liaison.getRoot().setOnClickListener(v -> ecouteur.auClic(e));
        h.liaison.btnSupprimer.setOnClickListener(v -> ecouteur.auSuppression(e));
    }

    static class SupportVue extends RecyclerView.ViewHolder {
        final ElementDiagnosticBinding liaison;

        SupportVue(ElementDiagnosticBinding liaison) {
            super(liaison.getRoot());
            this.liaison = liaison;
        }
    }
}
