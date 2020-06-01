package com.codurance.retropolis.controllers;


import com.codurance.retropolis.config.GoogleTokenAuthenticator;
import com.codurance.retropolis.models.Board;
import com.codurance.retropolis.models.User;
import com.codurance.retropolis.services.BoardService;
import com.codurance.retropolis.services.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
public class BoardController extends BaseController {

  private final BoardService boardService;
  private final GoogleTokenAuthenticator googleTokenAuthenticator;
  private final UserService userService;

  @Autowired
  public BoardController(BoardService boardService, GoogleTokenAuthenticator googleTokenAuthenticator, UserService userService) {
    this.boardService = boardService;
    this.googleTokenAuthenticator = googleTokenAuthenticator;
    this.userService = userService;
  }

  @GetMapping(value = "")
  public List<Board> getUsersBoards(@RequestHeader(HttpHeaders.AUTHORIZATION) String token)
      throws GeneralSecurityException, IOException {
    String email = googleTokenAuthenticator.getEmail(token);
    User user = userService.findOrCreateBy(email);

    return boardService.getUsersBoards(user.getId());
  }

  @GetMapping(value = "/{id}")
  public Board getBoard(@PathVariable long id) {
    return boardService.getBoard(id);
  }
}