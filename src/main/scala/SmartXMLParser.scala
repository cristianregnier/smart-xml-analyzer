import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.{Failure, Try}

object SmartXMLParser {

  private val matchingScore = 0.5

  case class MatchingElement(element: Element, matchingAttributes: Seq[(String, String)]) {
    def score: Double = matchingAttributes.size / element.attributes().size.toDouble

    def path: String = {
      val p = element.parents().asScala.reverse
        .foldLeft(""){(acc, elem) => acc + elem.nodeName() concat ">"}
      p concat element.nodeName()
    }
  }

  def findMatchingElement(originFilePath: String, targetFilePath: String, elementId: String): Try[Option[MatchingElement]] = {
    def readDocument(filename: String): Try[Document] =
      Try(Jsoup.parse(Source.fromResource(filename).mkString))

    def readElement(document: Document): Option[Element] = {
      val element = document.getElementById(elementId)
      if (element == null) None else Some(element)
    }

    def findMatches(element: Element, document: Document): Seq[MatchingElement] = {
      document.getElementsByTag(element.nodeName())
        .asScala.map(MatchingElement(_, Seq.empty))
    }

    readDocument(originFilePath).flatMap {
      origin => {
        readElement(origin) match {
          case Some(element) => readDocument(targetFilePath).map {
            target => findMatches(element, target).headOption // TODO
          }
          case None => Failure(throw new java.lang.RuntimeException(s"Element ${elementId} not found in origin file"))
        }
      }
    }
  }

}
