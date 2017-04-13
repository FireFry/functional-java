package vlad.fp.examples.banking.dsl.model;

public class ModelFactory {
  private ModelFactory() {}

  public static Account account(String id) {
    return new Account(id);
  }

  public static Amount amount(int value) {
    return new Amount(value);
  }

  public static Error error(String reason) {
    return new Error(reason);
  }

  public static From from(Account account) {
    return new From(account);
  }

  public static To to(Account account) {
    return new To(account);
  }
}
