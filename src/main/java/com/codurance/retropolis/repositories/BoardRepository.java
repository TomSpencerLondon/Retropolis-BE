package com.codurance.retropolis.repositories;

import com.codurance.retropolis.models.Board;

public interface BoardRepository {

  Board getBoard(Long id);

  Object getUsersBoards(long userId);
}
