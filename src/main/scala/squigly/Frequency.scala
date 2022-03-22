package squigly

import cats.effect.IO
import cats.implicits.given
import cats.syntax.traverse.*

class Frequency[T](initialReceivers: SigReceiver[T]*) extends SigReceiver[T], SigSource[T] {
  private var receivers: List[SigReceiver[T]] = initialReceivers.toList

  def receive(signal: T, history: List[Any]): IO[Unit] = IO.defer {
    if(!history.contains(this)) {
      receivers.map(_.receive(signal, this :: history)).sequence.as(())
    } else {
      IO(())
    }
  }

  def subscribe(receiver: SigReceiver[T]) = IO[Unit] {
    receivers = receiver :: receivers
  }
}
