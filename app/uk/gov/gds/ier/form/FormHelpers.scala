package uk.gov.gds.ier.form

import views.html.helper.{FieldElements, FieldConstructor}
import views.html.includes.veryPlainConstructor
import uk.gov.gds.ier.model.FormKeys
import play.api.data.Field

object FormHelpers extends FormKeys {
  implicit val myFields = FieldConstructor(veryPlainConstructor.f)
  lazy val keys = FormKeys
  def validationMessage(field:Field) = views.html.includes.validationMessage.apply(field)
}
