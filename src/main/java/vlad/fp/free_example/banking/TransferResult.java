package vlad.fp.free_example.banking;

import vlad.fp.lib.Either;
import vlad.fp.lib.tuple.Tuple2;

final class TransferResult {
  final Either<Error, Tuple2<From, To>> result;

  TransferResult(Either<Error, Tuple2<From, To>> result) {
    this.result = result;
  }
}
