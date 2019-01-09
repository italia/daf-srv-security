let config =  /usr/local/var/dhall-kubernetes/api/Deployment/default
  ⫽ { name = "security-manager"
  , hostAliases = /env/hostaliases
  , secretSimpleVolumes = [{ name = "security-manager-cert-volume", secretName = "security-manager-cert" }]
  , configMapVolumes = ./configMapVolumes
  , containers =
                [   /usr/local/var/dhall-kubernetes/api/Deployment/defaultContainer
                  ⫽ /env/nexus
                  ⫽ { name = "security-manager"
                    , imageName = "daf-security-manager"
                    , imageTag = "2.0.1-SNAPSHOT"
                    , memory = 3000
                    , port = [ 9000 ] : Optional Natural
                    , mounts = ./volumeMounts
                    , simpleEnvVars = [ { mapKey = "JAVA_OPTS", mapValue = "-server -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+PerfDisableSharedMem -XX:+ParallelRefProcEnabled -Xmx2g -Xms2g -XX:MaxPermSize=1024m" }
                    , { mapKey = "KRB5_CONFIG", mapValue = "/etc/extKerberosConfig/krb5.conf" } ]
                    , secretEnvVars = ./secretEnvVars
                    }
                ]
            }

in   /usr/local/var/dhall-kubernetes/api/Deployment/mkDeployment config
