package controllers

import play.api.mvc.{Action, Controller, Call}
import models.Story
import play.api.libs.json._

/**
 *
 */
object StoryController extends Controller {

  def list = Action {

    Ok(Json.prettyPrint(Json.toJson(Story.findAll)))

  }

  def findByKey(key: String) = Action {

    Story.findByKey(key).map {
      story => Ok(Json.prettyPrint(Json.toJson(story)))
    }.getOrElse(NotFound)

  }

  def create = Action { implicit request =>

    Json.fromJson[Story](request.body.asJson.get).map {
      story => {
        val key = Story.createOrUpdate(story)
        val call: Call = routes.StoryController.findByKey(key)
        Ok.withHeaders(("Location",  call.absoluteURL()))
      }
    }.getOrElse(BadRequest)

  }

  def delete(key: String) =  Action { implicit request =>

    if (Story.delete(key)) Ok else NotFound

  }

}
