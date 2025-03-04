package com.floristeria.blomst.exception;

import com.floristeria.blomst.domain.Response;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import static com.floristeria.blomst.utils.RequestUtils.handleErrorResponse;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {
    private final HttpServletRequest request;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error(String.format("handleExceptionInternal: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, statusCode), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error(String.format("handleMethodArgumentNotValid: %s", exception.getMessage()));
        var fieldErrors = exception.getBindingResult().getFieldErrors();
        var fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                handleErrorResponse(fieldsMessage, getRootCauseMessage(exception), request, statusCode), statusCode);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Response> apiException(ApiException exception) {
        log.error(String.format("apiException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> badCredentialsException(BadCredentialsException exception) {
        log.error(String.format("badCredentialsException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Response> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error(String.format("SQLIntegrityConstraintViolationException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage().contains("Duplicate entry") ? "Información actualmente existente" : exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> constraintViolationException(ConstraintViolationException exception) {
        log.error(String.format("ConstraintViolationException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Violación de restricciones de datos", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error(String.format("MethodArgumentTypeMismatchException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Tipo de argumento incorrecto: " + exception.getName(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Response> dateTimeParseException(DateTimeParseException exception) {
        log.error(String.format("DateTimeParseException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Formato de fecha incorrecto", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Response> nullPointerException(NullPointerException exception) {
        log.error(String.format("NullPointerException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Referencia a un objeto nulo", getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> illegalArgumentException(IllegalArgumentException exception) {
        log.error(String.format("IllegalArgumentException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Argumento inválido: " + exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Response> unrecognizedPropertyException(UnrecognizedPropertyException exception) {
        log.error(String.format("UnrecognizedPropertyException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de FileNotFoundException para archivos no encontrados
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Response> handleFileNotFoundException(FileNotFoundException exception) {
        log.error(String.format("FileNotFoundException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Archivo no encontrado", getRootCauseMessage(exception), request, NOT_FOUND), NOT_FOUND);
    }

    // Captura de IOException para problemas de entrada/salida
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Response> handleIOException(IOException exception) {
        log.error(String.format("IOException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Error en la entrada/salida de datos", getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    // Captura de MalformedURLException para URLs mal formadas
    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<Response> handleMalformedURLException(MalformedURLException exception) {
        log.error(String.format("MalformedURLException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("URL mal formada", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de UnknownHostException para hosts desconocidos
    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<Response> handleUnknownHostException(UnknownHostException exception) {
        log.error(String.format("UnknownHostException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Host desconocido", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de InvalidParameterException para parámetros inválidos
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Response> handleInvalidParameterException(InvalidParameterException exception) {
        log.error(String.format("InvalidParameterException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Parámetro inválido", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de NoSuchElementException para elementos no encontrados en colecciones
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Response> handleNoSuchElementException(NoSuchElementException exception) {
        log.error(String.format("NoSuchElementException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Elemento no encontrado", getRootCauseMessage(exception), request, NOT_FOUND), NOT_FOUND);
    }

    // Captura de DataFormatException para problemas en el formato de datos comprimidos
    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<Response> handleDataFormatException(DataFormatException exception) {
        log.error(String.format("DataFormatException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Formato de datos incorrecto", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Response> handleUnsupportedOperationException(UnsupportedOperationException exception) {
        log.error(String.format("UnsupportedOperationException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Operación no soportada", getRootCauseMessage(exception), request, METHOD_NOT_ALLOWED), METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> accessDeniedException(AccessDeniedException exception) {
        log.error(String.format("AccessDeniedException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Acceso denegado. No tienes permisos suficientes", getRootCauseMessage(exception), request, FORBIDDEN), FORBIDDEN);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> exception(Exception exception) {
        log.error(String.format("Exception: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Response> transactionSystemException(TransactionSystemException exception) {
        log.error(String.format("TransactionSystemException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Response> handleTimeoutException(TimeoutException exception) {
        log.error(String.format("TimeoutException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Tiempo de espera excedido", getRootCauseMessage(exception), request, REQUEST_TIMEOUT), REQUEST_TIMEOUT);
    }

    // Captura de CompletionException para operaciones asíncronas
    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<Response> handleCompletionException(CompletionException exception) {
        Throwable rootCause = exception.getCause(); // Extraer la causa original
        log.error("Error de concurrencia en operación asíncrona: {}", rootCause != null ? rootCause.getMessage() : exception.getMessage(), exception);
        return new ResponseEntity<>(
                handleErrorResponse("Error de concurrencia en operación asíncrona", getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR),
                INTERNAL_SERVER_ERROR
        );
    }

    // Captura de NumberFormatException para errores de formato numérico
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Response> handleNumberFormatException(NumberFormatException exception) {
        log.error(String.format("NumberFormatException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Formato de número inválido", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de IllegalStateException para estados inválidos de la aplicación
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response> handleIllegalStateException(IllegalStateException exception) {
        log.error(String.format("IllegalStateException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Estado de la aplicación no permitido", getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    // Captura de JsonParseException para errores en el parseo de JSON
    @ExceptionHandler(com.fasterxml.jackson.core.JsonParseException.class)
    public ResponseEntity<Response> handleJsonParseException(com.fasterxml.jackson.core.JsonParseException exception) {
        log.error(String.format("JsonParseException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Error en el formato JSON", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de BeanCreationException para errores en la creación de beans
    @ExceptionHandler(BeanCreationException.class)
    public ResponseEntity<Response> handleBeanCreationException(BeanCreationException exception) {
        log.error(String.format("BeanCreationException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(String.format("Error en la creación del bean '%s': %s", exception.getBeanName(), getRootCauseMessage(exception)), getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR),
                INTERNAL_SERVER_ERROR);
    }

    // Captura de JsonMappingException para errores en el mapeo de JSON
    @ExceptionHandler(com.fasterxml.jackson.databind.JsonMappingException.class)
    public ResponseEntity<Response> handleJsonMappingException(com.fasterxml.jackson.databind.JsonMappingException exception) {
        log.error(String.format("JsonMappingException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Error al mapear JSON a objeto", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de IllegalMonitorStateException para errores de concurrencia en monitores
    @ExceptionHandler(IllegalMonitorStateException.class)
    public ResponseEntity<Response> handleIllegalMonitorStateException(IllegalMonitorStateException exception) {
        log.error(String.format("IllegalMonitorStateException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Error de sincronización en monitor", getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    // Captura de InterruptedException para operaciones interrumpidas
    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<Response> handleInterruptedException(InterruptedException exception) {
        log.error(String.format("InterruptedException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Operación interrumpida", getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    // Captura de ArrayIndexOutOfBoundsException para índices de array fuera de rango
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<Response> handleArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException exception) {
        log.error(String.format("ArrayIndexOutOfBoundsException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Índice fuera de rango en el array", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de ArithmeticException para operaciones aritméticas inválidas
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<Response> handleArithmeticException(ArithmeticException exception) {
        log.error(String.format("ArithmeticException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Error en operación aritmética", getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    // Captura de StackOverflowError para evitar recursión infinita
    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<Response> handleStackOverflowError(StackOverflowError exception) {
        log.error(String.format("StackOverflowError: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse("Error de desbordamiento de pila", getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Response> emptyResultDataAccessException(EmptyResultDataAccessException exception) {
        log.error(String.format("EmptyResultDataAccessException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Response> credentialsExpiredException(CredentialsExpiredException exception) {
        log.error(String.format("CredentialsExpiredException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Response> DisabledException(DisabledException exception) {
        log.error(String.format("DisabledException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Response> lockedException(LockedException exception) {
        log.error(String.format("LockedException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response> duplicateKeyException(DuplicateKeyException exception) {
        log.error(String.format("DuplicateKeyException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(processErrorMessage(exception), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> dataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error(String.format("DataIntegrityViolationException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(processErrorMessage(exception), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Response> dataAccessException(DataAccessException exception) {
        log.error(String.format("DataAccessException: %s", exception.getMessage()));
        return new ResponseEntity<>(
                handleErrorResponse(processErrorMessage(exception), getRootCauseMessage(exception), request, BAD_REQUEST), BAD_REQUEST);
    }

    private String processErrorMessage(Exception exception) {
        if (exception instanceof ApiException) {
            return exception.getMessage();
        }
        //if(exception instanceof TransactionSystemException){return getRootCauseMessage(exception).split(":")[1];}
        if (exception.getMessage() != null) {
            if (exception.getMessage().contains("duplicate") && exception.getMessage().contains("AccountVerifications")) {
                return "Ya has verificado tu cuenta";
            }

            if (exception.getMessage().contains("duplicate") && exception.getMessage().contains("ResetPasswordVerifications")) {
                return "Ya te hemos enviado un email para cambiar tu contraseña";
            }

            if (exception.getMessage().contains("duplicate") && exception.getMessage().contains("key (email)")) {
                return "Email en uso. Usa uno diferente e intentalo de nuevo";
            }

            if (exception.getMessage().contains("duplicate")) {
                return "Entidad duplicada. Por favor intentalo de nuevo";
            }
        }
        return "Ha ocurrido un error. Por favor intentalo de nuevo";
    }

}
