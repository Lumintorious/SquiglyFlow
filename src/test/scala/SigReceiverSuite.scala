import munit.FunSuite
import cats.effect.IO
import squigly.SigReceiver
import cats.effect.unsafe.implicits.global
import language.implicitConversions

class SigReceiverSuite extends FunSuite {
  test("SigReceiver from function should work as expected") {
    var changed = false

    def doSomething(int: Int): IO[Unit] = IO { changed = true }

    val receiver = SigReceiver.of(doSomething)

    receiver.receive(0).unsafeRunSync()

    println(changed)
    assert(changed)
  }
}
