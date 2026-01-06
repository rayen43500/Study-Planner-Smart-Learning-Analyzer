# Guide - Vérifier les Utilisateurs dans MongoDB

## 🔍 Problème : Les utilisateurs ne s'affichent pas dans MongoDB

### ✅ Vérifications à faire

#### 1. Vérifier que MongoDB est démarré

```bash
# Windows
net start MongoDB

# Ou vérifier dans le gestionnaire de services
services.msc
```

#### 2. Vérifier la connexion MongoDB

L'application est configurée pour se connecter à :
- **Host** : `localhost`
- **Port** : `27017`
- **Base de données** : `studyplanner`

Vérifiez dans `application.properties` :
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/studyplanner
```

#### 3. Se connecter à MongoDB pour vérifier

##### Option A : MongoDB Shell (mongosh)

```bash
# Ouvrir MongoDB Shell
mongosh

# Se connecter à la base de données
use studyplanner

# Voir toutes les collections
show collections

# Voir tous les utilisateurs
db.users.find().pretty()

# Compter les utilisateurs
db.users.countDocuments()

# Voir un utilisateur spécifique
db.users.findOne({username: "votre_username"})
```

##### Option B : MongoDB Compass (Interface Graphique)

1. Télécharger MongoDB Compass : https://www.mongodb.com/products/compass
2. Se connecter avec : `mongodb://localhost:27017`
3. Sélectionner la base de données `studyplanner`
4. Ouvrir la collection `users`

#### 4. Vérifier dans les logs de l'application

Quand vous créez un utilisateur, vous devriez voir dans les logs :
```
[DEBUG] Tentative d'enregistrement d'un nouvel utilisateur: ...
[DEBUG] Sauvegarde de l'utilisateur en MongoDB...
[DEBUG] Utilisateur sauvegardé avec succès! ID: ...
[DEBUG] ✓ Vérification: Utilisateur trouvé en base de données!
```

### 🔧 Problèmes possibles et solutions

#### Problème 1 : MongoDB n'est pas démarré
**Solution** : Démarrer MongoDB comme service Windows

#### Problème 2 : Mauvaise base de données
**Solution** : Vérifier que vous regardez dans la base `studyplanner` et non `test` ou autre

#### Problème 3 : Connexion échoue silencieusement
**Solution** : Vérifier les logs de l'application au démarrage - il devrait y avoir un message de connexion MongoDB

#### Problème 4 : Utilisateur créé mais pas visible
**Vérifications** :
- Regarder dans la bonne collection : `users` (pas `user`)
- Vérifier qu'il n'y a pas d'erreur dans les logs
- Essayer de se connecter avec cet utilisateur

### 📊 Structure d'un utilisateur dans MongoDB

Un utilisateur devrait ressembler à ceci :
```json
{
  "_id": ObjectId("..."),
  "username": "monusername",
  "email": "email@example.com",
  "password": "$2a$10$...",
  "roles": [
    {
      "$ref": "roles",
      "$id": ObjectId("...")
    }
  ]
}
```

### 🧪 Tester la création d'utilisateur

1. Aller sur `/register`
2. Créer un compte
3. Vérifier les logs de l'application
4. Vérifier dans MongoDB avec les commandes ci-dessus

### ✅ Commandes MongoDB utiles

```javascript
// Voir tous les utilisateurs
db.users.find().pretty()

// Voir tous les utilisateurs avec leurs rôles
db.users.find().forEach(user => {
    print("Username: " + user.username);
    print("Email: " + user.email);
    print("Roles: " + user.roles.length);
    print("---");
})

// Supprimer tous les utilisateurs (ATTENTION!)
// db.users.deleteMany({})

// Voir la taille de la collection
db.users.stats()

// Créer un index si nécessaire
db.users.createIndex({username: 1}, {unique: true})
db.users.createIndex({email: 1}, {unique: true})
```

### 🔍 Debug dans l'application

Les logs de debug ont été ajoutés dans `UserService.registerUser()` pour tracer :
- La tentative d'enregistrement
- Les vérifications de doublons
- La sauvegarde
- La vérification post-sauvegarde

Si vous ne voyez pas les utilisateurs :
1. Vérifiez les logs de l'application
2. Vérifiez que MongoDB est bien démarré
3. Vérifiez que vous regardez dans la bonne base de données (`studyplanner`)
4. Vérifiez la collection `users` (pas `user`)

