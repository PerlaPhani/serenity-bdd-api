@smoke
Feature: Smoke tests for dev environment
  As an API consumer
  I want to verify the API is up and responding correctly
  So that I can confirm basic connectivity and data availability

  Scenario: API health check - list all objects
    When a request to list all objects is made
    Then a 200 response code is returned
    And the response contains a list of objects

  Scenario: Retrieve a known seeded object by ID
    When the item is retrieved by ID "7"
    Then a 200 response code is returned
    And the response name is "Apple MacBook Pro 16"
    And the response data contains "Intel Core i9" for "CPU model"
    And the response data contains "1849.99" for "price"

  Scenario: Retrieve another known seeded object by ID
    When the item is retrieved by ID "1"
    Then a 200 response code is returned
    And the response name is "Google Pixel 6 Pro"
    And the response data contains "Cloudy White" for "color"
    And the response data contains "128 GB" for "capacity"

  Scenario: Retrieve a non-existent object returns 404
    When the item is retrieved by ID "invalid_id_99999"
    Then a 404 response code is returned
    And the response contains an error message
