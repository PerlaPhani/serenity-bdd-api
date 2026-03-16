@delete
Feature: Ability to delete an item
  As an API consumer
  I want to delete objects by their ID
  So that I can remove items that are no longer needed

  Scenario: Delete a previously created item
    Given a "Item To Delete" item is created
    And the request to add the item is made
    When the item is deleted by its ID
    Then a 200 response code is returned
    And the item no longer exists when retrieved

  Scenario: Attempt to delete a non-existent item
    When the item is deleted by ID "invalid_id_99999"
    Then a 404 response code is returned
