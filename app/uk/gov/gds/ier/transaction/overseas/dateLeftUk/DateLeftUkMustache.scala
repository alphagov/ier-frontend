package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait DateLeftUkMustache extends StepMustache {

  case class DateLeftUkModel(question:Question,
                             dateLeftUkFieldSet: FieldSet,
                             dateLeftUkMonth: Field,
                             dateLeftUkYear: Field)

  def dateLeftUkMustache(form:ErrorTransformForm[InprogressOverseas],
                         post: Call,
                         back: Option[Call]): Html = {

    implicit val progressForm = form

    val data = DateLeftUkModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = "When did you leave the UK?"
      ) ,
      dateLeftUkFieldSet = FieldSet(
        classes = if (progressForm(keys.dateLeftUk.month).hasErrors ||
          progressForm(keys.dateLeftUk.year).hasErrors) "invalid" else ""
      ),
      dateLeftUkMonth = SelectField(
        key = keys.dateLeftUk.month,
        optionList = generateOptionsList(progressForm(keys.dateLeftUk.month).value.getOrElse("")),
        default = SelectOption(text = "Month", value = "")
      ),
      dateLeftUkYear = TextField(
        key = keys.dateLeftUk.year
      )

    )
    val content = Mustache.render("overseas/dateLeftUk", data)
    MainStepTemplate(content, data.question.title)
  }

  def generateOptionsList (month:String): List[SelectOption] = {
    val dateLeftUkMonthOptionsList = DateOfBirthConstants.months.map {
      months => SelectOption(months._1, months._2)
    }.toList
    val updatedDateLeftUkMonthOptionsList = dateLeftUkMonthOptionsList.map { monthOption =>
      if (monthOption.value.equals(month))
        SelectOption(monthOption.value, monthOption.text, "selected")
      else monthOption
    }
    updatedDateLeftUkMonthOptionsList
  }
}
