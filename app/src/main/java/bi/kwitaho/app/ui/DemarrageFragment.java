package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.FragmentDemarrageBinding;
import bi.kwitaho.app.repository.Profil;
import bi.kwitaho.app.util.OutilsInterface;

public class DemarrageFragment extends Fragment {
    private final Handler gestionnaire = new Handler(Looper.getMainLooper());
    private final Runnable orienter = this::orienter;
    private FragmentDemarrageBinding liaison;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentDemarrageBinding.inflate(gonfleur, conteneur, false);
        OutilsInterface.ajouterMargesSysteme(liaison.getRoot(), true, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        gestionnaire.postDelayed(orienter, 1600L);
    }

    private void orienter() {
        if (!isAdded() || liaison == null) return;
        Profil profil = new Profil(requireContext());
        int destination;
        if (profil.obtenirLangue().isEmpty()) destination = R.id.langueFragment;
        else if (!profil.presentationTerminee()) destination = R.id.presentationFragment;
        else destination = R.id.accueilFragment;
        NavController navigateur = NavHostFragment.findNavController(this);
        navigateur.navigate(destination, null, new NavOptions.Builder().setPopUpTo(R.id.demarrageFragment, true).build());
    }

    @Override
    public void onDestroyView() {
        gestionnaire.removeCallbacks(orienter);
        liaison = null;
        super.onDestroyView();
    }
}
