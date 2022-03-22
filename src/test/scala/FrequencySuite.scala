import munit.FunSuite
import cats.effect.IO
import squigly.Frequency
import squigly.SigReceiver
import cats.effect.unsafe.implicits.global

class FrequencySuite extends FunSuite {
  test("Frequency should broadcast correctly") {
    var milestone = 0
    
    def changeMilestone(by: Int): IO[Unit] = IO { milestone += by }

    val frequency = Frequency[Int](
      SigReceiver.of(changeMilestone),
      SigReceiver.of(changeMilestone),
      SigReceiver.of(changeMilestone)
    )

    frequency.receive(1).unsafeRunSync()
    
    assertEquals(milestone, 3)
  }

  test("Frequency should not loop infinitely when cycled") {
    var milestone = 0
    
    def changeMilestone(by: Int): IO[Unit] = IO {
      if(milestone > 2) {
        throw Exception("Milestone greater than 2")
      }
      milestone += by
    }

    lazy val rec = SigReceiver.of(changeMilestone)

    lazy val freq1: Frequency[Int] = Frequency(rec)
    lazy val freq2: Frequency[Int] = Frequency(rec)
    
    (freq1 ~~> freq2).unsafeRunSync()
    (freq2 ~~> freq1).unsafeRunSync()

    freq1.receive(1).unsafeRunSync()
    
    assertEquals(milestone, 2)
  }
}
