package com.example.demo.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.model.dto.response.ErrorResponse;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /********************* Custom Exceptions *********************/

    /**
     * Handles BusinessException. Thrown when violating business rules
     * 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
	return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), ex.getParams()));
    }
    

    /********************* Common Exceptions *********************/

    /**
     * Handles ConstraintViolationException. Thrown from database on insert or update operation that violates the database constraints
     * 
     * @param ex
     * @param request
     * @return
     * @throws Exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) throws Exception {
	logger.error(ex);
	return ResponseEntity.badRequest().body(new ErrorResponse("error_invalidRequestParams"));
    }

    /**
     * Handles DataIntegrityViolationException. Thrown when an attempt to insert or update data results in violation of an integrity constraint
     * 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
	logger.error(ex);
	if (ex.getCause() instanceof ConstraintViolationException) {
	    return ResponseEntity.badRequest().body(new ErrorResponse("error_invalidRequestParams"));
	} else {
	    return ResponseEntity.badRequest().body(new ErrorResponse("error_general"));
	}
    }

    /**
     * Handle DataAccessResourceFailureException, Thrown when can't connect to database
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Object> handleDataAccessResourceFailureException(final DataAccessResourceFailureException ex, final WebRequest request) {
	logger.error(ex);
	return ResponseEntity.badRequest().body(new ErrorResponse("error_databaseConnectionFailed"));
    }

    /**
     * Handles Exception. handle all other exceptions not handled above
     * 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
	logger.error("An error occurred: ", ex);
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		.body(new ErrorResponse("error_general"));
    }

}