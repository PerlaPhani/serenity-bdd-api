@get
Feature: Ability to return an item
  As an API consumer
  I want to retrieve objects by their ID
  So that I can view the details of a specific item

  Scenario: Retrieve a previously created item by ID
    Given a "Samsung Galaxy S23" item is created
    And the CPU model is "Snapdragon 8 Gen 2"
    And has a price of "799.99"
    And the request to add the item is made
    When the item is retrieved by its ID
    Then a 200 response code is returned
    And the response name is "Samsung Galaxy S23"
    And the response data contains "Snapdragon 8 Gen 2" for "CPU model"
    And the response data contains "799.99" for "price"

  Scenario: Attempt to retrieve a non-existent item
    When the item is retrieved by ID "invalid_id_99999"
    Then a 404 response code is returned
    And the response contains an error message
