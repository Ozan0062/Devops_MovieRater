# MovieRater – start med Docker

## 1. Start Docker Desktop

Installér Docker Desktop, hvis det ikke allerede er installeret. Start det med Linux-containere, og vent, til Docker Engine er klar.

## 2. Åbn en terminal i projektmappen

Åbn projektmappen i VS Code, og vælg **Terminal → New Terminal**. Kør alle kommandoerne nedenfor i mappen, hvor `docker-compose.yml` ligger.

## 3. Udfyld Supabase-oplysninger

Hvis du ikke allerede har en `.env`-fil, skal du kopiere `.env.example` og kalde kopien `.env`.

Find **Project URL** og **Publishable key** under **Connect** i dit Supabase-projekt. Udfyld `.env` med dine værdier:

```dotenv
VITE_SUPABASE_URL=https://DIT-PROJEKT.supabase.co
VITE_SUPABASE_PUBLISHABLE_KEY=DIN_PUBLISHABLE_KEY
```

Brug den offentlige publishable key, aldrig en secret key eller `service_role`. Upload ikke `.env` til Git.

## 4. Byg og start projektet

```sh
docker compose up --build -d
```

Åbn [http://localhost:8080](http://localhost:8080).

Kør samme kommando igen, hvis du ændrer koden eller værdierne i `.env`.

## 5. Kontrollér, at containeren kører

```sh
docker compose ps
```

Vent cirka 30 sekunder, og kontrollér, at `frontend` viser `Up` og `healthy`. Test også søgning og filmvisning på hjemmesiden.

Se loggen, hvis noget ikke virker:

```sh
docker compose logs --tail=50 frontend
```

Hvis Docker Engine ikke kan kontaktes, skal du kontrollere, at Docker Desktop er startet og klar.

## 6. Stop projektet

```sh
docker compose down
```

Start igen uden at genbygge:

```sh
docker compose up -d
```
