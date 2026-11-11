package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;

import bi.kwitaho.app.R;
import bi.kwitaho.app.data.db.EntiteDiagnostic;
import bi.kwitaho.app.databinding.FragmentAccueilBinding;
import bi.kwitaho.app.repository.Profil;
import bi.kwitaho.app.util.OutilsNavigation;
import bi.kwitaho.app.util.OutilsInterface;
import bi.kwitaho.app.viewmodel.VueModeleHistorique;

public class AccueilFragment extends Fragment {
    private static final int[] ASTUCES = {R.string.conseil_1, R.string.conseil_3, R.string.conseil_4,
            R.string.conseil_6, R.string.conseil_7};

    private FragmentAccueilBinding liaison;
    private NavController navigation;
    private long demarreA;
    private boolean revele;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentAccueilBinding.inflate(gonfleur, conteneur, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        navigation = NavHostFragment.findNavController(this);
        demarreA = SystemClock.uptimeMillis();
        revele = false;
/**
        String nom = new Profil(requireContext()).obtenirNom();
        liaison.txtBonjour.setText(nom.isEmpty() ? getString(R.string.accueil_bonjour_sans_nom)
                : getString(R.string.accueil_bonjour, nom));
**/
        liaison.rvAstuces.setAdapter(new ConseilsHome(ASTUCES, () -> navigation.navigate(R.id.conseilsFragment)));
        liaison.btnAstucesPlus.setOnClickListener(v -> navigation.navigate(R.id.conseilsFragment));

        AdaptateurDiagnostic adaptateur = new AdaptateurDiagnostic(new AdaptateurDiagnostic.Ecouteur() {
            @Override
            public void auClic(EntiteDiagnostic e) {
                OutilsNavigation.ouvrirResultat(navigation, e.id);
            }

            @Override
            public void auSuppression(EntiteDiagnostic e) {
                // pas de suppression depuis l'accueil
            }
        }, false);
        liaison.rvDerniers.setAdapter(adaptateur);

        liaison.btnNouveauDiagnostic.setOnClickListener(v -> OutilsNavigation.ouvrirScan(navigation));
        liaison.actionScanner.setOnClickListener(v -> OutilsNavigation.ouvrirScan(navigation));
        liaison.actionMaladies.setOnClickListener(v -> navigation.navigate(R.id.maladiesFragment, null, OutilsNavigation.optionsOnglet()));
        liaison.actionHistorique.setOnClickListener(v -> navigation.navigate(R.id.historiqueFragment, null, OutilsNavigation.optionsOnglet()));
        liaison.actionConseil.setOnClickListener(v -> navigation.navigate(R.id.conseilsFragment));
        liaison.btnVoirTout.setOnClickListener(v -> navigation.navigate(R.id.historiqueFragment, null, OutilsNavigation.optionsOnglet()));

        VueModeleHistorique vueModele = new ViewModelProvider(this).get(VueModeleHistorique.class);
        vueModele.obtenirTout().observe(getViewLifecycleOwner(), liste -> {
            if (liste == null) return;
            lierStatistiques(liste);
            int n = Math.min(3, liste.size());
            adaptateur.submitList(liste.subList(0, n));
            liaison.txtVideDerniers.setVisibility(liste.isEmpty() ? View.VISIBLE : View.GONE);
            liaison.rvDerniers.setVisibility(liste.isEmpty() ? View.GONE : View.VISIBLE);
            if (!revele) {
                revele = true;
                OutilsInterface.reveler(liaison.squelette.getRoot(), liaison.defilement, demarreA);
            }
        });
    }

    private void lierStatistiques(List<EntiteDiagnostic> liste) {
        int saine = 0;
        int malades = 0;
        for (EntiteDiagnostic e : liste) {
            int c = e.categorie();
            if (c == 1) saine++;
            else if (c == 2) malades++;
        }
        liaison.txtStatTotal.setText(String.valueOf(liste.size()));
        liaison.txtStatSaine.setText(String.valueOf(saine));
        liaison.txtStatMalade.setText(String.valueOf(malades));
    }

    @Override
    public void onDestroyView() {
        liaison.rvDerniers.setAdapter(null);
        liaison.rvAstuces.setAdapter(null);
        liaison = null;
        super.onDestroyView();
    }
}
