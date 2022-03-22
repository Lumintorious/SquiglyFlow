package squigly

import cats.effect.IO

trait SigReceiver[-T] {
  def receive(signal: T, history: List[Any] = List.empty): IO[Unit]

  def <~ (signal: T): IO[Unit] = receive(signal)
  def <~~ (source: SigSource[T]): IO[Unit] = source.subscribe(this)

  inline def backMap[X](fn: X => T): SigReceiver[X] = SigReceiver.Mapped(this, fn)
}

object SigReceiver {
  inline def of[T](function: T => IO[Any]) = new SigReceiver[T] {
    def receive(signal: T, history: List[Any]): IO[Unit] = IO.defer {
      if(!history.contains(this)) {
        function(signal).as(())
      } else {
        IO(())
      }
    }
  }

  given [T]: Conversion[(T => IO[Any]), SigReceiver[T]] =
    fn => SigReceiver.of(fn)

  final class Mapped[T, X](original: SigReceiver[T], mapper: X => T) extends SigReceiver[X] {
    def receive(signal: X, history: List[Any] = List.empty): IO[Unit] =
      original.receive(mapper(signal), this :: history)
  }
}