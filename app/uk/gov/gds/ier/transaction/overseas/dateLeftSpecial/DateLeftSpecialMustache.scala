package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

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
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/dateLeftService") { (form, post) =>

    implicit val progressForm = form

    val title = "When did you cease to be a " + service + "?"

    DateLeftSpecialModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ) ,
      dateLeftSpecialFieldSet = FieldSet(
        classes = if (progressForm(keys.dateLeftSpecial.month).hasErrors ||
          progressForm(keys.dateLeftSpecial.year).hasErrors) "invalid" else ""
      ),
      dateLeftSpecialMonth = TextField(keys.dateLeftSpecial.month),
      dateLeftSpecialYear = TextField(key = keys.dateLeftSpecial.year),
      service = service
    )
  }
}
