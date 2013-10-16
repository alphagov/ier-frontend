package uk.gov.gds.ier.form

import views.html.helper.{FieldElements, FieldConstructor}
import views.html.includes.veryPlainConstructor
import play.api.data.{Form, Field}
import uk.gov.gds.ier.validation.{InProgressForm, FormKeys}
import play.api.templates.Html

object FormHelpers extends FormKeys {
  implicit val myFields = FieldConstructor(veryPlainConstructor.f)
  lazy val keys = FormKeys
  lazy val validationMessage = views.html.includes.validationMessage
  lazy val validationWrap = views.html.includes.validationWrap
  lazy val validationSwitch = views.html.includes.validationSwitch
  lazy val classIf = views.html.includes.classIf
  lazy val checkedIf = views.html.includes.checkedIf
  lazy val visibleIf = views.html.includes.visibleIf
  lazy val selectedIf = views.html.includes.selectedIf
  lazy val check = views.html.inputs.checkBox
  lazy val radio = views.html.inputs.radioButton
  lazy val text = views.html.inputs.textBox
  lazy val select = views.html.inputs.selectBox
  lazy val textArea = views.html.inputs.textArea

  implicit def InprogressForm2Form(implicit inprogress:InProgressForm):Form[_] = inprogress.form
}
