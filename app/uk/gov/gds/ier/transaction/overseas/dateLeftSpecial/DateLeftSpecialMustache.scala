package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait DateLeftSpecialMustache extends StepTemplate[InprogressOverseas] {

  val service:String

  case class DateLeftSpecialModel(
      question:Question,
      dateLeftSpecialFieldSet: FieldSet,
      dateLeftSpecialMonth: Field,
      dateLeftSpecialYear: Field,
      service: String
  )

  val mustache = MustacheTemplate("overseas/dateLeftService") { (form, post, back) =>

    implicit val progressForm = form

    val title = "When did you cease to be a " + service + "?"

    val data = DateLeftSpecialModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title
      ) ,
      dateLeftSpecialFieldSet = FieldSet(
        classes = if (progressForm(keys.dateLeftSpecial.month).hasErrors ||
          progressForm(keys.dateLeftSpecial.year).hasErrors) "invalid" else ""
      ),
      dateLeftSpecialMonth = SelectField(
        key = keys.dateLeftSpecial.month,
        optionList = generateOptionsList(progressForm(keys.dateLeftSpecial.month).value.getOrElse("")),
        default = SelectOption(text = "Month", value = "")
      ),
      dateLeftSpecialYear = TextField(
        key = keys.dateLeftSpecial.year
      ),
      service = service
    )

    MustacheData(data, title)
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
