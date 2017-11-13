
all:
	cp modules/gradle-clojure-plugin/build/libs/`ls -t modules/gradle-clojure-plugin/build/libs/ | grep -v javadoc | grep -v sources | head -n 1` ./plugin.jar
