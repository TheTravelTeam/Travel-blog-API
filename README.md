New readme
Lancer en local

docker compose -f docker-compose.dev.yml up --build -d
# logs si besoin
docker compose -f docker-compose.dev.yml logs -f app
# stopper
docker compose -f docker-compose.dev.yml down
# stopper + wipe DB dev
docker compose -f docker-compose.dev.yml down -v