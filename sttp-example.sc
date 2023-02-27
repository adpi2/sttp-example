import sttp.client3.*
import sttp.client3.upicklejson.*
import upickle.default.*

case class Project(organization: String, repository: String, description: String) derives ReadWriter

val baseUrl = "https://index.scala-lang.org"
val backend = HttpClientSyncBackend()

def getProjects(query: Option[String], languages: Seq[String], platforms: Seq[String]) =
  val q = query.getOrElse("*")
  basicRequest
    .get(uri"$baseUrl/api/autocomplete?q=$q&languages=$languages&platforms=$platforms")
    .response(asJsonAlways[Seq[Project]])
    .mapResponse(_.toOption.get)

val request = getProjects(None, Seq.empty, Seq.empty)
request.send(backend).body.foreach(p => println(p.repository))

// scribe.Logger.root
//   .clearHandlers()
//   .clearModifiers()
//   .withHandler(minimumLevel = Some(scribe.Level.Debug))
//   .replace()
