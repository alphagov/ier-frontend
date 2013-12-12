package uk.gov.gds.ier.security

import java.security._
import javax.crypto.{SecretKey, BadPaddingException, Cipher, KeyGenerator}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import sun.security.rsa.{RSAPublicKeyImpl, RSAPrivateCrtKeyImpl}
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject

class Base64EncodingService {

  import org.apache.commons.codec.binary.Base64

  def encode(input: Array[Byte]): String = Base64.encodeBase64String(input)
  def decode(input: String): Array[Byte] = Base64.decodeBase64(input)
}
