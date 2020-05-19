package com.codurance.retropolis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codurance.retropolis.factories.CardFactory;
import com.codurance.retropolis.models.Card;
import com.codurance.retropolis.repositories.CardRepository;
import com.codurance.retropolis.repositories.InMemoryCardRepository;
import com.codurance.retropolis.requests.NewCardRequestObject;
import com.codurance.retropolis.services.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CardAcceptanceTest {

    private CardService cardService;

    @BeforeEach
    void setUp() {
        CardRepository cardRepository = new InMemoryCardRepository();
        CardFactory cardFactory = new CardFactory();
        cardService = new CardService(cardRepository, cardFactory);
    }

    @Test
    void should_return_card_when_created() {
        String text = "new card";
        NewCardRequestObject requestObject = new NewCardRequestObject(text);
        Card card = cardService.addCard(requestObject);

        assertEquals(text, card.getText());
        assertEquals(1, card.getId());
    }
}