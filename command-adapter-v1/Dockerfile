FROM openjdk:8-jdk

COPY target/*.jar /opt/

CMD java $(echo $JAVA_OPTIONS) -jar $(ls /opt/*.jar | grep -v original  | grep -v javadoc | grep -v sources)
