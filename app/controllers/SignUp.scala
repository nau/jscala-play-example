package controllers

import javax.inject.Inject

import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._

import models._
import org.jscala._

object Validation {

  @Javascript(debug = false) class Validator {
    def validate(username: String) = {
      username.length > 3
    }
  }
  object Validator
}

class SignUp @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  implicit val i18n = messagesApi
  import Validation._
  val v = new Validator
  val validation = Validation.Validator.javascript ++ org.jscala.javascript {
    val v = new Validator()
    def validate(form: Map[String, JsDynamic]) = v.validate(form("username").value.as[JString])
  } asString
  /**
   * Sign Up Form definition.
   *
   * Once defined it handle automatically, ,
   * validation, submission, errors, redisplaying, ...
   */
  val signupForm: Form[User] = Form(
    
    // Define a mapping that will handle User values
    mapping(
      "username" -> text().verifying(v.validate(_)),
      "email" -> email
    )
    // The mapping signature doesn't match the User case class signature,
    // so we have to define custom binding/unbinding functions
    {
      // Binding: Create a User from the mapping result (ignore the second password and the accept field)
      (username, email) => User(username, email)
    } 
    {
      // Unbinding: Create the mapping values from an existing User value
      user => Some(user.username, user.email)
    }.verifying(
      // Add an additional constraint: The username must not be taken (you could do an SQL request here)
      "This username is not available",
      user => !Seq("admin", "guest").contains(user.username)
    )
  )
  
  /**
   * Display an empty form.
   */
  def form = Action {
    import org.jscala._
    Ok(html.signup.form(signupForm, validation))
  }

  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      errors => BadRequest(html.signup.form(errors, validation)),
      // We got a valid User value, display the summary
      user => Ok(html.signup.summary(user))
    )
  }
  
}