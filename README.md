# Salamanka (Minecraft 1.20.1, Forge 47.2.0)

Как собрать .jar:
1. Залей содержимое этой папки в корень репозитория на GitHub.
2. Вкладка Actions -> "Build mod" (запускается сам на push, или Run workflow).
3. Готовый .jar скачай из Artifacts (salamanka-mod). Это файл без суффикса "-sources".
4. Положи .jar в папку mods (Minecraft 1.20.1 + Forge 47.x).

Локально: `gradle build` (Gradle 8.1.1, Java 17), jar будет в build/libs/.

Проверка в игре:
- Креатив -> вкладка Salamanka: яйцо призыва, кока 1/3/5, маузер, пластинка, блок коки.
- Найти ангар: `/locate structure salamanka:hangar`
- Тестировать агр в выживании (в креативе выстрелы не наносят урон).
