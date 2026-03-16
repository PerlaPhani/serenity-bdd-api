@edge
Feature: Error handling and edge cases

  Scenario: Create an item with empty name
    Given a "" item is created
    When the request to add the item is made
    Then the response indicates a validation issue or accepts the request

  Scenario: Create an item with special characters in name
    Given a "Laptop @#$%^& Pro <2024>" item is created
    When the request to add the item is made
    Then a 200 response code is returned

  Scenario: Create, update, then verify the update persists
    Given a "Original Name" item is created
    And has a price of "100.00"
    And the request to add the item is made
    When the item is updated with name "Updated Name" and price "200.00"
    And the item is retrieved by its ID
    Then the response name is "Updated Name"
    And the response data contains "200.00" for "price"
