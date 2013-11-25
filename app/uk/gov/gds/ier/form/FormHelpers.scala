package uk.gov.gds.ier.form

import play.api.data.{Field, Form}
import uk.gov.gds.ier.validation.{InProgressForm, FormKeys}
import play.api.templates.{Template3, Html}

object FormHelpers extends FormKeys {
  implicit val basicRenderer = FormRenderer.basicRenderer
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
  lazy val wrapLabel = views.html.inputs.wrapLabel
  def label(id:uk.gov.gds.ier.validation.Key, label:String, attributes:Map[Symbol,String] = Map.empty)
           (implicit formData: uk.gov.gds.ier.validation.InProgressForm) = {
    views.html.inputs.wrapLabel(id, attributes)(Html(label))(formData)
  }

  trait FormRenderer extends NotNull {
    def apply(input:Html, label:Html => Html, field:Field): Html
  }

  def asId(key:String) = {
    key.replace(".", "_")
  }

  object FormRenderer {
    implicit val basicRenderer = FormRenderer(views.html.inputs.basicRenderer.render)

    def apply(renderer: (Html, Html => Html, Field) => Html) = new FormRenderer {
      def apply(input: Html, label: Html => Html, field: Field): Html = renderer(input, label, field)
    }
  }
}
