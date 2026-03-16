package com.restfulapi.exception;

/**
 * Domain exception thrown when an object with a given ID does not exist.
 * - Extends RuntimeException (unchecked) so service methods don't need checked throws
 * - Caught by GlobalExceptionHandler and mapped to a 404 response
 */
public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String id) {
        super(" Object with id=" + id + " was not found.");
    }
}
