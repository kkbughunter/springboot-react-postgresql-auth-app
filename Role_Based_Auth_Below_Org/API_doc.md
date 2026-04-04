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

## POST /auth/refresh

Rotates both cookies silently. Called automatically by the frontend when an access token expires. No request body needed — the `refreshToken` cookie is read by the backend.

**200 OK**
```json
{ "success": true, "message": "Token refreshed", "data": null }
```
**401 Unauthorized** — refresh token expired or invalid → user must log in again.

---

## POST /auth/logout

Clears both `accessToken` and `refreshToken` cookies on the server.

**200 OK**
```json
{ "success": true, "message": "Logged out successfully", "data": null }
```

---

## GET /user/profile  *(protected)*

Returns the currently authenticated user's profile.

**200 OK**
```json
{
  "success": true,
  "message": "Profile fetched",
  "data": {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "karthikeyan@example.com",
    "fullName": "karthikeyan",
    "gender": "Male",
    "isActive": true
  }
}
```

---

## PATCH /user/profile  *(protected)*

Updates the authenticated user's full name and/or gender.

### Request Body
```json
{
  "fullName": "Karthikeyan S",
  "gender": "Male"
}
```

| Field      | Type   | Required | Notes                        |
|------------|--------|----------|------------------------------|
| `fullName` | string | yes      | max 255 chars                |
| `gender`   | string | no       | max 20 chars; `""` clears it |

**200 OK**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "karthikeyan@example.com",
    "fullName": "Karthikeyan S",
    "gender": "Male",
    "isActive": true
  }
}
```

---

## Token & Cookie Strategy

| Cookie         | HttpOnly | Expiry   | Purpose                        |
|----------------|----------|----------|--------------------------------|
| `accessToken`  | ✅        | 15 min   | Authenticates every API call   |
| `refreshToken` | ✅        | 7 days   | Issues new access token silently|

- Tokens are **never** present in response bodies.
- The browser sends both cookies automatically (no JS access).
- On 401, the frontend Axios interceptor calls `/auth/refresh` and retries transparently.
- For **Postman** testing, enable **"Send cookies"** and use **Cookie Manager** to inspect them.  
  Alternatively, send `Authorization: Bearer <token>` header — the filter accepts both.

---

## Postman Quick-Start

1. **Enable cookie support** — in Postman Settings → General → turn on "Automatically follow redirects" and use the Cookie Manager.

2. **Register** — `POST http://localhost:8080/api/auth/register`

3. **Login** — `POST http://localhost:8080/api/auth/login`  
   After login, Postman stores `accessToken` and `refreshToken` cookies automatically.

4. **Get profile** — `GET http://localhost:8080/api/user/profile` (no extra headers needed)

5. **Edit profile** — `PATCH http://localhost:8080/api/user/profile`

6. **Refresh** — `POST http://localhost:8080/api/auth/refresh` (uses `refreshToken` cookie)

7. **Logout** — `POST http://localhost:8080/api/auth/logout`

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
