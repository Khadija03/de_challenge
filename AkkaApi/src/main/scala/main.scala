import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives.{complete, path}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.server.Route
import com.datastax.oss.driver.api.core.CqlSession
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ContentTypes
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.collection.JavaConverters._
import java.net.InetSocketAddress
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import play.api.libs.json._


object Main extends App  {
  println("It works ! ")

  implicit val system: ActorSystem = ActorSystem("akkaapi")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val session = CqlSession.builder()
    .addContactPoint(InetSocketAddress.createUnresolved("cassandra", 9042))
    .withLocalDatacenter("datacenter1")
    .withAuthCredentials("cassandra", "cassandra")
    .build()
  val route = (path("data") ) {
    Directives.parameters("sku", "promo_cat","promo_discount") { (sku, promo_cat,promo_discount) =>
      Directives.get {
        Directives.complete {
          val data = session.execute(s"SELECT total_sales_units_promo_prod, units, promo_lift_percentage FROM first.mydb WHERE sku= '$sku' and promo_cat = '$promo_cat' and promo_discount = '$promo_discount' ALLOW FILTERING;")
            .all()
            .asScala.map { row =>
            Json.obj(
              "total_sales_unit_promo_prod" -> row.getLong("total_sales_units_promo_prod"),
              "units" -> row.getLong("units"),
              "promo_lift_percentage" -> row.getDouble("promo_lift_percentage")
            )
          }
          HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, Json.toJson(data).toString))

        }
      }
    }
  }
  Http().newServerAt("localhost", 8080).bind(route)
  println(s"Server online at http://localhost:8080/")

}