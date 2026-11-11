package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.DialogueNomBinding;
import bi.kwitaho.app.databinding.DialoguePreferencesBinding;
import bi.kwitaho.app.databinding.FragmentProfilBinding;
import bi.kwitaho.app.util.AideLangue;
import bi.kwitaho.app.util.OutilsInterface;
import bi.kwitaho.app.viewmodel.VueModeleProfil;

/** Profil local : nom, langue, préférences, confidentialité, à propos, aide. */
public class ProfilFragment extends Fragment {
    private static final String[] CODES = {AideLangue.FR, AideLangue.RN, AideLangue.SW};
    private static final int[] NOMS_LANGUES = {R.string.langue_fr, R.string.langue_rn, R.string.langue_sw};

    private FragmentProfilBinding liaison;
    private VueModeleProfil vueModele;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentProfilBinding.inflate(gonfleur, conteneur, false);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        vueModele = new ViewModelProvider(this).get(VueModeleProfil.class);

        vueModele.obtenirNom().observe(getViewLifecycleOwner(), nom ->
                liaison.txtNom.setText(nom == null || nom.isEmpty() ? getString(R.string.profil_agriculteur) : nom));
        liaison.txtValeurLangue.setText(NOMS_LANGUES[indexLangueCourante()]);
        liaison.txtVersion.setText(getString(R.string.nom_app) + " " + OutilsInterface.nomVersion(requireContext()));

        liaison.ligneNom.setOnClickListener(v -> modifierNom());
        liaison.ligneLangue.setOnClickListener(v -> choisirLangue());
        liaison.lignePreferences.setOnClickListener(v -> afficherPreferences());
        liaison.ligneConfidentialite.setOnClickListener(v -> OutilsInterface.afficherInfo(requireContext(), R.string.profil_confidentialite,
                getString(R.string.confidentialite_texte)));
        liaison.ligneApropos.setOnClickListener(v -> OutilsInterface.afficherInfo(requireContext(), R.string.profil_apropos,
                getString(R.string.slogan_app_long) + "\n\n"
                        + getString(R.string.apropos_texte, OutilsInterface.nomVersion(requireContext()))
                        + "\n\n" + getString(R.string.resultat_avertissement)));
        liaison.ligneAide.setOnClickListener(v -> OutilsInterface.afficherInfo(requireContext(), R.string.profil_aide,
                getString(R.string.aide_texte)));
        liaison.btnEffacer.setOnClickListener(v -> confirmerEffacement());
    }

    private int indexLangueCourante() {
        String code = vueModele.obtenirLangue();
        for (int i = 0; i < CODES.length; i++) {
            if (CODES[i].equals(code)) return i;
        }
        return 0;
    }

    private void modifierNom() {
        DialogueNomBinding d = DialogueNomBinding.inflate(LayoutInflater.from(requireContext()));
        String actuel = vueModele.obtenirNom().getValue();
        d.saisieNom.setText(actuel == null ? "" : actuel);
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profil_modifier_nom)
                .setView(d.getRoot())
                .setNegativeButton(R.string.annuler, null)
                .setPositiveButton(R.string.enregistrer, (dialogue, w) ->
                        vueModele.definirNom(d.saisieNom.getText() == null ? "" : d.saisieNom.getText().toString()))
                .show();
    }

    private void choisirLangue() {
        String[] noms = new String[NOMS_LANGUES.length];
        for (int i = 0; i < noms.length; i++) noms[i] = getString(NOMS_LANGUES[i]);
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profil_langue)
                .setSingleChoiceItems(noms, indexLangueCourante(), (dialogue, indiceChoix) -> {
                    dialogue.dismiss();
                    String code = CODES[indiceChoix];
                    if (!code.equals(vueModele.obtenirLangue())) {
                        vueModele.definirLangue(code);
                        AideLangue.appliquer(code);   // l'activité est recréée dans la nouvelle langue
                    }
                })
                .setNegativeButton(R.string.annuler, null)
                .show();
    }

    private void afficherPreferences() {
        DialoguePreferencesBinding d = DialoguePreferencesBinding.inflate(LayoutInflater.from(requireContext()));
        d.interrupteurFlash.setChecked(vueModele.flashParDefaut());
        d.interrupteurFlash.setOnCheckedChangeListener((bouton, coche) -> vueModele.definirFlashParDefaut(coche));
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.pref_titre)
                .setView(d.getRoot())
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void confirmerEffacement() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.effacer_titre)
                .setMessage(R.string.effacer_texte)
                .setNegativeButton(R.string.annuler, null)
                .setPositiveButton(R.string.supprimer, (dialogue, w) -> vueModele.toutEffacer(() -> {
                    if (!isAdded()) return;
                    NavController navigateur = NavHostFragment.findNavController(this);
                    navigateur.navigate(R.id.langueFragment, null,
                            new NavOptions.Builder().setPopUpTo(R.id.accueilFragment, true).build());
                }))
                .show();
    }

    @Override
    public void onDestroyView() {
        liaison = null;
        super.onDestroyView();
    }
}
