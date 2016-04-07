package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import org.jscala._
import scala.util.Random

case class CypherText(text: String)

class Cypher @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  implicit val i18n = messagesApi

  val key = Array.fill(4)(Random.nextInt())

  val js = {
    val $ = new JsDynamic {}
    org.jscala.javascript {
      val k = inject(key)
      val aes = new Aes(k)
      $("#cypherText").keyup(() => {
        val str = $("#cypherText").`val`().as[JString]
        val d = JArray[Int]()
        for (i <- 0 until str.length) d(i) = str.charCodeAt(i)
        val n = Math.round(Math.ceil(str.length / 4.0) * 4).as[Int]
        for (i <- str.length until n) d(i) = 0
        var enc = JArray[Int]()
        for (i <- 0 until n/4) {
          enc = enc.concat(aes.crypt(d.slice(i*4, i*4 + 4), false))
        }
        $("#encrypted").`html`(enc.toString())
        ()
      })
    }
  }

  val fullJs = (Aes.jscala.javascript ++ js).asString
  /**
   * Contact Form definition.
   */
  val cypherForm: Form[CypherText] = Form(
    mapping("text" -> nonEmptyText)(CypherText.apply)(CypherText.unapply)
  )

  def decrypt(t: String) = {
    val aes = new Aes(key)

    val ints = t.split(",").map(_.toInt)
    ints.sliding(4, 4).foldLeft("") {
      case (r, a) =>
        val d = aes.crypt(a, true)
        r + d.takeWhile(_ != 0).map(_.toChar).mkString
    }
  }
  
  /**
   * Display an empty form.
   */
  def form = Action { req =>
    Ok(html.cypher.cypher(cypherForm, req.flash.get("t").map(decrypt).getOrElse(""), fullJs))
  }
  
  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    cypherForm.bindFromRequest.fold(
      errors => BadRequest(html.cypher.cypher(errors, "", fullJs)),
      cypherText =>
        Redirect(routes.Cypher.form).flashing("t" -> cypherText.text)
    )
  }
  
}