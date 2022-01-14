package org.mulesoft.common.test.util

import org.scalatest.Assertion
import org.scalatest.Assertions.{fail, succeed}
import org.scalatest.exceptions.TestFailedException

import scala.concurrent.{ExecutionContext, Future}

object ValidateAssertion {

  def validateTestFailedMessage[T](assertion: => Assertion, expected: String): Assertion = {
    try {
      assertion
      fail("no assertion was thrown")
    }
    catch {
      case actual: TestFailedException =>
        if(actual.message.contains(expected)) succeed
        else fail(s"actual message $actual did not equal expected message $expected")
      case _: Throwable =>
        fail("incorrect exception was thrown")
    }
  }

  def validateTestFailedMessage[T](assertion: => Future[Assertion], expected: String)(implicit ec: ExecutionContext): Future[Assertion] = {
    assertion.map { _ => fail("no assertion was thrown") }
      .recover {
        case actual: TestFailedException =>
          if(actual.message.contains(expected)) succeed
          else fail(s"actual message $actual did not equal expected message $expected")
        case _: Throwable =>
          fail("incorrect exception was thrown")
      }
  }
}
