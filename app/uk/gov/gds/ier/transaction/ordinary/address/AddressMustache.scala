package uk.gov.gds.ier.transaction.ordinary.address

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AddressMustache extends StepTemplate[InprogressOrdinary] {
  self: WithSerialiser =>

  case class LookupModel (
      question: Question,
      postcode: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/addressLookup") { implicit lang => (form, post) =>
    implicit val progressForm = form
    LookupModel(
      question = Question(
        postUrl = post.url,
        title = Messages("title") +" "+ Messages("ordinary_address_postcode_title"),
        errorMessages = Messages.translatedGlobalErrors(form),
        newQuestion = Messages("ordinary_address_postcode_title")
      ),
      postcode = TextField(
        key = keys.address.postcode
      )
    )
  }
}
