import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Success}

object Main extends App with StrictLogging {

  if (args.length < 3)
    throw new IllegalArgumentException("Origin filepath, target filepath and element id should be provided as arguments")

  val origin = args(0)
  val target = args(1)
  val elementId = args(2)


  SmartXMLParser.findMatchingElement(origin, target, elementId) match {
    case Success(Some(element)) => logger.info(s"Matching element: ${element.path}")
    case Success(None) => logger.info(s"No matching element was found for '${elementId}'")
    case Failure(exception) => logger.error("Error finding match", exception)
  }

}
