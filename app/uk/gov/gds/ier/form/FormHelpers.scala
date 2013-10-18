package uk.gov.gds.ier.form

import play.api.data.{Field, Form}
import uk.gov.gds.ier.validation.{InProgressForm, FormKeys}
import play.api.templates.{Template3, Html}

object FormHelpers extends FormKeys {
  implicit val basicRenderer = FormRenderer.basicRenderer
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

  trait FormRenderer extends NotNull {
    def apply(input:Html, label:Html, field:Field): Html
  }

  implicit def templateAsFieldConstructor(t: Template3[Html, Html, Field, Html]) = FormRenderer(t.render)
  implicit def inlineFieldConstructor(f: (Html, Html, Field) => Html) = FormRenderer(f)

  object FormRenderer {
    implicit val basicRenderer = FormRenderer(views.html.inputs.basicRenderer.render)

    def apply(renderer: (Html, Html, Field) => Html) = new FormRenderer {
      def apply(input: Html, label: Html, field: Field): Html = renderer(input, label, field)
    }
  }
}
