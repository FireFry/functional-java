package vlad.fp.examples.poker;

public class RankOrder {

  public static int getWeight(Rank rank) {
    switch (rank) {
      case TWO: return 0;
      case THREE: return 1;
      case FOUR: return 2;
      case FIVE: return 3;
      case SIX: return 4;
      case SEVEN: return 5;
      case EIGHT: return 6;
      case NINE: return 7;
      case TEN: return 8;
      case JACK: return 9;
      case QUEEN: return 10;
      case KING: return 11;
      case ACE: return 12;
      default: throw new AssertionError();
    }
  }

}
