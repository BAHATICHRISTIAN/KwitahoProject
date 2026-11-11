package bi.kwitaho.app.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bi.kwitaho.app.R;
import bi.kwitaho.app.databinding.FragmentCameraBinding;
import bi.kwitaho.app.databinding.ElementMiniatureBinding;
import bi.kwitaho.app.repository.Profil;
import bi.kwitaho.app.util.ErreurApp;
import bi.kwitaho.app.util.ExceptionApp;
import bi.kwitaho.app.util.Reglages;
import bi.kwitaho.app.util.StockagePhotos;
import bi.kwitaho.app.util.OutilsInterface;
import bi.kwitaho.app.viewmodel.VueModeleDiagnostic;

/** Caméra CameraX  jusqu'à 3 photos par diagnostic. */
public class CameraFragment extends Fragment {
    private static final int[] LIBELLES_ETAPES = {R.string.cam_etape_1, R.string.cam_etape_2, R.string.cam_etape_3};

    private FragmentCameraBinding liaison;
    private VueModeleDiagnostic vueModele;
    private ProcessCameraProvider fournisseurCamera;
    private ImageCapture captureImage;
    private boolean flashActif;
    private boolean occupe;
    private boolean permissionRefuseeDefinitivement;
    private boolean permissionDemandee;
    private boolean cameraDemarree;

    private final ExecutorService es = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String> lanceurPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (liaison == null) return;
                if (granted) {
                    demarrerCamera();
                } else {
                    permissionRefuseeDefinitivement = !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    afficherPanneauPermission();
                }
            });

    private final ActivityResultLauncher<String> lanceurGalerie =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) importerPhoto(uri);
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater gonfleur, @Nullable ViewGroup conteneur, @Nullable Bundle etat) {
        liaison = FragmentCameraBinding.inflate(gonfleur, conteneur, false);
        OutilsInterface.ajouterMargesSysteme(liaison.barreHaut, true, false);
        OutilsInterface.ajouterMargesSysteme(liaison.panneauBas, false, true);
        OutilsInterface.ajouterMargesSysteme(liaison.panneauPerm, true, true);
        return liaison.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View vue, @Nullable Bundle etatSauvegarde) {
        vueModele = new ViewModelProvider(requireActivity()).get(VueModeleDiagnostic.class);
        flashActif = new Profil(requireContext()).flashParDefaut();
        cameraDemarree = false;
        mettreAJourIconeFlash();

        liaison.btnRetourCam.setOnClickListener(v -> quitter());
        liaison.btnRetourPerm.setOnClickListener(v -> quitter());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        quitter();
                    }
                });

        liaison.btnFlash.setOnClickListener(v -> {
            flashActif = !flashActif;
            if (captureImage != null) {
                captureImage.setFlashMode(flashActif ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF);
            }
            mettreAJourIconeFlash();
        });
        liaison.btnCapture.setOnClickListener(v -> prendrePhoto());
        liaison.btnGalerie.setOnClickListener(v -> ouvrirGalerie());
        liaison.btnGaleriePerm.setOnClickListener(v -> ouvrirGalerie());
        liaison.btnAnalyser.setOnClickListener(v -> {
            NavController navigateur = NavHostFragment.findNavController(this);
            navigateur.navigate(R.id.analyseFragment);
        });
        liaison.btnAutoriserPerm.setOnClickListener(v -> {
            if (permissionRefuseeDefinitivement) {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireContext().getPackageName(), null));
                startActivity(i);
            } else {
                lanceurPermission.launch(Manifest.permission.CAMERA);
            }
        });

        vueModele.obtenirPhotos().observe(getViewLifecycleOwner(), this::afficherPhotos);

        if (aPermission()) {
            demarrerCamera();
        } else {
            afficherPanneauPermission();
            if (!permissionDemandee) {
                permissionDemandee = true;
                lanceurPermission.launch(Manifest.permission.CAMERA);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // retour depuis les réglages après avoir accordé la permission
        if (liaison != null && !cameraDemarree && aPermission()) demarrerCamera();
    }

    private boolean aPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void afficherPanneauPermission() {
        liaison.panneauPerm.setVisibility(View.VISIBLE);
        liaison.btnAutoriserPerm.setText(permissionRefuseeDefinitivement ? R.string.cam_perm_reglages : R.string.cam_perm_autoriser);
        if (permissionRefuseeDefinitivement) liaison.txtTextePerm.setText(R.string.err_camera_refusee);
        else liaison.txtTextePerm.setText(R.string.cam_perm_texte);
    }

    private void demarrerCamera() {
        liaison.panneauPerm.setVisibility(View.GONE);
        if (cameraDemarree) return;
        cameraDemarree = true;
        final Context ctx = requireContext();
        final ListenableFuture<ProcessCameraProvider> futur = ProcessCameraProvider.getInstance(ctx);
        futur.addListener(() -> {
            if (liaison == null) return;
            try {
                fournisseurCamera = futur.get();
                lierCamera();
            } catch (ExecutionException | InterruptedException e) {
                cameraDemarree = false;
                OutilsInterface.afficherErreur(getContext(), ErreurApp.CAMERA_INDISPONIBLE);
            }
        }, ContextCompat.getMainExecutor(ctx));
    }

    private void lierCamera() {
        if (liaison == null || fournisseurCamera == null) return;
        try {
            if (!fournisseurCamera.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                cameraDemarree = false;
                OutilsInterface.afficherErreur(getContext(), ErreurApp.CAMERA_INDISPONIBLE);
                return;
            }
            Preview apercu = new Preview.Builder().build();
            apercu.setSurfaceProvider(liaison.apercu.getSurfaceProvider());
            captureImage = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setFlashMode(flashActif ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF)
                    .build();
            fournisseurCamera.unbindAll();
            fournisseurCamera.bindToLifecycle(getViewLifecycleOwner(), CameraSelector.DEFAULT_BACK_CAMERA,
                    apercu, captureImage);
        } catch (Exception e) {
            cameraDemarree = false;
            OutilsInterface.afficherErreur(getContext(), ErreurApp.CAMERA_INDISPONIBLE);
        }
    }

    private void prendrePhoto() {
        if (captureImage == null || occupe || liaison == null) return;
        if (vueModele.nombrePhotos() >= Reglages.NB_PHOTOS_MAX) {
            OutilsInterface.barreMessage(liaison.getRoot(), R.string.cam_max);
            return;
        }
        final File fichier;
        try {
            fichier = StockagePhotos.nouveauFichier(requireContext());
        } catch (ExceptionApp e) {
            OutilsInterface.afficherErreur(requireContext(), e.erreur);
            return;
        }
        occupe = true;
        liaison.btnCapture.setEnabled(false);
        captureImage.takePicture(new ImageCapture.OutputFileOptions.Builder(fichier).build(),
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults resultats) {
                        normaliserEtAjouter(fichier.getPath());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        occupe = false;
                        //noinspection ResultOfMethodCallIgnored
                        fichier.delete();
                        if (liaison != null) {
                            liaison.btnCapture.setEnabled(true);
                            OutilsInterface.afficherErreur(getContext(), ErreurApp.INCONNUE);
                        }
                    }
                });
    }

    private void normaliserEtAjouter(final String chemin) {
        es.execute(() -> {
            try {
                StockagePhotos.normaliser(chemin);
                principal.post(() -> ajouterPhoto(chemin));
            } catch (ExceptionApp e) {
                StockagePhotos.supprimer(chemin);
                principal.post(() -> surEchecPhoto(e.erreur));
            }
        });
    }

    private void ouvrirGalerie() {
        if (vueModele.nombrePhotos() >= Reglages.NB_PHOTOS_MAX) {
            OutilsInterface.barreMessage(liaison.getRoot(), R.string.cam_max);
            return;
        }
        lanceurGalerie.launch("image/*");
    }

    private void importerPhoto(final Uri uri) {
        final Context app = requireContext().getApplicationContext();
        es.execute(() -> {
            try {
                String chemin = StockagePhotos.importerUri(app, uri);
                principal.post(() -> ajouterPhoto(chemin));
            } catch (ExceptionApp e) {
                principal.post(() -> surEchecPhoto(e.erreur));
            }
        });
    }

    private void ajouterPhoto(String chemin) {
        occupe = false;
        if (liaison == null) {
            StockagePhotos.supprimer(chemin);
            return;
        }
        if (!vueModele.ajouterPhoto(chemin)) {
            StockagePhotos.supprimer(chemin);
            OutilsInterface.barreMessage(liaison.getRoot(), R.string.cam_max);
        }
        liaison.btnCapture.setEnabled(vueModele.nombrePhotos() < Reglages.NB_PHOTOS_MAX);
    }

    private void surEchecPhoto(ErreurApp erreur) {
        occupe = false;
        if (liaison == null) return;
        liaison.btnCapture.setEnabled(vueModele.nombrePhotos() < Reglages.NB_PHOTOS_MAX);
        OutilsInterface.afficherErreur(getContext(), erreur);
    }

    private void afficherPhotos(List<String> chemins) {
        if (liaison == null) return;
        int n = chemins.size();
        int etape = Math.min(n + 1, Reglages.NB_PHOTOS_MAX);
        liaison.txtEtape.setText(getString(R.string.cam_etape, etape, Reglages.NB_PHOTOS_MAX));
        liaison.txtLibelleEtape.setText(LIBELLES_ETAPES[etape - 1]);
        if (n == 0) liaison.txtAstuceCam.setText(R.string.cam_astuce);
        else if (n < Reglages.NB_PHOTOS_MAX) liaison.txtAstuceCam.setText(R.string.cam_plus);
        else liaison.txtAstuceCam.setText(R.string.cam_max);

        liaison.miniatures.removeAllViews();
        LayoutInflater gonfleur = LayoutInflater.from(requireContext());
        for (int i = 0; i < n; i++) {
            final int indice = i;
            ElementMiniatureBinding t = ElementMiniatureBinding.inflate(gonfleur, liaison.miniatures, false);
            Glide.with(this).load(new File(chemins.get(i))).centerCrop().override(160, 160).into(t.imgMiniature);
            t.btnRetirer.setOnClickListener(v -> vueModele.retirerPhoto(indice));
            liaison.miniatures.addView(t.getRoot());
        }

        boolean peutAnalyser = n >= Reglages.NB_PHOTOS_MIN && n > 0;
        liaison.btnAnalyser.setVisibility(peutAnalyser ? View.VISIBLE : View.INVISIBLE);
        liaison.btnAnalyser.setText(getString(R.string.cam_analyser, n));
        liaison.btnCapture.setEnabled(n < Reglages.NB_PHOTOS_MAX && !occupe);
    }

    private void mettreAJourIconeFlash() {
        liaison.btnFlash.setImageResource(flashActif ? R.drawable.ic_flash_active : R.drawable.ic_flash_desactive);
    }

    private void quitter() {
        vueModele.abandonnerSession();
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onDestroyView() {
        captureImage = null;
        fournisseurCamera = null;
        liaison = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        es.shutdown();
        super.onDestroy();
    }
}
