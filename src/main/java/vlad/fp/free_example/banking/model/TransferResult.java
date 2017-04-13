package vlad.fp.free_example.banking.model;

import vlad.fp.lib.Either;
import vlad.fp.lib.tuple.Tuple2;

public final class TransferResult {
  public final Either<Error, Tuple2<From, To>> result;

  public TransferResult(Either<Error, Tuple2<From, To>> result) {
    this.result = result;
  }
}
