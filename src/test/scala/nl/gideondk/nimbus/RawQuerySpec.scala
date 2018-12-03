/*
 * Copyright (c) 2018 Gideon de Kok
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nl.gideondk.nimbus

import nl.gideondk.nimbus.datastore.api.CommitApi.CommitMode
import nl.gideondk.nimbus.datastore.api.QueryApi.Filter.PropertyFilter
import nl.gideondk.nimbus.datastore.api.QueryApi._
import nl.gideondk.nimbus.datastore.model._
import nl.gideondk.nimbus.datastore.model.Value._

class RawQuerySpec extends WithClientSpec {
  def randomPostfix() = java.util.UUID.randomUUID().toString

  "Queries" should {
    val entities = List(
      RawEntity(Key.named(client.projectId, "$TestObject", "Dog" + randomPostfix), Map("feet" -> Value(IntegerValue(4)), "color" -> Value(StringValue("Brown")))),
      RawEntity(Key.named(client.projectId, "$TestObject", "Cat" + randomPostfix), Map("feet" -> Value(IntegerValue(3)), "color" -> Value(StringValue("Black")))),
      RawEntity(Key.named(client.projectId, "$TestObject", "Cat" + randomPostfix), Map("feet" -> Value(IntegerValue(4)), "color" -> Value(StringValue("Black"))))
    )

    "return found items on basis of property filters" in {
      val mutations = entities.map(Insert.apply)
      val keys = entities.map(_.key)

      val query = RawQuery(None, Some(Seq("$TestObject")), Some(PropertyFilter("color", PropertyOperator.Equal, "Brown")), None, None, None, None, None, None)
      for {
        transactionId <- client.beginTransaction()
        _ <- client.commit(Some(transactionId), mutations, CommitMode.Transactional)
        query <- client.query(PartitionId(client.projectId), ExplicitConsistency(ReadConsistency.Eventual), query)
      } yield {
        val entityResults = query.batch.entityResults.get
        entityResults.find(x => x.entity.key == entities(0).key).map(_.entity) shouldEqual Some(entities(0))
        entityResults.find(x => x.entity.key == entities(1).key) should not be defined
      }
    }
  }
}
