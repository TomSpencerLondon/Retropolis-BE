package com.codurance.retropolis.models;

public class Card {

  private int id;
  private int columnId;
  private String text;

  public Card(String text, int id) {
    this.id = id;
    this.text = text;
  }

  public Card(String text, int id, int columnId) {
    this.text = text;
    this.id = id;
    this.columnId = columnId;
  }

  public Card() {
  }

  public String getText() {
    return text;
  }

  public int getColumnId() {
    return columnId;
  }

  public int getId() {
    return id;
  }
}