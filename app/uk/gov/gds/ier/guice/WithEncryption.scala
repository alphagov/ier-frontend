package uk.gov.gds.ier.guice

import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

trait WithEncryption {

  val encryptionService : EncryptionService
  val encryptionKeys : EncryptionKeys

}
