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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import bi.kwitaho.app.R;
import bi.kwitaho.app.data.db.EntiteDiagnostic;
import bi.kwitaho.app.databinding.FragmentHistoriqueBinding;
import bi.kwitaho.app.util.OutilsNavigation;
import bi.kwitaho.app.util.OutilsInterface;
import bi.kwitaho.app.viewmodel.VueModeleHistorique;

/** Diagnostics passés, avec filtre (toutes / saines / malades) et suppression. */
public class HistoriqueFragment extends Fragment {
    private FragmentHistoriqueBinding liaison;
    private VueModeleHistorique vueModele;
    private boolean revele;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentHistoriqueBinding.inflate(gonfleur, conteneur, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        final long demarreA = SystemClock.uptimeMillis();
        revele = false;
        NavController navigation = NavHostFragment.findNavController(this);
        vueModele = new ViewModelProvider(this).get(VueModeleHistorique.class);

        AdaptateurDiagnostic adaptateur = new AdaptateurDiagnostic(new AdaptateurDiagnostic.Ecouteur() {
            @Override
            public void auClic(EntiteDiagnostic e) {
                OutilsNavigation.ouvrirResultat(navigation, e.id);
            }

            @Override
            public void auSuppression(EntiteDiagnostic e) {
                confirmerSuppression(e);
            }
        }, true);
        liaison.rvHistorique.setAdapter(adaptateur);

        liaison.groupePuces.setOnCheckedStateChangeListener((groupe, ids) -> {
            if (ids.isEmpty()) return;
            int id = ids.get(0);
            if (id == R.id.puce_saine) vueModele.definirFiltre(VueModeleHistorique.FILTRE_SAINES);
            else if (id == R.id.puce_malade) vueModele.definirFiltre(VueModeleHistorique.FILTRE_MALADES);
            else vueModele.definirFiltre(VueModeleHistorique.FILTRE_TOUT);
        });

        liaison.btnScannerVide.setOnClickListener(v -> OutilsNavigation.ouvrirScan(navigation));

        vueModele.obtenirFiltres().observe(getViewLifecycleOwner(), liste -> {
            adaptateur.submitList(liste);
            boolean vide = liste == null || liste.isEmpty();
            liaison.etatVide.setVisibility(vide ? View.VISIBLE : View.GONE);
            liaison.rvHistorique.setVisibility(vide ? View.GONE : View.VISIBLE);
            if (!revele) {
                revele = true;
                OutilsInterface.reveler(liaison.squelette.getRoot(), liaison.contenu, demarreA);
            }
        });
    }

    private void confirmerSuppression(EntiteDiagnostic e) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.historique_supprimer_titre)
                .setMessage(R.string.historique_supprimer_texte)
                .setNegativeButton(R.string.annuler, null)
                .setPositiveButton(R.string.supprimer, (d, w) -> {
                    vueModele.supprimer(e);
                    OutilsInterface.barreMessage(requireView(), R.string.historique_supprime);
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        liaison.rvHistorique.setAdapter(null);
        liaison = null;
        super.onDestroyView();
    }
}
