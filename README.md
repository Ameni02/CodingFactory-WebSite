# CodingFactory-WebSite
# CodingFactory-WebSite

## Description du Projet
CodingFactory-WebSite est une plateforme de gestion éducative développée en architecture microservices. Ce projet vise à offrir une solution complète aux centres de formation pour gérer les utilisateurs, les formations, les évaluations, les événements, les services de consulting et l'espace PFE.

## Membres du Groupe
- Ons Fendouli
- Belkis Sekri
- Ameni Zoubeir
- Mohamed Amine Kalai
- Mouna Chokri
- Mootaz Chouchene

## Technologies Utilisées
### Backend (Spring Boot)
- **Spring Boot** - Framework principal pour la gestion des microservices
- **Spring Security** - Gestion des utilisateurs et authentification
- **Spring Cloud (Eureka, API Gateway)** - Gestion des microservices
- **Spring Data JPA** - Interaction avec la base de données
- **Hibernate** - ORM pour la gestion des entités
- **PostgreSQL / MySQL** - Base de données principale
- **Docker** - Conteneurisation des services

### Frontend (Angular)
- **Angular** - Framework JavaScript pour le développement du front-end
- **Angular Material** - Composants UI modernes
- **RxJS** - Gestion des flux de données asynchrones

### Autres Outils
- **Maven** - Gestion des dépendances
- **Swagger** - Documentation des APIs
- **Postman** - Test des APIs
- **Git & GitHub** - Gestion du code source
- **GitHub Actions** - CI/CD pour l'automatisation des déploiements

## Modules de Gestion
### 1. Gestion des Utilisateurs
- Authentification 
- Gestion des rôles (Administrateur, Formateur, Étudiant, Consultant)

### 2. Gestion des Formations
- Création et modification des formations
- Inscriptions et suivi des apprenants

### 3. Gestion des Évaluations
- Création et attribution des évaluations
- Suivi des performances des étudiants

### 4. Gestion des Événements
- Organisation et gestion des événements éducatifs
- Inscriptions aux événements

### 5. Gestion du Consulting
- Prise de rendez-vous avec des consultants
- Suivi des sessions de consulting

### 6. Gestion de l'Espace PFE
- Suivi des projets de fin d'études
- Gestion des livrables et des feedbacks

## Installation et Lancement du Projet
### Prérequis
- Java 17+
- Node.js & Angular CLI
- Docker
- PostgreSQL / MySQL

### Backend (Spring Boot)
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend (Angular)
```bash
cd frontend
npm install
ng serve
```

## Contribution
- Cloner le projet
- Créer une branche pour vos modifications
- Faire une Pull Request

## Contact
Pour toute question ou suggestion, contactez l'un des membres de l'équipe via GitHub.

