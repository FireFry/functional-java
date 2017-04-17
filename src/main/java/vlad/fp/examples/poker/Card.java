package vlad.fp.examples.poker;

public final class Card {
  private final Rank rank;
  private final Suit suit;

  public Card(Rank rank, Suit suit) {
    this.rank = rank;
    this.suit = suit;
  }

  public Rank rank() {
    return rank;
  }

  public Suit suit() {
    return suit;
  }
}
