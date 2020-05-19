package com.codurance.retropolis.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codurance.retropolis.models.Board;
import com.codurance.retropolis.models.Card;
import com.codurance.retropolis.models.Column;
import com.codurance.retropolis.services.BoardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(BoardController.class)
public class BoardControllerTest {

  private static final String URL = "/board";

  @MockBean
  private BoardService boardService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void returns_a_board() throws Exception {
    when(boardService.getBoard()).thenReturn(new Board(Collections.emptyList()));

    MvcResult httpResponse = mockMvc.perform(MockMvcRequestBuilders.get(URL))
        .andExpect(status().isOk()).andReturn();
    String contentAsString = httpResponse.getResponse().getContentAsString();
    Board board = objectMapper.readValue(contentAsString, new TypeReference<>() {
    });

    assertNotNull(board);
  }

  @Test
  void returns_board_with_columns() throws Exception {
    int columnID = 1;
    List<Column> columns = List.of(new Column(columnID, "start", Collections.emptyList()));
    when(boardService.getBoard()).thenReturn(new Board(columns));

    MvcResult httpResponse = mockMvc.perform(MockMvcRequestBuilders.get(URL))
        .andExpect(status().isOk()).andReturn();
    String contentAsString = httpResponse.getResponse().getContentAsString();
    Board board = objectMapper.readValue(contentAsString, new TypeReference<>() {
    });

    assertEquals(columns.size(), board.getColumns().size());
    Column columnResponse = board.getColumns().get(0);
    assertEquals(columnID, columnResponse.getId());
  }

  @Test
  void returns_board_with_columns_and_cards() throws Exception {
    int columnID = 1;
    String text = "hello";
    int cardId = 1;
    List<Card> cards = List.of(new Card(text, cardId, columnID));
    Column column = new Column(columnID, "start", cards);
    List<Column> columns = List.of(column);
    when(boardService.getBoard()).thenReturn(new Board(columns));

    MvcResult httpResponse = mockMvc.perform(MockMvcRequestBuilders.get(URL))
        .andExpect(status().isOk()).andReturn();
    String contentAsString = httpResponse.getResponse().getContentAsString();
    Board board = objectMapper.readValue(contentAsString, new TypeReference<>() {
    });

    Column columnResponse = board.getColumns().get(0);
    assertEquals(cards.size(), columnResponse.getCards().size());
    Card cardResponse = columnResponse.getCards().get(0);
    assertEquals(text, cardResponse.getText());
    assertEquals(cardId, cardResponse.getId());
    assertEquals(columnID, cardResponse.getColumnId());
  }


}