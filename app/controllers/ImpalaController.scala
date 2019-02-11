package controllers

import javax.inject.Inject

import it.gov.daf.common.utils.RequestContext
import it.gov.daf.common.utils.RequestContext.execInContext
import it.gov.daf.securitymanager.service.{ImpalaService, ProfilingService, WebHDFSApiClient, WebHDFSApiProxy}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


class ImpalaController @Inject()(webHDFSApiClient:WebHDFSApiClient,impalaService: ImpalaService) extends Controller {


  private val logger = Logger(this.getClass.getName)


  def createImpalaTableFromFile(path:String, schemaName:String, tableName:String) = Action.async { implicit request =>
    execInContext[Future[Result]]("createImpalaTableFromFile") { () =>

      val absPath="/"+path
      //val queryString: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }

      webHDFSApiClient.getParquetFileNameInFolder(path).map {
        case Right(Some(fileName)) => Try{impalaService.createTableFromParquet(absPath, fileName, schemaName, tableName) } match {
          case Success(_) => Ok(s"""{"message":"table created"}""")
          case Failure(e) => Logger.error("error in createTableFromParquet"+e.getMessage, e); InternalServerError(s"""{"message":"${e.getMessage}}""")
        }
        case Right(None) => NotFound(s"""{"message":"no parquet files under $absPath"}""")
        case Left(l) => new Status(l.httpCode).apply(l.jsValue.toString())
      }
    }
  }

  def createImpalaGrant(schemaName:String, tableName:String) = Action { implicit request =>

    execInContext[Result]("createImpalaGrant") { () =>

      /*
      val json = request.body.asJson.get
      val schemaName = (json \ "schemaName").as[String]
      val tableName = (json \ "tableName").as[String]
      val sbjName = (json \ "subjectName").as[String]
      val isUser = (json \ "isUser").as[Boolean]
      */
      //val tableName = profilingService.toTableName(path)

      val userName = RequestContext.getUsername()
      impalaService.createImpalaGrant(schemaName+"."+tableName, userName, "rwx", true, true) match {
        case Right(r) => Ok(r.toString)
        case Left(l) => InternalServerError(l.toString)
      }
    }

  }



}