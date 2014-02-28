package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.InprogressForces
import scala.Some

trait ServiceForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val serviceMapping = mapping(
    keys.serviceName.key -> optional(nonEmptyText),
    keys.regiment.key -> optional(nonEmptyText)
  ) (
    (serviceName, regiment) => Service(Some(ServiceType.parse(serviceName.get)), regiment)
  ) (
    service => Some(Some(service.serviceName.get.name), service.regiment)
  )

  val serviceForm = ErrorTransformForm(
    mapping(
      keys.service.key -> optional(serviceMapping)
    ) (
      service => InprogressForces(service = service)
    ) (
      inprogressApplication => Some(inprogressApplication.service)
    )
  )
}

