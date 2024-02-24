package it.unibo.scafi.example
import it.unibo.alchemist.model.scafi.ScafiIncarnationForAlchemist._

class ChannelWithObstacles extends AggregateProgram with StandardSensors with BlockG {
  override def main() : Any = {
    def obstacle : Boolean = sense("obstacle")
    def source : Boolean = sense("source")
    def target : Boolean = sense("target")
    def channel(source: Boolean, target: Boolean, width: Double): Boolean = {
      distanceTo(source) + distanceTo(target) <= distanceBetween(source, target) + width
    }
    val channelWidth = 0.5
    val inChannel = branch(obstacle){ false }{ channel(source, target, channelWidth) }
    inChannel
  }
}
