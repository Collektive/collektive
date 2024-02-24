package it.unibo.scafi.example
import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class NeighborCounter extends AggregateProgram with StandardSensors {
  override def main(): Any = foldhoodPlus(0)((a,b) => a + b)(nbr(1))
}
