package controllers

import javax.inject.Inject

import it.gov.daf.common.sso.common.{CacheWrapper, SecuredInvocationManager}
import it.gov.daf.common.utils.RequestContext.execInContext
import it.gov.daf.securitymanager.service.readLoginInfo
import it.gov.daf.securitymanager.utilities.ConfigReader
import it.gov.daf.sso.LoginClientLocal
import play.api.Logger
import play.api.http.HttpEntity
import play.api.libs.json.JsNull
import play.api.libs.ws.{StreamedBody, StreamedResponse, WSClient, WSResponse}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


class RestServiceProxyController @Inject()(secInvokeManager: SecuredInvocationManager, implicit val cacheWrapper:CacheWrapper) extends Controller {


  private val logger = Logger(this.getClass.getName)


  def proxy(service:String,path:String) = Action.async { implicit request =>

    execInContext[Future[Result]](s"proxy $path") { () =>

      logger.debug("Request:" + request)

      val queryString = request.queryString.map { case (k, v) => k -> v.mkString }.toList
      val headers = request.headers.toSimpleMap.toList
      val body = request.body.asJson.getOrElse(JsNull)


      def serviceInvoke(cookie: String, wsClient: WSClient): Future[StreamedResponse] = {
        // fixed url for now
        wsClient.url(s"${ConfigReader.livyUrl}/$path").withMethod(request.method)
          .withHeaders(headers: _*)
          .withHeaders("Cookie" -> cookie, "Host" -> ConfigReader.livyUrl)
          .withQueryString(queryString: _*)
          .withBody(body)
          .stream
      }


      secInvokeManager.manageServiceCall(readLoginInfo(LoginClientLocal.LIVY), serviceInvoke).map { response =>

        val headers = response.headers.headers.map(m => (m._1,m._2.head))
        val contentType = headers.getOrElse("Content-Type","no_content_type")
        val responseHeader = headers.filterNot(_._1=="Content-Type").toList

        val contentLenght = headers.get("Content-Length")
        val status = new Status(response.headers.status)

        contentLenght match {
            case Some(length) => status.sendEntity( HttpEntity.Streamed(response.body, Some(length.toLong), None) ).withHeaders(responseHeader:_*).as(contentType)
            case _ => status.chunked(response.body).withHeaders(responseHeader:_*).as(contentType)
        }


      }

    }
  }

}