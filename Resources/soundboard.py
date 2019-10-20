# The wiring for the LCD is as follows:
# 1 : GND
# 2 : 5V
# 3 : Contrast (0-5V)*
# 4 : RS (Register Select)
# 5 : R/W (Read Write)       - GROUND THIS PIN
# 6 : Enable or Strobe
# 7 : Data Bit 0             - NOT USED
# 8 : Data Bit 1             - NOT USED
# 9 : Data Bit 2             - NOT USED
# 10: Data Bit 3             - NOT USED
# 11: Data Bit 4
# 12: Data Bit 5
# 13: Data Bit 6
# 14: Data Bit 7
# 15: LCD Backlight +5V**
# 16: LCD Backlight GND

#import
import RPi.GPIO as GPIO
import time
import smbus
from time import sleep
import pygame.mixer
from sys import exit

# Define GPIO to LCD mapping
LCD_RS = 7
LCD_E  = 8
LCD_D4 = 25
LCD_D5 = 24
LCD_D6 = 23
LCD_D7 = 18

# Define some device constants
LCD_WIDTH = 16    # Maximum characters per line
LCD_CHR = True
LCD_CMD = False

LCD_LINE_1 = 0x80 # LCD RAM address for the 1st line
LCD_LINE_2 = 0xC0 # LCD RAM address for the 2nd line

# Timing constants
E_PULSE = 0.0005
E_DELAY = 0.0005

# Sound Mixer
pygame.mixer.init(44100, -16, 1, 1024)
pygame.mixer.set_num_channels(72)

# Sound Files
soundChannels = [
  pygame.mixer.Channel(0),
  pygame.mixer.Channel(1),
  pygame.mixer.Channel(2),
  pygame.mixer.Channel(3),
  pygame.mixer.Channel(4),
  pygame.mixer.Channel(5),
  pygame.mixer.Channel(6),
  pygame.mixer.Channel(7),
  pygame.mixer.Channel(8),
  pygame.mixer.Channel(9),
  pygame.mixer.Channel(10),
  pygame.mixer.Channel(11),
  pygame.mixer.Channel(12),
  pygame.mixer.Channel(13),
  pygame.mixer.Channel(14),
  pygame.mixer.Channel(15),
  pygame.mixer.Channel(16),
  pygame.mixer.Channel(17),
  pygame.mixer.Channel(18),
  pygame.mixer.Channel(19),
  pygame.mixer.Channel(20),
  pygame.mixer.Channel(21),
  pygame.mixer.Channel(22),
  pygame.mixer.Channel(23),
  pygame.mixer.Channel(24),
  pygame.mixer.Channel(25),
  pygame.mixer.Channel(26),
  pygame.mixer.Channel(27),
  pygame.mixer.Channel(28),
  pygame.mixer.Channel(29),
  pygame.mixer.Channel(30),
  pygame.mixer.Channel(31),
  pygame.mixer.Channel(32),
  pygame.mixer.Channel(33),
  pygame.mixer.Channel(34),
  pygame.mixer.Channel(35),
  pygame.mixer.Channel(36),
  pygame.mixer.Channel(37),
  pygame.mixer.Channel(38),
  pygame.mixer.Channel(39),
  pygame.mixer.Channel(40),
  pygame.mixer.Channel(41),
  pygame.mixer.Channel(42),
  pygame.mixer.Channel(43),
  pygame.mixer.Channel(44),
  pygame.mixer.Channel(45),
  pygame.mixer.Channel(46),
  pygame.mixer.Channel(47),
  pygame.mixer.Channel(48),
  pygame.mixer.Channel(49),
  pygame.mixer.Channel(50),
  pygame.mixer.Channel(51),
  pygame.mixer.Channel(52),
  pygame.mixer.Channel(53),
  pygame.mixer.Channel(54),
  pygame.mixer.Channel(55),
  pygame.mixer.Channel(56),
  pygame.mixer.Channel(57),
  pygame.mixer.Channel(58),
  pygame.mixer.Channel(59),
  pygame.mixer.Channel(60),
  pygame.mixer.Channel(61),
  pygame.mixer.Channel(62),
  pygame.mixer.Channel(63),
  pygame.mixer.Channel(64),
  pygame.mixer.Channel(65),
  pygame.mixer.Channel(66),
  pygame.mixer.Channel(67),
  pygame.mixer.Channel(68),
  pygame.mixer.Channel(69),
  pygame.mixer.Channel(70),
  pygame.mixer.Channel(71)
]

sounds = [
  pygame.mixer.Sound("DrSchaedel_Lachen.wav"),
  pygame.mixer.Sound("Mathemann_Limes.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("CantinaBand_Text.wav"),
  pygame.mixer.Sound("CantinaBand.wav"),
  pygame.mixer.Sound("Bibi.wav"),
  pygame.mixer.Sound("dubgun_drop.wav"),
  pygame.mixer.Sound("dubgun_loop.wav"),
  pygame.mixer.Sound("POW.wav"),
  pygame.mixer.Sound("Monsterkill.wav"),
  pygame.mixer.Sound("Kann nicht Reden.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("Sound1.wav"),
  pygame.mixer.Sound("Sound2.wav"),
  pygame.mixer.Sound("Sound3.wav"),
  pygame.mixer.Sound("Sound4.wav"),
  pygame.mixer.Sound("Sound5.wav"),
  pygame.mixer.Sound("Sound6.wav"),
  pygame.mixer.Sound("Sound7.wav"),
  pygame.mixer.Sound("Sound8.wav"),
  pygame.mixer.Sound("Sound9.wav"),
  pygame.mixer.Sound("Sound10.wav"),
  pygame.mixer.Sound("Sound11.wav"),
  pygame.mixer.Sound("Sound12.wav"),
  pygame.mixer.Sound("Sound13.wav"),
  pygame.mixer.Sound("Sound14.wav"),
  pygame.mixer.Sound("Sound15.wav"),
  pygame.mixer.Sound("Sound16.wav"),
  pygame.mixer.Sound("Sound17.wav"),
  pygame.mixer.Sound("Sound18.wav"),
  pygame.mixer.Sound("Sound19.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("Ich bin eine Biene.wav"),
  pygame.mixer.Sound("Du wirst Gefickt.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav"),
  pygame.mixer.Sound("PLACEHOLDER.wav")
]
soundnames = [
  "DrSchaedel_Lachen",
  "Mathemann_Limes",
  "  PLACEHOLDER",
  "CantinaBand_Text",
  "  CantinaBand",
  "      Bibi",
  "  dubgun_drop",
  "  dubgun_loop",
  "      POW",
  "  Monsterkill",
  "Kann nicht Reden",
  "  PLACEHOLDER",
  "     Sound1",
  "     Sound2",
  "     Sound3",
  "     Sound4",
  "     Sound5",
  "     Sound6",
  "     Sound7",
  "     Sound8",
  "     Sound9",
  "    Sound10",
  "    Sound11",
  "    Sound12",
  "    Sound13",
  "    Sound14",
  "    Sound15",
  "    Sound16",
  "    Sound17",
  "    Sound18",
  "    Sound19",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "Ich bin eine Biene",
  "Du wirst Gefickt",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER",
  "  PLACEHOLDER"
]
bus = smbus.SMBus(1)
addr = 0x20

PORTS = [0x14, 0x13]
OUTPUTS = [0x01, 0x02, 0x04, 0x08]
INPUTS = [0x01, 0x02, 0x04, 0x08, 0x10, 0x20]
PROOUTPUT = 0x10
PROINPUTS = [0x01, 0x02, 0x04]
PROLEDS = [0x20, 0x40, 0x80]

def getstate(row):

  try:
    read = bus.read_byte_data(addr, row)
  except pigpio.error as e:
    print "Error: %s"%(e)

  return read

def setstate(row, data):
  try:
    bus.write_byte_data(0x20, row, data)
  except pigpio.error as e:
    print "Error: %s"%(e)
  return 0

def checkRemote():
    filepath = "command.txt"
    pro = ""
    index = 0
    if os.path.exists(filepath):
      datei = open(filepath)
      i = 0
      input = ["","",""]
      try:
        for line in datei:
          input[i] = line.rstrip()
          i = i + 1
          starten = False
          stoppen = False
          if input[0].rstrip() == "start":
            starten = True
          if input[0].rstrip() == "stop":
            stoppen = True
      except Exception:
        os.remove(filepath)
      pro = input[1].rstrip()
      index = input[2].rstrip()
      if starten == True:
        soundChannels[index + pro * 24].play(sounds[index + pro * 24])
      elif stoppen == True:
        soundChannels[index + pro * 24].stop()
      os.remove(filepath)



def main():
  # Main program block
  GPIO.setwarnings(False)
  GPIO.setmode(GPIO.BCM)       # Use BCM GPIO numbers
  GPIO.setup(LCD_E, GPIO.OUT)  # E
  GPIO.setup(LCD_RS, GPIO.OUT) # RS
  GPIO.setup(LCD_D4, GPIO.OUT) # DB4
  GPIO.setup(LCD_D5, GPIO.OUT) # DB5
  GPIO.setup(LCD_D6, GPIO.OUT) # DB6
  GPIO.setup(LCD_D7, GPIO.OUT) # DB7

  # Initialise display
  lcd_init()

  # Send some test
  lcd_string("    Starting",LCD_LINE_1)
  lcd_string(" Soundboard Pi",LCD_LINE_2)

  time.sleep(3) # 3 second delay

  print "Soundboard Ready."

  lcd_string("   Soundboard", LCD_LINE_1)
  lcd_string("    FERTIG", LCD_LINE_2)

  PROFILE = 0
  while True:
    INDEX = 0
    for L in OUTPUTS:
      setstate(PORTS[0], L + PROLEDS[PROFILE])
      input = hex(getstate(PORTS[1]))
      for C in INPUTS:
        if (input == hex(C)):
          INDEX = getIndex(L,C, PROFILE)
          soundChannels[INDEX].play(sounds[INDEX])
          print "Taster", INDEX + 1, " gedrueckt!"
          print "Spiele Sound ", soundnames[INDEX]
          lcd_string("  Spiele Sound", LCD_LINE_1)
          lcd_string(soundnames[INDEX], LCD_LINE_2)
          setstate(PORTS[0], 0x00 + PROLEDS[PROFILE])
          time.sleep(1)
    setstate(PORTS[0], PROOUTPUT + PROLEDS[PROFILE])
    input = hex(getstate(PORTS[1]))
    if (input == hex(PROINPUTS[0])):
      PROFILE = 0
      print "Profil 1 gewaehlt"
      time.sleep(1)
    if (input == hex(PROINPUTS[1])):
      PROFILE = 1
      print "Profil 2 gewaehlt"
      time.sleep(1)
    if (input == hex(PROINPUTS[2])):
      PROFILE = 2
      print "Profil 3 gewaehlt"
      time.sleep(1)
    setstate(PORTS[0], 0x00 + PROLEDS[PROFILE])
    #checkRemote()

def getIndex(Out,In,Pro):
  Index = 0
  if (Out == OUTPUTS[0]):
    Index = Index
  if (Out == OUTPUTS[1]):
    Index = Index + 6
  if (Out == OUTPUTS[2]):
    Index = Index + 12
  if (Out == OUTPUTS[3]):
    Index = Index + 18

  if (In == INPUTS[0]):
    Index = Index
  if (In == INPUTS[1]):
    Index = Index + 1
  if (In == INPUTS[2]):
    Index = Index + 2
  if (In == INPUTS[3]):
    Index = Index + 3
  if (In == INPUTS[4]):
    Index = Index + 4
  if (In == INPUTS[5]):
    Index = Index + 5
  return Index + Pro * 24

def lcd_init():
  # Initialise display
  lcd_byte(0x33,LCD_CMD) # 110011 Initialise
  lcd_byte(0x32,LCD_CMD) # 110010 Initialise
  lcd_byte(0x06,LCD_CMD) # 000110 Cursor move direction
  lcd_byte(0x0C,LCD_CMD) # 001100 Display On,Cursor Off, Blink Off
  lcd_byte(0x28,LCD_CMD) # 101000 Data length, number of lines, font size
  lcd_byte(0x01,LCD_CMD) # 000001 Clear display
  time.sleep(E_DELAY)

def lcd_byte(bits, mode):
  # Send byte to data pins
  # bits = data
  # mode = True  for character
  #        False for command

  GPIO.output(LCD_RS, mode) # RS

  # High bits
  GPIO.output(LCD_D4, False)
  GPIO.output(LCD_D5, False)
  GPIO.output(LCD_D6, False)
  GPIO.output(LCD_D7, False)
  if bits&0x10==0x10:
    GPIO.output(LCD_D4, True)
  if bits&0x20==0x20:
    GPIO.output(LCD_D5, True)
  if bits&0x40==0x40:
    GPIO.output(LCD_D6, True)
  if bits&0x80==0x80:
    GPIO.output(LCD_D7, True)

  # Toggle 'Enable' pin
  lcd_toggle_enable()

  # Low bits
  GPIO.output(LCD_D4, False)
  GPIO.output(LCD_D5, False)
  GPIO.output(LCD_D6, False)
  GPIO.output(LCD_D7, False)
  if bits&0x01==0x01:
    GPIO.output(LCD_D4, True)
  if bits&0x02==0x02:
    GPIO.output(LCD_D5, True)
  if bits&0x04==0x04:
    GPIO.output(LCD_D6, True)
  if bits&0x08==0x08:
    GPIO.output(LCD_D7, True)

  # Toggle 'Enable' pin
  lcd_toggle_enable()

def lcd_toggle_enable():
  # Toggle enable
  time.sleep(E_DELAY)
  GPIO.output(LCD_E, True)
  time.sleep(E_PULSE)
  GPIO.output(LCD_E, False)
  time.sleep(E_DELAY)

def lcd_string(message,line):
  # Send string to display

  message = message.ljust(LCD_WIDTH," ")

  lcd_byte(line, LCD_CMD)

  for i in range(LCD_WIDTH):
    lcd_byte(ord(message[i]),LCD_CHR)

if __name__ == '__main__':

  try:
    main()
  except KeyboardInterrupt:
    pass
  finally:
    lcd_byte(0x01, LCD_CMD)
    GPIO.cleanup()