package squigly

import cats.effect.IO

trait SigSource[+T] {
  def subscribe(receiver: SigReceiver[T]): IO[Unit]

  def ~~> (receiver: SigReceiver[T]): IO[Unit] = subscribe(receiver)

  inline def map[X](mapper: T => X): SigSource[X] =
    SigSource.Mapped(this, mapper)
}

object SigSource {
  final class Mapped[T, X](original: SigSource[T], mapper: T => X) extends SigSource[X] {
    def subscribe(receiver: SigReceiver[X]): IO[Unit] =
      original.subscribe(receiver.backMap(mapper))
  }
}
