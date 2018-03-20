# Compte-rendu TD 2

Les points principaux abordés au cours du second TD de PdP du mercredi 31/01 à 18h sont : 

## Le cahier des besoins

* Sur l'analyse de l'existant :
	* il faut parler des moteurs physiques qui existent déjà, de ce qu'ils font et ne font pas ;
	* il faut expliquer pour chaque moteur physique cité pourquoi on s'en inspire ou non, quels sont les avantages et les inconvénients qu'ils peuvent nous apporter ;
* Nous devons préciser notre introduction de telle sorte à ce qu'elle résume et explique entièrement le projet qui va être réalisé. On doit comprendre à quoi sert l'application et comment elle va foncionner avec l'introduction ;
* La hiérarchisation des besoins dans la partie analyse des besoins fonctionnels et non-fonctionnels est libre. Il faut en revanche, pour chaque besoin, rajouter un indicateur de priorité ;
* Il faut augmenter la précision de la description de nos besoins. Si ça n'est pas possible pour tous les besoins d'ici la remise de la première version, il faut au moins avoir pris un besoin et l'avoir détaillé en profondeur afin de montrer que l'on en est capables, la précision des autres besoins pouvant être réalisée au fur et à mesure que le projet avance ;
* Ce document étant destiné à nos professeurs, il est possible d'y insérer des notes s'adressant directement à ce dernier afin de préciser un choix qui a été fait dans le cahier des besoins. Il faut tout de même éviter d'abuser de cette pratique et toujours bien justifier quand nous l'utilisons ;
* La remise du cahier des besoins peut se faire par mail et/ou commit dans le savane (sous réserve de l'envoi d'un mail prévenant le chargé de TD que le document a été poussé).


## La première release

* Lors de la première  release du code à fournir pour le 16 février, il faut pouvoir présenter quelques fonctionnalités :
	* bien réfléchies ;
	* pas forcément entièrement implémentées ;
	* pas forcément liées à un programme global ;
	* qui ne sont pas forcées de fonctionner (même si c'est tout de même préférable) ;
	* qui n'ont pas forcément d'affichage graphique (affichage terminal).

	
## Le roulement <> le glissement

* Le glissement étant plus facile que le roulement, on doit lui donner une priorité supérieure, c'est plus raisonnable ;
* Le fonctionnement du roulement est une question qu'il est cruciale de se poser, à laquelle on doit penser tout au long du projet, mais, au vue de la difficulté de cette fonctionnalité, elle pourra n'être dévelopée qu'a la fin du projet ;
* Si on arrive à avoir le roulement et le glissement, on peut se contenter de faire en sorte que ces deux phénomènes physiques aient un impact sur la vitesse/direction de la bille indépendamment l'un de l'autre. Il n'est pas prioritaire (voire pas nécessaire car compliqué) de faire en sorte que le roulement aie un impact sur le glissement et inversement.


## Autres 

* Le coeur de notre projet étant la réalisation d'un moteur physique, on peut se concentrer sur cette partie quitte à délaisser un peu l'interface graphique ;
* Pour simplifier le développement, au moins au départ, on peut réaliser l'écoulement des billes en considérant celles-ci comme des points (avec un rayon) sur lesquels ont applique la physique de base des points ;
* La documentation se faisant rare sur notre sujet, on peut orienter nos recherches vers des jeux opensource de billard (comportant des billes se déplaçant et une gestion de collision entre plusieurs billes et avec des obstacles) et de flipper (comportant des billes se déplaçant sur un plan incliné avec des collision sur des obstacles).