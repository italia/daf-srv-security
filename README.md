# Security Manager for Piattaforma Digitale Nazionale Dati (PDND), previously DAF

Security Manager is the microservice responsable to manage security in PDND (ex DAF) environment.

## What is the PDND (ex DAF)?

PDND stays for "Piattaforma Digitale Nazionale Dati" (the Italian Digital Data Platform), previously known as Data & Analytics Framework (DAF).

You can find more informations about the PDND on the official [Digital Transformation Team website](https://teamdigitale.governo.it/it/projects/daf.htm).

## What is Security Manager?

Security Manager performes a set of REST API to manage users, organizations, internal groups of the organization and data permissions.

This project provides Single Sign-On functionality and propagates changes to the users and the organizations in all tools. 

In the file project-path/daf-srv-security/conf/security_manager.yaml the API are described in detail with the standard swagger 2.0.

The `master` branch refers to the code available for the production release. 
All the development starts from `dev` branch working on feature branches and merged first on 
dev and after testing and reviewing can be merged on master.

### Tools references

This project references the following tools.

* [FreeIPA](https://www.freeipa.org/page/Main_Page)
* [HDFS](https://hadoop.apache.org/docs/r1.2.1/hdfs_design.html)
* [Impala](https://hadoop.apache.org/docs/r1.2.1/hdfs_design.html)
* [Superset](https://github.com/apache/incubator-superset)

### Project components 

This project depends by the following components.

* **Scala** version 2.11.11, available [here](https://www.scala-lang.org/download/all.html)
* **SBT** version 0.13.16, available [here](https://www.scala-sbt.org/download.html)


### Related PDND component:

* **daf-util-common** available [here](https://github.com/italia/daf-util-common).

## How to install and use Security Manager

If you don't have access to PDND environment, follow this step, otherwise skip this section

### Publish common in your pc

Clone the project from git

```
git clone https://github.com/italia/daf-util-common
```

Go to directory of common library in your pc. Open sbt shell and run following command to compile the library 
```
clean
compile
```

Run the command in sbt shell to publish the library
```
publishLocal
```

### Clone the project

``
git clone https://github.com/italia/daf-srv-security.git
``

## Configure the project without access to the PDND tools

Open file `build.sbt` and comment the following lines:
```
import Environment._
```
and
```
"daf repo" at s"$nexusUrl/maven-public/"
```
**NOTE**: You need to also comment the comma in the previous line.

Open file `Filters.scala` and comment the class ***Filters***.

Open file `security_manager.yaml` (don't warry about all errors) and comment
```
val playSessionStore: PlaySessionStore 
```
and
```
Authentication(configuration, playSessionStore)
```

Open file  `application.conf` and comment
```
play.modules.enabled += "it.gov.daf.common.modules.authentication.SecurityModule"
```

## Configure the project with access to the PDND tools

In the directory `project` you need to create Scala object like this:

```
object Environment {

  val nexus = "{NEXUS_URL}"
  val nexusPort = "{NEXUS_PORT}"
  val nexusUrl = s"http://$nexus:$nexusPort/repository/"

  val deployEnv = "{ENV}"
}
```

## Run the app

Open the sbt shell in your project directory and run the following commands
```
clean
compile
run -Dconfig.file={PATH_LOCAL_PROJECT}/daf-srv-security/conf/prod/prodBase.conf
```

## How to build and test Security Manager

<p>There's two ways to test the project.</p></br>
<p>The first (and simple one) run the server on your local machine via sbt shell</p>

```
run
```

The second is based on Docker (you can read more on the official [web page](https://docs.docker.com/get-started/)). Configure in your `build.sbt` Docker settings and run the following command in sbt shell
```
clean
compile
docker:publish
```

<p>Run your docker image</br></p>

If you want see all the API exposed by the server go to `{PATH_RUNNING_SERVER}/security-manager` 


## How to contribute

Contributions are welcome. Feel free to [open issues](./issues) and submit a [pull request](./pulls) at any time, but please read [our handbook](https://github.com/teamdigitale/daf-handbook) first.

## License

Copyright (c) 2019 Presidenza del Consiglio dei Ministri

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
