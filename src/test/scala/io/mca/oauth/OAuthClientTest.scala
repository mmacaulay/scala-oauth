package io.mca.oauth

import org.scalatest._

class OAuthClientTest extends FunSpec with Matchers {

  describe("Twitter API example") {
    it("generates the expected signature base") {
      val token = Fixtures.TwitterAPITest.token
      val requestParams = Fixtures.TwitterAPITest.requestParams
      val oAuthParams = Fixtures.TwitterAPITest.createOAuthParams :+ ("oauth_token", token)
      val params = requestParams ++ oAuthParams
      val parameterBase = Fixtures.TwitterAPITest.parameterBase(params)
      val signatureBase = Fixtures.TwitterAPITest.signatureBase("POST", "https://api.twitter.com/1/statuses/update.json", parameterBase)
      
      signatureBase should equal("POST&https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521")
    }

    it("generates the expected signing key") {
      val signingKey = Fixtures.TwitterAPITest.signingKey("LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE")

      signingKey should equal("kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE")
    }

    it("generates the expected signature") {
      val token = Fixtures.TwitterAPITest.token
      val requestParams = Fixtures.TwitterAPITest.requestParams
      val oAuthParams = Fixtures.TwitterAPITest.createOAuthParams :+ ("oauth_token", token)
      val params = requestParams ++ oAuthParams

      val signature = Fixtures.TwitterAPITest.createSignature("POST", 
        "https://api.twitter.com/1/statuses/update.json", 
        params,
        Fixtures.TwitterAPITest.tokenSecret)

      signature should equal("tnnArxj06cWHq44gCs1OSKk/jLY=")
    }

    it("generates the expected header") {
      val token = Fixtures.TwitterAPITest.token
      val tokenSecret = Fixtures.TwitterAPITest.tokenSecret
      val requestParams = Fixtures.TwitterAPITest.requestParams

      val header = Fixtures.TwitterAPITest.resourceHeader("POST",
        "https://api.twitter.com/1/statuses/update.json", 
        requestParams,
        token,
        tokenSecret)

      val expected = "OAuth oauth_consumer_key=\"xvz1evFS4wEEPTGEFPHBog\", oauth_nonce=\"kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1318622958\", oauth_version=\"1.0\", oauth_token=\"370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb\", oauth_signature=\"tnnArxj06cWHq44gCs1OSKk%2FjLY%3D\""
      header should equal(expected)
    }
  }

  describe("Resource Header") {
    it("starts with OAuth followed by a space") {
      Fixtures.resourceHeader should startWith("OAuth ")
    }

    it("should include the consumer key") {
      Fixtures.resourceHeader should include("oauth_consumer_key=\"testconsumerkey\"")
    }

    it("should include the nonce") {
      Fixtures.resourceHeader should include("oauth_nonce=\"testnonce\"")
    }

    it("should include the signature") {
      Fixtures.resourceHeader should include("oauth_signature=\"testsignature\"")
    }

    it("should include the signature method") {
      Fixtures.resourceHeader should include("oauth_signature_method=\"HMAC-SHA1\"")
    }

    it("should include the timestamp") {
      Fixtures.resourceHeader should include("oauth_timestamp=\"1318622958\"")
    }

    it("should include the token") {
      Fixtures.resourceHeader should include("oauth_token=\"testtoken\"")
    }

    it("should include the OAuth version") {
      Fixtures.resourceHeader should include("oauth_version=\"1.0\"")
    }
  }

  describe("Request Token Header") {
    it("starts with OAuth followed by a space") {
      Fixtures.tokenRequestHeader should startWith("OAuth ")
    }

    it("should include the consumer key") {
      Fixtures.tokenRequestHeader should include("oauth_consumer_key=\"testconsumerkey\"")
    }

    it("should include the nonce") {
      Fixtures.tokenRequestHeader should include("oauth_nonce=\"testnonce\"")
    }

    it("should include the signature") {
      Fixtures.tokenRequestHeader should include("oauth_signature=\"testsignature\"")
    }

    it("should include the signature method") {
      Fixtures.tokenRequestHeader should include("oauth_signature_method=\"HMAC-SHA1\"")
    }

    it("should include the timestamp") {
      Fixtures.tokenRequestHeader should include("oauth_timestamp=\"1318622958\"")
    }

    it("should include the callback") {
      Fixtures.tokenRequestHeader should include("oauth_callback=\"http%3A%2F%2Fexample.com%2Fcallback\"")
    }

    it("should include the OAuth version") {
      Fixtures.tokenRequestHeader should include("oauth_version=\"1.0\"")
    }

    describe("when a callback is not supplied") {
      it("should insert 'oob' in the callback parameter") {
        Fixtures.tokenRequestHeaderNoCallback should include("oauth_callback=\"oob\"")
      }
    }
  }

  describe("Signing Key") {
    it("should append the token secret to the consumer secret") {
      val signingKey = Fixtures.OAuthTestClient.signingKey(Fixtures.OAuthTestClient.tokenSecret)
      signingKey should equal("testconsumersecret&testtokensecret")
    }
  }

  describe("Signature Base") {
    it("should start with the HTTP method") {
      val method = "GET"
      val baseUri = "http://example.com"
      val parameterBase = "parameterBase"
      val signatureBase = Fixtures.OAuthTestClient.signatureBase(method, baseUri, parameterBase)
      signatureBase should startWith("GET")
    }

    it("should uppercase the HTTP method") {
      val method = "get"
      val baseUri = "http://example.com"
      val parameterBase = "parameterBase"
      val signatureBase = Fixtures.OAuthTestClient.signatureBase(method, baseUri, parameterBase)
      signatureBase should startWith("GET")
    }

    it("should append the percent encoded base URI after the HTTP method") {
      val method = "GET"
      val baseUri = "http://example.com"
      val parameterBase = "parameterBase"
      val signatureBase = Fixtures.OAuthTestClient.signatureBase(method, baseUri, parameterBase)
      signatureBase should startWith("GET&http%3A%2F%2Fexample.com")
    }

    it("should append the percent encoded parameter string after the base URI") {
      val method = "GET"
      val baseUri = "http://example.com"
      val token = Fixtures.OAuthTestClient.token
      val requestParams = Fixtures.OAuthTestClient.requestParams
      val oAuthParams = Fixtures.OAuthTestClient.createOAuthParams :+ ("oauth_token", token)
      val params = requestParams ++ oAuthParams
      val parameterBase = Fixtures.OAuthTestClient.parameterBase(params)
      val signatureBase = Fixtures.OAuthTestClient.signatureBase(method, baseUri, parameterBase)
      signatureBase should equal("GET&http%3A%2F%2Fexample.com&include_entities%3Dtrue%26oauth_consumer_key%3Dtestconsumerkey%26oauth_nonce%3Dtestnonce%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3Dtesttoken%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521")
    }
  }

  describe("Parameter Base") {
    it("should include base oauth params") {
      val token = Fixtures.OAuthTestClient.token
      val requestParams = Fixtures.OAuthTestClient.requestParams
      val oAuthParams = Fixtures.OAuthTestClient.createOAuthParams :+ ("oauth_token", token)
      val params = requestParams ++ oAuthParams
      val parameterBase = Fixtures.OAuthTestClient.parameterBase(params)
      parameterBase should include("oauth_consumer_key=testconsumerkey")
      parameterBase should include("oauth_nonce=testnonce")
      parameterBase should include("oauth_signature_method=HMAC-SHA1")
      parameterBase should include("oauth_timestamp=1318622958")
      parameterBase should include("oauth_token=testtoken")
      parameterBase should include("oauth_version=1.0")
    }

    it("should add any request params passed to it") {
      val parameterBase = Fixtures.OAuthTestClient.parameterBase(Seq(("foo", "bar"), ("baz", "qux")))
      parameterBase should include("foo=bar")
      parameterBase should include("baz=qux")
    }
  }

  describe("Percent Encoding") {
    it("should return an empty string for null values") {
      Fixtures.OAuthTestClient.percentEncode(null) should equal("")
    }

    it("should not encode alphanumeric characters") {
      Fixtures.OAuthTestClient.percentEncode("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890") should equal("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890")
    }

    it("should not encode reserved characters") {
      Fixtures.OAuthTestClient.percentEncode("-._~") should equal("-._~")
    }

    it("should encode other chars") {
      Fixtures.OAuthTestClient.percentEncode("Ladies + Gentlemen") should equal("Ladies%20%2B%20Gentlemen")
      Fixtures.OAuthTestClient.percentEncode("An encoded string!") should equal("An%20encoded%20string%21")
      Fixtures.OAuthTestClient.percentEncode("Dogs, Cats & Mice") should equal("Dogs%2C%20Cats%20%26%20Mice")
      Fixtures.OAuthTestClient.percentEncode("☃") should equal("%E2%98%83")
    }
  }
}

