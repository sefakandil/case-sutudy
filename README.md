# Flight Route Planner

Bu repo, iki ana bilesenden olusan bir ucus rota planlama calismasidir:
- `route`: Spring Boot tabanli REST API
- `flight-route-ui`: React + Vite tabanli web arayuzu

## Proje Yapisi

- `docker-compose-postgre.yml`: PostgreSQL, Redis ve Adminer servisleri
- `route/`: Backend uygulamasi (JWT auth, role-based yetkilendirme, route search)
- `flight-route-ui/`: Frontend uygulamasi (giris, rota arama, lokasyon ve tasima yonetimi)

## Teknoloji Yigini

- Backend: Java 17, Spring Boot 4, Spring Security, Spring Data JPA, PostgreSQL, Redis, Springdoc OpenAPI
- Frontend: React 19, Vite 7, React Router, Axios, React Toastify
- Altyapi: Docker Compose (db + redis + adminer)

## On Kosullar

- Java 17
- Maven (veya `route/mvnw`)
- Node.js 20+ ve npm
- Docker (altyapiyi container ile calistirmak icin)

## Ortam Degiskenleri (Environment Variables)

Hassas konfigurasyon degerleri environment variable olarak yonetilmektedir.
Calistirmadan once `.env.example` dosyasini `.env` olarak kopyalayin ve degerleri doldurun:

```powershell
Copy-Item .env.example .env
```

| Degisken | Aciklama | Varsayilan (local) |
|---|---|---|
| `POSTGRES_DB` | Veritabani adi | `route_planner` |
| `POSTGRES_USER` | DB kullanici adi | `postgres` |
| `POSTGRES_PASSWORD` | DB sifresi | `postgres` |
| `DB_URL` | JDBC baglanti URL | `jdbc:postgresql://localhost:5432/route_planner` |
| `DB_USERNAME` | Spring datasource kullanici | `postgres` |
| `DB_PASSWORD` | Spring datasource sifre | `postgres` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | JWT imzalama anahtari (min 32 karakter) | — |
| `JWT_EXPIRATION` | JWT suresi (ms) | `3600000` |

> ⚠️ **Onemli:** `JWT_SECRET` degerini guclu ve rastgele bir degerle degistirin. `.env` dosyasi asla versiyon kontrolune eklenmemelidir.

## Hizli Baslangic (Local)

### 1) Altyapi servislerini baslatin

```powershell
docker compose -f docker-compose-postgre.yml up -d
```

Bu adim su servisleri ayaga kaldirir:
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- Adminer: `http://localhost:8080`

### 2) Backend'i calistirin

```powershell
cd route
.\mvnw.cmd spring-boot:run
```

Backend varsayilan olarak `http://localhost:8080` uzerinde calisir.
Swagger UI: `http://localhost:8080/swagger`

### 3) Frontend'i calistirin

```powershell
cd flight-route-ui
npm install
npm run dev
```

Vite dev server varsayilan olarak `http://localhost:5173` adresinde acilir.

## Kimlik Dogrulama ve Roller

- Auth endpointleri: `api/v1/auth/login`, `api/v1/auth/register`
- Roller: `ADMIN`, `AGENCY`
- Frontend tarafinda token `localStorage` uzerinden `Authorization: Bearer <token>` olarak gonderilir.

## API Ozeti

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `GET /api/v1/locations`
- `GET /api/v1/locations/{id}`
- `POST /api/v1/locations` (ADMIN)
- `GET /api/v1/transportations`
- `POST /api/v1/transportations` (ADMIN)
- `POST /api/v1/routes/search` (ADMIN, AGENCY)

## Onemli Notlar

- Local gelistirme akisinda frontend, API icin `http://localhost:8080/` base URL kullanir.
- `route/Dockerfile` icinde `8081` expose edilirken, `route/src/main/resources/application.yaml` server portu `8080` olarak tanimlidir.
- `flight-route-ui/Dockerfile` dosyasinda `COPY nginx.conf ...` satiri bulunur; repoda dosya adi `Nginx.CONF` olarak gecmektedir. Container image olusturmadan once dosya adini dogrulamaniz gerekir.

## Faydali Komutlar

```powershell
# Frontend lint
cd flight-route-ui
npm run lint

# Backend test
cd route
.\mvnw.cmd test
```
