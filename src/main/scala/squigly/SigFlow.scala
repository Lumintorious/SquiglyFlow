package squigly

import cats.effect.IO

trait SigFlow[T] extends SigSource[T], SigReceiver[T] {
  inline def <~> (other: SigFlow[T]): IO[Unit] =
    (this ~~> other) >> (this <~~ other) 
}
