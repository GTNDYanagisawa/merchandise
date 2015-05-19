
java -server -Xmx1024m -Xms256m -Dsolr.solr.home=. -Dsolr.data.dir=./data -DzkRun -Dbootstrap_confdir=./conf -Dcollection.configName=hysearch -jar start.jar
