# MESSAGE APP — PROJECT SPECIFICATION

**Document:** `PROJECT_SPEC.md`  
**Project:** `message-app`  
**Purpose:** Persistent technical context for AI-assisted development  
**Last updated:** 2026-09-01  
**Current milestone:** Backend functional → Backend review → Backend smoke test → Frontend review → Frontend integration

---

# 1. PROJECT PURPOSE

`message-app` is a full-stack messaging application designed to provide a modern, professional and accessible user experience while maintaining a clean, secure and maintainable technical architecture.

The project has two main parts:

```text
message-app/
├── backend/
└── frontend/
```

The backend provides the REST API, authentication, authorization, persistence and business logic.

The frontend provides the user interface and consumes the backend API.

A third element is explicitly part of the development workflow:

```text
Figma
```

Figma is used to design and define the visual language, UI components, layouts and user experience before or alongside their implementation in Vue.

---

# 2. MAIN PROJECT OBJECTIVES

## Functional objectives

The application must allow users to:

- Register
- Log in
- Authenticate using JWT
- Maintain an authenticated session
- View their messages
- Create messages
- View individual messages
- Edit messages
- Delete messages
- Log out

The backend must enforce authorization independently of the frontend.

---

## Technical objectives

The project should demonstrate:

- Clean separation of responsibilities
- REST API design
- JWT authentication
- Backend authorization
- DTO-based API contracts
- Validation
- Centralized exception handling
- Automated testing
- Code coverage
- OpenAPI/Swagger documentation
- Modern frontend architecture
- Accessible UI
- Responsive design
- Maintainable code
- Clear frontend/backend boundaries

---

## UX/UI objectives

The frontend should provide:

- Modern visual design
- Professional appearance
- Consistent visual language
- Responsive layout
- Clear navigation
- Good typography
- Consistent spacing
- Reusable components
- Clear interaction states
- Accessible forms
- Clear feedback
- Loading states
- Empty states
- Error states
- Keyboard accessibility

Figma should be used to establish these characteristics before implementing the final UI.

---

# 3. TECHNOLOGY STACK

## 3.1 Backend

| Technology | Current choice |
|---|---|
| Language | Java |
| Framework | Spring Boot 3.5.x |
| Build | Maven |
| API | REST |
| Security | Spring Security |
| Authentication | JWT |
| JWT library | JJWT |
| Persistence | Spring Data / Repository pattern |
| Validation | Jakarta Validation |
| Documentation | OpenAPI / Swagger |
| Boilerplate | Lombok |
| Testing | JUnit 5 |
| Mocking | Mockito |
| Assertions | AssertJ |
| Coverage | JaCoCo 0.8.14 |

Current important versions:

```text
Java release:             25
Spring Boot:              3.5.15
Maven Surefire:            3.5.6
Mockito:                   5.17.0
JaCoCo:                   0.8.14
```

---

## 3.2 Frontend

| Technology | Choice |
|---|---|
| Framework | Vue |
| Language | TypeScript |
| Routing | Vue Router |
| API | REST |
| State management | Use existing implementation; otherwise define during frontend review |
| Testing | To be implemented/reviewed |
| Accessibility | Required |
| UI design | Figma |

The exact frontend dependency versions must be obtained from the current `package.json`.

Do not invent versions in this specification.

---

## 3.3 Design

Primary design tool:

```text
Figma
```

Figma is not considered merely an optional mockup tool.

It forms part of the product development workflow:

```text
Requirements
     ↓
UX structure
     ↓
Figma design
     ↓
Design system
     ↓
Vue components
     ↓
Pages
     ↓
Backend integration
     ↓
Visual QA
```

---

# 4. HIGH-LEVEL ARCHITECTURE

```text
                    ┌─────────────────┐
                    │      FIGMA      │
                    │                 │
                    │ UX/UI Design    │
                    │ Design System   │
                    │ Components      │
                    │ Prototypes      │
                    └────────┬────────┘
                             │
                             │ Design reference
                             ▼
┌────────────────────────────────────────────────┐
│                    FRONTEND                    │
│                                                │
│                 Vue + TypeScript               │
│                                                │
│ Views → Components → Composables/Stores        │
│                    ↓                           │
│               API Services                     │
└──────────────────────┬─────────────────────────┘
                       │
                       │ HTTP / JSON
                       │ Bearer JWT
                       ▼
┌────────────────────────────────────────────────┐
│                    BACKEND                     │
│                                                │
│              Spring Boot REST API              │
│                                                │
│ Controller                                     │
│      ↓                                         │
│ Service                                        │
│      ↓                                         │
│ Repository                                     │
│      ↓                                         │
│ Database                                       │
└────────────────────────────────────────────────┘
```

---

# 5. BACKEND STRUCTURE

Logical package structure:

```text
com.messageapp
├── config
├── controller
├── dto
├── exception
│   └── handler
├── model
├── repository
├── security
└── service
```

---

# 6. BACKEND RESPONSIBILITIES

## Controller

Controllers expose REST endpoints.

Responsibilities:

- Receive HTTP requests
- Validate request DTOs
- Delegate to services
- Return HTTP responses

Controllers must remain thin.

Business logic should not be implemented in controllers.

Current controllers:

```text
AuthController
MessageController
```

---

## Service

Services contain business logic.

Current services:

```text
AuthService
MessageService
```

Current JaCoCo coverage:

```text
100%
```

---

## Repository

Repositories handle persistence.

Current repository concepts include:

```text
UserRepository
MessageRepository
```

Repositories should not contain application-level business rules.

---

## Model

Main domain concepts:

```text
User
Message
```

Entities must not be exposed unnecessarily through the REST API.

DTOs should be preferred for API contracts.

---

## DTO

Current authentication DTOs:

```text
RegisterRequest
LoginRequest
AuthResponse
```

Current `AuthResponse`:

```java
public record AuthResponse(
    String id,
    String username,
    String token
) {}
```

---

# 7. SECURITY ARCHITECTURE

Security classes:

```text
security/
├── JwtService
├── JwtAuthenticationFilter
├── CustomUserDetailsService
└── AuthenticatedUserService
```

---

## JwtService

Responsibilities:

- Generate JWT
- Extract username
- Validate signature
- Validate expiration

Current implementation uses a signing key generated from the configured secret.

The current expiration is:

```text
3600000 ms
```

Equivalent to:

```text
1 hour
```

---

## JwtAuthenticationFilter

Responsibilities:

1. Read `Authorization`
2. Detect Bearer token
3. Validate token
4. Extract username
5. Load user details
6. Populate Spring Security context

Conceptually:

```text
Request
   ↓
Authorization header
   ↓
Bearer token
   ↓
JwtAuthenticationFilter
   ↓
JwtService
   ↓
CustomUserDetailsService
   ↓
SecurityContext
```

---

## AuthenticatedUserService

Responsibilities:

- Obtain authenticated username
- Resolve the corresponding database user
- Reject unauthenticated users
- Reject authenticated users that no longer exist

It throws:

```text
UnauthorizedException
```

when the authenticated user cannot be resolved correctly.

---

# 8. AUTHENTICATION FLOW

## Registration

```text
POST /api/auth/register
        ↓
AuthController
        ↓
AuthService
        ↓
Create User
        ↓
Generate JWT
        ↓
AuthResponse
```

---

## Login

```text
POST /api/auth/login
        ↓
AuthController
        ↓
AuthService
        ↓
Validate credentials
        ↓
Generate JWT
        ↓
AuthResponse
```

---

## Authenticated request

```text
Frontend
   │
   │ Authorization: Bearer <JWT>
   ▼
JwtAuthenticationFilter
   │
   ▼
JWT validation
   │
   ▼
SecurityContext
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
```

---

# 9. REST API

Authentication:

```text
POST /api/auth/register
POST /api/auth/login
```

Messages:

```text
GET    /api/messages
GET    /api/messages/{id}
POST   /api/messages
PUT    /api/messages/{id}
DELETE /api/messages/{id}
```

The exact request and response schemas must be verified against the current implementation before frontend integration.

---

# 10. AUTHORIZATION REQUIREMENT

Authorization is a backend responsibility.

The application must ensure:

```text
User A
  ├── can access User A's messages
  └── cannot access User B's messages
```

The frontend must never be considered a security boundary.

Even if a frontend route or button is hidden, the backend must independently reject unauthorized requests.

---

# 11. VALIDATION

Use Jakarta Validation for externally supplied data.

Validation should cover:

- Registration
- Login
- Message creation
- Message updates

The API must return consistent validation errors.

---

# 12. EXCEPTION HANDLING

The project contains:

```text
exception/
exception/handler/
```

including application-specific exceptions and a global exception handler.

Current JaCoCo coverage:

```text
exception:         100%
exception.handler: 100%
```

The frontend must receive predictable error responses.

Important statuses:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

Only statuses actually used by the backend should be exposed in the final API documentation.

---

# 13. OPENAPI / SWAGGER

OpenAPI documentation has already been integrated.

Current annotations include:

```text
@Operation
@ApiResponse
@Tag
@ValidationErrorResponse
@ConflictResponse
@UnauthorizedResponse
```

The final review must verify:

- All endpoints documented
- Request DTOs documented
- Response DTOs documented
- Authentication documented
- Error responses documented
- Bearer authentication configured correctly

---

# 14. TESTING STATUS

Backend automated tests have been implemented.

Testing technologies:

```text
JUnit 5
Mockito
AssertJ
Spring MockMvc
JaCoCo
```

Tested areas include:

```text
AuthService
MessageService
AuthController
MessageController
JwtService
JwtAuthenticationFilter
CustomUserDetailsService
AuthenticatedUserService
Exception handling
```

A previous complete run reported:

```text
35 tests
0 failures
0 errors
0 skipped
```

Additional tests were subsequently added.

The current backend test suite is:

```text
55 tests
0 failures
0 errors
0 skipped
```

---

# 15. CURRENT COVERAGE

Most recent global JaCoCo report:

```text
Instructions: 90%
Branches:     95%
Lines:        approximately 96%
Methods:      approximately 95%
Classes:      approximately 87%
```

Security:

```text
Instructions: 99%
Branches:     94%
```

Services:

```text
100%
```

Controllers:

```text
100%
```

Exception handling:

```text
100%
```

DTO:

```text
100%
```

The remaining uncovered code is mainly configuration/application bootstrap code and a small branch in `JwtService`.

---

# 16. COVERAGE DECISION

Do not continue adding tests solely to reach 100% JaCoCo coverage.

The current coverage is considered sufficient.

Tests should be added when they provide meaningful protection against regressions or verify important behavior.

Coverage percentage is not the primary project objective.

Priority order:

```text
Functional correctness
        >
Security
        >
Architecture
        >
Maintainability
        >
Meaningful tests
        >
Coverage percentage
```

---

# 17. COMPLETED WORK

The following work has already been completed.

## Backend

- Spring Boot backend created
- Maven build configured
- REST architecture established
- User model implemented
- Message model implemented
- Repositories implemented
- Authentication service implemented
- Message service implemented
- Authentication controllers implemented
- Message controller implemented
- JWT generation implemented
- JWT validation implemented
- JWT authentication filter implemented
- Spring Security integrated
- Custom UserDetails service implemented
- Authenticated user service implemented
- Application exceptions implemented
- Global exception handler implemented
- DTOs implemented
- OpenAPI annotations implemented
- Automated tests implemented
- JaCoCo configured
- Coverage report generated

---

# 18. BACKEND CURRENT STATUS

```text
Backend implementation             COMPLETE
Authentication                     COMPLETE
JWT                                COMPLETE
Security integration               COMPLETE
Message functionality              COMPLETE
Exception handling                 COMPLETE
DTOs                               COMPLETE
OpenAPI annotations                COMPLETE
Automated tests                    COMPLETE
Coverage analysis                  COMPLETE
Backend hardening/review           COMPLETE (2026-09-01)
Manual API smoke test              COMPLETE (2026-09-01) — 17/17 scenarios PASS
```

The backend should now move from:

```text
implementation/testing
```

to:

```text
review/hardening
```

---

# 19. IMMEDIATE NEXT PHASE — BACKEND REVIEW

Do not modify code blindly.

First inspect the current implementation.

Review:

```text
pom.xml
application.properties
application.yml
SecurityConfig
AuthController
MessageController
AuthService
MessageService
User
Message
UserRepository
MessageRepository
JwtService
JwtAuthenticationFilter
CustomUserDetailsService
AuthenticatedUserService
GlobalExceptionHandler
DTOs
OpenAPI configuration
```

Objectives:

- Identify inconsistencies
- Verify responsibilities
- Verify API contracts
- Verify security
- Verify authorization
- Verify configuration
- Verify persistence
- Verify documentation

Only then make justified changes.

---

# 20. BACKEND SMOKE TEST

Before frontend implementation/integration, verify:

```text
1. Register user
2. Login
3. Receive JWT
4. Call protected endpoint
5. Create message
6. List messages
7. Read message
8. Update message
9. Delete message
10. Request protected endpoint without JWT
11. Request protected endpoint with invalid JWT
12. Attempt access to another user's resource
```

The backend should be considered ready for frontend integration only after this workflow works correctly.

---

# 21. FIGMA STRATEGY

Figma is a first-class part of the frontend development process.

The objective is not to create static screens and then ignore them.

The objective is:

```text
Figma Design System
        ↓
Reusable UI components
        ↓
Vue components
        ↓
Vue pages
        ↓
Integrated application
```

---

# 22. FIGMA DELIVERABLES

The Figma work should produce at least:

## 22.1 Design foundations

Define:

- Color palette
- Typography
- Font sizes
- Font weights
- Spacing scale
- Border radius
- Shadows
- Icons
- Layout rules
- Breakpoints
- Focus states
- Error states
- Disabled states

---

## 22.2 Design tokens

Where practical, define reusable tokens for:

```text
colors
spacing
typography
radius
shadows
borders
breakpoints
```

The implementation should map these concepts into CSS variables or the chosen frontend styling system.

---

## 22.3 Component library

Design reusable components such as:

```text
Button
Input
Textarea
FormField
Alert
Modal
Card
Navbar
Sidebar
MessageCard
MessageList
LoadingIndicator
EmptyState
ErrorState
UserMenu
```

Only components actually needed by the application should be created.

Avoid creating a large design system with unused components.

---

# 23. FIGMA SCREENS

At minimum, design:

```text
Login
Register
Message list
Message detail
Create message
Edit message
Empty message list
Loading state
Error state
Authenticated navigation
User/logout menu
```

Responsive versions should be considered for:

```text
Desktop
Tablet
Mobile
```

Exact breakpoints should be defined during design.

---

# 24. FIGMA PROTOTYPING

Important user flows should be represented in the prototype.

At minimum:

```text
Registration
    ↓
Authenticated state
    ↓
Message list
    ↓
Create message
    ↓
Message detail
    ↓
Edit
    ↓
Delete
    ↓
Message list
```

Authentication:

```text
Login
  ↓
Success
  ↓
Messages
```

Error examples:

```text
Invalid credentials
Validation error
Unauthorized session
Message not found
Server error
```

The prototype should communicate intended interaction behavior before implementation.

---

# 25. FRONTEND STRUCTURE

Expected logical structure:

```text
frontend/
└── src/
    ├── assets/
    ├── components/
    ├── composables/
    ├── router/
    ├── services/
    ├── stores/
    ├── types/
    ├── views/
    └── ...
```

If the current frontend already has a structure, inspect it first.

Do not restructure a working frontend without a clear reason.

---

# 26. FRONTEND API LAYER

HTTP communication should be centralized.

Recommended logical structure:

```text
services/
├── api.ts
├── authService.ts
└── messageService.ts
```

Responsibilities:

### `api.ts`

- Base API URL
- HTTP requests
- JWT header
- Common response handling
- Common error handling

### `authService.ts`

- Register
- Login
- Logout
- Authentication/session handling

### `messageService.ts`

- List messages
- Get message
- Create message
- Update message
- Delete message

Vue components should not duplicate API implementation.

---

# 27. FRONTEND AUTHENTICATION

Implement:

```text
Login
Register
Logout
JWT/session persistence
Authentication state
Protected routes
Expired token handling
Invalid token handling
```

Expected flow:

```text
Login
  ↓
JWT received
  ↓
Authentication state
  ↓
Protected routes enabled
  ↓
Messages available
```

Logout must clear authentication state.

---

# 28. FRONTEND ROUTING

Expected logical routes:

```text
/login
/register
/messages
/messages/:id
/messages/new
/messages/:id/edit
```

Authenticated routes must be protected.

Unauthenticated users should be redirected to login where appropriate.

The exact route structure should follow the final UX design.

---

# 29. FRONTEND MESSAGE FEATURES

Implement:

```text
Message list
Message detail
Create message
Edit message
Delete message
```

Each operation must support:

```text
Loading
Success
Empty
Validation error
Unauthorized
Not found
Server error
Network error
```

---

# 30. FRONTEND STATE MANAGEMENT

Authentication state should be centralized.

Potential state:

```text
user
token
authenticated
loading
error
```

Message state may contain:

```text
messages
selectedMessage
loading
error
```

Use global state only when necessary.

Prefer local state for isolated component behavior.

---

# 31. FRONTEND DESIGN IMPLEMENTATION

The Vue implementation should follow the Figma design.

Implementation workflow:

```text
Figma component
      ↓
Vue component
      ↓
Component behavior
      ↓
Responsive behavior
      ↓
Accessibility
      ↓
Integration
```

The implementation should not simply reproduce screenshots.

It must reproduce:

- Layout
- Behavior
- States
- Responsiveness
- Accessibility
- Component relationships

---

# 32. VISUAL QA

After implementing the frontend, compare the implementation against Figma.

Check:

```text
Typography
Spacing
Alignment
Sizing
Colors
Borders
Radius
Shadows
Icons
Responsive behavior
States
```

Visual discrepancies should be corrected before considering the frontend complete.

---

# 33. ACCESSIBILITY

Accessibility is a mandatory requirement.

Review:

- Semantic HTML
- Heading hierarchy
- Form labels
- Accessible inputs
- Keyboard navigation
- Visible focus
- Button names
- Link names
- Screen reader behavior
- Error announcements
- Modal accessibility
- Color contrast
- Responsive behavior

Accessibility should be considered during Figma design, not added only after implementation.

---

# 34. FRONTEND TESTING

After the main UI is implemented:

## Unit tests

Test:

- Utilities
- Composables
- State logic
- API-related helpers

## Component tests

Test:

```text
Login
Register
MessageList
MessageCard
MessageForm
MessageDetail
ErrorState
EmptyState
```

## Integration tests

Test:

```text
Register
  ↓
Login
  ↓
View messages
  ↓
Create message
  ↓
Edit message
  ↓
Delete message
  ↓
Logout
```

### Current frontend test status (2026-09-01)

```text
Test framework: Vitest + @vue/test-utils
Environment:    jsdom
Test files:     9
Tests:          40
Failures:       0
```

Covered areas:

```text
- Auth store (login, register, logout, session restore)
- Message store (fetch, create, update, remove, error handling)
- LoginView (form, login, errors)
- RegisterView (form, validation, errors)
- CreateMessageView (form, validation, CRUD)
- EditMessageView (load, edit, errors, retry)
- MessageListView (load, empty state, error state, delete)
- MessageDetailView (load, detail, delete, errors, retry)
- useTheme composable
```

---

# 35. FULL APPLICATION INTEGRATION

Final architecture:

```text
             FIGMA
               │
               ▼
        Vue UI Components
               │
               ▼
         Vue Views/Pages
               │
               ▼
          API Services
               │
               │ HTTP
               ▼
       Spring Boot API
               │
               ▼
       Spring Security
               │
               ▼
          Services
               │
               ▼
         Repositories
               │
               ▼
           Database
```

---

# 36. FINAL QUALITY CHECK

## Backend

Verify:

```text
Build succeeds
Tests pass
Security works
Authorization works
API contracts stable
Swagger correct
Exceptions consistent
Secrets externalized
Database behavior correct
```

## Frontend

Verify:

```text
Build succeeds
Tests pass
Routes work
Authentication works
JWT handling works
CRUD works
Errors handled
Loading states work
Empty states work
Responsive UI works
Accessibility works
Figma design implemented accurately
```

## Full application

Verify:

```text
Register
Login
Authenticated session
Messages
Create
Read
Update
Delete
Logout
Invalid authentication
Unauthorized access
Validation errors
Server errors
Responsive UI
Accessibility
```

---

# 37. DEVELOPMENT ROADMAP

```text
┌──────────────────────────────┐
│ 1. BACKEND IMPLEMENTATION    │
│                              │
│ COMPLETE                     │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ 2. BACKEND REVIEW            │
│                              │
│ Structure                    │
│ Security                     │
│ Authorization                │
│ API contracts                │
│ Validation                   │
│ Exceptions                   │
│ Database                     │
│ Swagger                      │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ 3. BACKEND SMOKE TEST        │
│                              │
│ Complete API workflow        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ 4. FIGMA DESIGN              │
│                              │
│ UX flows                     │
│ Design system                │
│ Components                   │
│ Responsive layouts           │
│ States                       │
│ Prototype                    │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ 5. FRONTEND IMPLEMENTATION   │
│                              │
│ Vue + TypeScript             │
│ API layer                    │
│ Auth                         │
│ Routing                      │
│ Messages                     │
│ State                        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ 6. ACCESSIBILITY + QUALITY   │
│                              │
│ A11Y                         │
│ Responsive                   │
│ Error states                 │
│ Loading states               │
│ Tests                        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ 7. FULL INTEGRATION          │
│                              │
│ Frontend ↔ Backend           │
│ Auth ↔ JWT                   │
│ CRUD                         │
│ Authorization                │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ 8. FINAL REVIEW              │
│                              │
│ Functional                  │
│ Security                     │
│ UX                           │
│ Accessibility                │
│ Testing                      │
│ Documentation                │
└──────────────────────────────┘
```

---

# 38. IMMEDIATE NEXT TASK

The next task is **not** to add more backend tests.

The next task is:

```text
BACKEND REVIEW AND HARDENING
```

The workflow should be:

```text
1. Inspect backend source tree
2. Inspect configuration
3. Inspect security configuration
4. Inspect controllers
5. Inspect services
6. Inspect repositories/models
7. Inspect DTOs
8. Inspect exception handling
9. Inspect OpenAPI configuration
10. Identify concrete issues
11. Fix justified issues
12. Run complete backend test suite
13. Perform API smoke test
14. Confirm backend ready
```

After that:

```text
FIGMA UX/UI DESIGN
```

Then:

```text
VUE FRONTEND IMPLEMENTATION
```

Then:

```text
FRONTEND/BACKEND INTEGRATION
```

---

# 39. RULES FOR AI CONTINUATION

Any AI model continuing the project must follow these rules.

## General

1. Inspect the actual code before making assumptions.
2. Treat this document as project context, not as a substitute for source code.
3. When this document conflicts with the current source code, the source code is authoritative.
4. Do not rewrite working code without a concrete reason.
5. Prefer incremental changes.
6. Preserve existing architecture unless there is a strong justification for changing it.

## Backend

7. Keep controllers thin.
8. Keep business logic in services.
9. Keep persistence logic in repositories.
10. Keep authentication/security logic in the security layer.
11. Enforce authorization in the backend.
12. Validate external input.
13. Use DTOs for API contracts.
14. Do not expose secrets.
15. Keep API responses consistent.

## Testing

16. Tests must protect meaningful behavior.
17. Do not add tests solely to increase coverage percentage.
18. Do not sacrifice maintainability to achieve 100% JaCoCo coverage.

## Frontend

19. Keep HTTP logic in the API/service layer.
20. Do not duplicate API calls throughout components.
21. Protect routes appropriately.
22. Handle loading, empty, error and success states.
23. Keep authentication state centralized where necessary.
24. Follow the Figma design system.
25. Maintain responsive behavior.
26. Treat accessibility as a first-class requirement.

## Figma

27. Use Figma to establish the visual system before final UI implementation.
28. Prefer reusable components over one-off designs.
29. Design important states, not only happy paths.
30. Include responsive layouts.
31. Keep the implementation aligned with the approved design.
32. Perform visual QA after implementation.

## Integration

33. The frontend must consume the backend through documented API contracts.
34. The frontend must never be responsible for enforcing backend security.
35. Test the application through real user workflows.
36. Update this document after significant architectural decisions or milestones.

---

# 40. CURRENT MILESTONE SUMMARY

```text
PROJECT
├── Backend
│   ├── Architecture             COMPLETE
│   ├── Authentication           COMPLETE
│   ├── JWT                      COMPLETE
│   ├── Authorization            COMPLETE
│   ├── Message CRUD             COMPLETE
│   ├── Exception handling       COMPLETE
│   ├── DTOs                     COMPLETE
│   ├── OpenAPI                  COMPLETE
│   ├── Automated tests          COMPLETE (55 tests, 0 failures)
│   ├── Coverage                 ~90%
│   ├── Backend hardening        COMPLETE (2026-09-01)
│   └── Smoke test               COMPLETE (17/17 PASS)
│
├── Backend hardening             COMPLETE
│
├── Figma
│   ├── UX flows                  DEFERRED (frontend uses Tailwind design system directly)
│   ├── Design system             PARTIAL (navy palette, dark mode, consistent spacing via Tailwind)
│   ├── Component library         IN PROGRESS (NavBar, reusable patterns via utility classes)
│   ├── Responsive layouts        PARTIAL (Tailwind responsive utilities used)
│   └── Prototype                 N/A
│
├── Frontend
│   ├── Vue + TypeScript          COMPLETE
│   ├── API layer                 COMPLETE (api.ts, authService.ts, messageService.ts)
│   ├── Authentication            COMPLETE (login, register, logout, JWT, session persistence)
│   ├── Message UI                COMPLETE (list, detail, create, edit, delete)
│   ├── Routing                   COMPLETE (protected routes, auth guard, 401 handling)
│   ├── State management          COMPLETE (Pinia stores: auth, messages)
│   ├── Build                     COMPLETE (vue-tsc + vite build OK)
│   ├── Tests                     COMPLETE (40 tests, 0 failures)
│   └── Integration with backend  COMPLETE (proxy, E2E CRUD verified)
│
├── Frontend quality
│   ├── Accessibility             IMPROVED (role alerts, aria labels, for/id labels, focus styles)
│   ├── Responsive QA             PARTIAL (Tailwind responsive classes)
│   ├── Component tests           COMPLETE (40 tests across 9 test files)
│   └── Integration tests         IN PROGRESS (backend smoke test via Vite proxy)
│
└── Final integration              IN PROGRESS
```

---

# 41. CURRENT PRIORITY

The immediate priority is:

> **Finish the backend review and smoke testing without pursuing additional artificial coverage.**

DONE (2026-09-01): Backend hardened, 55 tests pass, 17/17 smoke test scenarios pass.

Then:

> **Design the frontend experience and reusable component system in Figma.**

PARTIAL (2026-09-01): Frontend is fully implemented using a consistent Tailwind design system (navy palette, dark mode, consistent spacing and typography). Figma was not used; design decisions were made directly in code. This is acceptable for the current scope but a visual QA pass is recommended.

Then:

> **Implement the Figma design in Vue + TypeScript and integrate it with the existing Spring Boot REST API.**

DONE (2026-09-01): Frontend is complete with all functional views, services, stores, routing, authentication, message CRUD, and backend integration verified.

The desired final result is not simply a technically functional messaging application.

It is a:

```text
Secure
Maintainable
Tested
Documented
Accessible
Responsive
Modern
Professional
Full-stack messaging application
```

with a clear relationship between:

```text
Requirements
    ↓
Figma UX/UI
    ↓
Vue implementation
    ↓
REST API
    ↓
Spring Boot
    ↓
Database
```