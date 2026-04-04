
## Backend Folder Structure Example.
```
src/
└── main/
    └── java/
        └── com/
            └── group_name/
                └── artifact_name/
                    ├── ArtifactNameApplication.java
                    │
                    ├── config/
                    │   ├── SecurityConfig.java
                    │   ├── JwtAuthenticationFilter.java
                    │   ├── JpaConfig.java
                    │   ├── CorsConfig.java
                    │   ├── AclFilter.java
                    │   └── CommonResponse.java
                    │
                    ├── common/
                    │   ├── exception/
                    │   │   └── handler/
                    │   │       └── GlobalExceptionHandler.java
                    │   │
                    │   └── util/
                    │       ├── JwtUtil.java
                    │       ├── SecurityUtil.java
                    │       ├── ApiResponse.java
                    │       └── ApiResponseFactory.java
                    │
                    └── modules/
                        ├── auth/ # use user entity
                        |   ├── request/
                        |   ├── response/
                        |   ├── mapper/
                        |   └── service/
                        └── user/
                            ├── entity/
                            ├── dto/
                            ├── request/
                            ├── response/
                            ├── mapper/
                            ├── service/
                            └── repo/
```