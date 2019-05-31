import org.jsoup.Jsoup
import org.jsoup.nodes.{Attribute, Document, Element}

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.{Failure, Try}

object SmartXMLParser {

  private val minScore = 0.5

  case class MatchingElement(element: Element, score: Double) {
    def path: String = element.parents().asScala.reverse
      .foldLeft("") { (acc, elem) => acc + elem.nodeName() concat ">" }
      .concat(element.nodeName())
  }

  def findMatchingElement(originFilePath: String, targetFilePath: String, elementId: String): Try[Option[MatchingElement]] = {
    def readDocument(filename: String): Try[Document] =
      Try(Jsoup.parse(Source.fromResource(filename).mkString))

    def readElement(document: Document): Option[Element] = {
      val element = document.getElementById(elementId)
      if (element == null) None else Some(element)
    }

    def findMatches(element: Element, document: Document): Seq[MatchingElement] = {
      def findMatchingAttributes(matchingElemement: Element): Seq[Attribute] = {
        element.attributes().asScala
          .foldLeft(Seq[Attribute]()) { (acc, attr) => if (matchingElemement.attributes().hasKey(attr.getKey)) acc :+ attr else acc }
      }

      for {
        matchingElement <- document.getElementsByTag(element.nodeName()).asScala
        matchingAttributes = findMatchingAttributes(matchingElement)
        score = matchingAttributes.size / element.attributes().size.toDouble
        if (score >= minScore)
      } yield MatchingElement(matchingElement, score)
    }

    readDocument(originFilePath).flatMap {
      origin => {
        readElement(origin) match {
          case Some(element) => readDocument(targetFilePath).map {
            target => {
              findMatches(element, target)
                .sortBy(_.score).reverse
                .headOption
            }
          }
          case None => Failure(throw new java.lang.RuntimeException(s"Element ${elementId} not found in origin file"))
        }
      }
    }
  }

}
