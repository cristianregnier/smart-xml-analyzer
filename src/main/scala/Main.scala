import com.typesafe.scalalogging.StrictLogging

object Main extends App with StrictLogging {

  if (args.length < 3)
    throw new IllegalArgumentException("Origin filepath, target filepath and element id should be provided as arguments")

  val origin = args(0)
  val target = args(1)
  val elementId = args(2)


}
