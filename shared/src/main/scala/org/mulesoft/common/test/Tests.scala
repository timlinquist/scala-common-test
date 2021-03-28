package org.mulesoft.common.test

import org.mulesoft.common.io.{AsyncFile, Utf8}
import org.mulesoft.common.test.Diff.makeString
import org.scalatest.Matchers.fail
import org.scalatest.{Assertion, Succeeded}
import org.scalatest.Matchers._

import java.io.{File, FileNotFoundException, FileReader, Reader}
import java.lang.System.{arraycopy, getProperty}
import java.net.{InetAddress, UnknownHostException}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
  *
  */
object Tests {

  private implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def checkLinesDiff(a: AsyncFile, e: AsyncFile, encoding: String = Utf8): Future[Assertion] =
    computeDiff(a, e, Diff.caseSensitive, encoding)

  def checkDiff(a: AsyncFile, e: AsyncFile, encoding: String = Utf8): Future[Assertion] =
    computeDiff(a, e, Diff.ignoreAllSpace, encoding)

  def computeDiff(a: AsyncFile, e: AsyncFile, differ: Diff.Str, encoding: String = Utf8): Future[Assertion] = {
    a.read(encoding).zip(e.read(encoding)).map {
      case (actual, expected) =>
        lazy val diffs = differ.diff(actual.toString, expected.toString)
        if (goldenOverrideAll) {
          overwrite(a, e, encoding) // succeeds
        }
        else if (diffs.nonEmpty && (goldenOverride || goldenOverrideDifferent)) {
          overwrite(a, e, encoding) // succeeds
        }
        else if (diffs.nonEmpty) {
          fail(s"\ndiff -y -W 150 $a $e\n\n${makeString(diffs)}")
        }
        succeed
    }
  }

  /**
    *  Overwrites expected file with actual file's contents
    * @param actual file
    * @param expected file
    * @param encoding encoding for both reading actual file and writing expected file
    * @return Unit -> This is IO
    */
  private def overwrite(actual: AsyncFile, expected: AsyncFile, encoding: String): Future[Unit] = {
    for {
      content <- actual.read(encoding)
      _       <- expected.write(content.toString, encoding)
    } yield {}
  }

  def checkDiff(tuple: (String, String)): Assertion = tuple match {
    case (actual, expected) =>
      checkDiff(actual, expected, Diff.ignoreAllSpace)
      Succeeded
  }

  /** Diff between 2 strings. */
  def checkDiff(actual: String, expected: String, differ: Diff.Str): Unit = {
    val diffs: List[Diff.Delta[String]] = differ.diff(actual, expected)
    if (diffs.nonEmpty) {
      fail("\n" + makeString(diffs))
    }
  }

  @deprecated("Use goldenOverrideDifferent instead", since = "0.0.4")
  private def goldenOverride: Boolean = Option(getProperty("golden.override")).isDefined

  /** Overrides goldens which have differences */
  private def goldenOverrideDifferent: Boolean = Option(getProperty("golden.override.different")).isDefined

  /** Overrides all goldens without computing differences */
  private def goldenOverrideAll: Boolean = Option(getProperty("golden.override.all")).isDefined

}
