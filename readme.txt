Плагин предоставляет несколько простых команд, в основном для разработки и тестирования

[color=purple]/gt hasperm [player] perm[/color] - проверить если ли у игрока пермишен, если игрок не указан - у себя
[color=purple]/gt loc[/color] - текущая позиция с Yaw и Pitch
[color=purple]/gt chunk <block_x> <block_z> [world][/color] - проверить загружен ли чанк по x, z. Мир нужно указывать при запуске из консоли
[color=purple]/gt event <название класса-события>[/color] - посмотреть список обработчиков для события. Класс нужно указывать полностью, с пакетом, например [b]org.bukkit.event.block.BlockBreakEvent[/b]
[color=purple]/gt id[/color] - показать id и data предмета в руке
[color=purple]/gt tp <player> <world> <x> <y> <z> <pitch> <yaw>[/color] - телепорт указанного игрока в координаты (с pitch и yaw)

[color=purple]/sayas &1Test Name | &5Test Message[/color] - сказать от лица кого-то, будет видно всем игрокам. Можно использовать цвета

[color=purple]/pcmd <player> <command>[/color] - выполнить команду от указанного игрока. Полезно для CommandBlock