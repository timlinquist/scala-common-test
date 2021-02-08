package org.mulesoft.common.test

import scala.concurrent.Future

class AsyncBeforeAndAfterEachTest extends AsyncBeforeAndAfterEach {

  var pendingInitialization: Option[Int] = None

  override protected def beforeEach(): Future[Unit] = Future { pendingInitialization = Some(0)}

  test("Verify beforeEach is called"){
    assert(pendingInitialization.isDefined)
  }
}
