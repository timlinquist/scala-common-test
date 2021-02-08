package org.mulesoft.common.test
import org.mulesoft.common.io.{FileSystem, Fs}

class JsTestsObjectTest extends TestsObjectTest {
  override def fs: FileSystem = Fs
}
