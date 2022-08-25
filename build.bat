call ./gradlew jar
call copy ".\build\libs\modid-1.0.jar" "..\Mechanimation-1.12.2\libs\CraftLoader.jar"
call copy ".\build\libs\modid-1.0.jar" "..\1.12.2\libs\CraftLoader.jar"
call del ".\build\libs\1.12.2 CraftLoader %~1 - dev.jar"
call rename ".\build\libs\modid-1.0.jar" "1.12.2 CraftLoader %~1 - dev.jar"
call ./gradlew build
call del ".\build\libs\modid-1.0-sources.jar"
call del ".\build\libs\1.12.2 CraftLoader %~1.jar"
call rename ".\build\libs\modid-1.0.jar" "1.12.2 CraftLoader %~1.jar"