package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait PassportBlocks {
  self: ConfirmationBlock =>

  private val hasPassport = Some("true")
  private val noPassport = Some("false")
  private val bornInUk = Some("true")
  private val notBornInUk = Some("false")
  private val notBornBefore1983 = Some(false)

  def passport = {
    val passport = form(keys.passport.hasPassport).value
    val birth = form(keys.passport.bornInsideUk).value

    (passport, birth, form.bornBefore1983) match {
      case (`hasPassport`, _, _) => passportDetails
      case (`noPassport`, `notBornInUk`, _) => citizenDetails
      case (`noPassport`, `bornInUk`, `notBornBefore1983`) => citizenDetails
      case _ => ConfirmationQuestion(
        title = "British Passport Details",
        editLink = routes.PassportCheckController.editGet.url,
        changeName = "your passport details",
        content = completeThisStepMessage
      )
    }
  }

  def citizenDetails = {
    val howBecameCitizen = form(keys.passport.citizenDetails.howBecameCitizen).value
    val dateBecameCitizen = form.dateBecameCitizen.map { date =>
      s"${date.getDayOfMonth} ${date.getMonthOfYear} ${date.getYear}"
    }

    val citizenContent = for (
      how <- howBecameCitizen;
      date <- dateBecameCitizen
    ) yield {
      s"<p>How you became a citizen: $how</p>"+
        s"<p>Date you became a citizen: $date</p>"
    }

    val route = if(form(keys.passport).hasErrors) {
      routes.PassportCheckController.editGet
    } else {
      routes.CitizenDetailsController.editGet
    }

    ConfirmationQuestion(
      title = "British Citizenship Details",
      editLink = route.url,
      changeName = "your citizenship details",
      content = ifComplete(keys.passport) { citizenContent.getOrElse(completeThisStepMessage) }
    )
  }

  def passportDetails = {
    val passportNumber = form(keys.passport.passportDetails.passportNumber).value
    val authority = form(keys.passport.passportDetails.authority).value
    val issueDate = for(
      day <- form(keys.passport.passportDetails.issueDate.day).value;
      month <- form(keys.passport.passportDetails.issueDate.month).value;
      year <- form(keys.passport.passportDetails.issueDate.year).value
    ) yield s"$day $month $year"

    val passportContent = for(
      num <- passportNumber;
      auth <- authority;
      date <- issueDate
    ) yield {
      s"<p>Passport Number: $num</p>" +
        s"<p>Authority: $auth</p>" +
        s"<p>Issue Date: $date</p>"
    }

    val route = if(form(keys.passport).hasErrors) {
      routes.PassportCheckController.editGet
    } else {
      routes.PassportDetailsController.editGet
    }

    ConfirmationQuestion(
      title = "British Passport Details",
      editLink = route.url,
      changeName = "your passport details",
      content = ifComplete(keys.passport) { passportContent.getOrElse(completeThisStepMessage) }
    )
  }
}
