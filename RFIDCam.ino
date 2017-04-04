
//#include "mysql.h"

/*
  WriteMultipleVoltages

  Reads analog voltages from pins 0-7 and writes them to the 8 fields of a channel on ThingSpeak every 20 seconds.

  ThingSpeak ( https://www.thingspeak.com ) is an analytic IoT platform service that allows you to aggregate, visualize and
  analyze live data streams in the cloud.

  Copyright 2017, The MathWorks, Inc.

  Documentation for the ThingSpeak Communication Library for Arduino is in the extras/documentation folder where the library was installed.
  See the accompaning licence file for licensing information.
*/
#include <SPI.h>
#include <MFRC522.h>
//#include "ThingSpeak.h"
//#include <HttpClient.h>
//#include <BridgeClient.h>
#include <Bridge.h>

#define SS_PIN 10
#define RST_PIN 9


#if !defined(USE_WIFI101_SHIELD) && !defined(USE_ETHERNET_SHIELD) && !defined(ARDUINO_SAMD_MKR1000) && !defined(ARDUINO_AVR_YUN) && !defined(ARDUINO_ARCH_ESP8266)
#error "Uncomment the #define for either USE_WIFI101_SHIELD or USE_ETHERNET_SHIELD"
#endif


//BridgeClient client;
// ThingSpeak
//unsigned long myChannelNumber = 240874;
//const char * myWriteAPIKey = "ZSNVVL6NR70IG22F";

MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class
MFRC522::MIFARE_Key key;

// Init array that will store new NUID
byte nuidPICC[4];

//Process process;
//static char *host = "ckachur.com";
//static char *user = "CamRfidTeacher";
//static char *pass = "12teacher34";
//static char *dbname = "CamRFID";
//unsigned int port = 3306;
//static char *unix_socket = NULL;
//unsigned int flag = 0;
String hexRFID; 

void setup() {
  Serial.begin(9600);
  SPI.begin(); // Init SPI bus
  rfid.PCD_Init(); // Init MFRC522
  Bridge.begin();
//  while(!Bridge);
//  Console.begin();
//  while(!Console);


  //ThingSpeak.begin(client);

  Serial.println(F("Setting up Cam's RFID Reader"));
  //  process.begin("curl");
  //  process.runShellCommand("mysql -u -CamRfidAdmin -h ckachur.com -p A7aC5fd2R;");
  //  process.run();
  //  process.runShellCommand("A7aC5fd2R");
  //  process.run();
  //  process.runShellCommand("USE CamRFID;");
  //  process.run();
  //  Serial.print(F("Using the following key:"));
  //  printHex(key.keyByte, MFRC522::MF_KEY_SIZE);

  //  MYSQL *conn;
  //  conn = mysql_init(NULL);
  //  if(!mysql_real_connect(conn, host, user, pass, dbname, port, unix_socket, flag))
  //  {
  //    Serial.print("Error: "); Serial.print(mysql_error(conn)); Serial.println(mysql_errno(conn));
  //    exit(1);
  //  }
  //  Serial.println("Connection Successful!");


}

void loop() {
  // Look for new cards
  if ( ! rfid.PICC_IsNewCardPresent())
    return;

  // Verify if the NUID has been readed
  if ( ! rfid.PICC_ReadCardSerial())
    return;

  String decRFID = "";
  String tmp = "";
  hexRFID = "";
  //String tmp = "";
  for (byte i = 0; i < 4; i++) {
    nuidPICC[i] = rfid.uid.uidByte[i];
    //Serial.print(nuidPICC[i]);
    decRFID.concat(nuidPICC[i]);
    //hexRFID.concat(_HEX(nuidPICC[i]));
    //tmp = String(nuidPICC[i], HEX);
    tmp = String(nuidPICC[i], HEX);
    if (tmp.length() < 2)
    {
      tmp = 0 + tmp;
    }
    hexRFID.concat(tmp);
    hexRFID.toUpperCase();
    //hexRFID.concat(tmp);
    //strcat(hexRFID,tmp);
  }
  
  //    while(hexRFID[i])
  //    {
  //        putchar(toupper(hexRFID[i]));
  //        i++;
  //    }
  //toupper(hexRFID);
  //    int len = sizeof(hexRFID) + 1;
  //    char *hexRFIDchar[] = (char *)malloc(sizeof(char)*(strlen(hexRFID)+1));
  //    j = 0;
  //    while(hexRFIDchar[j] != NULL){
  //      hexRFIDchar[j] = toupper( hexRFID[j] );
  //      i++;
  //    }

  //    char hexRFID[8];
  //    strncpy(hexRFID, hexRFIDStr,8);
  //    toupper(hexRFID);
  
  
  if (hexRFID == "4E890785")
  {
    // DWEET 
//    
//    HttpClient httpclient;
//    Serial.println("Not a valid Student");
//    Serial.println("The last RFID read in was: ");
//    String readHexRFID = "";
//
//   
//    //httpclient.get("http://dweet.io/get/latest/dweet/for/rocky_55?hexRFID");
//    httpclient.get("https://dweet.io/get/latest/dweet/for/rocky_55");
//    delay(1000);
//    while (httpclient.available()) {
//      char c = httpclient.read();
//      //SerialUSB.print(c);
//      readHexRFID.concat(c);
//    }
//    delay(1000);
//    Serial.println(readHexRFID);
//    Serial.println("https://dweet.io/get/latest/dweet/for/rocky_55");
   

//    delay(1000);
//    String readDecRFID = "";
//    httpclient.get("http://dweet.io/get/latest/dweet/for/rocky_55?decRFID");
//    while (httpclient.available()) {
//      char c = httpclient.read();
//      //SerialUSB.print(c);
//      readDecRFID.concat(c);
//    }
//    Serial.println(readDecRFID);
  }

  else {
// DWEET
//    HttpClient httpclient;
    
    Serial.println(F("Hex read in: "));
    printHex(rfid.uid.uidByte, rfid.uid.size);
    Serial.println("");
    Serial.println(hexRFID);
    
//    Process logdata;
//    // date is a command line utility to get the date and the time
//    // in different formats depending on the additional parameter
//    //logdata.addParameter("linkmysql.lua");
//    logdata.begin("lua");
//    logdata.addParameter("/root/linkmysql.lua");
//    logdata.addParameter(String(hexRFID));  //
//    logdata.run();  // run the command
//
//   
////    // read the output of the command
////    String cmdOutput="";
////    while (logdata.available() > 0) {
////      char c = logdata.read();
////      cmdOutput.concat(c);
////    }
//    
//    Serial.println("send_data was run");
    //Serial.println(cmdOutput);

//    hexRFID.concat("_2");
//    logdata.begin("lua");
//    logdata.addParameter("~/linkmysql.lua");
//    logdata.addParameter(hexRFID);  //
//    logdata.run();  // run the command
//    Serial.println("second run");
//
//    hexRFID.concat("_3");
//    String commandRun = "lua linkmysql.lua ";
//    commandRun.concat(hexRFID);
//    logdata.runShellCommand(commandRun);
//    Serial.println(commandRun);
//    //while(logdata.running());
//    //logdata.runShellCommand("mysql -u CamRfidTeacher -h ckachur.com -p");
////    logdata.begin("mysql");
////    logdata.addParameter("-u");
////    logdata.addParameter("-CamRfidTeacher");
////    logdata.addParameter("-h");
////    logdata.addParameter("-ckachur.com");
////    logdata.addParameter("-p");
////    logdata.run();
//
//    hexRFID.concat("_4");
//    logdata.begin("lua");
//    logdata.addParameter("/linkmysql.lua");  //
//    logdata.addParameter(String(hexRFID));  //
//    logdata.run();  // run the command
//    Serial.println("4th run");
    

    send_data();
    delay(1000);

//      ThingSpeak.setField(1,hexRFID);
//      //Write the fields that you've set all at once.
//      ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);
//      delay(20000); // ThingSpeak will only accept updates every 15 seconds.
    //Serial.println("");
    
    // DWEET
//    String urlString = "http://dweet.io/dweet/for/rocky_55?hexRFID=";
//    urlString.concat(hexRFID);
//    urlString.concat("&decRFID=");
//    urlString.concat(decRFID);
//    httpclient.get(urlString);
//    
//    delay(1000);
//    Serial.println(urlString);
    
  }

  //    process.runShellCommand("INSERT INTO `Swipe` (`rfid`) VALUES (\""+hexRFID+"\",\"Test\");");
  //    process.run();
  // Make a HTTP request:
}

void printHex(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], HEX);
  }
}

// This function call the linkmysql.lua
void send_data() {
  Process logdata;
  // date is a command line utility to get the date and the time
  // in different formats depending on the additional parameter
  logdata.begin("lua");
  logdata.addParameter("/root/linkmysql.lua");  //
  logdata.addParameter(hexRFID);  //
  logdata.run();  // run the command
  while(logdata.running());
  Serial.println("data sent to database");
//
//  logdata.runShellCommand("lua linkmysql.lua "+hexRFID);
  
// 
//  logdata.begin("lua");
//  logdata.addParameter("/linkmysql.lua");  //
//  logdata.addParameter(String(hexRFID));  //
//  logdata.run();  // run the command  
}

