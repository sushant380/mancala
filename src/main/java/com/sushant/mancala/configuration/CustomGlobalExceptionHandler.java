package com.sushant.mancala.configuration;

import com.sushant.mancala.exception.GameNotFoundException;
import com.sushant.mancala.exception.InvalidGameException;
import com.sushant.mancala.exception.InvalidPlayerMoveException;
import com.sushant.mancala.exception.UnauthorizedPlayerException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Custom Error handler for application exceptions. */
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {


  // error handle for internal data exception due to malformed data.
  @ExceptionHandler(GameNotFoundException.class)
  public ResponseEntity<Object> handleGameNotFoundException(
      GameNotFoundException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "Game not found. May be its finished or deleted");

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  // error handle for internal data exception due to malformed data.
  @ExceptionHandler(InvalidGameException.class)
  public ResponseEntity<Object> handleInvalidGameException(
      InvalidGameException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "Game is running or finished. You can not join the game");

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  // error handle for internal data exception due to malformed data.
  @ExceptionHandler(InvalidPlayerMoveException.class)
  public ResponseEntity<Object> handleInvalidPlayerMoveException(
      InvalidPlayerMoveException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
  }

  // error handle for internal data exception due to malformed data.
  @ExceptionHandler(UnauthorizedPlayerException.class)
  public ResponseEntity<Object> handleUnauthorizedPlayerException(
      UnauthorizedPlayerException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", "You are not registered for this game. You can not join or make a move");

    return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
}
