package it.unibo.scafi.example
import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class FieldEvolution extends AggregateProgram with StandardSensors {
  override def main(): Any = rep(0){_ + 1}
}
