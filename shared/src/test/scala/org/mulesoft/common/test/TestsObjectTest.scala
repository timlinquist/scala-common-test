package org.mulesoft.common.test

import org.mulesoft.common.io.FileSystem
import org.mulesoft.common.test.util.ValidateAssertion.validateTestFailedMessage
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.{ExecutionContext, Future}

trait TestsObjectTest extends AsyncFunSuite {

  def fs: FileSystem
  implicit override def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  
  test("checkDiff - same files should return successful assertion") {
    val a = fs.asyncFile("./shared/src/test/resources/a.yaml")

    Tests.checkDiff(a, a)
  }

  test("checkDiff - different files return failed assertion showing diffs in message") {
    val a = fs.asyncFile("./shared/src/test/resources/a.yaml")
    val b = fs.asyncFile("./shared/src/test/resources/b.yaml")

    val expectedMessage =
      """
        |diff -y -W 150 ./shared/src/test/resources/a.yaml ./shared/src/test/resources/b.yaml
        |
        |3c3
        |<     name: someame
        |---
        |>     name: some name
        |5a6
        |>     autoDelete: true
        |7d7
        |<     other: new field
        |""".stripMargin

    validateTestFailedMessage(Tests.checkDiff(a, b), expectedMessage)
  }

  test("checkDiff - files with empty lines will be considered the same and should return successful assertion") {
    val a = fs.asyncFile("./shared/src/test/resources/a.yaml")
    val aWithEmptyLines = fs.asyncFile("./shared/src/test/resources/a-with-empty-lines.yaml")
    Tests.checkDiff(a, aWithEmptyLines)
  }

  test("checkLinesDiff - files with empty lines must return failed assertion") {
    val a = fs.asyncFile("./shared/src/test/resources/a.yaml")
    val aWithEmptyLines = fs.asyncFile("./shared/src/test/resources/a-with-empty-lines.yaml")
    val expectedMessage  = "\ndiff -y -W 150 ./shared/src/test/resources/a.yaml ./shared/src/test/resources/a-with-empty-lines.yaml\n\n1a2\n> \n2a4,5\n> \n> \n4a8\n> \n6a11\n> \n"

    validateTestFailedMessage(Tests.checkLinesDiff(a, aWithEmptyLines), expectedMessage)
  }

  test("checkDiff with strings - different content") {
    val assertion = validateTestFailedMessage(
      Tests.checkDiff(
        """
          |some content
          |with multiple lines
          |""".stripMargin ->
        """
          |other content
          |
          |with multiple liness
          |""".stripMargin),
      expected = "\n2,3c2,4\n< some content\n< with multiple lines\n---\n> other content\n> \n> with multiple liness\n"
    )
    Future.successful(assertion)
  }

  test("checkDiff with strings - same content should return successful assertion") {
    val content =
      """
        |some content
        |with multiple lines
        |"""
    Future.successful(Tests.checkDiff(content -> content))
  }
}
