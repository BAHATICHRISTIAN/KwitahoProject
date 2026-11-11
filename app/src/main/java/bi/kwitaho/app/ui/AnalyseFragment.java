package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.FragmentAnalyseBinding;
import bi.kwitaho.app.util.ErreurApp;
import bi.kwitaho.app.util.OutilsNavigation;
import bi.kwitaho.app.util.OutilsInterface;
import bi.kwitaho.app.viewmodel.VueModeleDiagnostic;

/** Animation d'attente pendant l'analyse locale des photos. */
public class AnalyseFragment extends Fragment {
    private FragmentAnalyseBinding liaison;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentAnalyseBinding.inflate(gonfleur, conteneur, false);
        OutilsInterface.ajouterMargesSysteme(liaison.getRoot(), true, true);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        NavController navigateur = NavHostFragment.findNavController(this);
        VueModeleDiagnostic vueModele = new ViewModelProvider(requireActivity()).get(VueModeleDiagnostic.class);

        // Pas de retour pendant l'analyse
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // ignoré
                    }
                });

        vueModele.obtenirEnregistre().observe(getViewLifecycleOwner(), event -> {
            Long id = event.obtenirContenuSiNonTraite();
            if (id == null) return;
            Bundle arguments = new Bundle();
            arguments.putLong(OutilsNavigation.ARG_ID, id);
            navigateur.navigate(R.id.resultatFragment, arguments,
                    new NavOptions.Builder().setPopUpTo(R.id.cameraFragment, true).build());
        });

        vueModele.obtenirErreur().observe(getViewLifecycleOwner(), event -> {
            ErreurApp erreur = event.obtenirContenuSiNonTraite();
            if (erreur == null) return;
            OutilsInterface.afficherErreur(requireContext(), erreur);
            navigateur.navigateUp();
        });

        vueModele.analyser();
    }

    @Override
    public void onDestroyView() {
        liaison = null;
        super.onDestroyView();
    }
}
