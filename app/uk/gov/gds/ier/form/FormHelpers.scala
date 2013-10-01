package uk.gov.gds.ier.form

import views.html.helper.{FieldElements, FieldConstructor}
import views.html.includes.veryPlainConstructor
import uk.gov.gds.ier.model.FormKeys
import play.api.data.Field

object FormHelpers extends FormKeys {
  implicit val myFields = FieldConstructor(veryPlainConstructor.f)
  lazy val keys = FormKeys
  def validationMessage(field:Field) = views.html.includes.validationMessage.apply(field)
  lazy val check = views.html.includes.check
  lazy val classIf = views.html.includes.classIf
  lazy val checkedIf = views.html.includes.checkedIf
  lazy val visibleIf = views.html.includes.visibleIf
  lazy val selectedIf = views.html.includes.selectedIf
}
