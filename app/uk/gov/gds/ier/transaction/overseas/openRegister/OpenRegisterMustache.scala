package uk.gov.gds.ier.transaction.overseas.openRegister

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait OpenRegisterMustache extends StepTemplate[InprogressOverseas] {

  val title = "Do you want to include your name and address on the open register?"

  case class OpenRegisterModel(
      question:Question,
      openRegister: Field
  )

  val mustache = MustacheTemplate("overseas/openRegister") { (form, post, back) =>

    implicit val progressForm = form

    val data = OpenRegisterModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title
      ),
      openRegister = CheckboxField (
        key = keys.openRegister.optIn,
        value = "false"
      )
    )
    MustacheData(data, title)
  }
}
