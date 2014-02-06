package uk.gov.gds.ier.mustache
import play.api.templates.Html
import views.html.layouts.{stepsBodyEnd, head}
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys}
import uk.gov.gds.ier.validation.{FormKeys, Key, ErrorTransformForm}
import uk.gov.gds.ier.model.InprogressApplication

trait StepMustache extends FormKeys {

  def Mustache = org.jba.Mustache

  def MainStepTemplate(
      content:Html,
      title: String,
      header:Html = head(),
      scripts:Html = stepsBodyEnd(),
      related:Html = Html.empty,
      insideHeader:Html = Html.empty,
      contentClasses:Option[String] = None
  ) = {
    views.html.layouts.main (
      title = Some(title),
      stylesheets = header,
      scripts = scripts,
      insideHeader = insideHeader,
      related = related,
      contentClasses = contentClasses
    )(content)
  }

  case class Text(value:String = "")
  case class FieldSet (classes:String = "")
  case class Field (
      id:String = "",
      name:String = "",
      classes:String ="",
      value:String = "",
      attributes:String = "",
      optionList:List[SelectOption] = List.empty
  )

  case class SelectOption(value:String, text:String, selected:String = "")
  case class Question (
      postUrl:String = "",
      backUrl:String = "",
      showBackUrl:Boolean = true,
      number:String = "",
      title:String = "",
      errorMessages:Seq[String] = Seq.empty
  )

  object SelectField {
    def apply[T<:InprogressApplication[T]]
        (key: Key, optionList:List[SelectOption], default:SelectOption)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(),
        name = key.key,
        value = progressForm(key.key).value.getOrElse(""),
        classes = if (progressForm(key.key).hasErrors) "invalid" else "",
        optionList = default :: optionList)
    }
  }

  object TextField {
    def apply[T<:InprogressApplication[T]]
        (key: Key)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(),
        name = key.key,
        value = progressForm(key.key).value.getOrElse(""),
        classes = if (progressForm(key.key).hasErrors) "invalid" else "")
    }
  }

  object CheckboxField {
    def apply[T<:InprogressApplication[T]]
        (key: Key, value: String)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(),
        name = key.key,
        attributes = if (progressForm(key.key).value.exists(_ == value)) {
          "checked=\"checked\""
        } else {
          ""
        },
        classes = if (progressForm(key.key).hasErrors) "invalid" else ""
      )
    }
  }

  object RadioField {
    def apply[T<:InprogressApplication[T]]
        (key: Key, value: String)
        (implicit progressForm: ErrorTransformForm[T]):Field = {
      Field(
        id = key.asId(value),
        name = key.key,
        attributes = if (progressForm(key.key).value.exists(_ == value)) {
          "checked=\"checked\""
        } else {
          ""
        },
        classes = if (progressForm(key.key).hasErrors) "invalid" else ""
      )
    }
  }
}
