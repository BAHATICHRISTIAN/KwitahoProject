package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.FragmentPresentationBinding;
import bi.kwitaho.app.databinding.ElementPagePresentationBinding;
import bi.kwitaho.app.repository.Profil;
import bi.kwitaho.app.util.OutilsInterface;

/** Deux écrans d'explication avec boutons Suivant et Passer. */
public class PresentationFragment extends Fragment {
    private static final int[] IMAGES = {R.drawable.img_presentation_agriculteur, R.drawable.img_presentation_bouclier};
    private static final int[] TITRES = {R.string.pres1_titre, R.string.pres2_titre};
    private static final int[] TEXTES = {R.string.pres1_texte, R.string.pres2_texte};

    private FragmentPresentationBinding liaison;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentPresentationBinding.inflate(gonfleur, conteneur, false);
        OutilsInterface.ajouterMargesSysteme(liaison.getRoot(), false, true);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        liaison.pager.setAdapter(new AdaptateurPages());
        construirePoints();
        liaison.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                afficher(position);
            }
        });
        liaison.btnSuivant.setOnClickListener(v -> {
            int p = liaison.pager.getCurrentItem();
            if (p < IMAGES.length - 1) liaison.pager.setCurrentItem(p + 1, true);
            else terminer();
        });
        liaison.btnPasser.setOnClickListener(v -> terminer());
        afficher(liaison.pager.getCurrentItem());
    }

    private void construirePoints() {
        liaison.points.removeAllViews();
        for (int i = 0; i < IMAGES.length; i++) {
            ImageView point = new ImageView(requireContext());
            LinearLayout.LayoutParams parametres = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int m = OutilsInterface.dp(requireContext(), 4);
            parametres.setMargins(m, 0, m, 0);
            point.setLayoutParams(parametres);
            liaison.points.addView(point);
        }
    }

    private void afficher(int position) {
        for (int i = 0; i < liaison.points.getChildCount(); i++) {
            ((ImageView) liaison.points.getChildAt(i)).setImageResource(
                    i == position ? R.drawable.point_actif : R.drawable.point_inactif);
        }
        boolean dernier = position == IMAGES.length - 1;
        liaison.btnSuivant.setText(dernier ? R.string.pres_debut : R.string.pres_suivant);
        liaison.btnPasser.setVisibility(dernier ? View.INVISIBLE : View.VISIBLE);
    }

    private void terminer() {
        new Profil(requireContext()).definirPresentationTerminee(true);
        NavController navigateur = NavHostFragment.findNavController(this);
        navigateur.navigate(R.id.accueilFragment, null, new NavOptions.Builder().setPopUpTo(R.id.presentationFragment, true).build());
    }

    @Override
    public void onDestroyView() {
        liaison.pager.setAdapter(null);
        liaison = null;
        super.onDestroyView();
    }

    private static class AdaptateurPages extends RecyclerView.Adapter<AdaptateurPages.SupportVue> {
        @NonNull
        @Override
        public SupportVue onCreateViewHolder(@NonNull ViewGroup parent, int typeVue) {
            return new SupportVue(ElementPagePresentationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SupportVue h, int position) {
            h.liaison.imgPage.setImageResource(IMAGES[position]);
            ((TextView) h.liaison.txtTitrePage).setText(TITRES[position]);
            ((TextView) h.liaison.txtTextePage).setText(TEXTES[position]);
        }

        @Override
        public int getItemCount() {
            return IMAGES.length;
        }

        static class SupportVue extends RecyclerView.ViewHolder {
            final ElementPagePresentationBinding liaison;

            SupportVue(ElementPagePresentationBinding liaison) {
                super(liaison.getRoot());
                this.liaison = liaison;
            }
        }
    }
}
