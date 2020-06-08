package com.codurance.retropolis.controllers;

import com.codurance.retropolis.entities.Card;
import com.codurance.retropolis.exceptions.CardNotFoundException;
import com.codurance.retropolis.exceptions.ColumnNotFoundException;
import com.codurance.retropolis.exceptions.UnauthorizedException;
import com.codurance.retropolis.requests.NewCardRequestObject;
import com.codurance.retropolis.requests.UpVoteRequestObject;
import com.codurance.retropolis.requests.UpdateCardRequestObject;
import com.codurance.retropolis.services.CardService;
import com.codurance.retropolis.services.LoginService;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
public class CardController extends BaseController {

  private final CardService cardService;
  private final LoginService loginService;

  @Autowired
  public CardController(CardService cardService, LoginService loginService) {
    this.cardService = cardService;
    this.loginService = loginService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Card postCard(@RequestBody @Valid NewCardRequestObject request, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
    if (!loginService.isAuthorized(request.getEmail(), token)) {
      throw new UnauthorizedException();
    }
    return cardService.addCard(request);
  }

  @DeleteMapping(value = "/{cardId}")
  public ResponseEntity<HttpStatus> deleteCard(@PathVariable Long cardId) {
    cardService.delete(cardId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PatchMapping(value = "/{cardId}")
  public Card updateText(@PathVariable Long cardId, @RequestBody @Valid UpdateCardRequestObject request) {
    return cardService.update(cardId, request);
  }

  @PatchMapping(value = "/{cardId}/vote")
  public Card updateVote(@PathVariable Long cardId, @RequestBody @Valid UpVoteRequestObject request) {
    return cardService.updateVotes(cardId, request);
  }

  @ExceptionHandler(ColumnNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<String> handleColumnNotFound(ColumnNotFoundException exception) {
    return Collections.singletonList(exception.getMessage());
  }

  @ExceptionHandler(CardNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<String> handleCardNotFound(CardNotFoundException exception) {
    return Collections.singletonList(exception.getMessage());
  }

}