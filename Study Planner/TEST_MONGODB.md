# 🧪 Test MongoDB - Vérifier que tout fonctionne

## 🎯 Objectif

Vérifier que les utilisateurs sont bien sauvegardés dans MongoDB.

## 📋 Étapes de test

### 1. Vérifier que MongoDB est démarré

```powershell
# Ouvrir PowerShell
Get-Service MongoDB

# Si le service n'est pas démarré :
net start MongoDB
```

### 2. Tester la connexion MongoDB

```bash
# Ouvrir mongosh
mongosh

# Vérifier la connexion
db.runCommand({connectionStatus: 1})

# Utiliser la base studyplanner
use studyplanner

# Vérifier qu'il n'y a pas d'utilisateurs (ou voir les existants)
db.users.countDocuments()
db.users.find().pretty()
```

### 3. Démarrer l'application Spring Boot

```bash
cd "C:\Users\AXELL\Desktop\Projet spring\Study Planner"
mvn spring-boot:run
```

**Observer les logs au démarrage** - Vous devriez voir :
- `Connected to MongoDB`
- `MongoDB connection established`

### 4. Créer un nouvel utilisateur via l'interface web

1. Ouvrir le navigateur : `http://localhost:8080/register`
2. Remplir le formulaire :
   - Username : `testuser1`
   - Email : `test1@example.com`
   - Password : `password123`
3. Cliquer sur "Créer mon compte"

### 5. Observer les LOGS de l'application

**Dans la console de l'application**, vous devriez voir :

```
========== [DEBUG] DÉBUT ENREGISTREMENT UTILISATEUR ==========
[DEBUG] Username: testuser1
[DEBUG] Email: test1@example.com
[DEBUG] ✓ Vérifications de doublons passées
[DEBUG] Rôle assigné: USER (1 rôle(s))
[DEBUG] Objet User créé (avant sauvegarde)
[DEBUG] >>> Début de la sauvegarde en MongoDB...
[DEBUG] ✓✓✓ Utilisateur SAUVEGARDÉ avec succès! ✓✓✓
[DEBUG] ID MongoDB: 675abc123def456789012345
[DEBUG] Username: testuser1
[DEBUG] Email: test1@example.com
[DEBUG] ✓✓✓ VÉRIFICATION RÉUSSIE: Utilisateur trouvé en base de données MongoDB! ✓✓✓
[DEBUG] ✓✓✓ Vérification par username RÉUSSIE: Utilisateur trouvé! ✓✓✓
[DEBUG] Nombre total d'utilisateurs en base: 1
========== [DEBUG] FIN ENREGISTREMENT UTILISATEUR ==========
```

**Si vous voyez "SAUVEGARDÉ avec succès" et "VÉRIFICATION RÉUSSIE", l'utilisateur EST en MongoDB !**

### 6. Vérifier IMMÉDIATEMENT dans MongoDB

**Dans un autre terminal**, ouvrir mongosh :

```javascript
// Se connecter
mongosh

// Utiliser la BONNE base de données
use studyplanner

// Voir tous les utilisateurs
db.users.find().pretty()

// Compter
db.users.countDocuments()

// Chercher l'utilisateur spécifique
db.users.findOne({username: "testuser1"})
```

**Vous DEVRIEZ voir votre utilisateur !**

### 7. Si vous ne voyez PAS l'utilisateur

#### ✅ Vérification 1 : Base de données correcte ?

```javascript
// Vérifier toutes les bases
show dbs

// Vérifier que vous êtes dans la bonne base
db.getName()  // Devrait afficher: studyplanner
```

#### ✅ Vérification 2 : Collection correcte ?

```javascript
// Voir toutes les collections
show collections

// Vous devriez voir: users (avec un 's' à la fin)
```

#### ✅ Vérification 3 : Chercher dans TOUTES les bases

```javascript
// Chercher dans la base 'test'
use test
db.users.find().pretty()

// Chercher dans la base 'admin'
use admin
db.users.find().pretty()
```

#### ✅ Vérification 4 : Vérifier les logs de l'application

- Si les logs montrent "SAUVEGARDÉ avec succès" → L'utilisateur EST en MongoDB
- Si les logs montrent une erreur → Regarder l'erreur

#### ✅ Vérification 5 : MongoDB est-il bien démarré ?

```powershell
# Vérifier le statut
Get-Service MongoDB

# Redémarrer si nécessaire
net stop MongoDB
net start MongoDB
```

### 8. Créer plusieurs utilisateurs de test

1. Créer un 2ème utilisateur :
   - Username : `testuser2`
   - Email : `test2@example.com`
   - Password : `password123`

2. Vérifier dans MongoDB :
   ```javascript
   use studyplanner
   db.users.countDocuments()  // Devrait être 2 maintenant
   db.users.find({}, {username: 1, email: 1})
   ```

### 9. Tester la connexion avec un utilisateur créé

1. Aller sur `http://localhost:8080/login`
2. Se connecter avec :
   - Username : `testuser1`
   - Password : `password123`

**Si vous pouvez vous connecter, c'est que l'utilisateur EST bien en MongoDB !**

## ✅ Résultat attendu

Après avoir créé un utilisateur :
- ✓ Les logs montrent "SAUVEGARDÉ avec succès"
- ✓ Les logs montrent "VÉRIFICATION RÉUSSIE"
- ✓ `db.users.find().pretty()` dans mongosh montre l'utilisateur
- ✓ `db.users.countDocuments()` augmente de 1
- ✓ Vous pouvez vous connecter avec cet utilisateur

## 🚨 Problèmes courants

### Problème : "SAUVEGARDÉ avec succès" mais pas dans MongoDB

**Cause probable** : Vous regardez dans la mauvaise base de données

**Solution** :
```javascript
use studyplanner  // Pas 'test' ou 'admin' !
db.users.find().pretty()
```

### Problème : Erreur de connexion MongoDB

**Cause probable** : MongoDB n'est pas démarré

**Solution** :
```powershell
net start MongoDB
```

### Problème : Erreur "username already exists"

**Cause probable** : L'utilisateur existe déjà

**Solution** : Utiliser un autre username ou supprimer l'utilisateur existant :
```javascript
use studyplanner
db.users.deleteOne({username: "testuser1"})
```

## 📝 Commandes MongoDB utiles

```javascript
// Voir toutes les bases
show dbs

// Changer de base
use studyplanner

// Voir toutes les collections
show collections

// Voir tous les utilisateurs
db.users.find().pretty()

// Compter
db.users.countDocuments()

// Chercher par username
db.users.find({username: "testuser1"})

// Voir les index
db.users.getIndexes()

// Supprimer un utilisateur
db.users.deleteOne({username: "testuser1"})

// Supprimer TOUS les utilisateurs (ATTENTION!)
// db.users.deleteMany({})
```

## ✨ Conclusion

**Si les logs montrent "SAUVEGARDÉ avec succès" et "VÉRIFICATION RÉUSSIE", les utilisateurs SONT dans MongoDB !**

Le problème est généralement que vous regardez dans la mauvaise base de données.

**Utilisez toujours** : `use studyplanner` avant de chercher les utilisateurs.

