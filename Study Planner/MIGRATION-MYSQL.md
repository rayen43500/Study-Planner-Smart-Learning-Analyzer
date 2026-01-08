# Migration MongoDB vers MySQL

Ce document décrit la migration de MongoDB vers MySQL réalisée pour le projet Study Planner.

## 📋 Résumé des modifications

### 1. Dépendances Maven (`pom.xml`)

**Supprimé :**
- `spring-boot-starter-data-mongodb`
- `de.flapdoodle.embed.mongo` (pour les tests)

**Ajouté :**
- `spring-boot-starter-data-jpa`
- `mysql-connector-j`
- `h2` (pour les tests avec base de données en mémoire)

### 2. Entités (Models)

Toutes les entités ont été converties de MongoDB à JPA/Hibernate :

#### User
- `@Document` → `@Entity`
- `@Id String id` → `@Id @GeneratedValue Long id`
- `@DBRef` → `@ManyToMany` avec `@JoinTable`
- Ajout de `@Table` avec contraintes d'unicité

#### Role
- `@Document` → `@Entity`
- `@Id String id` → `@Id @GeneratedValue Long id`
- `ERole name` → `@Enumerated(EnumType.STRING)`
- Ajout de `@Table` avec contrainte d'unicité

#### Subject
- `@Document` → `@Entity`
- `@Id String id` → `@Id @GeneratedValue Long id`
- `@DBRef User` → `@ManyToOne` avec `@JoinColumn`

#### StudySession
- `@Document` → `@Entity`
- `@Id String id` → `@Id @GeneratedValue Long id`
- `@DBRef Subject` → `@ManyToOne` avec `@JoinColumn`
- `@DBRef User` → `@ManyToOne` avec `@JoinColumn`

### 3. Repositories

Tous les repositories ont été migrés :
- `MongoRepository<User, String>` → `JpaRepository<User, Long>`
- `MongoRepository<Subject, String>` → `JpaRepository<Subject, Long>`
- `MongoRepository<StudySession, String>` → `JpaRepository<StudySession, Long>`
- `MongoRepository<Role, String>` → `JpaRepository<Role, Long>`

### 4. Services

Les méthodes utilisant des IDs ont été mises à jour :
- `getOwnedSubject(User user, String id)` → `getOwnedSubject(User user, Long id)`
- `deleteSubject(User user, String id)` → `deleteSubject(User user, Long id)`
- `getOwnedSession(User user, String id)` → `getOwnedSession(User user, Long id)`
- `deleteSession(User user, String id)` → `deleteSession(User user, Long id)`

### 5. Contrôleurs

Tous les contrôleurs ont été mis à jour pour utiliser `Long` au lieu de `String` :
- `@PathVariable String id` → `@PathVariable Long id`
- `StudySessionDTO.subjectId` : `String` → `Long`

### 6. Configuration

#### `application.properties`
- Configuration MongoDB supprimée
- Ajout de la configuration MySQL/JPA/Hibernate
- Configuration de la base de données locale

#### `application-docker.properties`
- Configuration pour Docker avec MySQL
- Variables d'environnement pour MySQL

#### `application-test.properties` (nouveau)
- Configuration H2 pour les tests
- Base de données en mémoire

### 7. Docker

#### `docker-compose.yml`
- Service `mongodb` → `mysql`
- Image `mongo:7.0` → `mysql:8.0`
- Configuration MySQL avec healthcheck
- Variables d'environnement MySQL

### 8. Tests

#### Tests d'intégration
- `@DataMongoTest` → `@DataJpaTest`
- Utilisation de H2 en mémoire pour les tests
- Tous les IDs changés de `String` à `Long`

#### Tests unitaires et fonctionnels
- Tous les IDs de test changés de `String` à `Long`
- Mise à jour des assertions pour utiliser `Long`

### 9. Pipeline CI/CD

#### `.gitlab-ci.yml`
- Service `mongo:7.0` → `mysql:8.0` (pour les tests d'intégration)
- Variables MongoDB → Variables MySQL
- Les tests unitaires utilisent maintenant H2 (pas de service externe nécessaire)

## 🔄 Changements de types

### IDs
- **Avant :** `String id` (MongoDB ObjectId)
- **Après :** `Long id` (Auto-increment MySQL)

### Relations
- **Avant :** `@DBRef` (références MongoDB)
- **Après :** `@ManyToOne`, `@ManyToMany` avec `@JoinColumn`, `@JoinTable` (JPA)

## ✅ Points importants

1. **Migration des données** : Les données MongoDB existantes ne sont pas automatiquement migrées. Il faudra créer un script de migration si nécessaire.

2. **Contraintes de base de données** : Les contraintes d'unicité et les relations sont maintenant gérées par MySQL au niveau de la base de données.

3. **Transactions** : JPA/Hibernate gère les transactions automatiquement, contrairement à MongoDB.

4. **Performance** : Les requêtes SQL sont optimisées par MySQL, ce qui peut améliorer les performances pour les requêtes complexes.

5. **Tests** : Les tests utilisent H2 (base de données en mémoire) pour plus de rapidité.

## 🚀 Déploiement

Le déploiement reste identique avec Docker. Seule la base de données change :

```bash
docker-compose up -d
```

La base de données MySQL sera créée automatiquement au premier démarrage grâce à `createDatabaseIfNotExist=true`.

## 📝 Notes

- Les schémas de base de données sont créés automatiquement avec `spring.jpa.hibernate.ddl-auto=update`
- Pour la production, il est recommandé d'utiliser `validate` ou `none` avec des scripts de migration (Flyway/Liquibase)
- Les indexes MongoDB sont maintenant gérés par les annotations JPA (`@Indexed` → contraintes de table)
