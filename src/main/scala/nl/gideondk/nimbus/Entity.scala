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

import nl.gideondk.nimbus.datastore.model.{ PathElement, PathElementId, PathElementName, Value }
import scala.language.implicitConversions

case class Path(elements: Seq[PathElement])

object Path {

  implicit class ExtendablePath(s: Path) {
    def /(kind: Symbol, name: String): Path = Path(s.elements :+ PathElement(kind.name, Some(PathElementName(name))))

    def /(kind: Symbol, id: Long): Path = Path(s.elements :+ PathElement(kind.name, Some(PathElementId(id))))

    def /(tpl: Tuple2[Symbol, String]): Path = /(tpl._1, tpl._2)

    def /(kind: Symbol): Path = Path(s.elements :+ PathElement(kind.name, None))
  }

  implicit class ExtendableNameTuple(s: (Symbol, String)) {
    def /(kind: Symbol, name: String): Path = Path(Seq(PathElement(s._1.name, Some(PathElementName(s._2))), PathElement(kind.name, Some(PathElementName(name)))))

    def /(kind: Symbol, id: Long): Path = Path(Seq(PathElement(s._1.name, Some(PathElementName(s._2))), PathElement(kind.name, Some(PathElementId(id)))))

    def /(tpl: Tuple2[Symbol, String]): Path = /(tpl._1, tpl._2)

    def /(kind: Symbol): Path = Path(Seq(PathElement(s._1.name, Some(PathElementName(s._2)))) :+ PathElement(kind.name, None))

  }

  implicit class ExtendableIdTuple(s: (String, Long)) {
    def /(kind: Symbol, name: String): Path = Path(Seq(PathElement(s._1, Some(PathElementId(s._2))), PathElement(kind.name, Some(PathElementName(name)))))

    def /(kind: Symbol, id: Long): Path = Path(Seq(PathElement(s._1, Some(PathElementId(s._2))), PathElement(kind.name, Some(PathElementId(id)))))

    def /(tpl: Tuple2[Symbol, String]): Path = /(tpl._1, tpl._2)

    def /(kind: Symbol): Path = Path(Seq(PathElement(s._1, Some(PathElementId(s._2)))) :+ PathElement(kind.name, None))
  }

  implicit def SymbolToPath(kind: Symbol) = Path(Seq(PathElement(kind.name, None)))

  implicit def SymbolStringToPath(kindNameTuple: (Symbol, String)) = Path(Seq(PathElement(kindNameTuple._1.name, Some(PathElementName(kindNameTuple._2)))))
}

final case class Entity(path: Path, properties: Map[String, Value])

trait EntityWriter[A] {
  def write(value: A): Entity
}

trait EntityReader[A] {
  def read(entity: Entity): A
}

trait EntityConverter[A] extends EntityWriter[A] with EntityReader[A]

object Entity {
  implicit val passThroughEntityFormatter = new EntityConverter[Entity] {
    override def write(value: Entity): Entity = value

    override def read(entity: Entity): Entity = entity
  }

  def apply(kind: Symbol, name: String, properties: Map[String, Value]): Entity = Entity(Path.SymbolStringToPath((kind, name)), properties)
}
