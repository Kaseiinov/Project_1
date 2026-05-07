# Система управления банковскими картами

Проект представляет собой backend-приложение на Spring Boot для управления банковскими картами: создание, блокировка, просмотр, переводы между своими картами, ролевая модель ADMIN/USER, JWT-аутентификация, Swagger-документация.

## Технологии

- Java 17, Spring Boot 3.3.5, Spring Security, Spring Data JPA
- PostgreSQL 15
- Liquibase (миграции)
- Docker / Docker Compose
- Swagger (OpenAPI 3)

## Требования

- **Docker** и **Docker Compose** (установлены и запущены)
- **Git** (для клонирования)

## Конфигурация

Все настройки задаются через переменные окружения в файле `.env` (создаётся в корне проекта). Пример содержимого `.env`:

```env
# PostgreSQL
POSTGRES_DB=bank_db
POSTGRES_USER=bank_user
POSTGRES_PASSWORD=bank_password
POSTGRES_PORT=5433

# JWT
JWT_SECRET=mySecretKey12345678901234567890123456789012
JWT_EXPIRATION=86400000