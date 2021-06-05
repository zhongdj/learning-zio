package imadz.team.efficiency.domain

import imadz.team.efficiency.domain.Errors.DomainError
import zio.IO

trait EventPublisher {
  def publish(aggregate: String)(event: DomainEvent): IO[DomainError, Unit]

}
