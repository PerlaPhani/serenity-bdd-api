@create
Feature: Ability to create an item

  Scenario: Successfully create an item with full details
    Given a "Apple MacBook Pro 16" item is created
    And the CPU model is "Intel Core i9"
    And has a price of "1849.99"
    When the request to add the item is made
    Then a 200 response code is returned
    And a "Apple MacBook Pro 16" is created
    And the response contains a non-null "id"
    And the response "createdAt" timestamp is present

  Scenario: Create an item with minimal data (name only)
    Given a "Basic Item" item is created
    When the request to add the item is made
    Then a 200 response code is returned
    And a "Basic Item" is created
