package org.mulesoft.common.test

import org.mulesoft.common.test.util.ValidateAssertion.validateTestFailedMessage
import org.scalatest.{Assertion, FunSuite}
import org.scalatest.exceptions.TestFailedException

class ListAssertionsTest extends FunSuite with ListAssertions {

  test("equal lists should assert correctly"){
    val a = List(1, 2)
    val b = List (1, 2)
    assert(a, b)
  }

  test("list with different sizes"){
    validateTestFailedMessage(
      assert(List(1, 2), List(1)),
      "List(1, 2) did not contain the same elements that \nList(1)"
    )
  }

  test("list with different value in certain index"){
    validateTestFailedMessage(assert(List(1, 2, 4),List(1, 2, 3)),
      "4 did not equal 3 at index 2"
    )
  }


}
