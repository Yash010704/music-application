# 🎵 Music Clone Backend

A production-style REST API backend for a music streaming application, built with **Java 17** and **Spring Boot 3**. Handles authentication, artists/albums/songs catalog management, audio file upload & streaming, playlists, and favorites.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Data Model](#data-model)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Clone & Configure](#clone--configure)
  - [Run](#run)
- [API Reference](#api-reference)
  - [Auth](#auth)
  - [Songs](#songs)
  - [Streaming](#streaming)
  - [Artists](#artists)
  - [Albums](#albums)
  - [Playlists](#playlists)
  - [Favorites](#favorites)
  - [Users](#users)
- [Authentication](#authentication)
- [Example Usage](#example-usage)
- [Connecting a Frontend](#connecting-a-frontend)
- [Roadmap / Ideas for Extension](#roadmap--ideas-for-extension)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- 🔐 **JWT authentication** — register/login, BCrypt-hashed passwords, stateless sessions
- 🎤 **Artists & Albums** — full CRUD, search by name/title
- 🎧 **Songs** — upload audio files (multipart/form-data), rich metadata, full-text search, trending list ranked by play count
- 📡 **Streaming** — dedicated endpoint with **HTTP Range request** support, so players can seek instantly instead of downloading the whole file first
- 📃 **Playlists** — create/update/delete, add/remove songs, public vs. private visibility with ownership checks
- ❤️ **Favorites** — like/unlike songs, list a user's liked tracks
- 👤 **User profiles** — view and update display name & avatar
- ⚠️ **Centralized error handling** — consistent JSON error responses via `@RestControllerAdvice`
- 🌐 **CORS configured** for a separate frontend dev server

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3 |
| Security | Spring Security + JWT (`jjwt` 0.12) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL (default), H2 (in-memory, for quick testing) |
| Build Tool | Maven |
| Boilerplate reduction | Lombok |

## Architecture

```
Client (React/Vue/mobile app)
        │  HTTPS + JWT Bearer token
        ▼
┌───────────────────────────────┐
│         Controllers           │  REST endpoints, request validation
├───────────────────────────────┤
│           Services            │  Business logic, authorization checks
├───────────────────────────────┤
│         Repositories          │  Spring Data JPA
├───────────────────────────────┤
│            MySQL              │
└───────────────────────────────┘

Audio files stored on disk under /uploads, served through
a dedicated streaming controller with Range-request support.
```

## Project Structure

```
music-clone-backend/
├── pom.xml
├── src/main/java/com/musicclone/backend/
│   ├── MusicCloneBackendApplication.java
│   ├── config/
│   │   └── SecurityConfig.java        # CORS, JWT filter chain, route authorization rules
│   ├── controller/                    # REST controllers (Auth, Song, Stream, Artist, Album, Playlist, Favorite, User)
│   ├── dto/                           # Request/response DTOs
│   ├── entity/                        # JPA entities: User, Artist, Album, Song, Playlist, Favorite, Role
│   ├── exception/                     # Custom exceptions + GlobalExceptionHandler
│   ├── repository/                    # Spring Data JPA repositories
│   ├── security/                      # JwtUtil, JwtAuthFilter, UserPrincipal, CustomUserDetailsService
│   └── service/                       # Business logic layer
├── src/main/resources/
│   └── application.properties
└── uploads/
    ├── songs/                         # Uploaded audio files land here
    └── covers/                        # Uploaded cover art lands here
```

## Data Model

```
User ──< Playlist >── Song >── Album ──< Artist
  │                     │
  └──────< Favorite >───┘
```

- A **User** owns many **Playlists** and can favorite many **Songs**.
- A **Playlist** holds a many-to-many set of **Songs**.
- A **Song** belongs to one **Artist** and optionally one **Album**.
- An **Album** belongs to one **Artist** and contains many **Songs**.

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8 (or use the included H2 in-memory option for zero-setup local testing)

### Clone & Configure

```bash
git clone <your-repo-url>
cd music-clone-backend
```

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/music_clone_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

Prefer not to install MySQL right now? Comment out the MySQL block and uncomment the H2 block in the same file to run fully in-memory.

**Before deploying anywhere real**, replace `app.jwt.secret` with your own long, random secret, and update `app.cors.allowed-origins` to match your frontend's URL.

### Run

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

Build a runnable jar instead:

```bash
mvn clean package
java -jar target/music-clone-backend-1.0.0.jar
```

## API Reference

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user, returns a JWT |
| POST | `/api/auth/login` | Public | Login with username/email + password, returns a JWT |

### Songs

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/songs` | Public | List all songs |
| GET | `/api/songs/{id}` | Public | Get a song by id |
| GET | `/api/songs/search?query=` | Public | Search by title or artist name |
| GET | `/api/songs/trending` | Public | Top 20 most-played songs |
| GET | `/api/songs/artist/{artistId}` | Public | Songs by a given artist |
| GET | `/api/songs/album/{albumId}` | Public | Songs on a given album |
| POST | `/api/songs` *(multipart: `songFile`, optional `coverImage`)* | Authenticated | Upload a new song |
| POST | `/api/songs/{id}/play` | Authenticated | Increment play count |
| DELETE | `/api/songs/{id}` | Authenticated | Delete a song |

### Streaming

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/stream/{songId}` | Public | Streams the audio file; honors the `Range` header for seeking (HTTP 206 Partial Content) |

### Artists

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/artists` | Public | List all artists |
| GET | `/api/artists/{id}` | Public | Get artist by id |
| GET | `/api/artists/search?name=` | Public | Search artists by name |
| POST | `/api/artists` | Authenticated | Create an artist |
| PUT | `/api/artists/{id}` | Authenticated | Update an artist |
| DELETE | `/api/artists/{id}` | Authenticated | Delete an artist |

### Albums

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/albums` | Public | List all albums |
| GET | `/api/albums/{id}` | Public | Get album by id |
| GET | `/api/albums/artist/{artistId}` | Public | Albums by a given artist |
| GET | `/api/albums/search?title=` | Public | Search albums by title |
| POST | `/api/albums` | Authenticated | Create an album |
| PUT | `/api/albums/{id}` | Authenticated | Update an album |
| DELETE | `/api/albums/{id}` | Authenticated | Delete an album |

### Playlists

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/playlists/public` | Public | Browse public playlists |
| GET | `/api/playlists/me` | Authenticated | Your playlists |
| GET | `/api/playlists/{id}` | Public if the playlist is public, otherwise owner-only | Get a playlist |
| POST | `/api/playlists` | Authenticated | Create a playlist |
| PUT | `/api/playlists/{id}` | Authenticated (owner) | Update a playlist |
| DELETE | `/api/playlists/{id}` | Authenticated (owner) | Delete a playlist |
| POST | `/api/playlists/{id}/songs/{songId}` | Authenticated (owner) | Add a song to a playlist |
| DELETE | `/api/playlists/{id}/songs/{songId}` | Authenticated (owner) | Remove a song from a playlist |

### Favorites

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/favorites` | Authenticated | List your favorite songs |
| POST | `/api/favorites/{songId}` | Authenticated | Like a song |
| DELETE | `/api/favorites/{songId}` | Authenticated | Unlike a song |
| GET | `/api/favorites/{songId}/status` | Authenticated | Check whether a song is favorited |

### Users

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/users/me` | Authenticated | Current user's profile |
| GET | `/api/users/{id}` | Authenticated | Any user's public profile |
| PATCH | `/api/users/me` | Authenticated | Update display name / avatar |

## Authentication

Include the JWT returned by `/api/auth/login` or `/api/auth/register` on every subsequent request:

```
Authorization: Bearer <token>
```

Tokens expire after 24 hours by default (`app.jwt.expiration-ms` in `application.properties`).

## Example Usage

```bash
# 1. Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"password123"}'

# 2. Create an artist
curl -X POST http://localhost:8080/api/artists \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d '{"name":"The Testers","bio":"A demo band"}'

# 3. Upload a song
curl -X POST http://localhost:8080/api/songs \
  -H "Authorization: Bearer <TOKEN>" \
  -F "title=Test Track" \
  -F "artistId=1" \
  -F "genre=Rock" \
  -F "songFile=@/path/to/song.mp3"

# 4. Stream it back
curl http://localhost:8080/api/stream/1 --output track.mp3

# 5. Create a playlist and add the song to it
curl -X POST http://localhost:8080/api/playlists \
  -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" \
  -d '{"name":"My Mix","isPublic":true}'

curl -X POST http://localhost:8080/api/playlists/1/songs/1 \
  -H "Authorization: Bearer <TOKEN>"
```

## Connecting a Frontend

1. Set `app.cors.allowed-origins` in `application.properties` to your frontend's dev URL (defaults already include `localhost:3000` and `localhost:5173`).
2. Store the JWT from login/register (e.g., in memory or `httpOnly` cookie — avoid `localStorage` for anything sensitive in production).
3. Attach it as `Authorization: Bearer <token>` on every authenticated request.
4. Point your `<audio>` player's `src` at `/api/stream/{songId}` — the browser will automatically issue `Range` requests as the user seeks.

## Roadmap / Ideas for Extension

- [ ] Move file storage to S3 / Cloud Storage instead of local disk (`FileStorageService` is the single place to change)
- [ ] Enforce `ARTIST`/`ADMIN` roles on upload/delete endpoints (the `Role` enum already exists, `SecurityConfig` just needs tightening)
- [ ] Add refresh-token rotation (`app.jwt.refresh-expiration-ms` is already reserved for this)
- [ ] Add pagination (`Pageable`) to list endpoints as the catalog grows
- [ ] Add integration tests with Testcontainers + MySQL
- [ ] Add a "recently played" / listening history feature
- [ ] Rate-limit auth endpoints

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you'd like to change.

## License

[Yash Shrivastava](LICENSE)
