package editor;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 30.05.17.
 */
class common {

    static final String programInfo = "Das Projekt Soundboard Pi entstand im Rahmen des WadJ. Aufgabe war es, ein Soundboard zu entwerfen, welches durch drücken von Tastern verschiedene Soundeffekte ausgibt. Vergleichbar ist dies mit dem Nippelboard aus der TV Show \"TV Total\" mit Stefan Raab. Um das Soundboard möglichst unkompliziert verwalten zu können, wurde diese Software entwickelt. Hauptaufgabe dieses Programms soll es sein das Ändern und Verwalten der Soundeffekte möglichst einfach zu gestalten und den Endanwender mit so wenig Code wie möglich zu konfrontieren.\n\nIdee:\nSteven Ohad und Keno Oelrichs Garcia\n\nHardware:\nSteven Ohad\n\nSoftware:\nKeno Oelrichs García\n";

    static final String VersionInfo = "Neu in Version: 00.08\n\n" +
            "- Buttons sind dynamisch und wechseln das Bild beim drücken\n" +
            "- Die Verbindung zum SSH Gerät wird regelmäßig überprüft\n" +
            "- Die Einstellungen werden zum Programmstart geladen und überprüft\n" +
            "- Das Programm unterstützt jetzt mehrere Profile (3)\n" +
            "- Das Programm erstellt automatisch die Software für das SSH Gerät\n" +
            "- Alle benötigten Daten werden an das SSH Gerät automatisch übertragen\n" +
            "- Benutze und unbenutzte Buttons werden gekennzeichnet\n" +
            "- Die Einstellungen und Benutzerdaten werden verschlüsselt gespeichert\n" +
            "- Das SSH Gerät kann im Programm verwaltet werden\n" +
            "- Auf dem SSH Gerät können Remote Kommandos wie z.B. Updates ausgeführt werden\n" +
            "- Das Programm kann automatisch nach SSH Geräten in verbundenen Netzwerken suchen\n" +
            "- Changelog wird nach Update angezeigt\n\n" +
            "Neu in Version 00.09\n\n" +
            "Bugfixes & Optimierungen:\n" +
            "- Ein Fehler wurde behoben, der dafür sorgte, das einige grafische elemente nicht richtig angezeigt wurden\n" +
            "- Ein Fehler wurde behoben, der dafür sorgte, das gefundene Geräte nicht richtig übernommen wurden\n" +
            "- Die Netzwerkadapter übergreifende suche nach Geräten wurde verbessert" +
            "- Die Darstellung der Soundnamen auf dem SoundBoard LCD wurde verbessert"
            ;
    
    static final String python_header = "# The wiring for the LCD is as follows:\n" +
            "# 1 : GND\n" +
            "# 2 : 5V\n" +
            "# 3 : Contrast (0-5V)*\n" +
            "# 4 : RS (Register Select)\n" +
            "# 5 : R/W (Read Write)       - GROUND THIS PIN\n" +
            "# 6 : Enable or Strobe\n" +
            "# 7 : Data Bit 0             - NOT USED\n" +
            "# 8 : Data Bit 1             - NOT USED\n" +
            "# 9 : Data Bit 2             - NOT USED\n" +
            "# 10: Data Bit 3             - NOT USED\n" +
            "# 11: Data Bit 4\n" +
            "# 12: Data Bit 5\n" +
            "# 13: Data Bit 6\n" +
            "# 14: Data Bit 7\n" +
            "# 15: LCD Backlight +5V**\n" +
            "# 16: LCD Backlight GND\n" +
            "\n" +
            "#import\n" +
            "import RPi.GPIO as GPIO\n" +
            "import time\n" +
            "import smbus\n" +
            "from time import sleep\n" +
            "import pygame.mixer\n" +
            "from sys import exit\n" +
            "\n" +
            "# Define GPIO to LCD mapping\n" +
            "LCD_RS = 7\n" +
            "LCD_E  = 8\n" +
            "LCD_D4 = 25\n" +
            "LCD_D5 = 24\n" +
            "LCD_D6 = 23\n" +
            "LCD_D7 = 18\n" +
            "\n" +
            "# Define some device constants\n" +
            "LCD_WIDTH = 16    # Maximum characters per line\n" +
            "LCD_CHR = True\n" +
            "LCD_CMD = False\n" +
            "\n" +
            "LCD_LINE_1 = 0x80 # LCD RAM address for the 1st line\n" +
            "LCD_LINE_2 = 0xC0 # LCD RAM address for the 2nd line\n" +
            "\n" +
            "# Timing constants\n" +
            "E_PULSE = 0.0005\n" +
            "E_DELAY = 0.0005\n" +
            "\n" +
            "# Sound Mixer\n" +
            "pygame.mixer.init(44100, -16, 1, 1024)\n" +
            "pygame.mixer.set_num_channels(72)\n" +
            "\n" +
            "# Sound Files\n" +
            "soundChannels = [\n" +
            "  pygame.mixer.Channel(0),\n" +
            "  pygame.mixer.Channel(1),\n" +
            "  pygame.mixer.Channel(2),\n" +
            "  pygame.mixer.Channel(3),\n" +
            "  pygame.mixer.Channel(4),\n" +
            "  pygame.mixer.Channel(5),\n" +
            "  pygame.mixer.Channel(6),\n" +
            "  pygame.mixer.Channel(7),\n" +
            "  pygame.mixer.Channel(8),\n" +
            "  pygame.mixer.Channel(9),\n" +
            "  pygame.mixer.Channel(10),\n" +
            "  pygame.mixer.Channel(11),\n" +
            "  pygame.mixer.Channel(12),\n" +
            "  pygame.mixer.Channel(13),\n" +
            "  pygame.mixer.Channel(14),\n" +
            "  pygame.mixer.Channel(15),\n" +
            "  pygame.mixer.Channel(16),\n" +
            "  pygame.mixer.Channel(17),\n" +
            "  pygame.mixer.Channel(18),\n" +
            "  pygame.mixer.Channel(19),\n" +
            "  pygame.mixer.Channel(20),\n" +
            "  pygame.mixer.Channel(21),\n" +
            "  pygame.mixer.Channel(22),\n" +
            "  pygame.mixer.Channel(23),\n" +
            "  pygame.mixer.Channel(24),\n" +
            "  pygame.mixer.Channel(25),\n" +
            "  pygame.mixer.Channel(26),\n" +
            "  pygame.mixer.Channel(27),\n" +
            "  pygame.mixer.Channel(28),\n" +
            "  pygame.mixer.Channel(29),\n" +
            "  pygame.mixer.Channel(30),\n" +
            "  pygame.mixer.Channel(31),\n" +
            "  pygame.mixer.Channel(32),\n" +
            "  pygame.mixer.Channel(33),\n" +
            "  pygame.mixer.Channel(34),\n" +
            "  pygame.mixer.Channel(35),\n" +
            "  pygame.mixer.Channel(36),\n" +
            "  pygame.mixer.Channel(37),\n" +
            "  pygame.mixer.Channel(38),\n" +
            "  pygame.mixer.Channel(39),\n" +
            "  pygame.mixer.Channel(40),\n" +
            "  pygame.mixer.Channel(41),\n" +
            "  pygame.mixer.Channel(42),\n" +
            "  pygame.mixer.Channel(43),\n" +
            "  pygame.mixer.Channel(44),\n" +
            "  pygame.mixer.Channel(45),\n" +
            "  pygame.mixer.Channel(46),\n" +
            "  pygame.mixer.Channel(47),\n" +
            "  pygame.mixer.Channel(48),\n" +
            "  pygame.mixer.Channel(49),\n" +
            "  pygame.mixer.Channel(50),\n" +
            "  pygame.mixer.Channel(51),\n" +
            "  pygame.mixer.Channel(52),\n" +
            "  pygame.mixer.Channel(53),\n" +
            "  pygame.mixer.Channel(54),\n" +
            "  pygame.mixer.Channel(55),\n" +
            "  pygame.mixer.Channel(56),\n" +
            "  pygame.mixer.Channel(57),\n" +
            "  pygame.mixer.Channel(58),\n" +
            "  pygame.mixer.Channel(59),\n" +
            "  pygame.mixer.Channel(60),\n" +
            "  pygame.mixer.Channel(61),\n" +
            "  pygame.mixer.Channel(62),\n" +
            "  pygame.mixer.Channel(63),\n" +
            "  pygame.mixer.Channel(64),\n" +
            "  pygame.mixer.Channel(65),\n" +
            "  pygame.mixer.Channel(66),\n" +
            "  pygame.mixer.Channel(67),\n" +
            "  pygame.mixer.Channel(68),\n" +
            "  pygame.mixer.Channel(69),\n" +
            "  pygame.mixer.Channel(70),\n" +
            "  pygame.mixer.Channel(71)\n" +
            "]\n";
    
    static final String python_main = "bus = smbus.SMBus(1)\n" +
            "addr = 0x20\n" +
            "\n" +
            "PORTS = [0x14, 0x13]\n" +
            "OUTPUTS = [0x01, 0x02, 0x04, 0x08]\n" +
            "INPUTS = [0x01, 0x02, 0x04, 0x08, 0x10, 0x20]\n" +
            "PROOUTPUT = 0x10\n" +
            "PROINPUTS = [0x01, 0x02, 0x04]\n" +
            "PROLEDS = [0x20, 0x40, 0x80]\n" +
            "\n" +
            "def getstate(row):\n" +
            "\n" +
            "  try:\n" +
            "    read = bus.read_byte_data(addr, row)\n" +
            "  except pigpio.error as e:\n" +
            "    print \"Error: %s\"%(e)\n" +
            "\n" +
            "  return read\n" +
            "\n" +
            "def setstate(row, data):\n" +
            "  try:\n" +
            "    bus.write_byte_data(0x20, row, data)\n" +
            "  except pigpio.error as e:\n" +
            "    print \"Error: %s\"%(e)\n" +
            "  return 0\n" +
            "\n" +
            "def checkRemote():\n" +
            "    filepath = \"command.txt\"\n" +
            "    pro = \"\"\n" +
            "    index = 0\n" +
            "    if os.path.exists(filepath):\n" +
            "      datei = open(filepath)\n" +
            "      i = 0\n" +
            "      input = [\"\",\"\",\"\"]\n" +
            "      try:\n" +
            "        for line in datei:\n" +
            "          input[i] = line.rstrip()\n" +
            "          i = i + 1\n" +
            "          starten = False\n" +
            "          stoppen = False\n" +
            "          if input[0].rstrip() == \"start\":\n" +
            "            starten = True\n" +
            "          if input[0].rstrip() == \"stop\":\n" +
            "            stoppen = True\n" +
            "      except Exception:\n" +
            "        os.remove(filepath)\n" +
            "      pro = input[1].rstrip()\n" +
            "      index = input[2].rstrip()\n" +
            "      if starten == True:\n" +
            "        soundChannels[index + pro * 24].play(sounds[index + pro * 24])\n" +
            "      elif stoppen == True:\n" +
            "        soundChannels[index + pro * 24].stop()\n" +
            "      os.remove(filepath)\n" +
            "\n" +
            "\n" +
            "\n" +
            "def main():\n" +
            "  # Main program block\n" +
            "  GPIO.setwarnings(False)\n" +
            "  GPIO.setmode(GPIO.BCM)       # Use BCM GPIO numbers\n" +
            "  GPIO.setup(LCD_E, GPIO.OUT)  # E\n" +
            "  GPIO.setup(LCD_RS, GPIO.OUT) # RS\n" +
            "  GPIO.setup(LCD_D4, GPIO.OUT) # DB4\n" +
            "  GPIO.setup(LCD_D5, GPIO.OUT) # DB5\n" +
            "  GPIO.setup(LCD_D6, GPIO.OUT) # DB6\n" +
            "  GPIO.setup(LCD_D7, GPIO.OUT) # DB7\n" +
            "\n" +
            "  # Initialise display\n" +
            "  lcd_init()\n" +
            "\n" +
            "  # Send some test\n" +
            "  lcd_string(\"    Starting\",LCD_LINE_1)\n" +
            "  lcd_string(\" Soundboard Pi\",LCD_LINE_2)\n" +
            "\n" +
            "  time.sleep(3) # 3 second delay\n" +
            "\n" +
            "  print \"Soundboard Ready.\"\n" +
            "\n" +
            "  lcd_string(\"   Soundboard\", LCD_LINE_1)\n" +
            "  lcd_string(\"    FERTIG\", LCD_LINE_2)\n" +
            "\n" +
            "  PROFILE = 0\n" +
            "  while True:\n" +
            "    INDEX = 0\n" +
            "    for L in OUTPUTS:\n" +
            "      setstate(PORTS[0], L + PROLEDS[PROFILE])\n" +
            "      input = hex(getstate(PORTS[1]))\n" +
            "      for C in INPUTS:\n" +
            "        if (input == hex(C)):\n" +
            "          INDEX = getIndex(L,C, PROFILE)\n" +
            "          soundChannels[INDEX].play(sounds[INDEX])\n" +
            "          print \"Taster\", INDEX + 1, \" gedrueckt!\"\n" +
            "          print \"Spiele Sound \", soundnames[INDEX]\n" +
            "          lcd_string(\"  Spiele Sound\", LCD_LINE_1)\n" +
            "          lcd_string(soundnames[INDEX], LCD_LINE_2)\n" +
            "          setstate(PORTS[0], 0x00 + PROLEDS[PROFILE])\n" +
            "          time.sleep(1)\n" +
            "    setstate(PORTS[0], PROOUTPUT + PROLEDS[PROFILE])\n" +
            "    input = hex(getstate(PORTS[1]))\n" +
            "    if (input == hex(PROINPUTS[0])):\n" +
            "      PROFILE = 0\n" +
            "      print \"Profil 1 gewaehlt\"\n" +
            "      time.sleep(1)\n" +
            "    if (input == hex(PROINPUTS[1])):\n" +
            "      PROFILE = 1\n" +
            "      print \"Profil 2 gewaehlt\"\n" +
            "      time.sleep(1)\n" +
            "    if (input == hex(PROINPUTS[2])):\n" +
            "      PROFILE = 2\n" +
            "      print \"Profil 3 gewaehlt\"\n" +
            "      time.sleep(1)\n" +
            "    setstate(PORTS[0], 0x00 + PROLEDS[PROFILE])\n" +
            "    #checkRemote()\n" +
            "\n" +
            "def getIndex(Out,In,Pro):\n" +
            "  Index = 0\n" +
            "  if (Out == OUTPUTS[0]):\n" +
            "    Index = Index\n" +
            "  if (Out == OUTPUTS[1]):\n" +
            "    Index = Index + 6\n" +
            "  if (Out == OUTPUTS[2]):\n" +
            "    Index = Index + 12\n" +
            "  if (Out == OUTPUTS[3]):\n" +
            "    Index = Index + 18\n" +
            "\n" +
            "  if (In == INPUTS[0]):\n" +
            "    Index = Index\n" +
            "  if (In == INPUTS[1]):\n" +
            "    Index = Index + 1\n" +
            "  if (In == INPUTS[2]):\n" +
            "    Index = Index + 2\n" +
            "  if (In == INPUTS[3]):\n" +
            "    Index = Index + 3\n" +
            "  if (In == INPUTS[4]):\n" +
            "    Index = Index + 4\n" +
            "  if (In == INPUTS[5]):\n" +
            "    Index = Index + 5\n" +
            "  return Index + Pro * 24\n" +
            "\n" +
            "def lcd_init():\n" +
            "  # Initialise display\n" +
            "  lcd_byte(0x33,LCD_CMD) # 110011 Initialise\n" +
            "  lcd_byte(0x32,LCD_CMD) # 110010 Initialise\n" +
            "  lcd_byte(0x06,LCD_CMD) # 000110 Cursor move direction\n" +
            "  lcd_byte(0x0C,LCD_CMD) # 001100 Display On,Cursor Off, Blink Off\n" +
            "  lcd_byte(0x28,LCD_CMD) # 101000 Data length, number of lines, font size\n" +
            "  lcd_byte(0x01,LCD_CMD) # 000001 Clear display\n" +
            "  time.sleep(E_DELAY)\n" +
            "\n" +
            "def lcd_byte(bits, mode):\n" +
            "  # Send byte to data pins\n" +
            "  # bits = data\n" +
            "  # mode = True  for character\n" +
            "  #        False for command\n" +
            "\n" +
            "  GPIO.output(LCD_RS, mode) # RS\n" +
            "\n" +
            "  # High bits\n" +
            "  GPIO.output(LCD_D4, False)\n" +
            "  GPIO.output(LCD_D5, False)\n" +
            "  GPIO.output(LCD_D6, False)\n" +
            "  GPIO.output(LCD_D7, False)\n" +
            "  if bits&0x10==0x10:\n" +
            "    GPIO.output(LCD_D4, True)\n" +
            "  if bits&0x20==0x20:\n" +
            "    GPIO.output(LCD_D5, True)\n" +
            "  if bits&0x40==0x40:\n" +
            "    GPIO.output(LCD_D6, True)\n" +
            "  if bits&0x80==0x80:\n" +
            "    GPIO.output(LCD_D7, True)\n" +
            "\n" +
            "  # Toggle 'Enable' pin\n" +
            "  lcd_toggle_enable()\n" +
            "\n" +
            "  # Low bits\n" +
            "  GPIO.output(LCD_D4, False)\n" +
            "  GPIO.output(LCD_D5, False)\n" +
            "  GPIO.output(LCD_D6, False)\n" +
            "  GPIO.output(LCD_D7, False)\n" +
            "  if bits&0x01==0x01:\n" +
            "    GPIO.output(LCD_D4, True)\n" +
            "  if bits&0x02==0x02:\n" +
            "    GPIO.output(LCD_D5, True)\n" +
            "  if bits&0x04==0x04:\n" +
            "    GPIO.output(LCD_D6, True)\n" +
            "  if bits&0x08==0x08:\n" +
            "    GPIO.output(LCD_D7, True)\n" +
            "\n" +
            "  # Toggle 'Enable' pin\n" +
            "  lcd_toggle_enable()\n" +
            "\n" +
            "def lcd_toggle_enable():\n" +
            "  # Toggle enable\n" +
            "  time.sleep(E_DELAY)\n" +
            "  GPIO.output(LCD_E, True)\n" +
            "  time.sleep(E_PULSE)\n" +
            "  GPIO.output(LCD_E, False)\n" +
            "  time.sleep(E_DELAY)\n" +
            "\n" +
            "def lcd_string(message,line):\n" +
            "  # Send string to display\n" +
            "\n" +
            "  message = message.ljust(LCD_WIDTH,\" \")\n" +
            "\n" +
            "  lcd_byte(line, LCD_CMD)\n" +
            "\n" +
            "  for i in range(LCD_WIDTH):\n" +
            "    lcd_byte(ord(message[i]),LCD_CHR)\n" +
            "\n" +
            "if __name__ == '__main__':\n" +
            "\n" +
            "  try:\n" +
            "    main()\n" +
            "  except KeyboardInterrupt:\n" +
            "    pass\n" +
            "  finally:\n" +
            "    lcd_byte(0x01, LCD_CMD)\n" +
            "    GPIO.cleanup()";
}
