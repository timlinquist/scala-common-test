package org.mulesoft.common.test

import org.scalatest.FunSuite
import org.scalatest.Matchers.convertToAnyShouldWrapper
import org.scalatest.exceptions.TestFailedException

class ListAssertionsTest extends FunSuite with ListAssertions {

  test("equal lists should assert correctly"){
    val a = List(1, 2)
    val b = List (1, 2)
    assert(a, b)
  }

  test("list with different sizes"){
    validateTestFailedMessage(
      List(1, 2),
      List(1),
      "List(1, 2) did not contain the same elements that \nList(1)"
    )
  }

  test("list with different value in certain index"){
    validateTestFailedMessage(
      List(1, 2, 4),
      List(1, 2, 3),
      "4 did not equal 3 at index 2"
    )
  }

  private def validateTestFailedMessage[T](a: List[T], b: List[T], msg: String) = {
    try {
      assert(a,b)
      fail("no assertion was thrown")
    }
    catch {
      case expected: TestFailedException =>
        assert(expected.message.contains(msg))
      case _: Throwable =>
        fail("incorrect exception was thrown")
    }
  }



}
