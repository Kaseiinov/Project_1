## Система управления банковскими картами

```markdown

Backend-приложение на Spring Boot для управления банковскими картами: создание, блокировка, просмотр, переводы между своими картами, ролевая модель (ADMIN/USER), JWT-аутентификация, Swagger-документация.

## Технологии

- Java 17, Spring Boot 3.3.5, Spring Security, Spring Data JPA
- PostgreSQL 15
- Liquibase (миграции)
- Docker / Docker Compose
- Swagger (OpenAPI 3)

## Требования

- Установленные **Docker** и **Docker Compose**
- **Git** (для клонирования)

## Запуск проекта через Docker Compose

1. **Клонируйте репозиторий**

   ```bash
   git clone https://github.com/ваш-репозиторий/bank-rest.git
   cd bank-rest
   ```

2. **Создайте файл `.env` в корне проекта**

   Скопируйте в него следующее содержимое (при необходимости измените значения):

   ```env
   # PostgreSQL
   POSTGRES_DB=bank_db
   POSTGRES_USER=bank_user
   POSTGRES_PASSWORD=bank_password
   POSTGRES_PORT=5433

   # Приложение
   JWT_SECRET=mySecretKey12345678901234567890123456789012
   JWT_EXPIRATION=86400000
   SERVER_PORT=8080

   # Swagger/OpenAPI (опционально)
   API_DOCS_PATH=/api-docs
   SWAGGER_UI_PATH=/swagger-ui.html
   ```

3. **Запустите контейнеры**

   ```bash
   docker compose up --build
   ```

   После успешного запуска приложение будет доступно по адресу: [http://localhost:8080](http://localhost:8080)

4. **Swagger UI**

   Документация API доступна по ссылке:  
   [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

5. **Остановка приложения**

   ```bash
   docker compose down
   ```


> **Примечание:** Пароль администратора Test123

## Устранение неполадок

- Убедитесь, что файл `.env` создан в той же директории, что и `docker-compose.yml`.

## Дополнительная информация

- **JWT токен** должен передаваться в заголовке `Authorization: Bearer <token>`.

