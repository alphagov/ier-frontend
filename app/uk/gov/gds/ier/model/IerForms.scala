package uk.gov.gds.ier.model

import play.api.data.Form
import play.api.data.Forms._

trait IerForms {
  val dobFormat = "yyyy-MM-dd"
  val timeFormat = "yyyy-MM-dd HH:mm:ss"
  val webApplicationForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "middleName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "previousLastName" -> nonEmptyText,
      "nino" -> nonEmptyText,
      "dob" -> jodaLocalDate(dobFormat)
      )(WebApplication.apply)(WebApplication.unapply)
  )
  val apiApplicationForm = Form(
    mapping(
      "fn" -> nonEmptyText,
      "mn" -> nonEmptyText,
      "ln" -> nonEmptyText,
      "pln" -> nonEmptyText,
      "gssCode" -> nonEmptyText,
      "nino" -> text,
      "dob" -> jodaLocalDate(dobFormat)
    )(ApiApplication.apply)(ApiApplication.unapply)
  )
  val apiApplicationResponseForm = Form(
    mapping(
      "detail" -> apiApplicationForm.mapping,
      "ierId" -> nonEmptyText,
      "createdAt" -> nonEmptyText,
      "status" -> nonEmptyText,
      "source" -> nonEmptyText
    )(ApiApplicationResponse.apply)(ApiApplicationResponse.unapply)
  )
}
