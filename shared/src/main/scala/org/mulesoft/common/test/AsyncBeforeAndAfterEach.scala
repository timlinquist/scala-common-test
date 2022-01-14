package org.mulesoft.common.test

import org.scalactic.source.Position
import org.scalatest.Tag
import org.scalatest.compatible.Assertion
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.Future

trait AsyncBeforeAndAfterEach extends AsyncFunSuite {
  override protected def test(testName: String, testTags: Tag*)(testFun: => Future[Assertion])(
      implicit pos: Position): Unit = {
    lazy val composedFn = for {
      _         <- beforeEach()
      assertion <- testFun
      _         <- afterEach()
    } yield {
      assertion
    }
    super.test(testName, testTags: _*)(composedFn)
  }

  protected def beforeEach(): Future[Unit] = Future.successful(Unit)

  protected def afterEach(): Future[Unit] = Future.successful(Unit)
}
