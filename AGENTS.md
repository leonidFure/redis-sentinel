# Target stack

- Java 21.
- Spring Boot + Spring Data R2DBC (reactive): repositories, services, and controllers use Reactor types `Mono`/`Flux`; do not use blocking calls (no `.block()` or `Thread.sleep()` in reactive flows).
- Single-module project (no multi-module structure).

# Common rules

- Prefer declaring local variables as `final` when not reassigned; use `var` when the type is evident; do not use `var` in public APIs.
- Every service MUST have an interface and a single implementation.
- Inject by interfaces only via constructor injection. No field injection.
- No static utility singletons unless strictly justified as pure stateless helpers; prefer Spring beans.
- Return the most generic reasonable type in method signatures (prefer interfaces and reactive types `Publisher`/`Mono`/`Flux` over concrete implementations).

# Naming rules

- Entity: `XXXEntity`
- Model: `XXXModel`
- Request DTOs: `CreateXXXRequestDto`, `UpdateXXXRequestDto`, ...
- Response DTOs: `XXXResponseDto`
- Service interface: `XXXService`
- Service impl: `XXXServiceImpl`
- Repository service interface: `XXXRepositoryService`
- Repository service impl: `XXXRepositoryServiceImpl`
- Repository: `XXXRepository` (extends `ReactiveCrudRepository`/`R2dbcRepository`)
- Controller: `XXXController`
- Configuration: `XXXConfig`
- Client: `XXXClient`
- Client impl: `XXXClientImpl`

# Layering rules

- Controller → Service (interface) → RepositoryService (interface) → Repository
- Repositories are called ONLY from RepositoryServices.
- RepositoryServices are called ONLY from Services.
- External clients are called ONLY from Services.
- All layers above the repository are reactive: methods return `Mono`/`Flux` and must not block.

# Entity vs Model/DTO

- Entity: Spring Data Relational/R2DBC-mapped classes (e.g., `@Table`, `@Id`). Entities are used as parameters/return values ONLY in repositories. They are not exposed in controllers or service public APIs.
- Model: Internal domain objects for Services and RepositoryServices. Models are used as parameters/return values in Service and RepositoryService methods. Models do not contain ORM annotations.
- DTO: I/O boundary objects. DTOs are used as parameters/return values in controllers, and also as parameters/return values of Service public methods to shield internal models/entities.
- Mapping:
  - DTO ↔ Model occurs in controllers/services.
  - Model ↔ Entity occurs in RepositoryService.
  - Repositories operate ONLY on Entities and never see DTOs/Models.
- Field mutability:
  - DTO: all fields MUST be `final`.
  - Model: all fields MUST be `final`.
  - Entity: fields MUST NOT be `final`.

# Lombok rules

- Services / RepositoryServices / components / controllers: `@RequiredArgsConstructor` for constructor injection; `@Slf4j` when logging is used.
- Entities: use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`; DO NOT use `@Builder` or `@Data` on entities.
- Models/DTOs: `@Data` + `@Builder` or `@Data(staticConstructor = "of")` (when 2 or less parameters) are allowed (no ORM concerns).
- Do not write boilerplate getters/setters/constructors when Lombok can generate them.

# R2DBC mapping

- Annotate entities with `@Table("...")` and `@Id`. Use auditing annotations if needed.
- All persistent fields in entities MUST be annotated with `@Column("...")`; do not rely on implicit field-to-column mapping.
- Repositories extend `ReactiveCrudRepository<XXXEntity, ID>` or `R2dbcRepository` and return `Mono`/`Flux`.
- Avoid ORM-style lazy relationships; model joins/aggregations explicitly (R2DBC is not an ORM). Use projections or manual mapping when necessary.

# Files structure (single module)

java..  
  config\XXXConfig.java
  api\XXXController.java
  dto\XXXRequestDto.java
  dto\XXXResponseDto.java
  model\XXXModel.java
  service\XXXService.java
    impl\XXXServiceImpl.java
  infrastructure  
    service\XXXRepositoryService.java
      impl\XXXRepositoryServiceImpl.java
    repository\XXXRepository.java
    entity\XXXEntity.java
  web\XXXClient.java
    impl\XXXClientImpl.java

# Workflow

- Infer entity name(s) and target layer(s) from the user request.
- Create/validate folders exactly according to the directory schema (single module).
- Generate the interface first, then its implementation under `impl/`.
- Produce compilable code blocks with correct packages/imports and reactive signatures (`Mono`/`Flux`) for R2DBC.
