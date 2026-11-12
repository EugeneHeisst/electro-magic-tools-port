# Electro-Magic Tools UNOFFICIAL PORT

Неофициальный порт **Electro-Magic Tools 1.2.6.2** (fewizz, TheSilentium; оригинальный проект Tombenpotter) на **Minecraft 1.21.1 / NeoForge 21.1.256**. Текущая версия: **0.1.15**.

Солнечные панели и генераторы эссенции, зарядка жезлов, эфирный процессор, инструменты и крылья, броня и её модули, кольца, фокусы жезлов, переносимые узлы и электрические облака. Используются штатные энергосеть IC2, исследования, рецепты, аспекты и API Thaumcraft. Есть русская и английская локализации.

## Совместимость

**Sodium и Iris не поддерживаются**: с ними возможны визуальные баги блоков, моделей, эффектов и интерфейса. Перед отправкой отчёта о такой проблеме проверьте игру без этих модов.

## Сборка

Нужны **Java 21**, **IndustrialCraft 2 UNOFFICIAL PORT 0.1.23 или новее** и **Thaumcraft UNOFFICIAL PORT 0.17.178 или новее**. Зависимости устанавливаются из JAR соответствующих релизов:

| Зависимость | Путь по умолчанию | Gradle property | Переменная окружения |
|---|---|---|---|
| IC2 | `libs/industrialcraft2-modern-0.1.23.jar` | `ic2LocalJar` | `IC2_JAR` |
| Thaumcraft | `libs/tc4modern-0.17.178.jar` | `tc4LocalJar` | `TC4_JAR` |
| ASP, необязательно для среды разработки | `libs/advanced-solar-panels-modern-0.1.2.jar` | `aspLocalJar` | `ASP_JAR` |

Версии ожидаемых файлов задаются в `gradle.properties`. ASP подключается в среду разработки только если указан соответствующий путь или JAR существует в `libs/`; для сборки и обычной работы EMT он не требуется. Например, произвольные имена файлов можно указать так:

```sh
./gradlew build -Pic2LocalJar=libs/ic2.jar -Ptc4LocalJar=libs/thaumcraft.jar
```

С файлами по умолчанию достаточно `./gradlew build`. Результат: `build/libs/electro-magic-tools-modern-0.1.15.jar`. Все сгенерированные игровые ресурсы включены в репозиторий.

JEI необязателен в игре. При сборке его API загружается из официального Maven-репозитория BlameJared; `-PwithJei=true` добавляет JEI в среду разработки. Для сборки без скачивания JEI можно указать `-PjeiLocalJar=libs/jei.jar` или переменную `JEI_JAR`. Версия Maven-зависимости задаётся свойством `jei_version`.

На Linux можно использовать `./build.sh`: скрипт сохраняет выбранный `JAVA_HOME`. На Windows используйте `gradlew.bat build` с Java 21. Локальные JAR в `libs/` не добавляются в Git.

## Проверки

`JAVA_TOOL_OPTIONS=-Demt.audit=server ./gradlew runServer` запускает серверные проверки машин, инструментов, брони и интеграций. Для серверных проверок требуется принять Minecraft EULA.

`./audit-client.sh -PwithJei=true` запускает клиентские проверки в Xvfb без звука; нужен подготовленный тестовый мир `EMTAudit` в `run-audit/saves/`. `./mp-flight-audit.sh` проверяет полёт в настоящем соединении клиента и выделенного сервера с `allow-flight=false`. Клиентские проверки требуют Xvfb и OpenAL. Результаты создаются в локальных папках `run-*`.
