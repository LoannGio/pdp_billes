# Compte-rendu du TD n°6

Les points suivants ont été abordés lors de ce TD.

## Mémoire
- Il faut que nous commencions à rédiger le mémoire du projet en même temps que nous continuons à développer l'application. 
- Revoir l'analyse des besoins pour améliorer sa qualité/précision et pouvoir l'intégrer au mémoire

## Git
- Mettre à jour le Git du serveur Savane plus régulièrement. Pour ça, deux choix : 
- 1) Utiliser le Git du savane comme dépôt principal
- 2) Synchroniser le Git du savane avec notre Github, synchroniser les historiques et push régulièrement sur le Git du savane (solution mise en place)

## Vecteurs
- Modifier notre classe Vector : nous stockons à la fois les coordonées cartésiennes et polaires, c'est une redondance d'information à éviter puisque l'on peut retrouver l'une à partir de l'autre.
- Modifier le calcul de l'angle entre deux vecteurs : il s'agit simplement de faire la différence des arguments

## Moteur physique
- Correction du bug de collision entre les obstacles et les billes : nous appliquions le coefficient de restitutions aux deux vitesses verticales et horizontales alors qu'il faut uniquement l'appliquer à la vitesse verticale.
- Penser à indiquer (dans le mémoire sans doute) que notre modèle est limité par sa mécanique de pas de temps, ce qui empêche de pouvoir détecter certaines collisions lorsque les billes atteignent une vitesse élevée. Toutefois, ce problème n'est pas trop problématique pour nous puisque nos objets ne sont pas censés atteindre une vitesse élevée. Si on voulait gérer ce problème, il faudrait revoir tout notre modèle et intégrer des notions de calculs d'intégrales sur le déplacement des billes afin de savoir si elles entrent en collisions entre deux pas de temps.

## Optimisation
- Maintenant que le moteur physique est bien avancé, nous allons commencer à nous pencher sur la question de l'optimisation de l'interface. Cela concerne principalement l'implémentation d'un Panel Buffer qui permettra d'accélérer l'affichage des objets sur l'interface, notemment en permettant l'optimisation de l'affichage des traces des billes (jusqu'ici première raisond e lenteur de l'application).
-L'idée du Panel Buffer est la suivante : on a un Panel de dessin en mémoire sur lequel on dessine les obstacles et la dernière trace ajoutée par chaque bille à chaque pas de temps. De fait, ce Panel contiendra en permanence l'affichage des obstacles et des traces des billes actualisées. Lorsqu'on veut repeindre les billes, on les repeint alors sur le PanelBuffer. On n'a donc plus à reparcourir la liste des traces à chaque pas de temps pour les afficher. En revanche, cela nécessite de stocker dans chaque bille, en plus de la liste de ses traces, la position de la dernière trace ajoutée. (je ne suis pas certain qu'un ajout dans une ArrayList en java ajoute l'objet en fin de liste. Si c'est le cas, on peut utiliser traces[traces.length-1] et on a donc plus à stocker la dernière trace dans la bille pour y avoir accès)
