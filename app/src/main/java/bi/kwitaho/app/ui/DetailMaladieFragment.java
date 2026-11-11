package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import bi.kwitaho.app.R;
import bi.kwitaho.app.data.model.Maladie;
import bi.kwitaho.app.data.model.CatalogueMaladies;
import bi.kwitaho.app.databinding.FragmentDetailMaladieBinding;
import bi.kwitaho.app.util.Reglages;
import bi.kwitaho.app.util.OutilsNavigation;
import bi.kwitaho.app.util.OutilsInterface;

/** Fiche d'une maladie : deux onglets (description, conseils),  */
public class DetailMaladieFragment extends Fragment {
    private FragmentDetailMaladieBinding liaison;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentDetailMaladieBinding.inflate(gonfleur, conteneur, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        Bundle arguments = requireArguments();
        Maladie d = CatalogueMaladies.parCode(arguments.getString(OutilsNavigation.ARG_CODE, CatalogueMaladies.SAINE));
        float confiance = arguments.getFloat(OutilsNavigation.ARG_CONFIANCE, -1f);
        boolean demo = arguments.getBoolean(OutilsNavigation.ARG_DEMO, false);

        liaison.imgMaladie.setImageResource(d.imageRes);
        liaison.txtNature.setText(d.natureRes);
        liaison.txtNom.setText(d.nomRes);
        liaison.bandeauDemo.setVisibility(demo ? View.VISIBLE : View.GONE);
        liaison.txtNoteExemple.setVisibility(Reglages.AFFICHER_NOTE_ILLUSTRATION && !d.saine ? View.VISIBLE : View.GONE);

        if (confiance >= 0f) {
            int pourcent = Math.round(confiance * 100f);
            liaison.txtLigneConfiance.setVisibility(View.VISIBLE);
            liaison.txtLigneConfiance.setText(getString(R.string.resultat_confiance, pourcent));
            liaison.progressionConf.setVisibility(View.VISIBLE);
            liaison.progressionConf.setProgress(pourcent);
        }

        liaison.txtDesc.setText(d.descRes);
        liaison.txtSymptomes.setText(d.symptomesRes);
        liaison.txtCause.setText(d.causeRes);

        List<String> prevention = new ArrayList<>();
        for (int r : d.preventionRes) prevention.add(getString(r));
        List<String> priseEnCharge = new ArrayList<>();
        for (int r : d.conseilsRes) priseEnCharge.add(getString(r));
        OutilsInterface.remplirCoches(liaison.listePrevention, prevention);
        OutilsInterface.remplirCoches(liaison.listePriseEnCharge, priseEnCharge);

        liaison.onglets.addTab(liaison.onglets.newTab().setText(R.string.detail_onglet_description));
        liaison.onglets.addTab(liaison.onglets.newTab().setText(R.string.detail_onglet_conseil));
        liaison.onglets.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab onglet) {
                boolean premier = onglet.getPosition() == 0;
                liaison.voletDescription.setVisibility(premier ? View.VISIBLE : View.GONE);
                liaison.voletConseils.setVisibility(premier ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab onglet) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab onglet) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        liaison = null;
        super.onDestroyView();
    }
}
