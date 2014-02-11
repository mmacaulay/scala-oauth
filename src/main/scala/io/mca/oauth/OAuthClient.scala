package io.mca.oauth

import java.util.UUID
import java.util.Date
import java.net.URLEncoder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64

trait OAuthClient {
  val consumerKey: String
  val consumerSecret: String
  val signatureMethod: String = "HMAC-SHA1"
  val version: String = "1.0"

  def tokenRequestHeader(method: String, baseUri: String, callback: Option[String]): String = {
    val params = createOAuthParams :+ ("oauth_callback", callback.getOrElse("oob"))

    val signature = createSignature(method, baseUri, params, "")
    val signatureParam = ("oauth_signature", signature)

    oauthHeader(params :+ signatureParam)
  }

  def resourceHeader(method: String, baseUri: String, requestParams: Seq[(String, String)], token: String, tokenSecret: String): String = {
    val oAuthParams = createOAuthParams :+ ("oauth_token", token)
    val params = requestParams ++ oAuthParams

    val signature = createSignature(method, baseUri, params, tokenSecret)
    val signatureParam = ("oauth_signature", signature)

    oauthHeader(oAuthParams :+ signatureParam)
  }

  def oauthHeader(params: Seq[(String, String)]): String = {
    "OAuth " + params.map { case (key, value) =>
      percentEncode(key) +"=\"" + percentEncode(value) + "\""
    }.mkString(", ")
  }

  def createOAuthParams: Seq[(String, String)] = {
    Seq(("oauth_consumer_key"      , consumerKey),
        ("oauth_nonce"             , createNonce),
        ("oauth_signature_method"  , signatureMethod),
        ("oauth_timestamp"         , createTimestamp.toString),
        ("oauth_version"           , version))
  }

  def createNonce = UUID.randomUUID.toString
  def createTimestamp = (new Date).getTime / 1000

  def percentEncode(str: String) = {
    if(str == null) {
      ""
    } else {
      URLEncoder.encode(str, "UTF-8")
        .replace("*", "%2A")
        .replace("+", "%20")
        .replace("%7E", "~")
    }
  }

  def createSignature(method: String, baseUri: String, 
    params: Seq[(String, String)], tokenSecret: String) = {

    val parameterBaseString = parameterBase(params)
    val signatureBaseString = signatureBase(method, baseUri, parameterBaseString)
    val key = signingKey(tokenSecret)
    hmacSha1(key, signatureBaseString)
  }

  def hmacSha1(key: String, data: String) = {
    val HMAC_SHA1 = "HmacSHA1"
    val UTF_8 = "UTF-8"

    // get an hmac_sha1 key from the raw key bytes
    val signingKey = new SecretKeySpec(key.getBytes(UTF_8), HMAC_SHA1)

    // get an hmac_sha1 Mac instance and initialize with the signing key
    val mac = Mac.getInstance(HMAC_SHA1)
    mac.init(signingKey)

    // compute the hmac on input data bytes
    val rawHmac = mac.doFinal(data.getBytes(UTF_8))

    // base64-encode the hmac
     new String(Base64.encodeBase64(rawHmac), UTF_8)
  }

  def signingKey(tokenSecret: String) = {
    percentEncode(consumerSecret) + "&" + percentEncode(tokenSecret)
  }

  def signatureBase(method: String, baseUri: String, paramString: String) = {
    method.toUpperCase + "&" + percentEncode(baseUri) + "&" + 
      percentEncode(paramString)
  }

  def parameterBase(params: Seq[(String, String)]) = {
    params.map { 
      case(key, value) => (percentEncode(key), percentEncode(value))
    }.sortBy { 
      case (key, value) => (key, value) 
    }.map { 
      case (key, value) => key + "=" + value
    }.mkString("&")
  }
}

