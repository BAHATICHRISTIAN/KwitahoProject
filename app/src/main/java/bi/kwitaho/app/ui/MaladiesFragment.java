package bi.kwitaho.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import bi.kwitaho.app.data.model.Maladie;
import bi.kwitaho.app.databinding.FragmentMaladiesBinding;
import bi.kwitaho.app.util.OutilsNavigation;
import bi.kwitaho.app.util.OutilsInterface;
import bi.kwitaho.app.viewmodel.VueModeleMaladie;

/** Catalogue des maladies, consultable hors ligne, avec recherche. */
public class MaladiesFragment extends Fragment {
    private FragmentMaladiesBinding liaison;
    private boolean revele;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentMaladiesBinding.inflate(gonfleur, conteneur, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        final long demarreA = SystemClock.uptimeMillis();
        revele = false;
        NavController navigation = NavHostFragment.findNavController(this);
        VueModeleMaladie vueModele = new ViewModelProvider(this).get(VueModeleMaladie.class);

        AdaptateurMaladie adaptateur = new AdaptateurMaladie(d -> OutilsNavigation.ouvrirMaladie(navigation, d.code, -1f, false));
        liaison.rvMaladies.setAdapter(adaptateur);

        vueModele.obtenirResultats().observe(getViewLifecycleOwner(), liste -> {
            adaptateur.submitList(liste);
            boolean vide = liste == null || liste.isEmpty();
            liaison.txtVide.setVisibility(vide ? View.VISIBLE : View.GONE);
            liaison.rvMaladies.setVisibility(vide ? View.GONE : View.VISIBLE);
            if (!revele) {
                revele = true;
                OutilsInterface.reveler(liaison.squelette.getRoot(), liaison.contenu, demarreA);
            }
        });

        liaison.saisieRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int debut, int nombre, int apres) {
            }

            @Override
            public void onTextChanged(CharSequence s, int debut, int avant, int nombre) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                vueModele.rechercher(requireContext(), s.toString());
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null && arguments.getBoolean(OutilsNavigation.ARG_FOCUS_RECHERCHE, false)) {
            arguments.putBoolean(OutilsNavigation.ARG_FOCUS_RECHERCHE, false);
            liaison.saisieRecherche.postDelayed(() -> {
                if (liaison == null) return;
                liaison.saisieRecherche.requestFocus();
                InputMethodManager clavier = (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (clavier != null) clavier.showSoftInput(liaison.saisieRecherche, InputMethodManager.SHOW_IMPLICIT);
            }, 600L);
        }
    }

    @Override
    public void onDestroyView() {
        liaison.rvMaladies.setAdapter(null);
        liaison = null;
        super.onDestroyView();
    }
}
