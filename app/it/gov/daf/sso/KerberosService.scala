package it.gov.daf.sso

  sealed abstract class KerberosService(login_ulr: String)  {
    def loginUrl=login_ulr
  }

  case object HdfsService extends KerberosService("/webhdfs/v1/daf?op=GETFILESTATUS")
  case object LivyService extends KerberosService("/ui")



