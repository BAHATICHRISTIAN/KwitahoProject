package bi.kwitaho.app.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import bi.kwitaho.app.R;

/** Catalogue des 5 classes du modèle. */
public final class CatalogueMaladies {
    public static final String SAINE = "HEALTHY";

    private static final List<Maladie> TOUTES;

    static {
        List<Maladie> liste = new ArrayList<>();
        liste.add(new Maladie("CMD", false,
                R.string.maladie_cmd_nom, R.string.maladie_cmd_nature, R.string.maladie_cmd_desc, R.string.maladie_cmd_symptomes, R.string.maladie_cmd_cause,
                R.drawable.img_cmd,
                new int[]{R.string.maladie_cmd_prevention1, R.string.maladie_cmd_prevention2, R.string.maladie_cmd_prevention3},
                new int[]{R.string.maladie_cmd_conseil1, R.string.maladie_cmd_conseil2}));
        liste.add(new Maladie("CBSD", false,
                R.string.maladie_cbsd_nom, R.string.maladie_cbsd_nature, R.string.maladie_cbsd_desc, R.string.maladie_cbsd_symptomes, R.string.maladie_cbsd_cause,
                R.drawable.img_cbsd,
                new int[]{R.string.maladie_cbsd_prevention1, R.string.maladie_cbsd_prevention2, R.string.maladie_cbsd_prevention3},
                new int[]{R.string.maladie_cbsd_conseil1, R.string.maladie_cbsd_conseil2}));
        liste.add(new Maladie("CBB", false,
                R.string.maladie_cbb_nom, R.string.maladie_cbb_nature, R.string.maladie_cbb_desc, R.string.maladie_cbb_symptomes, R.string.maladie_cbb_cause,
                R.drawable.not_image,
                new int[]{R.string.maladie_cbb_prevention1, R.string.maladie_cbb_prevention2, R.string.maladie_cbb_prevention3},
                new int[]{R.string.maladie_cbb_conseil1, R.string.maladie_cbb_conseil2}));
        liste.add(new Maladie("CGM", false,
                R.string.maladie_cgm_nom, R.string.maladie_cgm_nature, R.string.maladie_cgm_desc, R.string.maladie_cgm_symptomes, R.string.maladie_cgm_cause,
                R.drawable.not_image,
                new int[]{R.string.maladie_cgm_prevention1, R.string.maladie_cgm_prevention2, R.string.maladie_cgm_prevention3},
                new int[]{R.string.maladie_cgm_conseil1, R.string.maladie_cgm_conseil2}));
        liste.add(new Maladie("HEALTHY", true,
                R.string.maladie_saine_nom, R.string.maladie_saine_nature, R.string.maladie_saine_desc, R.string.maladie_saine_symptomes, R.string.maladie_saine_cause,
                R.drawable.img_feuille_saine,
                new int[]{R.string.maladie_saine_prevention1, R.string.maladie_saine_prevention2, R.string.maladie_saine_prevention3},
                new int[]{R.string.maladie_saine_conseil1, R.string.maladie_saine_conseil2}));
        TOUTES = Collections.unmodifiableList(liste);
    }

    private CatalogueMaladies() {}

    public static List<Maladie> toutes() {
        return TOUTES;
    }

    /** Retourne la fiche correspondant au code (feuille saine par défaut). */
    public static Maladie parCode(String code) {
        String c = normaliser(code);
        for (Maladie d : TOUTES) {
            if (d.code.equals(c)) return d;
        }
        return TOUTES.get(TOUTES.size() - 1);
    }

    /** Convertit une étiquette de labels.txt en code interne (Healthy devient HEALTHY). */
    public static String normaliser(String libelle) {
        if (libelle == null) return SAINE;
        String s = libelle.trim().toUpperCase(Locale.ROOT);
        if (s.equals("HEALTHY") || s.equals("SAINE") || s.equals("SAIN")) return SAINE;
        return s;
    }

    /** Ordre par défaut des classes (identique à labels.txt fourni). */
    public static List<String> etiquettesParDefaut() {
        List<String> l = new ArrayList<>();
        l.add("CBB");
        l.add("CBSD");
        l.add("CGM");
        l.add("CMD");
        l.add(SAINE);
        return l;
    }
}
