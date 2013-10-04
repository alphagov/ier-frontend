package uk.gov.gds.ier.form

import views.html.helper.{FieldElements, FieldConstructor}
import views.html.includes.veryPlainConstructor
import play.api.data.Field
import uk.gov.gds.ier.validation.FormKeys

object FormHelpers extends FormKeys {
  implicit val myFields = FieldConstructor(veryPlainConstructor.f)
  lazy val keys = FormKeys
  lazy val validationMessage = views.html.includes.validationMessage
  lazy val validationWrap = views.html.includes.validationWrap
  lazy val validationSwitch = views.html.includes.validationSwitch
  lazy val check = views.html.includes.check
  lazy val classIf = views.html.includes.classIf
  lazy val checkedIf = views.html.includes.checkedIf
  lazy val visibleIf = views.html.includes.visibleIf
  lazy val selectedIf = views.html.includes.selectedIf
}
