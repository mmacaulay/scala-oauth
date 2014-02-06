package io.mca.oauth

object Fixtures {
  object OAuthTestClient extends OAuthClient {
    val consumerKey = "testconsumerkey"
    val consumerSecret = "testconsumersecret"
    val token = "testtoken"
    val tokenSecret ="testtokensecret"
    override def createNonce = "testnonce"
    override def createSignature(method: String, baseUri: String, params: Seq[(String, String)], token: String, tokenSecret: String) = {
      "testsignature"
    }

    override def createTimestamp = 1318622958

    val queryParams = Seq(("include_entities", "true"))
    val formParams = Seq(("status", "Hello Ladies + Gentlemen, a signed OAuth request!"))
    val requestParams = queryParams ++ formParams
  }

  object TwitterAPITest extends OAuthClient {
    val consumerKey = "xvz1evFS4wEEPTGEFPHBog"
    val consumerSecret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw"
    val token = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb"
    val tokenSecret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"

    override def createNonce = "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg"
    override def createTimestamp = 1318622958

    val queryParams = Seq(("include_entities", "true"))
    val formParams = Seq(("status", "Hello Ladies + Gentlemen, a signed OAuth request!"))
    val requestParams = queryParams ++ formParams
  }

  def oAuthHeader = {
    OAuthTestClient.oAuthHeader("GET", "http://example.com/path/to/something", Seq(), "testtoken", "testtokensecret")
  }
}

