package com.codurance.retropolis.applicationservices;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.codurance.retropolis.entities.Board;
import com.codurance.retropolis.entities.User;
import com.codurance.retropolis.exceptions.BoardNotFoundException;
import com.codurance.retropolis.services.BoardService;
import com.codurance.retropolis.services.UserService;
import com.codurance.retropolis.web.requests.NewBoardRequestObject;
import com.codurance.retropolis.web.responses.BoardResponseObject;
import com.codurance.retropolis.web.responses.BoardResponseObjectFactory;
import com.codurance.retropolis.web.responses.UserBoardResponseObject;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApplicationBoardServiceTest {

  private final Long BOARD_ID = 1L;
  private final String BOARD_TITLE = "test board";
  private final User USER = new User("john.doe@codurance.com", "John Doe");

  @Mock
  private UserService userService;
  @Mock
  private BoardService boardService;

  @Mock
  private BoardResponseObjectFactory boardResponseFactory;

  private ApplicationBoardService applicationBoardService;

  @BeforeEach
  void setUp() {
    applicationBoardService = new ApplicationBoardService(userService, boardResponseFactory, boardService);
  }

  @Test
  void returns_board_response_object() {
    NewBoardRequestObject requestObject = new NewBoardRequestObject(BOARD_TITLE,
        USER.email);
    requestObject.setUser(USER);
    Board board = new Board(BOARD_ID, BOARD_TITLE, emptyList());
    when(userService.findByEmail(USER.email)).thenReturn(USER);
    when(boardService.getBoard(USER, BOARD_ID)).thenReturn(board);

    BoardResponseObject boardResponseObject = new BoardResponseObject(BOARD_ID, BOARD_TITLE,
        emptyList());
    when(boardResponseFactory.create(board, USER.getId())).thenReturn(boardResponseObject);

    BoardResponseObject boardResponse = applicationBoardService.getBoard(USER, BOARD_ID);

    assertEquals(BOARD_ID, boardResponse.getId());
    assertEquals(BOARD_TITLE, boardResponse.getTitle());
    assertEquals(0, boardResponse.getColumns().size());
  }

  @Test
  public void throws_board_not_found_exception_when_board_id_is_invalid() {
    doThrow(new RuntimeException()).when(boardService).getBoard(USER, BOARD_ID);
    assertThrows(BoardNotFoundException.class,
        () -> applicationBoardService.getBoard(USER, BOARD_ID));
  }

  @Test
  void returns_list_of_board_response_objects() {
    Board board = new Board(BOARD_ID, BOARD_TITLE);
    List<Board> boards = Collections.singletonList(board);
    when(boardService.getUsersBoards(USER)).thenReturn(boards);

    UserBoardResponseObject userBoardResponseObject = new UserBoardResponseObject(BOARD_ID,
        BOARD_TITLE);

    List<UserBoardResponseObject> userBoards = Collections.singletonList(userBoardResponseObject);
    when(boardResponseFactory.create(boards)).thenReturn(userBoards);
    List<UserBoardResponseObject> result = applicationBoardService.getUserBoards(USER);

    assertEquals(BOARD_ID, result.get(0).getId());
    assertEquals(BOARD_TITLE, result.get(0).getTitle());
  }

  @Test
  void creates_a_board() {
    NewBoardRequestObject requestObject = new NewBoardRequestObject(BOARD_TITLE,
        USER.email);
    requestObject.setUser(USER);
    Board board = new Board(BOARD_ID, BOARD_TITLE);
    when(boardService.createBoard(requestObject)).thenReturn(board);

    BoardResponseObject boardResponseObject = new BoardResponseObject(BOARD_ID, BOARD_TITLE,
        emptyList());

    when(boardResponseFactory.create(board, requestObject.getUser().getId()))
        .thenReturn(boardResponseObject);

    BoardResponseObject result = applicationBoardService.createBoard(requestObject);

    assertEquals(BOARD_ID, result.getId());
    assertEquals(BOARD_TITLE, result.getTitle());
  }
}
