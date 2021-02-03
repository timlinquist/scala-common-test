package org.mulesoft.common.test

import org.mulesoft.common.io.{AsyncFile, Utf8}
import org.mulesoft.common.test.Diff.makeString
import org.scalatest.Matchers.fail
import org.scalatest.{Assertion, Succeeded}
import org.scalatest.Matchers._

import java.io.{File, FileNotFoundException, FileReader, Reader}
import java.lang.System.getProperty
import java.net.{InetAddress, UnknownHostException}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
  *
  */
object Tests {

  private implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def checkLinesDiff(a: AsyncFile, e: AsyncFile, encoding: String = Utf8): Future[Assertion] = computeDiff(a, e, Diff.caseSensitive, encoding)

  def checkDiff(a: AsyncFile, e: AsyncFile, encoding: String = Utf8): Future[Assertion] = computeDiff(a, e, Diff.ignoreAllSpace, encoding)

  def computeDiff(a: AsyncFile, e: AsyncFile, differ: Diff.Str, encoding: String = Utf8): Future[Assertion] = {
    a.read(encoding).zip(e.read(encoding)).map {
      case (actual, expected) =>
        val diffs = differ.diff(actual.toString, expected.toString)
        if (diffs.nonEmpty) {
          if (goldenOverride) {
            a.read(encoding).map(content => e.write(content.toString, encoding))
          } else {
            fail(s"\ndiff -y -W 150 $a $e \n\n${makeString(diffs)}")
          }
        }
        succeed
    }
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
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
      println(expected)
      println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
      println(actual)
      println("==============================================")
      fail("\n" + makeString(diffs))
    }
  }

  /** Force golden override. */
  private def goldenOverride: Boolean = Option(getProperty("golden.override")).isDefined

}
