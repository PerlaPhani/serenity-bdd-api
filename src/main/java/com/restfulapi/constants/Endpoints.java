package com.restfulapi.constants;

/**
 * URI path constants for the /objects API.
 * - Used by ApiHelper to build request URLs consistently
 */
public final class Endpoints {

    private Endpoints() {
    }

    public static final String OBJECTS       = "/objects";
    public static final String OBJECT_BY_ID  = "/objects/{id}";
    public static final String PATH_PARAM_ID = "id";
    public static final String QUERY_PARAM_ID = "id";
}
