package it.unibo.scafi.example
import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class Branching extends AggregateProgram {
  override def main(): Any = branch(mid() % 2 == 0) {
    foldhoodPlus(0)((a,b) => a + b)(nbr(1))
    } {
      0
    }
}
