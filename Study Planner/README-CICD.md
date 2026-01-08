# Pipeline CI/CD GitLab - Study Planner

Ce document décrit la configuration et l'utilisation du pipeline CI/CD pour le projet Study Planner.

## 📋 Table des matières

1. [Prérequis](#prérequis)
2. [Structure du pipeline](#structure-du-pipeline)
3. [Configuration GitLab](#configuration-gitlab)
4. [Utilisation locale avec Docker](#utilisation-locale-avec-docker)
5. [Tests](#tests)
6. [Déploiement](#déploiement)

## 🔧 Prérequis

- GitLab avec CI/CD activé
- Docker Hub account (pour le déploiement)
- SonarQube (optionnel, pour l'analyse de code)

## 🏗️ Structure du pipeline

Le pipeline GitLab CI/CD est défini dans `.gitlab-ci.yml` et comprend les étapes suivantes :

### 1. **Build** (`build`)
- Compilation du projet avec Maven
- Génération des artefacts

### 2. **Tests** (`test`)
- **Unit tests** : Tests unitaires des services et utilitaires
- **Integration tests** : Tests d'intégration avec MongoDB

### 3. **Qualité** (`quality`)
- **SonarQube** : Analyse statique du code (bonus)
- **SpotBugs** : Détection de bugs potentiels

### 4. **Package** (`package`)
- Construction de l'image Docker
- Push vers le registre GitLab

### 5. **Deploy** (`deploy`)
- Push vers Docker Hub (uniquement sur la branche `main`)

## ⚙️ Configuration GitLab

### Variables d'environnement requises

Dans GitLab, allez dans **Settings > CI/CD > Variables** et ajoutez :

1. **Docker Hub** :
   - `DOCKER_HUB_USERNAME` : Votre nom d'utilisateur Docker Hub
   - `DOCKER_HUB_PASSWORD` : Votre token Docker Hub

2. **SonarQube** (optionnel) :
   - `SONARQUBE_URL` : URL de votre instance SonarQube
   - `SONARQUBE_TOKEN` : Token d'authentification SonarQube

### Configuration SonarQube

Pour activer SonarQube dans le pipeline :

1. Créez un projet dans SonarQube
2. Ajoutez les variables `SONARQUBE_URL` et `SONARQUBE_TOKEN` dans GitLab
3. Le pipeline exécutera automatiquement l'analyse lors des merge requests et sur `main`

## 🐳 Utilisation locale avec Docker

### Démarrage avec docker-compose

```bash
# Construire et démarrer les services
docker-compose up -d

# Voir les logs
docker-compose logs -f app

# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

L'application sera accessible sur `http://localhost:8080`
MongoDB sera accessible sur `mongodb://localhost:27017`

### Construction manuelle de l'image Docker

```bash
# Construire l'image
docker build -t study-planner:latest .

# Exécuter le conteneur
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/studyplanner \
  study-planner:latest
```

## 🧪 Tests

### Exécution des tests localement

```bash
# Tous les tests
mvn test

# Tests unitaires uniquement
mvn test -Dtest=**/*Test

# Tests d'intégration uniquement
mvn test -Dtest=**/*RepositoryTest

# Avec couverture de code (JaCoCo)
mvn clean test jacoco:report
```

### Types de tests

1. **Tests unitaires** (`*Test.java`)
   - Tests des services sans dépendances externes
   - Tests des utilitaires

2. **Tests d'intégration** (`*RepositoryTest.java`)
   - Tests avec MongoDB embarqué
   - Tests des repositories et services avec base de données

3. **Tests fonctionnels** (`*RestControllerTest.java`)
   - Tests des endpoints REST
   - Tests avec MockMvc

## 🚀 Déploiement

### Déploiement automatique

Le déploiement vers Docker Hub se fait automatiquement :
- ✅ Lors d'un merge sur la branche `main`
- ✅ Après tous les tests réussis
- ✅ Après l'analyse de code

### Images Docker Hub

Les images sont taguées avec :
- `latest` : Dernière version sur `main`
- `{commit-sha}` : Version spécifique par commit

### Pull et exécution de l'image Docker Hub

```bash
# Pull l'image
docker pull <docker-hub-username>/study-planner:latest

# Exécuter
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/studyplanner \
  <docker-hub-username>/study-planner:latest
```

## 📊 Rapports et métriques

### Couverture de code

Les rapports JaCoCo sont générés dans `target/site/jacoco/index.html`

### Analyse statique

- SpotBugs : `target/spotbugsXml.xml`
- SonarQube : Accessible via l'interface SonarQube

## 🔍 Dépannage

### Le pipeline échoue au build

- Vérifiez que Java 21 est utilisé
- Vérifiez les dépendances Maven dans `pom.xml`

### Les tests échouent

- Vérifiez que MongoDB est accessible dans le service GitLab CI
- Vérifiez les variables d'environnement MongoDB

### Le déploiement Docker Hub échoue

- Vérifiez les credentials Docker Hub dans les variables GitLab
- Vérifiez que vous avez les permissions d'écriture sur le repository Docker Hub

## 📝 Notes

- Le pipeline utilise un cache Maven pour accélérer les builds
- Les artefacts sont conservés pendant 1 semaine
- Les tests sont exécutés en parallèle quand possible
- SonarQube est optionnel (allow_failure: true)
