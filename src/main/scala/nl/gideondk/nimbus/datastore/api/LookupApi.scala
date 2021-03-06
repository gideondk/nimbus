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

package nl.gideondk.nimbus.datastore.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{ HttpMethods, HttpRequest, RequestEntity, Uri }
import nl.gideondk.nimbus.Connection
import nl.gideondk.nimbus.datastore.model.{ Key, RawEntity, ReadOption }
import nl.gideondk.nimbus.datastore.serialization.Serializers

import scala.concurrent.Future

object LookupApi extends Serializers {

  implicit val lookupRequestFormat = jsonFormat2(LookupRequest.apply)

  implicit val entityResultFormat = jsonFormat3(EntityResult.apply)

  implicit val lookupResponseFormat = jsonFormat3(LookupResponse.apply)

  case class LookupRequest(readOptions: ReadOption, keys: Seq[Key])

  case class EntityResult(entity: RawEntity, version: Option[String], cursor: Option[String])

  case class LookupResponse(found: Option[Seq[EntityResult]], missing: Option[Seq[EntityResult]], deferred: Option[Seq[Key]])

}

trait LookupApi extends Connection {

  import LookupApi._

  def lookup(readOption: ReadOption, keys: Seq[Key]): Future[LookupResponse] = {
    val uri: Uri = baseUri + ":lookup"
    for {
      request <- Marshal(LookupRequest(readOption, keys)).to[RequestEntity].map(x => HttpRequest(HttpMethods.POST, uri, entity = x))
      response <- singleRequest(request)
      entity <- handleErrorOrUnmarshal[LookupResponse](response)
    } yield {
      entity
    }
  }
}
