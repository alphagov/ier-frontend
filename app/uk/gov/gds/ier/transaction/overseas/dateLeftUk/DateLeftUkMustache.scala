package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait DateLeftUkMustache extends StepTemplate[InprogressOverseas] {

  val title = "When did you leave the UK?"

  case class DateLeftUkModel(
      question:Question,
      dateLeftUkFieldSet: FieldSet,
      dateLeftUkMonth: Field,
      dateLeftUkYear: Field
  )

  val mustache = MustacheTemplate("overseas/dateLeftUk") { (form, post, back) =>

    implicit val progressForm = form

    val data = DateLeftUkModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title
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

    MustacheData(data, title)
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
