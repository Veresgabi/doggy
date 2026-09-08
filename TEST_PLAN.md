# Test Plan for Doggy Application

## Overview
This document outlines a comprehensive test strategy for the Doggy REST API application, a Spring Boot application that manages dogs and user authentication. The application provides endpoints for syncing dogs, filtering, pagination, and user authentication with JWT tokens.

---

## 1. Unit Tests

### 1.1 DogServiceImpl Tests

#### Test Case 1.1.1: syncDogs() - Successful Sync
- **Description**: Verify successful synchronization of dogs from external API
- **Precondition**: External API is available and returns valid dog data
- **Expected Result**: 
  - All dogs are saved to database
  - Response contains first page of dogs
  - Message indicates successful sync
  - HTTP status 200

#### Test Case 1.1.2: syncDogs() - Empty Response
- **Description**: Handle empty response from external API
- **Precondition**: API returns null or empty list
- **Expected Result**: Returns null or empty response gracefully
- **Coverage**: Null pointer handling

#### Test Case 1.1.3: syncDogs() - API Exception
- **Description**: Handle API connection failures
- **Precondition**: External API is unavailable
- **Expected Result**: Exception is caught and propagated
- **Coverage**: Error handling

#### Test Case 1.1.4: getDogPage() - Valid Page
- **Description**: Retrieve paginated dog list
- **Precondition**: Dogs exist in database, page number is valid
- **Expected Result**:
  - Returns dogs for requested page
  - Correct number of pages calculated
  - Message indicates success
  - HTTP status 200

#### Test Case 1.1.5: getDogPage() - Invalid Page
- **Description**: Handle invalid page numbers
- **Precondition**: Page number is beyond available pages
- **Expected Result**: Empty list or appropriate error handling

#### Test Case 1.1.6: getDogPage() - Database Exception
- **Description**: Handle database errors during page retrieval
- **Expected Result**: Exception caught, error message returned, HTTP 500

#### Test Case 1.1.7: filter() - Filter by Name
- **Description**: Filter dogs by name (minimum 3 characters)
- **Precondition**: Dogs with various names exist in database
- **Expected Result**: Only dogs matching name filter are returned
- **Edge Cases**: Name < 3 characters should be ignored

#### Test Case 1.1.8: filter() - Filter by Life Span
- **Description**: Filter dogs by life span (minimum 2 characters)
- **Precondition**: Dogs with various life spans exist
- **Expected Result**: Only dogs matching life span filter are returned
- **Edge Cases**: Life span < 2 characters should be ignored

#### Test Case 1.1.9: filter() - Combined Filters
- **Description**: Apply both name and life span filters simultaneously
- **Expected Result**: Results match both filter criteria

#### Test Case 1.1.10: filter() - No Filters
- **Description**: Call filter with empty/null criteria
- **Expected Result**: All dogs returned or handled gracefully

#### Test Case 1.1.11: getDogById() - Valid ID
- **Description**: Retrieve single dog by ID
- **Precondition**: Dog with ID exists
- **Expected Result**: Returns correct dog, HTTP 200

#### Test Case 1.1.12: getDogById() - Non-existent ID
- **Description**: Retrieve dog with invalid ID
- **Expected Result**: Returns null or empty dog, HTTP 200 (graceful)

#### Test Case 1.1.13: saveDog() - Valid Dog
- **Description**: Save new dog to database
- **Precondition**: Valid Dog object with all required fields
- **Expected Result**: Dog is saved, returned in response, HTTP 200

#### Test Case 1.1.14: saveDog() - Invalid Dog Data
- **Description**: Save dog with invalid/missing data
- **Expected Result**: Database constraint violation or validation error, HTTP 500

#### Test Case 1.1.15: delete() - Valid ID
- **Description**: Delete existing dog
- **Precondition**: Dog exists in database
- **Expected Result**: Dog is deleted, success message, HTTP 200

#### Test Case 1.1.16: delete() - Non-existent ID
- **Description**: Delete dog that doesn't exist
- **Expected Result**: No error, graceful handling, HTTP 200

#### Test Case 1.1.17: createRandomDog()
- **Description**: Generate and save random dog
- **Expected Result**:
  - Dog created with valid random values
  - All required fields populated
  - Dog saved successfully
  - HTTP 200

#### Test Case 1.1.18: generateRandomDog() - Data Validity
- **Description**: Verify randomly generated dog has valid data
- **Expected Result**:
  - Name is from predefined list
  - Origin, temperament, bredFor from predefined lists
  - Weight and height have valid format
  - Image URL is valid

---

### 1.2 UserServiceImpl Tests

#### Test Case 1.2.1: login() - Successful Login
- **Description**: User successfully logs in
- **Precondition**: User exists in database with credentials
- **Expected Result**:
  - Auth tokens generated
  - Cookies set in response headers
  - User object returned
  - HTTP 200

#### Test Case 1.2.2: login() - User Not Found
- **Description**: Login with non-existent username
- **Expected Result**:
  - IllegalArgumentException caught
  - Error message returned
  - HTTP 401

#### Test Case 1.2.3: login() - Both Tokens Invalid
- **Description**: Login with no valid tokens
- **Expected Result**: New access and refresh tokens generated

#### Test Case 1.2.4: login() - Access Token Invalid, Refresh Valid
- **Description**: Access token expired but refresh token valid
- **Expected Result**: New access token generated, refresh token unchanged

#### Test Case 1.2.5: login() - Both Tokens Valid
- **Description**: Both tokens are still valid
- **Expected Result**: Both tokens regenerated for security

#### Test Case 1.2.6: refresh() - Valid Refresh Token
- **Description**: Refresh access token with valid refresh token
- **Precondition**: Refresh token is valid
- **Expected Result**:
  - New access token generated
  - User found and returned
  - HTTP 200

#### Test Case 1.2.7: refresh() - Invalid Refresh Token
- **Description**: Attempt refresh with invalid token
- **Expected Result**:
  - Error message returned
  - HTTP 401

#### Test Case 1.2.8: refresh() - User Not Found
- **Description**: Refresh for non-existent user
- **Expected Result**: Error message, HTTP 401

#### Test Case 1.2.9: logout() - Successful
- **Description**: User successfully logs out
- **Expected Result**:
  - Access and refresh tokens cleared
  - Success message returned
  - HTTP 200

#### Test Case 1.2.10: logout() - Exception Handling
- **Description**: Handle exceptions during logout
- **Expected Result**: Error message, HTTP 500

#### Test Case 1.2.11: getUserProfile() - Authenticated User
- **Description**: Get profile for authenticated user
- **Precondition**: User is authenticated in SecurityContext
- **Expected Result**: User summary returned, HTTP 200

#### Test Case 1.2.12: getUserProfile() - User Not Found
- **Description**: Get profile when user not in database
- **Expected Result**: Error message, HTTP 500

---

### 1.3 DogController Tests

#### Test Case 1.3.1: syncDogs() Endpoint - Success
- **Description**: Call /dog/sync-dogs successfully
- **Expected Result**: Response contains dogs, HTTP 200

#### Test Case 1.3.2: syncDogs() Endpoint - Exception Handling
- **Description**: Handle exceptions in sync endpoint
- **Expected Result**: Error message in response body, HTTP 500

#### Test Case 1.3.3: getDogPage() - Valid Request
- **Description**: Call /dog/get-dog-page with valid page number
- **Expected Result**: Page of dogs returned, HTTP 200

#### Test Case 1.3.4: filter() - POST Request
- **Description**: Call /dog/filter with FilterDogDTO
- **Expected Result**: Filtered results returned, HTTP 200

#### Test Case 1.3.5: getDogById() - Valid ID
- **Description**: Call /dog/get-dog with valid ID
- **Expected Result**: Dog returned, HTTP 200

#### Test Case 1.3.6: saveDog() - Authentication Required
- **Description**: Save dog with valid authentication
- **Precondition**: Valid access and refresh tokens in cookies
- **Expected Result**: Dog saved, HTTP 200

#### Test Case 1.3.7: saveDog() - Invalid Tokens
- **Description**: Save dog with invalid tokens
- **Expected Result**: Error message, HTTP 401 or error from userService

#### Test Case 1.3.8: saveDog() - Invalid Dog Data
- **Description**: Save dog with validation errors
- **Expected Result**: Validation error, HTTP 400

#### Test Case 1.3.9: deleteDog() - Valid ID
- **Description**: Delete dog with valid ID and auth
- **Expected Result**: Success message, HTTP 200

#### Test Case 1.3.10: createRandomDog()
- **Description**: Call /dog/create-random endpoint
- **Expected Result**: New random dog created and returned, HTTP 200

---

### 1.4 AuthController Tests

#### Test Case 1.4.1: login() - Valid Credentials
- **Description**: POST /auth/login with valid credentials
- **Expected Result**: Auth tokens in response headers, user returned, HTTP 200

#### Test Case 1.4.2: login() - Invalid Credentials
- **Description**: POST /auth/login with invalid credentials
- **Expected Result**: Authentication error, HTTP 401

#### Test Case 1.4.3: login() - Missing Request Body
- **Description**: POST /auth/login without body
- **Expected Result**: Bad request error, HTTP 400

#### Test Case 1.4.4: logout() - Valid Request
- **Description**: POST /auth/logout-user
- **Expected Result**: Tokens cleared, success message, HTTP 200

#### Test Case 1.4.5: refresh() - Valid Tokens
- **Description**: POST /auth/refresh with valid tokens
- **Expected Result**: New access token generated, HTTP 200

#### Test Case 1.4.6: refresh() - Missing Tokens
- **Description**: POST /auth/refresh without tokens
- **Expected Result**: Error or null token handling, HTTP 401

---

### 1.5 TokenProviderImpl Tests

#### Test Case 1.5.1: generateAccessToken() - Valid User
- **Description**: Generate access token
- **Expected Result**: Token created with valid structure and claims

#### Test Case 1.5.2: generateRefreshToken() - Valid User
- **Description**: Generate refresh token
- **Expected Result**: Token created, longer expiration than access token

#### Test Case 1.5.3: validateToken() - Valid Token
- **Description**: Validate a valid token
- **Expected Result**: Returns true

#### Test Case 1.5.4: validateToken() - Expired Token
- **Description**: Validate an expired token
- **Expected Result**: Returns false

#### Test Case 1.5.5: validateToken() - Malformed Token
- **Description**: Validate malformed/invalid token
- **Expected Result**: Returns false or throws exception

#### Test Case 1.5.6: getUsernameFromToken() - Valid Token
- **Description**: Extract username from valid token
- **Expected Result**: Correct username returned

#### Test Case 1.5.7: getUsernameFromToken() - Invalid Token
- **Description**: Extract username from invalid token
- **Expected Result**: Exception or null handling

---

## 2. Integration Tests

### 2.1 Dog Management Flow

#### Test Case 2.1.1: Complete Dog Sync Workflow
- **Steps**:
  1. Call sync-dogs endpoint
  2. Verify dogs are in database
  3. Retrieve first page
  4. Verify pagination
- **Expected Result**: All steps succeed

#### Test Case 2.1.2: Filter and Pagination
- **Steps**:
  1. Sync dogs from API
  2. Apply name filter
  3. Check pagination for filtered results
- **Expected Result**: Correct filtered and paginated results

#### Test Case 2.1.3: Create, Read, Update Flow
- **Steps**:
  1. Create random dog
  2. Retrieve by ID
  3. Modify (if update endpoint exists)
  4. Verify changes
- **Expected Result**: Dog operations work correctly

---

### 2.2 Authentication Flow

#### Test Case 2.2.1: Complete Auth Lifecycle
- **Steps**:
  1. Login with credentials
  2. Extract tokens from response headers
  3. Decrypt tokens
  4. Use tokens for protected endpoint (saveDog)
  5. Refresh tokens
  6. Logout
- **Expected Result**: All steps complete successfully

#### Test Case 2.2.2: Token Expiration and Refresh
- **Steps**:
  1. Login and get tokens
  2. Wait for access token to expire (simulated)
  3. Call refresh endpoint
  4. Verify new token works
- **Expected Result**: Token refresh maintains session

#### Test Case 2.2.3: Concurrent Requests with Auth
- **Steps**:
  1. Login once
  2. Make multiple requests with same token simultaneously
  3. Verify all succeed
- **Expected Result**: Token handling is thread-safe

---

### 2.3 Database Integration

#### Test Case 2.3.1: CRUD Operations on Dog
- **Steps**:
  1. Create dog
  2. Read dog
  3. Update dog (if supported)
  4. Delete dog
- **Expected Result**: All operations persist correctly

#### Test Case 2.3.2: User Authentication DB
- **Steps**:
  1. Verify user exists in DB
  2. Login user
  3. Verify user can be retrieved
- **Expected Result**: User data consistent

#### Test Case 2.3.3: Cascade Deletes
- **Steps**:
  1. Delete dog with weight, height, image
  2. Verify cascade delete works
- **Expected Result**: Related entities deleted (if cascade configured)

---

## 3. Performance Tests

#### Test Case 3.1: Pagination Performance
- **Objective**: Verify page retrieval is performant
- **Criteria**: Response time < 500ms for 100 dogs per page
- **Load**: Retrieve 10 different pages sequentially

#### Test Case 3.2: Filter Performance
- **Objective**: Verify filter queries are optimized
- **Criteria**: Response time < 1 second with 10,000 dogs
- **Load**: Apply various filter combinations

#### Test Case 3.3: Database Connection Pooling
- **Objective**: Verify connection pool handles load
- **Criteria**: 50 concurrent requests without connection exhaustion

#### Test Case 3.4: Token Generation Performance
- **Objective**: Verify token creation is fast
- **Criteria**: Generate 100 tokens in < 1 second

---

## 4. Security Tests

#### Test Case 4.1: Authentication Required
- **Description**: Verify protected endpoints require auth
- **Test**: Call /dog/save-dog without tokens
- **Expected Result**: HTTP 401 or error response

#### Test Case 4.2: Token Encryption
- **Description**: Verify tokens are encrypted in cookies
- **Test**: Inspect cookie values
- **Expected Result**: Cookie values are encrypted, not plain JWT

#### Test Case 4.3: CORS Policy
- **Description**: Verify CORS is properly configured
- **Test**: Make request from different origin
- **Expected Result**: Appropriate CORS headers or rejection

#### Test Case 4.4: Input Validation
- **Description**: Verify input validation on endpoints
- **Test**: Send invalid FilterDogDTO, malformed JSON
- **Expected Result**: Validation errors, HTTP 400

#### Test Case 4.5: SQL Injection Prevention
- **Description**: Verify parameterized queries prevent SQL injection
- **Test**: Provide SQL injection payloads in filter
- **Expected Result**: No SQL injection, treated as literal string

#### Test Case 4.6: Password Handling
- **Description**: Verify password security (if password update exists)
- **Test**: Verify passwords are hashed/encrypted
- **Expected Result**: Plain passwords never stored

---

## 5. Error Handling & Edge Cases

#### Test Case 5.1: Null Pointer Handling
- Null request bodies
- Null path variables
- Null database results
- **Expected**: Graceful error responses

#### Test Case 5.2: Empty Collections
- Empty dog list from API
- Empty filter results
- Empty database
- **Expected**: Handled without exceptions

#### Test Case 5.3: Large Data Sets
- Sync 10,000+ dogs
- Filter large result set
- **Expected**: Completed without timeout or memory error

#### Test Case 5.4: Concurrent Operations
- Multiple users adding/deleting dogs simultaneously
- Database locking behavior
- **Expected**: No race conditions or data corruption

#### Test Case 5.5: Boundary Values
- Page number = 0, negative, very large
- Dog ID = 0, negative, max long
- Filter text = empty, very long (1MB+)
- **Expected**: Appropriate error handling

---

## 6. API Documentation Tests

#### Test Case 6.1: Request/Response Formats
- Verify all endpoints accept/return correct content types
- Verify response structures match documentation

#### Test Case 6.2: Status Codes
- Verify correct HTTP status codes for all scenarios
- 200, 201 for success
- 400 for validation errors
- 401 for auth failures
- 500 for server errors

#### Test Case 6.3: Error Responses
- Verify all error responses include message field
- Verify error message clarity

---

## 7. Test Execution Strategy

### Phase 1: Unit Tests (Priority: Critical)
- All service layer tests
- All controller tests
- Estimated coverage: 80%+
- Timeline: Week 1

### Phase 2: Integration Tests (Priority: High)
- Database integration
- Authentication flow
- API endpoints end-to-end
- Timeline: Week 2

### Phase 3: Performance Tests (Priority: Medium)
- Load testing with pagination
- Filter query optimization verification
- Timeline: Week 2-3

### Phase 4: Security Tests (Priority: Critical)
- Authentication/authorization
- Input validation
- Token security
- Timeline: Week 3

### Phase 5: Edge Cases & Error Handling (Priority: High)
- Boundary value testing
- Null/empty data handling
- Timeline: Week 3-4

---

## 8. Tools & Frameworks

- **Test Framework**: JUnit 5
- **Mocking**: Mockito
- **Integration Testing**: Spring Boot Test (@SpringBootTest)
- **API Testing**: RestAssured or MockMvc
- **Performance**: JMH or custom timing
- **Code Coverage**: JaCoCo (already configured)

---

## 9. Test Data Requirements

- Test users with valid/invalid credentials
- Sample dog data (various breeds, origins)
- Pagination test data (100-1000 dogs)
- Token fixtures (valid, expired, malformed)
- Filter test cases (edge cases, wildcards)

---

## 10. Success Criteria

- [ ] Unit test coverage ≥ 80%
- [ ] All integration tests pass
- [ ] All security tests pass
- [ ] No critical bugs found
- [ ] Performance tests meet criteria
- [ ] Error handling comprehensive
- [ ] Code review approval

---

## 11. Known Issues to Test

- DogServiceImpl.syncDogs() returns null (line 115) - test this edge case
- Password validation is commented out in login (line 38-40) - test login flow
- No explicit null checks in some methods - test null parameter handling

---

## 12. Future Enhancements

- Add update endpoint tests
- Add bulk operations tests
- Add search functionality tests
- Add export/import functionality tests
- Add audit logging tests

