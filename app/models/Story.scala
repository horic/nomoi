package models

import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.JsObject


import anorm._
import anorm.SqlParser._
import play.api.db.DB
import scala.language.postfixOps

/**
 * Model class for a user story
 * @param id
 * @param key
 * @param name
 * @param narrative
 * @param ready
 * @param version
 */
case class Story(id: Integer, key: String, name: String, narrative: String, ready: Boolean, version: Integer) {

  def this(key: String, name: String, narrative: String, ready: Boolean) = this(null, key, name, narrative, ready, 0)

}

/**
 * Compagnion object for story's business logic and utilities
 */
object Story {


  /**
   * ********************** Crud operations
   */


  def findAll: Seq[Story] = {

    DB.withConnection(implicit connection =>
      SQL("SELECT * FROM story").as(Story.simple *)
    )

  }


  def findByKey(key: String): Option[Story] = DB.withConnection {
    implicit connection =>

      SQL("SELECT * FROM story WHERE `key` = {key}").on('key -> key).as(Story.simple *).headOption

  }


  def createOrUpdate(story: Story): String = DB.withTransaction {
    implicit connection =>

      SQL(
        """
        INSERT INTO story (`key`, `name`, `narrative`, `is_ready`, `version`)
        VALUES ({key}, {name}, {narrative}, {ready}, {version})
        ON DUPLICATE KEY UPDATE `name` = {name}, `narrative` = {narrative}, `is_ready` = {ready}, `version` = {version};
        """)
        .on(
        'key -> story.key,
        'name -> story.name,
        'narrative -> story.narrative,
        'ready -> story.ready,
        'version -> story.version
      ).executeUpdate()
      story.key

  }

  def delete(key: String): Boolean = DB.withTransaction {
    implicit connection =>

      SQL("DELETE FROM story WHERE `key` = {key}").on('key -> key).executeUpdate() > 0

  }


  /**
   * simple row parser for mapping story queries to story objects
   */
  val simple = {
    get[Int]("story.id") ~
      get[String]("story.key") ~
      get[String]("story.name") ~
      get[String]("story.narrative") ~
      get[Boolean]("story.is_ready") ~
      get[Int]("story.version") map {
      case id ~ key ~ name ~ narrative ~ is_ready ~ version => Story(id, key, name, narrative, is_ready, version)
    }
  }

  /**
   * Json formatter
   */
  implicit object StoryFormat extends Format[Story] {

    def reads(json: JsValue): JsResult[Story] = JsSuccess[Story](
      new Story(
        (json \ "key").as[String],
        (json \ "name").as[String],
        (json \ "narrative").as[String],
        (json \ "isReady").as[Boolean]
      ),
      null
    )


    def writes(s: Story): JsValue = JsObject(List(
      "key" -> JsString(s.key),
      "name" -> JsString(s.name),
      "narrative" -> JsString(s.narrative),
      "name" -> JsString(s.name),
      "isReady" -> JsBoolean(s.ready)))
  }

}