New readme
Lancer en local

docker compose -f docker-compose.dev.yml up --build -d
# logs si besoin
docker compose -f docker-compose.dev.yml logs -f app
# stopper
docker compose -f docker-compose.dev.yml down
# stopper + wipe DB dev
docker compose -f docker-compose.dev.yml down -v

# SI test en local docker avec le front sous docker 
ne pas oubliez d'accepter le localhost du front dans le .env par exemple
#CORS_ALLOWED_ORIGIN=http://localhost:4200,http://localhost:8081