# 🌿 Green Code – Optimisations Environnementales du Projet MediLabo

Ce document présente les objectifs du *Green Code*, les méthodes pour identifier les zones de consommation excessive dans une application, ainsi qu’une liste de recommandations concrètes pour améliorer la sobriété numérique du projet MediLabo.

---

## 🎯 Objectifs du Green Code

Le *Green Code* vise à réduire l’empreinte environnementale d’une application en optimisant :

- la consommation CPU  
- la consommation mémoire  
- les accès réseau  
- les opérations de stockage  
- la taille des données échangées  
- la duplication de traitements  

Un code plus “vert” est également :

- plus rapide  
- plus scalable  
- plus économique en infrastructure  
- plus simple à maintenir  

---

## 🔍 Identifier les zones de consommation excessive

Plusieurs signaux permettent de repérer les parties du code qui consomment inutilement des ressources :

### 🧠 1. Collections non maîtrisées
- Listes ou maps qui grossissent sans limite  
- Absence de pagination  
- Données chargées en masse alors qu’une partie suffit  

### 🔁 2. Création répétée d’objets
- Instanciation d’objets lourds dans des boucles  
- Création répétée de clients HTTP  
- Conversions inutiles  

### 📦 3. Chargement excessif de données
- Appels `findAll()` non filtrés  
- Récupération de documents complets au lieu de champs ciblés  
- Absence de projection ou de DTO minimalistes  

### 🧵 4. Absence de streaming
- Lecture de fichiers entiers en mémoire  
- Manipulation de grandes listes au lieu de flux  

### 🧰 5. Structures de données inadaptées
- Utilisation de structures lourdes pour des besoins simples  
- Absence de caches légers  

### 📝 6. Logs trop verbeux
- Logs DEBUG en production  
- Logs dans des boucles  
- Logs de gros objets  

### 🌐 7. Appels réseau redondants
- Absence de mutualisation  
- Pas de cache  
- Pas de timeout  

---

## ♻️ Recommandations Green pour MediLabo

### 🔧 Backend (microservices)

- Utiliser un **RestTemplate/WebClient singleton** (éviter les instanciations répétées).  
- Ajouter une **pagination** sur les endpoints retournant des listes (patients, notes).  
- Réduire les données chargées :  
  - éviter `findAll()` quand un filtre suffit  
  - utiliser des DTO minimalistes  
- Mettre en place un **cache léger** pour les données fréquemment consultées.  
- Éviter les normalisations répétées (ex : accents) en les centralisant.  
- Limiter les logs au niveau INFO en production.  
- Préférer des structures immuables pour réduire les copies d’objets.  

---

### 🗄️ Base de données (MongoDB)

- Ajouter des **index** sur les champs utilisés dans les requêtes (`patientId` dans notes).  
- Utiliser des projections pour réduire la taille des documents retournés.  
- Éviter les requêtes non filtrées.  
- Nettoyer les données obsolètes pour réduire la taille des collections.  

---

### 🧪 Tests

- Utiliser **Mockito** pour les tests unitaires (pas de contexte Spring Boot inutile).  
- Éviter les bases embarquées si non nécessaires (H2, MongoMemoryServer).  
- Exécuter les tests en parallèle si possible.  
- Réduire les données mockées au strict minimum.  

---

### 🏗️ Architecture

- Mutualiser les configurations communes entre microservices.  
- Réduire la duplication de code (normalisation, DTO, utilitaires).  
- Préférer des microservices **stateless** pour faciliter la scalabilité.  
- Documenter les flux pour éviter les appels réseau inutiles.  

---

## 📘 Conclusion

L’adoption du *Green Code* dans MediLabo permet :

- une réduction de l’empreinte environnementale  
- une amélioration des performances  
- une diminution des coûts d’infrastructure  
- une meilleure maintenabilité du projet  

Ces recommandations peuvent être intégrées progressivement, sans refonte majeure, pour rendre l’application plus durable et plus efficace.

---
