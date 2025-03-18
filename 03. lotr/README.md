# Kata Lord of the Rings - Refactoring Avancé

Ce projet est un kata de refactoring avancé sur le thème du Seigneur des Anneaux. Il contient du code délibérément complexe, rigide et difficile à maintenir pour pratiquer différentes techniques de refactoring.

## Contexte

Le code simule un système de gestion d'inventaire, de personnages et de quêtes dans l'univers du Seigneur des Anneaux. Les différentes classes implémentent :

- Une gestion d'items avec des propriétés et comportements spéciaux
- Un système de quêtes avec calcul de réussite et récompenses
- Un gestionnaire de personnages et leurs attributs
- Un système d'inventaire avec vente et achat d'objets

## Structure du Code

Le code présente délibérément plusieurs problèmes de conception et violations de principes :

- Utilisation intensive de "code smells"
- Classes avec responsabilités multiples (violation SRP)
- Difficultés d'extension (violation OCP)
- Dépendances rigides entre classes (violation DIP)
- Singletons difficiles à tester
- Variables et méthodes mal nommées
- Duplication de code
- Longues méthodes à complexité cyclomatique élevée
- Mutations d'état intempestives
- Tests fragiles

## Objectifs du Kata

Le but est de refactorer le code pour le rendre plus maintenable, testable et extensible, tout en préservant son comportement externe.

## Comment Démarrer

1. Clonez ce repository
2. Compilez le projet avec Maven : `mvn clean compile`
3. Exécutez les tests : `mvn test`
4. Lancez l'application : `mvn exec:java -Dexec.mainClass="org.lotr.kata.LordOfTheRingsApp"`

## Inspirations

Ce kata est inspiré par :
- [Gilded Rose Kata](https://github.com/emilybache/GildedRose-Refactoring-Kata)
- [Trip Service Kata](https://github.com/sandromancuso/trip-service-kata)
- [Tennis Refactoring Kata](https://github.com/emilybache/Tennis-Refactoring-Kata)

Mais avec une complexité et une profondeur accrue, plus de responsabilités, et dans l'univers du Seigneur des Anneaux.

## Conseils

- Commencez par écrire des tests pour couvrir le comportement actuel
- Refactorez petit à petit, en vérifiant que les tests continuent de passer
- Utilisez les outils de refactoring de votre IDE
- Cherchez les "code smells" et appliquez les patrons de conception appropriés
- N'essayez pas de tout refactorer d'un coup, procédez de manière incrémentale