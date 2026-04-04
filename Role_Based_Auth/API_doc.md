# Role-Based Auth — API Documentation

## Overview

| Item | Value |
|---|---|
| Base URL | `http://localhost:8080/api` |
| Auth mechanism | HTTP-only cookies (`accessToken`, `refreshToken`) |
| Token type | JWT (stateless) |
| Access token TTL | 15 minutes |
| Refresh token TTL | 7 days |
| Content-Type | `application/json` |

All requests that carry a body must set `Content-Type: application/json`.  
All responses that need credentials (non-auth endpoints) must send `withCredentials: true` (or equivalent).

---

## Standard Response Envelope

Every endpoint returns this wrapper:

```json
{
  "success": true,
  "message": "Human-readable status",
  "data": { ... }
}
```

On validation or business errors:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

---

## Roles

| roleId | roleName | Landing Page |
|---|---|---|
| `1` | `ADMIN` | `/admin/dashboard` |
| `2` | `USER` | `/dashboard` |

---

## Auth Endpoints

> All `/api/auth/**` routes are **public** — no token required.

---

### POST `/api/auth/register`

Register a new user. Assigns USER role by default; pass `roleId: 1` to register as ADMIN.

**Request body**

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "secret123",
  "phone": "+1 555 0100",
  "roleId": 2
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `fullName` | string | yes | max 150 chars |
| `email` | string | yes | valid email, must be unique |
| `password` | string | yes | min 8 chars |
| `phone` | string | no | max 20 chars |
| `roleId` | integer | no | `1` = ADMIN, `2` = USER · defaults to `2` |

**Response — 201 Created**

Sets `accessToken` and `refreshToken` as HTTP-only cookies.

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "email": "john@example.com",
    "fullName": "John Doe",
    "gender": null,
    "phone": "+1 555 0100",
    "roles": ["USER"]
  }
}
```

**Errors**

| Status | Condition |
|---|---|
| 400 | Validation failure (missing/invalid fields) |
| 409 | Email already registered |

---

### POST `/api/auth/login`

Authenticate with email and password.

**Request body**

```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

| Field | Type | Required |
|---|---|---|
| `email` | string | yes |
| `password` | string | yes |

**Response — 200 OK**

Sets `accessToken` and `refreshToken` as HTTP-only cookies.

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "email": "john@example.com",
    "fullName": "John Doe",
    "gender": "Male",
    "phone": "+1 555 0100",
    "roles": ["ADMIN"]
  }
}
```

**Errors**

| Status | Condition |
|---|---|
| 400 | Validation failure |
| 401 | Invalid email or password |
| 401 | Account is deactivated |

---

### POST `/api/auth/refresh`

Exchange a valid refresh token cookie for a new access + refresh token pair.

**Request** — No body. Reads the `refreshToken` HTTP-only cookie automatically.

**Response — 200 OK**

Sets new `accessToken` and `refreshToken` cookies.

```json
{
  "success": true,
  "message": "Token refreshed",
  "data": null
}
```

**Errors**

| Status | Condition |
|---|---|
| 401 | Missing, invalid, or expired refresh token |
| 401 | Account is deactivated |

---

### POST `/api/auth/logout`

Clear both auth cookies server-side.

**Request** — No body required.

**Response — 200 OK**

Expires both `accessToken` and `refreshToken` cookies.

```json
{
  "success": true,
  "message": "Logged out successfully",
  "data": null
}
```

---

## User Endpoints

> Requires a valid `accessToken` cookie. Any authenticated role (`ADMIN` or `USER`) may access these.

---

### GET `/api/user/profile`

Get the currently authenticated user's profile.

**Response — 200 OK**

```json
{
  "success": true,
  "message": "Profile fetched",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "email": "john@example.com",
    "fullName": "John Doe",
    "gender": "Male",
    "phone": "+1 555 0100",
    "isActive": true,
    "roles": ["USER"]
  }
}
```

**Errors**

| Status | Condition |
|---|---|
| 401 | Not authenticated |
| 404 | User record not found |

---

### PATCH `/api/user/profile`

Update own full name, gender, and/or phone. Email and roles cannot be changed here.

**Request body**

```json
{
  "fullName": "John Updated",
  "gender": "Male",
  "phone": "+1 555 9999"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `fullName` | string | yes | max 150 chars |
| `gender` | string | no | max 10 chars · send `""` to clear |
| `phone` | string | no | max 20 chars · send `""` to clear |

**Response — 200 OK**

```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "email": "john@example.com",
    "fullName": "John Updated",
    "gender": "Male",
    "phone": "+1 555 9999",
    "isActive": true,
    "roles": ["USER"]
  }
}
```

**Errors**

| Status | Condition |
|---|---|
| 400 | Validation failure |
| 401 | Not authenticated |
| 404 | User record not found |

---

## Admin Endpoints

> Requires a valid `accessToken` cookie **with `ADMIN` role**. All endpoints under `/api/admin/**` return `403 Forbidden` for non-admin tokens.

---

### POST `/api/admin/users`

Create a new user. Defaults to USER role if `roleId` is omitted.

**Request body**

```json
{
  "fullName": "Jane Smith",
  "email": "jane@example.com",
  "password": "secret123",
  "gender": "Female",
  "phone": "+1 555 0200",
  "roleId": 2
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `fullName` | string | yes | max 150 chars |
| `email` | string | yes | valid email, must be unique |
| `password` | string | yes | min 8 chars |
| `gender` | string | no | max 10 chars |
| `phone` | string | no | max 20 chars |
| `roleId` | integer | no | `1` = ADMIN, `2` = USER · defaults to `2` |

**Response — 201 Created**

```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "userId": "a1b2c3d4-0000-0000-0000-000000000001",
    "email": "jane@example.com",
    "fullName": "Jane Smith",
    "gender": "Female",
    "phone": "+1 555 0200",
    "isActive": true,
    "roles": ["USER"]
  }
}
```

**Errors**

| Status | Condition |
|---|---|
| 400 | Validation failure |
| 401 | Not authenticated |
| 403 | Caller is not ADMIN |
| 404 | Specified `roleId` does not exist |
| 409 | Email already registered |

---

### GET `/api/admin/users`

List all active users in the system.

**Response — 200 OK**

```json
{
  "success": true,
  "message": "Users fetched",
  "data": [
    {
      "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      "email": "john@example.com",
      "fullName": "John Doe",
      "gender": "Male",
      "phone": "+1 555 0100",
      "isActive": true,
      "roles": ["ADMIN"]
    },
    {
      "userId": "a1b2c3d4-0000-0000-0000-000000000001",
      "email": "jane@example.com",
      "fullName": "Jane Smith",
      "gender": "Female",
      "phone": "+1 555 0200",
      "isActive": true,
      "roles": ["USER"]
    }
  ]
}
```

---

### GET `/api/admin/users/{userId}`

Get a single user by UUID.

**Path parameter** — `userId` (UUID)

**Response — 200 OK**

```json
{
  "success": true,
  "message": "User fetched",
  "data": {
    "userId": "a1b2c3d4-0000-0000-0000-000000000001",
    "email": "jane@example.com",
    "fullName": "Jane Smith",
    "gender": "Female",
    "phone": "+1 555 0200",
    "isActive": true,
    "roles": ["USER"]
  }
}
```

**Errors**

| Status | Condition |
|---|---|
| 401 | Not authenticated |
| 403 | Caller is not ADMIN |
| 404 | User not found |

---

### PUT `/api/admin/users/{userId}`

Update a user's profile details or active status.

**Path parameter** — `userId` (UUID)

**Request body**

```json
{
  "fullName": "Jane Updated",
  "gender": "Female",
  "phone": "+1 555 9999",
  "isActive": false
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `fullName` | string | yes | max 150 chars |
| `gender` | string | no | max 10 chars · send `""` to clear |
| `phone` | string | no | max 20 chars · send `""` to clear |
| `isActive` | boolean | no | `false` deactivates the account |

**Response — 200 OK**

```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "userId": "a1b2c3d4-0000-0000-0000-000000000001",
    "email": "jane@example.com",
    "fullName": "Jane Updated",
    "gender": "Female",
    "phone": "+1 555 9999",
    "isActive": false,
    "roles": ["USER"]
  }
}
```

**Errors**

| Status | Condition |
|---|---|
| 400 | Validation failure |
| 401 | Not authenticated |
| 403 | Caller is not ADMIN |
| 404 | User not found |

---

### DELETE `/api/admin/users/{userId}`

Soft-delete a user (sets `isActive = false` on both `users` and `user_details`).

**Path parameter** — `userId` (UUID)

**Response — 200 OK**

```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null
}
```

**Errors**

| Status | Condition |
|---|---|
| 401 | Not authenticated |
| 403 | Caller is not ADMIN |
| 404 | User not found |

---

## Database Schema (simplified)

```
users
  user_id       UUID  PK
  password_hash TEXT  NOT NULL
  is_active     BOOL  DEFAULT true
  created_at, created_by, modified_at, modified_by  (audit)

user_details
  id            BIGINT PK
  user_id       UUID   FK → users.user_id
  email         TEXT   UNIQUE NOT NULL
  full_name     TEXT
  gender        TEXT
  phone         TEXT
  is_active     BOOL   DEFAULT true
  created_at, created_by, modified_at, modified_by  (audit)

roles
  role_id       INT   PK
  role_name     TEXT  UNIQUE  (ADMIN | USER)
  landing_url   TEXT
  is_system     BOOL

user_role_map
  id            BIGINT PK
  user_id       UUID   FK → users.user_id
  role_id       INT    FK → roles.role_id
  is_active     BOOL   DEFAULT true
  UNIQUE (user_id, role_id)
  created_at, created_by, modified_at, modified_by  (audit)
```

---

## Cookie Reference

| Cookie | HttpOnly | Secure | Path | SameSite | Max-Age |
|---|---|---|---|---|---|
| `accessToken` | yes | false* | `/` | Lax | 15 min |
| `refreshToken` | yes | false* | `/` | Lax | 7 days |

\* Set `secure: true` when deployed behind HTTPS.
