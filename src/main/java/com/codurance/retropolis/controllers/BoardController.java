package com.codurance.retropolis.controllers;


import com.codurance.retropolis.entities.Board;
import com.codurance.retropolis.exceptions.BoardNotFoundException;
import com.codurance.retropolis.exceptions.UnauthorizedException;
import com.codurance.retropolis.factories.UserFactory;
import com.codurance.retropolis.requests.NewBoardRequestObject;
import com.codurance.retropolis.responses.BoardResponseObject;
import com.codurance.retropolis.services.BoardService;
import com.codurance.retropolis.services.LoginService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
public class BoardController extends BaseController {

  private final BoardService boardService;
  private final UserFactory userFactory;
  private final LoginService loginService;

  @Autowired
  public BoardController(BoardService boardService, UserFactory userFactory, LoginService loginService) {
    this.boardService = boardService;
    this.userFactory = userFactory;
    this.loginService = loginService;
  }

  @GetMapping
  public List<Board> getUsersBoards(@RequestHeader(HttpHeaders.AUTHORIZATION) String token)
      throws GeneralSecurityException, IOException {
    return boardService.getUsersBoards(userFactory.create(token));
  }

  @GetMapping(value = "/{id}")
  public BoardResponseObject getBoard(@PathVariable Long id,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token)
      throws GeneralSecurityException, IOException {
    return boardService.getBoard(userFactory.create(token), id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Board postBoard(@RequestBody @Valid NewBoardRequestObject request, @RequestHeader(HttpHeaders.AUTHORIZATION) String token)
      throws GeneralSecurityException, IOException {
    if (!loginService.isAuthorized(request.getUserEmail(), token)) {
      throw new UnauthorizedException();
    }
    request.setUser(userFactory.create(token));
    return boardService.createBoard(request);
  }

  @ExceptionHandler(BoardNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<String> handleBoardNotFound(BoardNotFoundException exception) {
    return Collections.singletonList(exception.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public List<String> handleUnauthorized(UnauthorizedException exception) {
    return Collections.singletonList(exception.getMessage());
  }
}