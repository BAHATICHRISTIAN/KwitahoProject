package bi.kwitaho.app.ui;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.ActivitePrincipaleBinding;
import bi.kwitaho.app.util.OutilsNavigation;
import bi.kwitaho.app.util.OutilsInterface;

/** Activité unique : en-tête vert, barre du bas à 5 emplacements et NavHostFragment. */
public class ActivitePrincipale extends AppCompatActivity {
    private static final Set<Integer> SANS_HABILLAGE = new HashSet<>(Arrays.asList(
            R.id.demarrageFragment, R.id.langueFragment, R.id.presentationFragment,
            R.id.cameraFragment, R.id.analyseFragment));
    private static final Set<Integer> BARRE_ETAT_CLAIRE = new HashSet<>(Arrays.asList(
            R.id.demarrageFragment, R.id.langueFragment, R.id.presentationFragment, R.id.analyseFragment));

    private ActivitePrincipaleBinding liaison;
    private NavController navigation;

    @Override
    protected void onCreate(Bundle etatSauvegarde) {
        EdgeToEdge.enable(this);
        super.onCreate(etatSauvegarde);
        liaison = ActivitePrincipaleBinding.inflate(getLayoutInflater());
        setContentView(liaison.getRoot());

        OutilsInterface.ajouterMargesSysteme(liaison.enteteApp, true, false);
        OutilsInterface.ajouterMargesSysteme(liaison.barreBas, false, true);

        NavHostFragment hote = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_hote);
        navigation = hote.getNavController();

        liaison.btnRetourEntete.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        liaison.btnRechercheEntete.setOnClickListener(v -> {
            Bundle a = new Bundle();
            a.putBoolean(OutilsNavigation.ARG_FOCUS_RECHERCHE, true);
            navigation.navigate(R.id.maladiesFragment, a, OutilsNavigation.optionsOnglet());
        });

        liaison.ongletAccueil.setOnClickListener(v -> allerOnglet(R.id.accueilFragment));
        liaison.ongletHistorique.setOnClickListener(v -> allerOnglet(R.id.historiqueFragment));
        liaison.ongletMaladies.setOnClickListener(v -> allerOnglet(R.id.maladiesFragment));
        liaison.ongletProfil.setOnClickListener(v -> allerOnglet(R.id.profilFragment));
        liaison.ongletScanner.setOnClickListener(v -> OutilsNavigation.ouvrirScan(navigation));
        liaison.fabScanner.setOnClickListener(v -> OutilsNavigation.ouvrirScan(navigation));

        navigation.addOnDestinationChangedListener((controleur, destination, arguments) -> mettreAJourHabillage(destination));
    }

    private void allerOnglet(int destination) {
        NavDestination actuel = navigation.getCurrentDestination();
        if (actuel != null && actuel.getId() == destination) return;
        if (destination == R.id.accueilFragment) {
            // L'accueil est déjà en bas de la pile : on revient dessus sans le recréer
            if (!navigation.popBackStack(R.id.accueilFragment, false)) navigation.navigate(destination);
            return;
        }
        navigation.navigate(destination, null, OutilsNavigation.optionsOnglet());
    }

    private void mettreAJourHabillage(NavDestination d) {
        int id = d.getId();
        boolean habillage = !SANS_HABILLAGE.contains(id);
        liaison.enteteApp.setVisibility(habillage ? View.VISIBLE : View.GONE);
        liaison.conteneurBarreBas.setVisibility(habillage ? View.VISIBLE : View.GONE);

        boolean accueil = id == R.id.accueilFragment;
        liaison.btnRetourEntete.setVisibility(accueil ? View.GONE : View.VISIBLE);
        liaison.imgLogoEntete.setVisibility(accueil ? View.VISIBLE : View.GONE);
        liaison.btnRechercheEntete.setVisibility(accueil ? View.VISIBLE : View.GONE);
        CharSequence libelle = d.getLabel();
        liaison.txtTitreEntete.setText(libelle != null ? libelle : getString(R.string.nom_app));

        liaison.ongletAccueil.setSelected(id == R.id.accueilFragment);
        liaison.ongletHistorique.setSelected(id == R.id.historiqueFragment);
        liaison.ongletMaladies.setSelected(id == R.id.maladiesFragment || id == R.id.detailMaladieFragment);
        liaison.ongletProfil.setSelected(id == R.id.profilFragment);

        WindowInsetsControllerCompat controleur = WindowCompat.getInsetsController(getWindow(), liaison.getRoot());
        controleur.setAppearanceLightStatusBars(BARRE_ETAT_CLAIRE.contains(id));
        controleur.setAppearanceLightNavigationBars(id != R.id.cameraFragment);
    }
}
