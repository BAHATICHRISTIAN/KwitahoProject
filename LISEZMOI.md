# Kwitaho — projet Android (version prototype, manioc)

Application Android pour les agriculteurs burundais : photographier une feuille de manioc, obtenir un diagnostic
par intelligence artificielle **embarquée** (sans Internet), lire la fiche de la maladie et les conseils.

- Langage : Java 17 — Interfaces : XML — Design : Material Design 3 (thème vert, inspiré de la structure de My Airtel)
- Architecture : MVVM (View / ViewModel / Repository / Data) — Navigation Component — CameraX — Room — LiteRT (TensorFlow Lite)
- Identifiant : `bi.kwitaho.app` — minSdk 24 (Android 7.0) — compileSdk / targetSdk 36
- Langues : français, kirundi, kiswahili (⚠ traductions à faire relire par des locuteurs natifs)

## 1. Ouvrir et lancer le projet (pas à pas)

1. Décompressez le dossier `Kwitaho`.
2. Dans Android Studio : **File > Open**, choisissez le dossier `Kwitaho` (celui qui contient `settings.gradle`).
3. Attendez la synchronisation Gradle (connexion Internet nécessaire la première fois, pour télécharger les bibliothèques).
4. Si Android Studio demande de créer/mettre à jour le *Gradle wrapper* : acceptez (le fichier
   `gradle/wrapper/gradle-wrapper.properties` demande Gradle 9.6.0 ; le fichier `gradle-wrapper.jar` n'est pas inclus).
5. Branchez un téléphone Android (débogage USB activé) ou lancez un émulateur, puis **Run ▶**.
6. Au premier lancement : démarrage → choix de la langue → 2 écrans de présentation → accueil.

> Ce projet a été écrit sans pouvoir être compilé dans l'environnement de création. Il a été vérifié par des contrôles
> automatiques (XML bien formé, toutes les ressources référencées existent, tous les champs ViewBinding existent,
> syntaxe Java valide), mais **le premier build peut afficher de petites erreurs à corriger**. C'est prévu dans le
> cahier des charges (section 19.3).

## 2. Versions à vérifier à la synchronisation

| Élément | Valeur dans le projet | Si ça échoue |
|---|---|---|
| Plugin Android (AGP) | 9.4.0 (`build.gradle`) | utiliser la version proposée par Android Studio (Assistant de mise à niveau) |
| Gradle | 9.6.0 (`gradle-wrapper.properties`) | idem |
| LiteRT | `com.google.ai.edge.litert:litert:1.2.0` (`app/build.gradle`) | remplacer par `org.tensorflow:tensorflow-lite:2.16.1` (même package `org.tensorflow.lite`, aucun code à changer) |
| Autres bibliothèques | versions fixées dans `app/build.gradle` | monter d'une version si Android Studio le propose |

## 3. Mode démonstration et branchement du vrai modèle

Tant que `app/src/main/assets/kwitaho_model.tflite` est **absent**, l'application tourne en **mode démonstration** :
le résultat est simulé (stable pour une même photo) et signalé à l'écran (bandeau jaune sur le résultat et la fiche,
pastille « Démo » dans l'historique).

Pour brancher le modèle entraîné :

1. Copiez le fichier dans `app/src/main/assets/` et nommez-le exactement `kwitaho_model.tflite`.
2. Ouvrez `app/src/main/assets/labels.txt` : une étiquette par ligne, **dans l'ordre exact des sorties du modèle**.
   Le fichier fourni suit l'ordre alphabétique habituel du jeu Cassava Leaf Disease (CBB, CBSD, CGM, CMD, Healthy) :
   vérifiez-le avec l'ordre de votre entraînement.
3. Vérifiez la normalisation dans `util/Config.java` → `FLOAT_INPUT_MODE`
   (0 = pixels bruts 0..255, cas d'un MobileNetV3 Keras avec prétraitement intégré ; 1 = 0..1 ; 2 = -1..1).
   Elle doit être **identique** à celle de l'export. Si l'entrée du modèle est en uint8, rien à régler.
4. Relancez : le mode démonstration disparaît automatiquement. Si le modèle ne peut pas être chargé
   (nombre de classes différent de `labels.txt`, entrée inattendue), un message traduit s'affiche.

## 4. Organisation du code (`app/src/main/java/bi/kwitaho/app`)

| Dossier | Contenu |
|---|---|
| `ui` | `MainActivity` (en-tête, barre du bas à 5 emplacements, navigation), un fragment par écran, adaptateurs |
| `ui/widget` | `SkeletonView` (effet squelette animé), `ScanOverlayView` (cadre guide de la caméra) |
| `viewmodel` | `DiagnosticViewModel` (photos + analyse), `HistoryViewModel`, `DiseaseViewModel`, `ProfileViewModel` |
| `repository` | `DiagnosticRepository` (Room + photos), `DiseaseRepository`, `ProfileRepository` (profil local) |
| `data/db`, `data/model` | entité/DAO/base Room ; fiches maladies (`DiseaseCatalog`), niveaux de confiance |
| `ml` | `Classifier`, `DemoClassifier`, `TfliteClassifier`, `ClassifierProvider` |
| `util` | `Config` (tous les réglages), erreurs typées, images, stockage privé des photos, aides d'interface |

Textes : `res/values/strings.xml` (français), `values-rn` (kirundi), `values-sw` (kiswahili).
Les fiches maladies et conseils sont dans ces fichiers (catalogue dans le code, conformément à la section 19.2).

## 5. Décisions prises (à confirmer ou à modifier)

- **Couleur** : vert de plante (`res/values/colors.xml`) à la place du rouge d'Airtel ; police Poppins (licence libre).
- **Photos** : jusqu'à 3 photos par diagnostic (haut, milieu, bas), probabilités moyennées. Minimum = 1 photo
  (`Config.MIN_PHOTOS`). Mettre `3` pour imposer les trois photos.
- **Ordre du premier lancement** : démarrage → **langue** → présentation → accueil (la langue est choisie avant les
  textes de présentation ; le cahier plaçait la langue après).
- **Slogan** de l'écran de démarrage : « Prendre soin de nos plantes » (maquette). Le slogan du cahier des charges
  apparaît dans « À propos ».
- **Barre du bas** : 5 emplacements avec bouton Scanner central surélevé (cahier des charges), masquée sur le
  démarrage, la langue, la présentation, la caméra et l'analyse.
- **Profil** : local, sans compte. « Se déconnecter » de la maquette est remplacé par « Effacer mes données ».
- **Seuils de confiance** : ≥ 80 % normal ; 60–79 % « résultat probable » ; < 60 % « résultat incertain,
  reprenez la photo » (aucun diagnostic définitif).
- **Images** : feuille saine = vos photos ; CMD, CBSD, CBB, CGM, agriculteur et bouclier = **illustrations
  générées**, pas de vraies photos. Remplacez-les (`res/drawable-nodpi/img_cmd.jpg`, etc.) par des photos validées par
  un agronome. Tant que `Config.SHOW_ILLUSTRATION_NOTE` vaut `true`, la fiche affiche « Illustration : à remplacer ».
- **Squelettes** : `SkeletonView`, affichés au moins 450 ms (`Config.SKELETON_MIN_MS`) sur l'accueil, les maladies et
  l'historique.

## 6. À faire avant les essais avec des agriculteurs

1. Compiler, lancer sur un vrai téléphone, corriger les erreurs éventuelles.
2. Entraîner le modèle MobileNetV3, l'exporter en `.tflite`, le brancher (section 3) et le valider sur des photos du Burundi.
3. Faire relire les traductions (kirundi, kiswahili) et les fiches maladies (agronome, locuteurs natifs). La classe CGM
   (« Marbrure verte ») doit être validée par un agronome avant publication.
4. Remplacer les illustrations par de vraies photos validées.
5. Écrire des tests automatiques ; tester sur au moins trois téléphones dont un d'entrée de gamme ; tester en mode avion.
6. Ajouter plus tard : compte en ligne (Firebase), synchronisation, classe « pas une feuille », retour vocal en kirundi.

## 7. Avertissement

Kwitaho est un outil d'aide au diagnostic. Il ne remplace pas l'avis d'un agronome ou d'un technicien agricole.
