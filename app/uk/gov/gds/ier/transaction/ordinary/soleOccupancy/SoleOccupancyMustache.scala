package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.form.AddressHelpers
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.model.{Country, SoleOccupancyOption}
import uk.gov.gds.ier.service.ScotlandService

trait SoleOccupancyMustache extends StepTemplate[InprogressOrdinary] with AddressHelpers {

  case class SoleOccupancyModel(
                              question: Question,
                              soleOccupancyYes: Field,
                              soleOccupancyNo: Field,
                              soleOccupancyNotSure: Field,
                              soleOccupancySkipThisQuestion: Field,
                              addressLine: String,
                              postcode: String,
                              displayAddress: Boolean,
                              questionIsMandatory: Boolean,
                              isScottish: Boolean
                              ) extends MustacheData

  val scotlandService: ScotlandService

  val mustache = MultilingualTemplate("ordinary/soleOccupancy") { implicit lang =>
    (form, postUrl) =>

      implicit val progressForm = form

      val addressLine = form(keys.address.addressLine).value.orElse{
        manualAddressToOneLine(form, keys.address.manualAddress)
      }.getOrElse("")

      val postcode = form(keys.address.postcode).value.getOrElse("").toUpperCase

      val country = (form(keys.country.residence).value, form(keys.country.origin).value) match {
        case (Some("Abroad"), origin) => Country(origin.getOrElse(""), true)
        case (residence, _) => Country(residence.getOrElse(""), false)
      }

      val isScottish = scotlandService.isScotByPostcodeOrCountry(postcode, country)

      SoleOccupancyModel(
        question = Question(
          postUrl = postUrl.url,
          title = if (!isScottish) Messages("ordinary_soleOccupancy_title") else "Are you the only person aged 14 or over living at this address?",
          errorMessages = Messages.translatedGlobalErrors(form)),

        soleOccupancyYes = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.Yes.name),
        soleOccupancyNo = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.No.name),
        soleOccupancyNotSure = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.NotSure.name),
        soleOccupancySkipThisQuestion = RadioField(
          key = keys.soleOccupancy.optIn,
          value = SoleOccupancyOption.SkipThisQuestion.name),
        addressLine = addressLine,
        postcode = postcode,
        displayAddress = !addressLine.isEmpty,
        questionIsMandatory = true,
        isScottish = isScottish
      )
  }
}

