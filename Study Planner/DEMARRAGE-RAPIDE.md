# 🚀 Démarrage Rapide - Pipeline CI/CD Study Planner

## 📋 Checklist de démarrage

### 1. Configuration GitLab (5 minutes)

- [ ] Créer un projet sur GitLab
- [ ] Ajouter le remote Git :
  ```bash
  git remote add origin <url-gitlab>
  git push -u origin main
  ```
- [ ] Configurer les variables CI/CD dans **Settings > CI/CD > Variables** :
  - `DOCKER_HUB_USERNAME` : Votre username Docker Hub
  - `DOCKER_HUB_PASSWORD` : Votre token Docker Hub
- [ ] Ajouter l'enseignante comme Owner : **Settings > Members > Invite members**

### 2. Test local (10 minutes)

```bash
# Tester la compilation
mvn clean compile

# Tester tous les tests
mvn clean test

# Tester le build Docker
docker build -t study-planner:test .

# Tester avec docker-compose
docker-compose up -d
# Vérifier http://localhost:8080/actuator/health
docker-compose down
```

### 3. Premier push sur GitLab

```bash
# Ajouter tous les fichiers
git add .

# Commit
git commit -m "feat: Ajout pipeline CI/CD avec Docker et GitLab"

# Push
git push origin main
```

### 4. Vérifier le pipeline

1. Allez dans **CI/CD > Pipelines** sur GitLab
2. Vérifiez que tous les jobs passent :
   - ✅ build
   - ✅ unit-tests
   - ✅ integration-tests
   - ✅ spotbugs
   - ✅ docker-build
   - ✅ docker-hub-deploy (sur main uniquement)

## 📁 Fichiers créés

### Configuration CI/CD
- ✅ `.gitlab-ci.yml` - Pipeline GitLab CI/CD
- ✅ `Dockerfile` - Image Docker de l'application
- ✅ `docker-compose.yml` - Environnement de développement
- ✅ `.dockerignore` - Fichiers exclus du build Docker
- ✅ `sonar-project.properties` - Configuration SonarQube

### Tests
- ✅ `src/test/java/com/studyplanner/services/SubjectServiceTest.java`
- ✅ `src/test/java/com/studyplanner/services/StudySessionServiceTest.java`
- ✅ `src/test/java/com/studyplanner/utils/ProductivityAnalyzerTest.java`
- ✅ `src/test/java/com/studyplanner/rest/SubjectRestControllerTest.java`
- ✅ `src/test/java/com/studyplanner/rest/SessionRestControllerTest.java`

### Configuration
- ✅ `src/main/resources/application-docker.properties` - Config pour Docker
- ✅ `pom.xml` - Mis à jour avec plugins JaCoCo, SpotBugs, SonarQube

### Documentation
- ✅ `README-CICD.md` - Guide complet CI/CD
- ✅ `GITLAB-SETUP.md` - Configuration GitLab détaillée
- ✅ `TEST-LOCAL.md` - Guide de test local
- ✅ `DEVOPS-SUMMARY.md` - Résumé du travail réalisé
- ✅ `DEMARRAGE-RAPIDE.md` - Ce fichier

## 🎯 Objectifs atteints

- ✅ Pipeline CI/CD complet avec GitLab
- ✅ Tests unitaires, d'intégration et fonctionnels
- ✅ Analyse de code (SpotBugs + SonarQube bonus)
- ✅ Déploiement automatique sur Docker Hub
- ✅ Environnement Docker pour développement local
- ✅ Documentation complète

## 🔗 Liens utiles

- **Documentation CI/CD** : `README-CICD.md`
- **Configuration GitLab** : `GITLAB-SETUP.md`
- **Tests locaux** : `TEST-LOCAL.md`
- **Résumé DevOps** : `DEVOPS-SUMMARY.md`

## ⚠️ Points importants

1. **Docker Hub** : Le déploiement ne se fait que sur la branche `main`
2. **SonarQube** : Optionnel, le pipeline fonctionne sans (allow_failure: true)
3. **Tests** : Tous les tests doivent passer pour que le pipeline continue
4. **Variables** : N'oubliez pas de configurer `DOCKER_HUB_USERNAME` et `DOCKER_HUB_PASSWORD`

## 🆘 Besoin d'aide ?

Consultez les fichiers de documentation :
- Problèmes de configuration GitLab → `GITLAB-SETUP.md`
- Problèmes de tests locaux → `TEST-LOCAL.md`
- Questions générales → `README-CICD.md`
