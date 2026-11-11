package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.card.MaterialCardView;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.FragmentLangueBinding;
import bi.kwitaho.app.repository.Profil;
import bi.kwitaho.app.util.AideLangue;
import bi.kwitaho.app.util.OutilsInterface;

/** Premier lancement : choix de la langue, puis création du profil local  */
public class LangueFragment extends Fragment {
    private static final String CLE_SELECTION = "selected";

    private FragmentLangueBinding liaison;
    private String selection = AideLangue.FR;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentLangueBinding.inflate(gonfleur, conteneur, false);
        OutilsInterface.ajouterMargesSysteme(liaison.getRoot(), true, true);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        if (etatSauvegarde != null) selection = etatSauvegarde.getString(CLE_SELECTION, AideLangue.FR);
        liaison.carteFr.setOnClickListener(v -> choisir(AideLangue.FR));
        liaison.carteRn.setOnClickListener(v -> choisir(AideLangue.RN));
        liaison.carteSw.setOnClickListener(v -> choisir(AideLangue.SW));
        liaison.btnContinuer.setOnClickListener(v -> poursuivre());
        afficher();
    }

    private void choisir(String code) {
        selection = code;
        afficher();
    }

    private void afficher() {
        liaison.cocheFr.setVisibility(AideLangue.FR.equals(selection) ? View.VISIBLE : View.GONE);
        liaison.cocheRn.setVisibility(AideLangue.RN.equals(selection) ? View.VISIBLE : View.GONE);
        liaison.cocheSw.setVisibility(AideLangue.SW.equals(selection) ? View.VISIBLE : View.GONE);
        styliser(liaison.carteFr, AideLangue.FR.equals(selection));
        styliser(liaison.carteRn, AideLangue.RN.equals(selection));
        styliser(liaison.carteSw, AideLangue.SW.equals(selection));
    }

    private void styliser(MaterialCardView carte, boolean on) {
        carte.setStrokeColor(ContextCompat.getColor(requireContext(), on ? R.color.kw_primaire : R.color.kw_separateur));
        carte.setStrokeWidth(OutilsInterface.dp(requireContext(), on ? 2 : 1));
    }

    private void poursuivre() {
        new Profil(requireContext()).definirLangue(selection);
        NavController navigateur = NavHostFragment.findNavController(this);
        navigateur.navigate(R.id.presentationFragment, null,
                new NavOptions.Builder().setPopUpTo(R.id.langueFragment, true).build());
        // Appliquée après la navigation : l'activité est recréée dans la nouvelle langue
        AideLangue.appliquer(selection);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle etatSortie) {
        super.onSaveInstanceState(etatSortie);
        etatSortie.putString(CLE_SELECTION, selection);
    }

    @Override
    public void onDestroyView() {
        liaison = null;
        super.onDestroyView();
    }
}
