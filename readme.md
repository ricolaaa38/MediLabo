🌱 Green Code – Optimisations environnementales du projet
L’objectif du Green Code est de réduire l’empreinte environnementale de l’application en limitant la consommation CPU, mémoire, réseau et stockage. Un code plus “vert” est également plus performant, plus scalable et moins coûteux à exécuter.

🎯 Objectifs du Green Code
Minimiser les ressources utilisées par chaque microservice

Réduire les traitements inutiles

Éviter les allocations mémoire superflues

Limiter les appels réseau redondants

Optimiser les accès aux bases de données

Améliorer la durée de vie et la sobriété de l’infrastructure

🔍 Comment identifier les zones de consommation excessive ?
Plusieurs indicateurs permettent de repérer les parties du code qui consomment inutilement de la mémoire ou du CPU :

Collections non maîtrisées : listes ou maps qui grossissent sans limite.

Objets créés en boucle : instanciations répétées dans des traitements fréquents.

Chargement excessif de données : récupération de données complètes alors qu’une partie suffit.

Absence de pagination : chargement de centaines d’éléments en une seule fois.

Logs trop verbeux : logs DEBUG en production ou logs dans des boucles.

Appels réseau redondants : absence de cache ou de mutualisation.

Structures de données inadaptées : utilisation de types lourds pour des besoins simples.

♻️ Recommandations Green pour ce projet
🔧 Backend (microservices)
Utiliser un RestTemplate ou WebClient singleton (éviter les instanciations répétées).

Ajouter une pagination sur les endpoints qui retournent des listes (patients, notes).

Éviter de charger toutes les notes d’un patient si seules certaines informations sont nécessaires.

Réduire les conversions et normalisations répétées (ex : normalisation des accents).

Mettre en place un cache léger pour les données fréquemment consultées.

Utiliser des DTO minimalistes pour réduire la taille des réponses JSON.

Limiter les logs au niveau INFO en production.

🗄️ Base de données
Ajouter des index sur les champs fréquemment utilisés (patientId dans notes).

Éviter les requêtes non filtrées (findAll()) dans les services critiques.

Utiliser des projections ou des champs ciblés pour réduire la taille des documents.

🧪 Tests
Utiliser des tests unitaires Mockito (pas de contexte Spring Boot inutile).

Éviter les bases embarquées si non nécessaires (H2, MongoMemoryServer).

Exécuter les tests en parallèle si possible.

🏗️ Architecture
Mutualiser les configurations communes entre microservices.

Réduire la duplication de code (ex : normalisation, DTO).

Préférer des microservices légers, stateless, facilement scalables.