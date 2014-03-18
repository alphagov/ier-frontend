package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants

trait DateLeftSpecialMustache extends StepMustache {

  case class DateLeftSpecialModel(question:Question,
                             dateLeftSpecialFieldSet: FieldSet,
                             dateLeftSpecialMonth: Field,
                             dateLeftSpecialYear: Field,
                             service: String)

  def dateLeftSpecialMustache(form:ErrorTransformForm[InprogressOverseas],
                         post: Call,
                         back: Option[Call], service: String): Html = {

    implicit val progressForm = form

    val data = DateLeftSpecialModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = "When did you cease to be a " + service + "?"
      ) ,
      dateLeftSpecialFieldSet = FieldSet(
        classes = if (progressForm(keys.dateLeftSpecial.month.key).hasErrors ||
          progressForm(keys.dateLeftSpecial.year.key).hasErrors) "invalid" else ""
      ),
      dateLeftSpecialMonth = SelectField(
        key = keys.dateLeftSpecial.month,
        optionList = generateOptionsList(progressForm(keys.dateLeftSpecial.month.key).value.getOrElse("")),
        default = SelectOption(text = "Month", value = "")
      ),
      dateLeftSpecialYear = TextField(
        key = keys.dateLeftSpecial.year
      ),
      service = service
    )
    val content = Mustache.render("overseas/dateLeftService", data)
    MainStepTemplate(content, data.question.title)
  }

  def generateOptionsList (month:String): List[SelectOption] = {
    val dateLeftSpecialMonthOptionsList = DateOfBirthConstants.months.map {
      months => SelectOption(months._1, months._2)
    }.toList
    val updatedDateLeftSpecialMonthOptionsList = dateLeftSpecialMonthOptionsList.map { monthOption =>
      if (monthOption.value.equals(month))
        SelectOption(monthOption.value, monthOption.text, "selected")
      else monthOption
    }
    updatedDateLeftSpecialMonthOptionsList
  }
}
