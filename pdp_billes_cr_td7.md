# Compte-rendu du TD n°7

Les points suivants ont été abordés lors de ce TD.

## Mémoire
###Analyse de l'existant
- Nous devons citer plus de moteurs que ceux que nous avions dans la version précédente.
- Éviter de dire "trop coûteux en apprentissage".
- Ne pas dire que le langage de programmation est une contrainte.

### Analyse des besoins
- Ergonomie : ajouter aux besoins non-fonctionnels.
- Ne pas faire de partie "besoins optionnels", il vaut mieux ajouter chaque besoin optionnel dans la partie où il se rattache.
- Mettre des notes de priorité à nos besoins.

### Extensions
- Pour chaque extension, décrire des pistes de choix techniques.
- Rend la variable "_scale" (précision de l'application) évolutive en fonction du nombre et de la vitesse des billes.

### Partie gestion de projet
- Il n'y a, a priori, pas de partie "gestion de projet" ou "gestion du développement" décrivant le dérouler du développement du projet à prévoir dans le mémoire.

## Application
- Comme la variable "_scale" est en quelque sorte responsable de la précision de notre simulation, il est plus intéressant de permettre à l'utilisateur de la modifier à sa guise. Augmenter cette valeur diminue la vitesse de la simulation et cela permet d'éviter certains bugs de passages au travers d'obstacles. Il faut par contre bien décrire ce bug dans le mémoire.
- Pour mettre en évidence les bugs, il vaut mieux faire un test censé réussir et qui échoue (rouge) car il y a un bug plutôt qu'un test réussi (vert) montrant qu'il y a un comportement anormal.
- Commentaires : éviter d'en mettre à l'intérieur des fonctions. Format Javadoc si possible. Mais surtout, commentaire en anglais.

## Autre
- Faire un makefile permettant de compiler le projet sans ide.
- Faire un readme décrivant le fonctionnement du makefile et les bibliothèques à importer.