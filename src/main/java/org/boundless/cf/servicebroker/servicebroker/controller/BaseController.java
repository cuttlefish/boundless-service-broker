package org.boundless.cf.servicebroker.servicebroker.controller;


import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.boundless.cf.servicebroker.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.boundless.cf.servicebroker.servicebroker.model.AsyncRequiredErrorMessage;
import org.boundless.cf.servicebroker.servicebroker.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Base controller.
 * Source from: https://github.com/cloudfoundry-community/spring-boot-cf-service-broker
 * @author sgreenberg@gopivotal.com
 *
 */
public class BaseController {

	private static final Log log = LogFactory.getLog(BaseController.class);

	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			HttpMessageNotReadableException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			MethodArgumentNotValidException ex, 
			HttpServletResponse response) {
	    BindingResult result = ex.getBindingResult();
	    String message = "Missing required fields:";
	    for (FieldError error: result.getFieldErrors()) {
	    	message += " " + error.getField();
	    }
		return getErrorResponse(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			Exception ex, 
			HttpServletResponse response) {
		log.warn("Exception", ex);
	    return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ServiceBrokerAsyncRequiredException.class)
	public ResponseEntity<AsyncRequiredErrorMessage> handleException(
			ServiceBrokerAsyncRequiredException ex, 
			HttpServletResponse response) {
		return new ResponseEntity<AsyncRequiredErrorMessage>(
				new AsyncRequiredErrorMessage(ex.getDescription()), HttpStatus.UNPROCESSABLE_ENTITY);
		
	}
	
	public ResponseEntity<ErrorMessage> getErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<ErrorMessage>(new ErrorMessage(message), 
				status);
	}
	
}