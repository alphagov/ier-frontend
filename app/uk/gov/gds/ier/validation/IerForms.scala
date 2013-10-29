package uk.gov.gds.ier.validation

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import com.google.inject.{Inject, Singleton}

trait IerForms extends FormKeys with FormMappings {
  self: WithSerialiser =>

  val dobFormat = "yyyy-MM-dd"
  val timeFormat = "yyyy-MM-dd HH:mm:ss"
  val postcodeForm = Form(
    single(
      postcode -> nonEmptyText.verifying(PostcodeValidator.isValid(_))
    )
  )
  val completePostcodeForm = Form(
    single(
      address.address.postcode -> nonEmptyText
    )
  )

  val nationalityForm = Form(
    mapping(nationality -> nationalityMapping)
      (nationality => InprogressApplication(nationality = Some(nationality)))
      (inprogressApplication => inprogressApplication.nationality)
  )
  val dateOfBirthForm = Form(
    mapping(dob -> dobMapping)
      (dob => InprogressApplication(dob = Some(dob)))
      (inprogress => inprogress.dob)
  )
  val nameForm = Form(
    mapping(name -> nameMapping)
      (name => InprogressApplication(name = Some(name)))
      (inprogress => inprogress.name)
  )
  val previousNameForm = Form(
    mapping(previousName -> previousNameMapping.verifying("Please enter you previous name", previous => (previous.hasPreviousName && previous.previousName.isDefined) || !previous.hasPreviousName))
      (prevName => InprogressApplication(previousName = Some(prevName)))
      (inprogress => inprogress.previousName)
  )
  val ninoForm = Form(
    mapping(nino -> ninoMapping.verifying("Please enter your National Insurance number", nino => nino.nino.isDefined || nino.noNinoReason.isDefined))
      (nino => InprogressApplication(nino = Some(nino)))
      (inprogress => inprogress.nino)
  )
  val addressForm = Form(
    mapping(
      address -> optional(addressMapping),
      possibleAddresses -> optional(possibleAddressMapping)
    ) ((address, possibleAddresses) => InprogressApplication(address = address, possibleAddresses = possibleAddresses))
      (inprogress => Some(inprogress.address, inprogress.possibleAddresses))
  )
  val previousAddressForm = Form(
    mapping(
      previousAddress -> optional(previousAddressMapping).verifying("Please answer this question", previousAddress => previousAddress.isDefined),
      possibleAddresses -> optional(possibleAddressMapping)
    ) ((prevAddress, possibleAddresses) => InprogressApplication(previousAddress = prevAddress, possibleAddresses = possibleAddresses))
      (inprogress => Some(inprogress.previousAddress, inprogress.possibleAddresses))
  )
  val otherAddressForm = Form(
    mapping(otherAddress -> optional(otherAddressMapping).verifying("Please answer this question", otherAddress => otherAddress.isDefined))
      (otherAddress => InprogressApplication(otherAddress = otherAddress))
      (inprogress => Some(inprogress.otherAddress))
  )
  val openRegisterForm = Form(
    mapping(openRegister -> optional(optInMapping))
      (openRegister => InprogressApplication(openRegisterOptin = openRegister.orElse(Some(true))))
      (inprogress => Some(inprogress.openRegisterOptin))
  )
  val postalVoteForm = Form(
    mapping(postalVote -> optional(optInMapping).verifying("Please answer this question", postalVote => postalVote.isDefined))
      (postalVote => InprogressApplication(postalVoteOptin = postalVote))
      (inprogress => Some(inprogress.postalVoteOptin))
  )
  val contactForm = Form(
    mapping(contact -> contactMapping
      .verifying("Please enter your phone number", contact => contact.contactType != "phone" || {contact.contactType == "phone" && contact.phone.exists(_.nonEmpty)})
      .verifying("Please enter your phone number", contact => contact.contactType != "text" || {contact.contactType == "text" && contact.textNum.exists(_.nonEmpty)})
      .verifying("Please enter your email address", contact => contact.contactType != "email" || {contact.contactType == "email" && contact.email.exists(_.nonEmpty)}))
      (contact => InprogressApplication(contact = Some(contact)))
      (inprogress => inprogress.contact)
  )

  val inprogressForm = Form(
    mapping(
      name -> optional(nameMapping).verifying("Please complete this step", _.isDefined),
      previousName -> optional(previousNameMapping).verifying("Please complete this step", _.isDefined),
      dob -> optional(dobMapping).verifying("Please complete this step", _.isDefined),
      nationality -> optional(nationalityMapping).verifying("Please complete this step", _.isDefined),
      nino -> optional(ninoMapping).verifying("Please complete this step", _.isDefined),
      address -> optional(addressMapping).verifying("Please complete this step", _.isDefined),
      previousAddress -> optional(previousAddressMapping).verifying("Please complete this step", _.isDefined),
      otherAddress -> optional(otherAddressMapping).verifying("Please complete this step", _.isDefined),
      openRegister -> optional(optInMapping).verifying("Please complete this step", _.isDefined),
      postalVote -> optional(optInMapping).verifying("Please complete this step", _.isDefined),
      contact -> optional(contactMapping).verifying("Please complete this step", _.isDefined),
      possibleAddresses -> optional(possibleAddressMapping)
    ) (InprogressApplication.apply) (InprogressApplication.unapply)
  )

  implicit class BetterForm[A](form: Form[A]) {
    def errorsAsMap = {
      form.errors.groupBy(_.key).mapValues {
        errors =>
          errors.map(e => play.api.i18n.Messages(e.message, e.args: _*))
      }
    }
    def simpleErrors: Map[String, String] = {
      form.errors.foldLeft(Map.empty[String, String]){
        (map, error) => map ++ Map(error.key -> play.api.i18n.Messages(error.message, error.args: _*))
      }
    }
  }
}

@Singleton
class InProgress @Inject() (serialiser: JsonSerialiser) extends IerForms with WithSerialiser {

  override def toJson(obj: AnyRef): String = serialiser.toJson(obj)
  override def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson[T](json)

  def apply(application:InprogressApplication):InProgressForm = {
    InProgressForm(inprogressForm.fill(application))
  }
}

case class InProgressForm(form:Form[InprogressApplication]) extends FormKeys{
  def apply(key:String) = {
    form(key)
  }
  def getNationalities = {
    form.value match {
      case Some(application) => application.nationality.map(_.nationalities.filter(_.nonEmpty)).filter(_.size > 0)
      case None => None
    }
  }
  def getOtherCountries = {
    form.value match {
      case Some(application) => application.nationality.map(_.otherCountries.filter(_.nonEmpty)).filter(_.size > 0)
      case None => None
    }
  }
  def hasNoNationalityReason = {
    form(nationality.noNationalityReason).value.exists(_.nonEmpty)
  }
  def hasNationality(thisNationality:String) = {
    form(nationality.nationalities).value.exists(_.contains(thisNationality))
  }
  def confirmationNationalityString = {
    val nationalityString = getNationalities.map(_.mkString(" and "))
    val otherString = getOtherCountries.map("a citizen of " + _.mkString(" and "))
    List(nationalityString.getOrElse(""), otherString.getOrElse("")).filter(_.nonEmpty).mkString(" and ")
  }
}
