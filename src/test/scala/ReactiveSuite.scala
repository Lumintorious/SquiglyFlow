import munit.FunSuite
import cats.effect.IO
import squigly.Frequency
import squigly.SigReceiver
import cats.effect.unsafe.implicits.global
import squigly.Reactive

class ReactiveSuite extends FunSuite {
  test("Reactive should keep track correctly") {
    val int = Reactive(0)

    int.receive(1).unsafeRunSync()

    assertEquals(int.value, 1)
  }
}