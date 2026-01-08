# Guide de Configuration GitLab CI/CD

Ce guide vous explique comment configurer le pipeline CI/CD sur GitLab pour le projet Study Planner.

## 📋 Étapes de configuration

### 1. Créer le projet sur GitLab

1. Créez un nouveau projet sur GitLab
2. Ajoutez le repository local :
   ```bash
   git remote add origin <url-du-projet-gitlab>
   git push -u origin main
   ```

### 2. Configurer les variables CI/CD

Allez dans **Settings > CI/CD > Variables** et ajoutez les variables suivantes :

#### Variables obligatoires pour Docker Hub

| Variable | Description | Exemple |
|----------|-------------|---------|
| `DOCKER_HUB_USERNAME` | Votre nom d'utilisateur Docker Hub | `monusername` |
| `DOCKER_HUB_PASSWORD` | Votre token Docker Hub (pas le mot de passe) | `dckr_pat_...` |

**Comment obtenir un token Docker Hub :**
1. Connectez-vous sur [Docker Hub](https://hub.docker.com)
2. Allez dans **Account Settings > Security > New Access Token**
3. Créez un token avec les permissions `Read, Write & Delete`
4. Copiez le token et ajoutez-le comme variable `DOCKER_HUB_PASSWORD`

#### Variables optionnelles pour SonarQube

| Variable | Description | Exemple |
|----------|-------------|---------|
| `SONARQUBE_URL` | URL de votre instance SonarQube | `https://sonarcloud.io` ou `http://sonarqube:9000` |
| `SONARQUBE_TOKEN` | Token d'authentification SonarQube | `sqp_...` |

**Note :** Si ces variables ne sont pas définies, le job SonarQube sera ignoré (allow_failure: true)

### 3. Ajouter l'enseignante comme Owner

1. Allez dans **Settings > Members**
2. Cliquez sur **Invite members**
3. Entrez l'email : `ines-abbes` (ou l'identifiant GitLab fourni)
4. Sélectionnez le rôle : **Owner**
5. Envoyez l'invitation

### 4. Vérifier la configuration du pipeline

Le fichier `.gitlab-ci.yml` est déjà configuré. Vérifiez que :
- ✅ Le fichier est présent à la racine du projet
- ✅ Les stages sont définis : `build`, `test`, `quality`, `package`, `deploy`
- ✅ Les jobs sont configurés pour s'exécuter sur `main`, `develop` et les merge requests

## 🚀 Déclenchement du pipeline

Le pipeline se déclenche automatiquement :
- ✅ À chaque push sur `main` ou `develop`
- ✅ À chaque création/modification de merge request
- ✅ Manuellement via **CI/CD > Pipelines > Run pipeline**

## 📊 Vérification du pipeline

1. Allez dans **CI/CD > Pipelines**
2. Cliquez sur un pipeline pour voir les détails
3. Vérifiez que tous les jobs passent :
   - ✅ `build` : Compilation réussie
   - ✅ `unit-tests` : Tests unitaires passés
   - ✅ `integration-tests` : Tests d'intégration passés
   - ✅ `spotbugs` : Analyse statique (peut échouer sans bloquer)
   - ✅ `sonarqube-check` : Analyse SonarQube (optionnel)
   - ✅ `docker-build` : Image Docker construite
   - ✅ `docker-hub-deploy` : Déploiement sur Docker Hub (uniquement sur `main`)

## 🐛 Dépannage

### Le pipeline échoue au build

**Problème :** Erreur de compilation Maven
**Solution :**
- Vérifiez que Java 21 est utilisé
- Vérifiez les dépendances dans `pom.xml`
- Testez localement avec `mvn clean compile`

### Les tests échouent

**Problème :** Tests qui échouent dans GitLab CI
**Solution :**
- Vérifiez que MongoDB est accessible (service `mongo:7.0` dans `.gitlab-ci.yml`)
- Testez localement avec `mvn test`
- Vérifiez les logs du job pour plus de détails

### Le déploiement Docker Hub échoue

**Problème :** Erreur d'authentification Docker Hub
**Solution :**
- Vérifiez que `DOCKER_HUB_USERNAME` et `DOCKER_HUB_PASSWORD` sont définis
- Utilisez un token Docker Hub, pas votre mot de passe
- Vérifiez que le token a les permissions `Read, Write & Delete`
- Assurez-vous que le nom d'image dans `.gitlab-ci.yml` correspond à votre repository Docker Hub

### SonarQube ne fonctionne pas

**Problème :** Le job SonarQube est ignoré ou échoue
**Solution :**
- Vérifiez que `SONARQUBE_URL` et `SONARQUBE_TOKEN` sont définis
- Vérifiez que l'URL SonarQube est accessible depuis GitLab CI
- Le job est configuré avec `allow_failure: true`, donc il ne bloque pas le pipeline

## 📝 Notes importantes

- Le pipeline utilise un cache Maven pour accélérer les builds
- Les artefacts sont conservés pendant 1 semaine
- Le déploiement Docker Hub ne se fait que sur la branche `main`
- Les images sont taguées avec `latest` et le SHA du commit

## 🔗 Liens utiles

- [Documentation GitLab CI/CD](https://docs.gitlab.com/ee/ci/)
- [Documentation Docker Hub](https://docs.docker.com/docker-hub/)
- [Documentation SonarQube](https://docs.sonarqube.org/)
