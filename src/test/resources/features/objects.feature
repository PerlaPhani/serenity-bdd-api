@api @objects
Feature: Restful-API Objects CRUD Operations
  As a consumer of the restful-api.dev API
  I want to perform full CRUD operations on /objects
  So that I can verify the API behaves according to its specification

  # ════════════════════════════════════════════════════════════════════════
  # GET /objects
  # ════════════════════════════════════════════════════════════════════════

  @smoke @get @list
  Scenario: Successfully retrieve all objects
    When I retrieve all objects
    Then the response status code should be 200
    And the response should contain a non-empty list of objects

  @get @list @seed-data
  Scenario: Retrieve objects filtered by multiple IDs
    When I retrieve objects filtered by IDs "3","5","10"
    Then the response status code should be 200
    And the response should contain exactly 3 objects

  @get @list @seed-data
  Scenario: Filter returns only the requested object IDs
    When I retrieve objects filtered by IDs "7","8","9"
    Then the response status code should be 200
    And the response should only contain objects with IDs "7","8","9"

  @get @list @jsonpath
  Scenario: All listed objects have an id and name
    When I retrieve all objects
    Then the response status code should be 200
    And every object in the response should have a non-null "id"
    And every object in the response should have a non-null "name"

  # ════════════════════════════════════════════════════════════════════════
  # GET /objects/{id}
  # ════════════════════════════════════════════════════════════════════════

  @smoke @get @single @seed-data
  Scenario: Successfully retrieve single object by ID
    When I retrieve the object with ID "7"
    Then the response status code should be 200
    And the response object should have name "Apple MacBook Pro 16"

  @get @single @seed-data
  Scenario: Retrieve Apple iPhone 12 Mini by ID
    When I retrieve the object with ID "2"
    Then the response status code should be 200
    And the response object should have name "Apple iPhone 12 Mini"

  @get @single @seed-data
  Scenario: Retrieve Apple AirPods by ID
    When I retrieve the object with ID "6"
    Then the response status code should be 200
    And the response object should have name "Apple AirPods"

  @get @single @jsonpath @seed-data
  Scenario: Verify object data fields using JSON path
    When I retrieve the object with ID "7"
    Then the response status code should be 200
    And the JSON path "name" should equal "Apple MacBook Pro 16"
    And the JSON path "data.year" should equal "2019"
    And the JSON path "data.price" should equal "1849.99"
    And the JSON path "data.'CPU model'" should equal "Intel Core i9"
    And the JSON path "data.'Hard disk size'" should equal "1 TB"

  @get @single @jsonpath @seed-data
  Scenario: Verify object ID field matches the requested ID
    When I retrieve the object with ID "4"
    Then the response status code should be 200
    And the JSON path "id" should equal "4"
    And the JSON path "name" should equal "Apple iPhone 11, 64 GB"

  @negative @get @single
  Scenario: Non-existent ID returns 404
    When I retrieve the object with ID "99999"
    Then the response status code should be 404

  @negative @get @single
  Scenario: Invalid string ID returns 404
    When I retrieve the object with ID "invalid-id-xyz"
    Then the response status code should be 404

  # ════════════════════════════════════════════════════════════════════════
  # POST /objects — PDF example style (builder pattern)
  # ════════════════════════════════════════════════════════════════════════

  @smoke @post @create
  Scenario: Ability to create an item
    Given a "Apple MacBook Pro 16" item is created
    And the CPU model is "Intel Core i9"
    And has a price of "1849.99"
    When the request to add the item is made
    Then the response status code should be 200
    And the JSON path "name" should equal "Apple MacBook Pro 16"
    And the JSON path "data.'CPU model'" should equal "Intel Core i9"
    And the JSON path "data.price" should equal "1849.99"
    And the JSON path "id" should not be null
    And the response should contain a createdAt timestamp

  # ════════════════════════════════════════════════════════════════════════
  # POST /objects — DataTable style
  # ════════════════════════════════════════════════════════════════════════

  @post @create
  Scenario: Create object with full payload
    When I create an object with the following details:
      | name           | Apple MacBook Pro 2023 |
      | year           | 2023                   |
      | price          | 2999.99                |
      | CPU model      | Apple M3 Pro           |
      | Hard disk size | 512 GB                 |
    Then the response status code should be 200
    And the response object should have name "Apple MacBook Pro 2023"
    And the response should contain a createdAt timestamp

  @post @create
  Scenario: Create object with minimal data
    When I create an object with the following details:
      | name  | Budget Phone |
      | price | 199.99       |
      | color | Black        |
    Then the response status code should be 200
    And the response object should have name "Budget Phone"
    And the response should contain a createdAt timestamp

  @post @create
  Scenario: Create object with device specs
    When I create an object with the following details:
      | name         | Apple Watch Ultra |
      | Strap Colour | Orange            |
      | Case Size    | 49mm              |
    Then the response status code should be 200
    And the response object should have name "Apple Watch Ultra"
    And the response should contain a createdAt timestamp

  @post @create @jsonpath
  Scenario: Created object ID is auto-generated
    When I create an object with the following details:
      | name  | ID Generation Test |
      | price | 100                |
    Then the response status code should be 200
    And the JSON path "id" should not be null
    And the JSON path "name" should equal "ID Generation Test"

  # ════════════════════════════════════════════════════════════════════════
  # GET after POST — persistence checks
  # ════════════════════════════════════════════════════════════════════════

  @smoke @post @get @persistence
  Scenario: Ability to return an item
    Given I have created an object with name "Test Persistence Device"
    When I retrieve the created object by its ID
    Then the response status code should be 200
    And the response object should have name "Test Persistence Device"

  @post @get @persistence @jsonpath
  Scenario: Created object data is correctly persisted and retrievable via JSON path
    Given a "Persistence Validation Device" item is created
    And has a price of "599.99"
    And the color is "Silver"
    When the request to add the item is made
    Then the response status code should be 200
    When I retrieve the created object by its ID
    Then the response status code should be 200
    And the JSON path "name" should equal "Persistence Validation Device"
    And the JSON path "data.price" should equal "599.99"
    And the JSON path "data.color" should equal "Silver"

  # ════════════════════════════════════════════════════════════════════════
  # Ability to list multiple items
  # ════════════════════════════════════════════════════════════════════════

  @get @list @persistence
  Scenario: Ability to list multiple items including a newly created one
    Given I have created an object with name "Listed Item Test"
    When I retrieve all objects
    Then the response status code should be 200
    And the response should contain a non-empty list of objects
    And the response list should contain an object with name "Listed Item Test"

  # ════════════════════════════════════════════════════════════════════════
  # PUT /objects/{id}
  # ════════════════════════════════════════════════════════════════════════

  @smoke @put @update
  Scenario: Full update changes name price and color and returns updatedAt
    Given I have created an object with name "Original Name"
    When I fully update the created object with name "Updated MacBook" price "3499.99" and color "Silver"
    Then the response status code should be 200
    And the response object should have name "Updated MacBook"
    And the response should contain an updatedAt timestamp

  @put @update @jsonpath
  Scenario: Full update completely replaces all fields verified via JSON path
    Given I have created an object with name "Device To Replace"
    When I fully update the created object with name "Replaced Device" price "999.99" and color "Black"
    Then the response status code should be 200
    And the JSON path "name" should equal "Replaced Device"
    And the JSON path "data.price" should equal "999.99"
    And the JSON path "data.color" should equal "Black"
    And the response should contain an updatedAt timestamp

  # ════════════════════════════════════════════════════════════════════════
  # PATCH /objects/{id}
  # ════════════════════════════════════════════════════════════════════════

  @smoke @patch @partial-update
  Scenario: Patch only the name field and other fields are retained
    Given I have created an object with name "Original Patch Name"
    When I partially update the created object name to "Patched Name"
    Then the response status code should be 200
    And the response object should have name "Patched Name"
    And the response should contain an updatedAt timestamp

  @patch @partial-update
  Scenario: Patch retains unmodified fields
    Given I have created an object with name "Partial Update Device"
    When I partially update the created object name to "New Partial Name"
    Then the response status code should be 200
    And the response object should have name "New Partial Name"

  @patch @partial-update
  Scenario: Patch a specific data field
    Given I have created an object with name "Color Patch Device"
    When I partially update the created object color to "Blue"
    Then the response status code should be 200
    And the response should contain an updatedAt timestamp

  # ════════════════════════════════════════════════════════════════════════
  # DELETE /objects/{id}
  # ════════════════════════════════════════════════════════════════════════

  @smoke @delete
  Scenario: Ability to delete an item
    Given I have created an object with name "Object To Delete"
    When I delete the created object
    Then the response status code should be 200
    And the delete response message should confirm deletion

  @delete
  Scenario: Delete then GET returns 404
    Given I have created an object with name "Object For Delete Then Get"
    When I delete the created object
    And I retrieve the created object by its ID
    Then the response status code should be 404

  @negative @delete
  Scenario: Double delete returns 404
    Given I have created an object with name "Object For Double Delete"
    When I delete the created object
    And I delete the created object again
    Then the response status code should be 404

  @negative @delete
  Scenario: Delete non-existent ID returns 404
    When I delete the object with ID "99999"
    Then the response status code should be 404

  @negative @delete @jsonpath
  Scenario: Delete response contains error message for non-existent object
    When I delete the object with ID "99999"
    Then the response status code should be 404
    And the JSON path "error" should not be null

  # ════════════════════════════════════════════════════════════════════════
  # End-to-End lifecycle
  # ════════════════════════════════════════════════════════════════════════

  @smoke @e2e @lifecycle
  Scenario: Full lifecycle Create Read PUT PATCH Delete verify 404
    When I create an object with the following details:
      | name           | E2E Lifecycle Device |
      | year           | 2024                 |
      | price          | 1299.99              |
      | CPU model      | M2                   |
      | Hard disk size | 256 GB               |
    Then the response status code should be 200
    And the response object should have name "E2E Lifecycle Device"
    And the response should contain a createdAt timestamp

    When I retrieve the created object by its ID
    Then the response status code should be 200
    And the response object should have name "E2E Lifecycle Device"

    When I fully update the created object with name "E2E Updated Device" price "1499.99" and color "Space Gray"
    Then the response status code should be 200
    And the response object should have name "E2E Updated Device"
    And the response should contain an updatedAt timestamp

    When I partially update the created object name to "E2E Final Name"
    Then the response status code should be 200
    And the response object should have name "E2E Final Name"

    When I delete the created object
    Then the response status code should be 200
    And the delete response message should confirm deletion

    When I retrieve the created object by its ID
    Then the response status code should be 404
