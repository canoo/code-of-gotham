# code-of-gotham
Awesome software visualization using sonar metrics and javafx

## Prerequisites
* Installation of Java SE Development Kit 8u45 or higher [download] (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Setup your own [SonarQube](http://www.sonarqube.org/screencasts2/installation-of-sonar/) or use the SonarQube example installation [http://nemo.sonarqube.org/](http://nemo.sonarqube.org/)


## Start the Software

You can use the following VM args:
 * -DcogSonarHost=<SonarURL> for example "-DcogSonarHost=http://nemo.sonarqube.org/"
 * -DcogProxy=<yourProxyServer> if you are behind a proxy


### from source

* clone the sources using git
* Run the main method of the Main class, e.g. **gradle run** or **mvn -DskipTests=true compile exec:java**

### from distribution

* download [https://jitpack.io/com/github/canoo/code-of-gotham/-SNAPSHOT/code-of-gotham--SNAPSHOT.zip](https://jitpack.io/com/github/canoo/code-of-gotham/-SNAPSHOT/code-of-gotham--SNAPSHOT.zip)
* unzip and run start script in the bin folder


