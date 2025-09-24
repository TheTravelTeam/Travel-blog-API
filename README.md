New readme
# Lancer l’API en fonction de l’environnement
Notre application utilise les profils Spring Boot (spring.profiles.active) pour séparer la configuration local et docker/prod.
Chaque profil charge ses propriétés depuis un fichier dédié (application.properties ou application-local.properties) et, si nécessaire, depuis un .env spécifique.

# Structure des fichiers de configuration
src/main/resources/
├── application.properties              # Config par défaut (équivalent dev/prod)
├── application-local.properties        # Config spécifique pour exécution 100% locale
.env                                       # Variables communes ou liées au profil par défaut
.env.local                                 # Variables pour profil local (API + BDD en local)


# Pourquoi utiliser les profils Spring Boot ?
Les profils permettent :

De séparer les configurations (URL BDD, CORS, secrets…)

De changer facilement d’environnement sans modifier le code

D’éviter d’écraser les paramètres d’un environnement en en lançant un autre

# Règle importante :
spring.profiles.active ne doit pas être défini dans les fichiers application-<profil>.properties (ça crée un conflit).
On l’active soit dans application.properties, soit en ligne de commande avec -Dspring-boot.run.profiles.

▶️ Lancer l’API dans différents environnements
1. Local (API + BDD locales)
   Utilise .env.local + application-local.properties :

mvn -s /home/antop/travels-blog/.m2/settings.xml \
    -Dspring-boot.run.profiles=local \
    spring-boot:run
(config antoine)

ou 

mvn -Dspring-boot.run.profiles=local spring-boot:run
./mvnw -Dspring-boot.run.profiles=local spring-boot:run
(config classique)

Si tu es sur PowerShell (Windows) : mettre l’option entre guillemets

mvn spring-boot:run "-Dspring.profiles.active=local"

BDD : MySQL sur localhost
API : Lancement direct en local, sans Docker

2. Docker en Dev :
   Utilise .env + application.properties :

# Lancer en local
docker compose -f docker-compose.dev.yml up --build -d
# logs si besoin
docker compose -f docker-compose.dev.yml logs -f app
# stopper
docker compose -f docker-compose.dev.yml down
# stopper + wipe DB dev
docker compose -f docker-compose.dev.yml down -v

BDD + API : Lancement complet en Docker
Profil par défaut (application.properties)

## SI test en back docker avec le front sous docker
ne pas oubliez d'accepter le localhost du front dans le .env par exemple
#CORS_ALLOWED_ORIGIN=http://localhost:4200,http://localhost:8081

3. Docker en prod :
   Lancer un merge sur main --> CICD s'occupe de tout

# Ordre de chargement des configurations
application.properties (général)

application-<profil>.properties (si profil activé)

.env[.properties] (communs)

.env.<profil>[.properties] (spécifique au profil actif)

Variables d’environnement système

Arguments en ligne de commande (-D...)

# Points importants
Les variables sensibles (JWT_SECRET, mots de passe DB…) sont dans les .env, jamais en dur dans le code

Les fichiers .env ne doivent pas être commités s’ils contiennent des secrets

Toujours activer un profil via la commande Maven ou une variable d’environnement


# BDD password en local
id1 : password123
id2: password456
id3: password789
