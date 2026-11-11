package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.FragmentConseilsBinding;
import bi.kwitaho.app.util.OutilsInterface;

/** Bonnes pratiques générales pour un manioc en bonne santé */
public class ConseilsFragment extends Fragment {
    private static final int[] CONSEILS = {R.string.conseil_1, R.string.conseil_2, R.string.conseil_3,
            R.string.conseil_4, R.string.conseil_5, R.string.conseil_6, R.string.conseil_7};

    private FragmentConseilsBinding liaison;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentConseilsBinding.inflate(gonfleur, conteneur, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        List<String> elements = new ArrayList<>();
        for (int r : CONSEILS) elements.add(getString(r));
        OutilsInterface.remplirCoches(liaison.listeConseils, elements);
    }

    @Override
    public void onDestroyView() {
        liaison = null;
        super.onDestroyView();
    }
}
