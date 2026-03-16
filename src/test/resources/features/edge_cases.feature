@edge
Feature: Error handling and edge cases
  As an API consumer
  I want to verify the API handles unusual inputs gracefully
  So that I can be confident in the system's robustness

  Scenario: Create an item with empty name
    Given a "" item is created
    When the request to add the item is made
    Then the response indicates a validation issue or accepts the request

  Scenario: Create an item with special characters in name
    Given a "Laptop @#$%^& Pro <2024>" item is created
    When the request to add the item is made
    Then a 200 response code is returned

  Scenario: Create and immediately retrieve confirms data consistency
    Given a "Consistency Check Device" item is created
    And the CPU model is "M3 Max"
    And has a price of "3499.99"
    And the request to add the item is made
    When the item is retrieved by its ID
    Then a 200 response code is returned
    And the response name is "Consistency Check Device"
    And the response data contains "M3 Max" for "CPU model"
    And the response data contains "3499.99" for "price"

  Scenario: Create, update, then verify the update persists
    Given a "Original Name" item is created
    And has a price of "100.00"
    And the request to add the item is made
    When the item is updated with name "Updated Name" and price "200.00"
    And the item is retrieved by its ID
    Then the response name is "Updated Name"
    And the response data contains "200.00" for "price"

  Scenario: Delete an item and confirm it is gone
    Given a "Temporary Item" item is created
    And the request to add the item is made
    When the item is deleted by its ID
    And the item is retrieved by its ID
    Then a 404 response code is returned
