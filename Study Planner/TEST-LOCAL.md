# Guide de Test Local - Study Planner

Ce guide vous explique comment tester le pipeline CI/CD localement avant de pousser sur GitLab.

## 🧪 Tests locaux

### 1. Tests unitaires et d'intégration

```bash
# Tous les tests
mvn clean test

# Tests unitaires uniquement
mvn test -Dtest=**/*Test

# Tests d'intégration uniquement
mvn test -Dtest=**/*RepositoryTest

# Tests avec couverture de code
mvn clean test jacoco:report
# Ouvrir target/site/jacoco/index.html dans un navigateur
```

### 2. Analyse statique avec SpotBugs

```bash
# Exécuter SpotBugs
mvn clean compile spotbugs:check

# Générer le rapport XML
mvn spotbugs:spotbugs
# Rapport dans target/spotbugsXml.xml
```

### 3. Build Docker local

```bash
# Construire l'image
docker build -t study-planner:local .

# Tester l'image (nécessite MongoDB)
docker run -d \
  --name mongodb-test \
  -p 27017:27017 \
  mongo:7.0

docker run -d \
  --name app-test \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/studyplanner \
  study-planner:local

# Vérifier les logs
docker logs -f app-test

# Arrêter et nettoyer
docker stop app-test mongodb-test
docker rm app-test mongodb-test
```

### 4. Test avec docker-compose

```bash
# Démarrer tous les services
docker-compose up -d

# Vérifier les logs
docker-compose logs -f app

# Tester l'application
curl http://localhost:8080/actuator/health

# Arrêter
docker-compose down
```

### 5. Test SonarQube local (optionnel)

Si vous avez SonarQube en local :

```bash
# Démarrer SonarQube avec Docker
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  sonarqube:community

# Analyser le code
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<votre-token>
```

## ✅ Checklist avant de pousser sur GitLab

- [ ] Tous les tests passent localement (`mvn test`)
- [ ] Le build Docker fonctionne (`docker build`)
- [ ] L'application démarre avec docker-compose
- [ ] Les variables GitLab CI/CD sont configurées
- [ ] Le fichier `.gitlab-ci.yml` est présent
- [ ] Le fichier `Dockerfile` est présent
- [ ] Le fichier `docker-compose.yml` est présent

## 🐛 Résolution de problèmes

### Les tests échouent

```bash
# Vérifier la version Java
java -version  # Doit être Java 21

# Nettoyer et reconstruire
mvn clean install

# Vérifier MongoDB (pour les tests d'intégration)
# Les tests utilisent MongoDB embarqué, pas besoin de MongoDB externe
```

### Le build Docker échoue

```bash
# Vérifier le Dockerfile
docker build --no-cache -t study-planner:test .

# Vérifier les logs détaillés
docker build --progress=plain -t study-planner:test .
```

### L'application ne démarre pas dans Docker

```bash
# Vérifier les logs
docker logs <container-id>

# Vérifier la connexion MongoDB
docker exec -it <mongodb-container> mongosh

# Tester la connexion depuis le conteneur app
docker exec -it <app-container> wget -O- http://localhost:8080/actuator/health
```

## 📊 Vérification de la couverture de code

```bash
# Générer le rapport
mvn clean test jacoco:report

# Ouvrir le rapport
# Windows
start target/site/jacoco/index.html

# Linux/Mac
open target/site/jacoco/index.html
# ou
xdg-open target/site/jacoco/index.html
```

## 🔍 Vérification des métriques

### Couverture de code minimale

Le pipeline exige une couverture minimale de 50% (configurée dans `pom.xml`).

Pour vérifier :
```bash
mvn clean test jacoco:check
```

### Analyse SpotBugs

Pour voir les problèmes détectés :
```bash
mvn spotbugs:spotbugs
# Ouvrir target/spotbugsXml.xml
```

## 🚀 Simulation du pipeline GitLab

Pour simuler le pipeline GitLab localement, vous pouvez utiliser GitLab Runner :

```bash
# Installer GitLab Runner (si disponible)
# Puis exécuter
gitlab-runner exec docker build
gitlab-runner exec docker unit-tests
gitlab-runner exec docker integration-tests
```

**Note :** La plupart des tests peuvent être exécutés directement avec Maven sans GitLab Runner.
