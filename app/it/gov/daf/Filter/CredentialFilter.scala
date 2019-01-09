package it.gov.daf.Filter


import javax.inject.Inject

import akka.stream.Materializer
import it.gov.daf.common.sso.common.{CacheWrapper, CredentialManager}
import it.gov.daf.common.utils._
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class CredentialFilter@Inject() (implicit val mat: Materializer, ec: ExecutionContext, cacheWrapper: CacheWrapper) extends Filter {

  private val logger = Logger(this.getClass.getName)

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    logger.debug("in credential filter")
    if( requestHeader.headers.get("authorization").nonEmpty ) {

      logger.debug("authorization header present")

      val credentials = CredentialManager.readCredentialFromRequest(requestHeader)
      credentials match {
        case Credentials(u,p,_) =>  logger.debug(s"caching $u")
                                    cacheWrapper.deleteCredentials(u)
                                    cacheWrapper.putCredentials(u,p)

        case Profile(u,t,_) => cacheToken(u,t)

        case _ => logger.debug("credential not cached")
      }

    }

    nextFilter(requestHeader)

  }

  private def cacheToken(user:String,token:String):Unit={

    cacheWrapper.getCredentials(token) match{
      case Some(_) => logger.debug("credential not cached")
      case None =>    logger.debug(s"caching token of $user")
                      cacheWrapper.putCredentials(token,user)
    }

  }



}