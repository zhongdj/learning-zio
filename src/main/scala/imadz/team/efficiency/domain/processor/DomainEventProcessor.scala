package imadz.team.efficiency.domain.processor

import imadz.team.efficiency.domain.DomainEvent

trait DomainEventProcessor[E <: DomainEvent]{

  def onEvent(e: E): Unit
}
