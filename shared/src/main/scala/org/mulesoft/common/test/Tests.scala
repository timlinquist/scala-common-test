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

  def checkLinesDiff(a: AsyncFile, e: AsyncFile, encoding: String = Utf8): Future[Assertion] = {
    a.read(encoding).zip(e.read(encoding)).flatMap {
      case (actual, expected) =>
        val actualLines = actual.toString.linesIterator.toSeq.map(_.trim).toSet
        val expectedLines = expected.toString.linesIterator.toSeq.map(_.trim).toSet
        if (actualLines != expectedLines) {
          val diff = actualLines.diff(expectedLines)
          System.err.println("Not matching lines")
          diff.foreach(l => System.err.println(l))
          checkDiff(a, e)
        } else {
          Future { assert(actualLines == expectedLines) }
        }
    }
  }

  def checkDiff(a: AsyncFile, e: AsyncFile, encoding: String = Utf8): Future[Assertion] = {
    a.read(encoding).zip(e.read(encoding)).map {
      case (actual, expected) =>
        val diffs = Diff.ignoreAllSpace.diff(actual.toString, expected.toString)
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

  /** Force golden override. */
  private def goldenOverride: Boolean = Option(getProperty("golden.override")).isDefined

}
