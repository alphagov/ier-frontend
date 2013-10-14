package uk.gov.gds.ier.model

import play.api.data.Form
import play.api.data.Forms._

case class AllErrors(singleField:String, crossField:CrossField)

case class CrossField(field1:String, field2:String)

object AllErrorsForm {
  val allErrorsFrom = Form(
    mapping(
      "singleField" -> text.verifying(c => true),
      "crossField" -> mapping(
        "field1" -> text,
        "field2" -> text
      ) (CrossField.apply) (CrossField.unapply).verifying(c => false)
    ) (AllErrors.apply) (AllErrors.unapply)
  )
}