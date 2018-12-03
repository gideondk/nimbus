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

package nl.gideondk.nimbus.datastore.model

trait Mutation

trait ContentMutation extends Mutation {
  def entity: RawEntity

  def baseVersion: Option[String]
}

case class Insert(entity: RawEntity, baseVersion: Option[String]) extends ContentMutation

object Insert {
  def apply(entity: RawEntity): Insert = Insert(entity, None)
}

case class Update(entity: RawEntity, baseVersion: Option[String]) extends ContentMutation

object Update {
  def apply(entity: RawEntity): Update = Update(entity, None)
}

case class Upsert(entity: RawEntity, baseVersion: Option[String]) extends ContentMutation

object Upsert {
  def apply(entity: RawEntity): Upsert = Upsert(entity, None)
}

case class Delete(key: Key) extends Mutation
