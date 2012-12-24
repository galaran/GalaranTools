GalaranTools
============

Плагин предоставляет несколько простых команд, в основном для разработки и тестирования

### Perm: gtools.main
`/gt hasperm [player] perm` - проверить если ли у игрока пермишен, если игрок не указан - у себя  
`/gt loc` - текущая позиция с Yaw и Pitch  
`/gt chunk <block_x> <block_z> [world]` - проверить загружен ли чанк по x, z. Мир нужно указывать при запуске из консоли  
`/gt event <название класса-события>` - посмотреть список обработчиков для события. Класс нужно указывать полностью, например **org.bukkit.event.block.BlockBreakEvent**  
`/gt id` - показать id и data предмета в руке  
`/gt tp <player> <world> <x> <y> <z> <pitch> <yaw>` - телепорт указанного игрока в координаты (с pitch и yaw)  
`/gt addlore <lore_line>` - добавить строку текста (lore) для предмета в руке. Можно использовать цвета  

### Perm: gtools.sayas
`/sayas &1Test Name | &5Test Message` - сказать от лица кого-то, будет видно всем игрокам. Можно использовать цвета  

### Perm: gtools.playercmd
`/pcmd <player> <command>` - выполнить команду от указанного игрока. Полезно для CommandBlock  

Download
========

Версия Bukkit: 1.4.6-R0.1  
https://dl.dropbox.com/u/14150510/dd/mccity/GalaranTools.jar