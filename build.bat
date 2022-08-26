call ./gradlew jar
call del ".\build\libs\custombgm-%~1-dev.jar"
call rename ".\build\libs\custombgm-%~1.jar" "custombgm-%~1-dev.jar"
call ./gradlew build