package uk.gov.gds.ier.step.previousName

import uk.gov.gds.ier.model.{InprogressApplication, PreviousName}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.step.name.NameForms
import play.api.data.Form
import play.api.data.Forms._

trait PreviousNameForms 
  extends NameForms {
    self:  FormKeys
      with ErrorMessages =>

  lazy val previousNameMapping = mapping(
    keys.hasPreviousName.key -> boolean,
    keys.previousName.key -> optional(nameMapping)
  ) (
    PreviousName.apply
  ) (
    PreviousName.unapply
  )    

  val previousNameForm = Form(
    mapping(
      keys.previousName.key -> optional(previousNameMapping
        .verifying("Please enter your previous name", previous => {
          (previous.hasPreviousName && previous.previousName.isDefined) || !previous.hasPreviousName
        })
      ).verifying("Please answer this question", _.isDefined)
    ) (
      prevName => InprogressApplication(previousName = prevName)
    ) (
      inprogress => Some(inprogress.previousName)
    )
  )
}
