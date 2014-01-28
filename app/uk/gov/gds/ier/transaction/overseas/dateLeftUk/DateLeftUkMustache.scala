package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait DateLeftUkMustache extends StepMustache {

  case class DateLeftUkModel(question:Question,
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
        number = "5",
        title = "When did you leave the UK?"
      ) ,
      dateLeftUkMonth = SelectField(
        key = keys.dateLeftUk.month,
        optionList = generateOptionsList(progressForm(keys.dateLeftUk.month.key).value.getOrElse(""))
      ),
      dateLeftUkYear = TextField(
        key = keys.dateLeftUk.year
      )

    )
    val content = Mustache.render("overseas/dateLeftUk", data)
    MainStepTemplate(content, data.question.title)
  }

  def generateOptionsList (month:String): List[SelectOption] = {
    val dateLeftUkMonthOptionsList = List(
      SelectOption("01", "January"),
      SelectOption("02", "February"),
      SelectOption("03", "March"),
      SelectOption("04", "April"),
      SelectOption("05", "May"),
      SelectOption("06", "June"),
      SelectOption("07", "July"),
      SelectOption("08", "August"),
      SelectOption("09", "September"),
      SelectOption("10", "October"),
      SelectOption("11", "November"),
      SelectOption("12", "December")
    )

    val updatedDateLeftUkMonthOptionsList = dateLeftUkMonthOptionsList.map(monthOption =>
      if (monthOption.value.equals(month))
        SelectOption(monthOption.value, monthOption.text, "selected")
      else monthOption
    )
    updatedDateLeftUkMonthOptionsList
  }
}
