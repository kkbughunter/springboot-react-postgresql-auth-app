# Auth API Documentation

Base URL: `http://localhost:8080/api`

All responses follow this envelope:

```json
{
  "success": true | false,
  "message": "Human-readable message",
  "data": { ... } | null
}
```

---

## POST /auth/register

Registers a new user. Creates a row in `users` and a corresponding row in `user_details`.

### Request

**Headers**
```
Content-Type: application/json
```

**Body**
```json
{
  "fullName": "karthikeyan",
  "email": "karthikeyan@example.com",
  "password": "password123"
}
```

| Field      | Type   | Required | Constraints                  |
|------------|--------|----------|------------------------------|
| `fullName` | string | yes      | max 255 chars                |
| `email`    | string | yes      | valid email format           |
| `password` | string | yes      | min 8 characters             |

### Responses

**201 Created** — registration successful
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "karthikeyan@example.com",
    "fullName": "karthikeyan"
  }
}
```

**409 Conflict** — email already registered
```json
{
  "success": false,
  "message": "Email is already registered",
  "data": null
}
```

**400 Bad Request** — validation failure
```json
{
  "success": false,
  "message": "Password must be at least 8 characters",
  "data": null
}
```

---

## POST /auth/login

Authenticates a user by email and password, returning a JWT.

### Request

**Headers**
```
Content-Type: application/json
```

**Body**
```json
{
  "email": "karthikeyan@example.com",
  "password": "password123"
}
```

| Field      | Type   | Required |
|------------|--------|----------|
| `email`    | string | yes      |
| `password` | string | yes      |

### Responses

**200 OK** — login successful
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "karthikeyan@example.com",
    "fullName": "karthikeyan"
  }
}
```

**401 Unauthorized** — wrong credentials or deactivated account
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

**400 Bad Request** — validation failure
```json
{
  "success": false,
  "message": "Email is required",
  "data": null
}
```

---

## Using the JWT

Include the token from any auth response as a Bearer header on protected endpoints:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

The token expires after **24 hours** (configurable via `jwt.expiration` in `application.yaml`).

---

## Postman Quick-Start

1. **Register**
   - Method: `POST`
   - URL: `http://localhost:8080/api/auth/register`
   - Body → raw → JSON → paste register body above

2. **Login**
   - Method: `POST`
   - URL: `http://localhost:8080/api/auth/login`
   - Body → raw → JSON → paste login body above
   - Copy the `data.token` from the response

3. **Authenticated request** (any protected endpoint)
   - Add header: `Authorization: Bearer <token>`

---

## Database Tables

### `users`
| Column          | Type        | Notes                      |
|-----------------|-------------|----------------------------|
| `user_id`       | UUID        | PK, auto-generated         |
| `email`         | VARCHAR(255)| Unique, not null           |
| `password_hash` | TEXT        | BCrypt hash                |
| `is_active`     | BOOLEAN     | Default `true`             |
| `created_at`    | TIMESTAMP   | Set on insert              |
| `created_by`    | VARCHAR(100)| Set on insert (`SYSTEM`)   |
| `modified_at`   | TIMESTAMP   | Updated on every save      |
| `modified_by`   | VARCHAR(100)| Updated on every save      |

### `user_details`
| Column       | Type        | Notes                       |
|--------------|-------------|-----------------------------|
| `user_id`    | UUID        | PK + FK → `users.user_id`  |
| `full_name`  | VARCHAR(255)| Not null                    |
| `gender`     | VARCHAR(20) | Nullable                    |
| `is_active`  | BOOLEAN     | Default `true`              |
| `created_at` | TIMESTAMP   |                             |
| `created_by` | VARCHAR(100)|                             |
| `modified_at`| TIMESTAMP   |                             |
| `modified_by`| VARCHAR(100)|                             |
