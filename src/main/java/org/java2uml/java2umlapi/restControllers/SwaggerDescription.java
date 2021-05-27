package org.java2uml.java2umlapi.restControllers;

/**
 * An abstract class with some common Http response code descriptions for swagger metadata
 *
 * @author kawaiifox
 */
abstract public class SwaggerDescription {
    public static final String INTERNAL_SERVER_ERROR_DESC = "Something went wrong on our side. We will fix it promise!";
    public static final String SOURCE_CODE_NOT_FOUND_DESC = "source code with given id was not found, please upload a zip file.";
    public static final String PROJECT_ID_DESC = "id of uploaded project.";
    public static final String OK_200_RESPONSE = "Request was successful.";
    public static final String NOT_FOUND_404 = "Not found.";
    public static final String DELETE_SUCCESS_204 = "Delete Successful";
    public static final String METHOD_ID_DESC = "id of method.";
    public static final String ERR_RESPONSE_MEDIA_TYPE = "application/json";
    public static final String SOURCE_ID_DESC = "id of Source";
}
