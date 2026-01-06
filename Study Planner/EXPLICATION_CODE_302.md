# Explication du Code HTTP 302

## ✅ Le code 302 est NORMAL et ATTENDU !

### Qu'est-ce qu'un code 302 ?

Un **code 302 (Found)** est une **redirection temporaire**. C'est le comportement standard et recommandé après un formulaire POST (comme l'inscription).

### Pourquoi une redirection après POST ?

Après avoir soumis un formulaire (comme l'inscription), il est recommandé de rediriger pour :

1. ✅ **Éviter la double soumission** : Si l'utilisateur actualise la page, il ne soumettra pas à nouveau le formulaire
2. ✅ **Sécurité** : Évite les attaques de rejouage
3. ✅ **Meilleure expérience utilisateur** : L'utilisateur est dirigé vers la page appropriée (login après inscription)

### Flux d'inscription dans votre application

```
1. Utilisateur remplit le formulaire /register
   ↓
2. POST /register (données envoyées)
   ↓
3. Vérifications et sauvegarde en MongoDB
   ↓
4. Code 302 - Redirection vers /login
   ↓
5. GET /login (nouvelle page chargée)
   ↓
6. Message de succès affiché : "Compte créé avec succès. Connectez-vous !"
```

### Que signifie chaque code HTTP ?

- **200 OK** : La requête a réussi (pas de redirection)
- **302 Found** : Redirection temporaire (c'est ce que vous voyez - **C'EST NORMAL**)
- **404 Not Found** : Page non trouvée
- **500 Internal Server Error** : Erreur serveur

### Comment vérifier que l'inscription a fonctionné ?

#### 1. Vérifier les logs de l'application

Après avoir créé un compte, vous devriez voir dans les logs :
```
[DEBUG] AuthViewController: Tentative de création d'utilisateur
[DEBUG] Username: votre_username
[DEBUG] Email: votre_email@example.com
[DEBUG] Tentative d'enregistrement d'un nouvel utilisateur: votre_username
[DEBUG] Sauvegarde de l'utilisateur en MongoDB...
[DEBUG] Utilisateur sauvegardé avec succès! ID: ...
[DEBUG] AuthViewController: Utilisateur créé avec succès, ID: ...
[DEBUG] AuthViewController: Redirection vers /login (302)
```

#### 2. Vérifier la redirection

Après l'inscription :
- Si succès → Vous êtes redirigé vers `/login` avec un message vert
- Si erreur → Vous êtes redirigé vers `/register` avec un message d'erreur rouge

#### 3. Vérifier dans MongoDB

```bash
# Se connecter à MongoDB
mongosh

# Utiliser la bonne base de données
use studyplanner

# Voir tous les utilisateurs
db.users.find().pretty()

# Chercher votre utilisateur
db.users.findOne({username: "votre_username"})
```

#### 4. Essayer de se connecter

Si l'inscription a fonctionné, vous devriez pouvoir vous connecter avec :
- Username : celui que vous avez saisi
- Password : celui que vous avez saisi

### En résumé

✅ **Code 302 = INSCRIPTION RÉUSSIE**

Le code 302 indique que :
- Le formulaire a été traité
- L'utilisateur a été créé en MongoDB
- Vous êtes redirigé vers la page de connexion

**C'est le comportement normal et attendu !**

