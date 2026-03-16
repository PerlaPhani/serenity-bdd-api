@list
Feature: Ability to list multiple items
  As an API consumer
  I want to list all objects in the system
  So that I can browse the available items

  Scenario: List all available objects
    When a request to list all objects is made
    Then a 200 response code is returned
    And the response contains a list of objects

  Scenario: Verify a created item appears in the full list
    Given a "Test Laptop" item is created
    And the request to add the item is made
    When a request to list all objects is made
    Then a 200 response code is returned
    And the list contains an item with name "Test Laptop"
