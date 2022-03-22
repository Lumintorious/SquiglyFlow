package squigly

import cats.effect.IO

trait Reactive[T] extends SigFlow[T] {
  def value: T
}

object Reactive {
  inline def apply[T](initialValue: T): Reactive[T] =
    VariableReactive[T](initialValue)

  inline def by[T](get: => T)(set: T => IO[Unit]) = GetSetReactive(get)(set)
}

class VariableReactive[T](initialValue: T) extends Reactive[T] {
  private var inner: T = initialValue
  private val frequency: Frequency[T] = Frequency[T](
    SigReceiver.of(sig => IO { inner = sig })
  )

  def value: T = inner

  export frequency.{ subscribe, receive }
}

class GetSetReactive[T](get: => T)(set: T => IO[Unit]) extends Reactive[T] {
  def value = get
  
  private val frequency: Frequency[T] = Frequency[T](
    SigReceiver.of(set)
  )
  
  export frequency.{ subscribe, receive }
}