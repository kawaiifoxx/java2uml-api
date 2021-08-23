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
    public static final String DELETE_SUCCESS_204 = "Delete Successful.";
    public static final String METHOD_ID_DESC = "id of method.";
    public static final String ERR_RESPONSE_MEDIA_TYPE = "application/json.";
    public static final String SOURCE_ID_DESC = "id of Source.";
    public static final String CLASS_ID_DESC = "id of class or interface.";
    public static final String CLASS_REL_ID_DESC = "id of class relation.";
    public static final String BODY_ID_DESC = "id of body(code snippet).";
    public static final String PARENT_ID_DESC = "id of parent.";
    public static final String CONSTRUCTOR_ID_DESC = "id of constructor.";
    public static final String ENUM_CONST_ID_DESC = "id of enum constant.";
    public static final String ENUM_ID_DESC = "id of enum.";
    public static final String FIELD_ID_DESC = "id of field.";
    public static final String ACCEPTED_DESC_202 = "Your request is currently being processed please wait.";
}
