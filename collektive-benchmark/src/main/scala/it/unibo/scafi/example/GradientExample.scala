package it.unibo.scafi.example
import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._
class GradientExample extends AggregateProgram with StandardSensors with BlockG with Gradients {
  override def main() = classicGradient(mid == 0)
}
