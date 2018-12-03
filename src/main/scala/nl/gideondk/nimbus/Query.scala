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

import nl.gideondk.nimbus.datastore.api.QueryApi.Filter.{ CompositeFilter, PropertyFilter }
import nl.gideondk.nimbus.datastore.api.QueryApi.{ CompositeOperator, PropertyOperator, PropertyReference, RawQuery }
import nl.gideondk.nimbus.datastore.model.{ Key, ValueWriter, Value }

object Query {

  implicit class symbolToFieldNameToComparisonFilter(s: Symbol) {
    def >[A: ValueWriter](v: A) = PropertyFilter(PropertyReference(s.name), PropertyOperator.GreaterThan, implicitly[ValueWriter[A]].write(v))

    def >=[A: ValueWriter](v: A) = PropertyFilter(PropertyReference(s.name), PropertyOperator.GreaterThanOrEqual, implicitly[ValueWriter[A]].write(v))

    def <[A: ValueWriter](v: A) = PropertyFilter(PropertyReference(s.name), PropertyOperator.LessThan, implicitly[ValueWriter[A]].write(v))

    def <=[A: ValueWriter](v: A) = PropertyFilter(PropertyReference(s.name), PropertyOperator.LessThanOrEqual, implicitly[ValueWriter[A]].write(v))

    def ===[A: ValueWriter](v: A) = PropertyFilter(PropertyReference(s.name), PropertyOperator.Equal, implicitly[ValueWriter[A]].write(v))

    def \\(v: Key) = PropertyFilter(PropertyReference(s.name), PropertyOperator.HasAncestor, Value(v))

    def hasAncestor(v: Key) = PropertyFilter(PropertyReference(s.name), PropertyOperator.HasAncestor, Value(v))
  }

  implicit class propertyFilterToCombinable(filter: PropertyFilter) {
    def and(combineWith: PropertyFilter) = CompositeFilter(CompositeOperator.And, Seq(filter, combineWith))
  }

  implicit class compositeFilterToCombinable(filter: CompositeFilter) {
    def and(combineWith: PropertyFilter) = CompositeFilter(CompositeOperator.And, filter.filters :+ combineWith)
  }

  case class QueryDSL(val inner: RawQuery) {

    import nl.gideondk.nimbus.datastore.api.QueryApi._

    def kindOf(kind: Symbol) = QueryDSL(inner.copy(kind = Some(Seq(kind.name)))) // Only one kind can be set in the current DS API

    def orderAscBy(field: Symbol) = QueryDSL(inner.copy(order = Some((inner.order.getOrElse(Seq.empty) :+ PropertyOrder(field.name, OrderDirection.Ascending)))))

    def orderDescBy(field: Symbol) = QueryDSL(inner.copy(order = Some((inner.order.getOrElse(Seq.empty) :+ PropertyOrder(field.name, OrderDirection.Descending)))))

    def filterBy(filter: Filter) = QueryDSL(inner.copy(filter = Some(filter)))

    def projectOn(fields: Symbol*) = QueryDSL(inner.copy(projection = Some(fields.toSeq.map(x => Projection(PropertyReference(x.name))))))

    def startFrom(cursor: String) = QueryDSL(inner.copy(startCursor = Some(cursor)))

    def startFrom(cursorOpt: Option[String]) = QueryDSL(inner.copy(startCursor = cursorOpt))

    def endAt(cursor: String) = QueryDSL(inner.copy(endCursor = Some(cursor)))

    def withOffset(offset: Int) = QueryDSL(inner.copy(offset = Some(offset)))

    def withLimit(limit: Int) = QueryDSL(inner.copy(limit = Some(limit)))
  }

  val Q = QueryDSL(RawQuery(None, None, None, None, None, None, None, None, None))
}
