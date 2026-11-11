package bi.kwitaho.app.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bi.kwitaho.app.R;
import bi.kwitaho.app.data.db.EntiteDiagnostic;
import bi.kwitaho.app.data.model.NiveauConfiance;
import bi.kwitaho.app.data.model.Maladie;
import bi.kwitaho.app.data.model.CatalogueMaladies;
import bi.kwitaho.app.databinding.FragmentResultatBinding;
import bi.kwitaho.app.databinding.ElementMiniatureBinding;
import bi.kwitaho.app.util.OutilsNavigation;
import bi.kwitaho.app.util.OutilsInterface;
import bi.kwitaho.app.viewmodel.VueModeleHistorique;

/** Résultat d'un diagnostic : verdict, confiance, conseils rapides, avertissement. */
public class ResultatFragment extends Fragment {
    private FragmentResultatBinding liaison;
    private NavController navigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentResultatBinding.inflate(gonfleur, conteneur, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        navigation = NavHostFragment.findNavController(this);
        long id = requireArguments().getLong(OutilsNavigation.ARG_ID, -1L);
        VueModeleHistorique vueModele = new ViewModelProvider(this).get(VueModeleHistorique.class);
        vueModele.obtenirParId(id).observe(getViewLifecycleOwner(), e -> {
            if (e != null) lier(e);
        });
        liaison.btnReprendre.setOnClickListener(v -> OutilsNavigation.ouvrirScan(navigation));
        liaison.btnNouveau.setOnClickListener(v -> OutilsNavigation.ouvrirScan(navigation));
    }

    private void lier(EntiteDiagnostic e) {
        NiveauConfiance niveau = NiveauConfiance.depuis(e.confiance);
        Maladie d = CatalogueMaladies.parCode(e.codeMaladie);
        List<String> chemins = e.chemins();

        afficherImagePrincipale(chemins.isEmpty() ? null : chemins.get(0));
        lierMiniatures(chemins);

        liaison.txtConfiance.setText(getString(R.string.resultat_confiance, Math.round(e.confiance * 100f)));
        liaison.bandeauDemo.setVisibility(e.demo ? View.VISIBLE : View.GONE);
        liaison.txtProbable.setVisibility(View.GONE);

        List<String> astuces = new ArrayList<>();
        if (niveau == NiveauConfiance.INCERTAIN) {
            definirIconeVerdict(R.drawable.ic_alerte, R.color.kw_alerte_icone);
            liaison.txtTitreVerdict.setText(R.string.resultat_incertain);
            liaison.txtSousVerdict.setText(R.string.resultat_incertain_sous);
            liaison.txtTitreAstuces.setText(R.string.resultat_photo_astuces);
            astuces.add(getString(R.string.resultat_photo_astuce_1));
            astuces.add(getString(R.string.resultat_photo_astuce_2));
            astuces.add(getString(R.string.resultat_photo_astuce_3));
            liaison.btnDetails.setVisibility(View.GONE);
            liaison.btnReprendre.setVisibility(View.VISIBLE);
        } else {
            if (d.saine) {
                definirIconeVerdict(R.drawable.ic_bouclier_coche, R.color.kw_primaire);
                liaison.txtTitreVerdict.setText(R.string.resultat_saine);
                liaison.txtSousVerdict.setText(R.string.resultat_saine_sous);
            } else {
                definirIconeVerdict(R.drawable.ic_alerte, R.color.kw_alerte_icone);
                liaison.txtTitreVerdict.setText(d.nomRes);
                liaison.txtSousVerdict.setText(R.string.resultat_pastille_maladie);
            }
            if (niveau == NiveauConfiance.PROBABLE) {
                liaison.txtProbable.setVisibility(View.VISIBLE);
                liaison.txtSousVerdict.setText(R.string.resultat_probable_sous);
            }
            liaison.txtTitreAstuces.setText(R.string.resultat_astuces);
            for (int ressource : d.conseilsRes) astuces.add(getString(ressource));
            liaison.btnDetails.setVisibility(View.VISIBLE);
            liaison.btnDetails.setOnClickListener(v -> OutilsNavigation.ouvrirMaladie(navigation, d.code, e.confiance, e.demo));
            liaison.btnReprendre.setVisibility(niveau == NiveauConfiance.PROBABLE ? View.VISIBLE : View.GONE);
        }
        OutilsInterface.remplirCoches(liaison.conteneurAstuces, astuces);
    }

    private void definirIconeVerdict(int ressource, int couleur) {
        liaison.imgVerdict.setImageResource(ressource);
        liaison.imgVerdict.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), couleur)));
    }

    private void afficherImagePrincipale(@Nullable String chemin) {
        Glide.with(this).load(chemin == null ? null : new File(chemin)).centerCrop()
                .error(R.drawable.img_feuille_saine).into(liaison.imgPrincipal);
    }

    private void lierMiniatures(List<String> chemins) {
        liaison.miniatures.removeAllViews();
        if (chemins.size() < 2) {
            liaison.miniatures.setVisibility(View.GONE);
            return;
        }
        liaison.miniatures.setVisibility(View.VISIBLE);
        LayoutInflater gonfleur = LayoutInflater.from(requireContext());
        for (String p : chemins) {
            ElementMiniatureBinding t = ElementMiniatureBinding.inflate(gonfleur, liaison.miniatures, false);
            t.btnRetirer.setVisibility(View.GONE);
            Glide.with(this).load(new File(p)).centerCrop().override(160, 160).into(t.imgMiniature);
            t.imgMiniature.setOnClickListener(v -> afficherImagePrincipale(p));
            liaison.miniatures.addView(t.getRoot());
        }
    }

    @Override
    public void onDestroyView() {
        liaison = null;
        super.onDestroyView();
    }
}
