
/*
  * Arduino YUN
  * Mifare RC522 card reader
  This uses the ATmega32u4 Microcontroller and MFRC522 Card reader to read in the RFIDs. 
  The RFIDs are then used as variables in the lua scripts that are run on the Atheros AR9331 Microprocessor (Linux) 
  through the Bridge.  The lua scripts are running MySQL queries to the database that I have setup.
*/

#include <SPI.h>
#include <MFRC522.h>
#include <Bridge.h>
#include <Process.h>
#define SS_PIN 10
#define RST_PIN 9
#define invalid2DB
const int MAX_STUDENTS = 50;

MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class

// Init array that will store new RFID
byte nuidPICC[4];
String hexRFID;
String validIDs[MAX_STUDENTS]; 
//char* hexRFID;
//char validIDs[MAX_STUDENTS]; 

void setup() {
  Serial.begin(9600);
  SPI.begin(); // Init SPI bus
  rfid.PCD_Init(); // Init MFRC522
  Bridge.begin();  // Init Bridge to AR9331

  Serial.println(F("Setting up Cam's RFID Reader"));
  getStudentTable();
//  String validIDs[]=""; 
//  getValidIDs(validIDs);
  
}

void loop() {
  // Look for new cards
  if ( ! rfid.PICC_IsNewCardPresent())
    return;

  // Verify if an RFID has been read
  if ( ! rfid.PICC_ReadCardSerial())
    return;

  // Reading in each byte of the RFID card and converting into a hex string, decimal string
  String decRFID = "";
  String tmp = "";
  //char tmp ="";
  hexRFID = "";
  for (byte i = 0; i < 4; i++) {
    nuidPICC[i] = rfid.uid.uidByte[i];
    decRFID.concat(nuidPICC[i]);
    tmp = String(nuidPICC[i], HEX);
    //tmp = nuidPICC[i];
    //strupr(tmp);
    //if (tmp.length() < 2)
    if(sizeof(tmp)<2)
    {
      tmp = 0 + tmp;
    }
    hexRFID.concat(tmp);
    hexRFID.toUpperCase();
    //putchar{toupper(tmp));
    //hexRFID = hexRFID+tmp;
  }  
  #ifdef invalid2DB
    //String validIDs[] = ""; 
    //Serial.println("before getValidIDs");
    int numIDs = getValidIDs();
    //Serial.println("after getValidIDs");
    //Serial.println(numIDs);
    bool invalid = true;
    //Serial.println(validIDs[1]);
    //Serial.println(hexRFID);
    for(int i = 0; i < numIDs; i++){
            if(validIDs[i] == hexRFID) {//validIDs[i] == char(hexRFID, HEX)) {
                    Serial.println("Found!");
                    invalid = false;
                    i = numIDs;
            }
            else{
                invalid = true;
            }
            
    }
    //Serial.println("after for loop");
    if (invalid)
    {
      Serial.println("Invalid Card!");
      Serial.println(hexRFID);
      Serial.println("");  
    }
  
    else {
      Serial.println(F("Hex read in: "));
      printHex(rfid.uid.uidByte, rfid.uid.size);
      Serial.println("");
      //Serial.println(hexRFID);
      
      sendData();
      Serial.println("");
      delay(1000);
      
    }
  #else
    Serial.println(F("Hex read in: "));
    printHex(rfid.uid.uidByte, rfid.uid.size);
    Serial.println("");
    //Serial.println(hexRFID);
    sendData();
    delay(1000);
  #endif
}

void printHex(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], HEX);
  }
}

// This function call the linkmysql.lua
void sendData() {
  Process logdata;
  // date is a command line utility to get the date and the time
  // in different formats depending on the additional parameter
  logdata.begin("lua");
  logdata.addParameter("/root/linkmysql.lua");  //
  logdata.addParameter(hexRFID);  //
  logdata.run();  // run the command
  while(logdata.running());
  Serial.println("data sent to database");

}

void getStudentTable()
{
  String cmdOutput="";
  //char cmdOutput="";
  Process receivedata;
  receivedata.begin("lua");
  receivedata.addParameter("/root/querymysql.lua");
  receivedata.run();
  // read the output of the command
  while (receivedata.available() > 0) {
    char c = receivedata.read();
    cmdOutput.concat(c);
  }
  Serial.println(cmdOutput);  
//  String sub = cmdOutput.substring(4,12);
//  Serial.println(sub);
  Serial.println("current table read");
}


int getValidIDs()
{
  Process iddata;
  iddata.begin("lua");
  iddata.addParameter("/root/idquerymysql.lua");
  iddata.run();
  // read the output of the command
  String cmdOutput="";
  //char cmdOutput[]="";
  int j=0;
  while (iddata.available() > 0) {
    char c = iddata.read();
    cmdOutput.concat(c);
    //cmdOutput[j] = c;
  }
  //Serial.println(cmdOutput);
  
  //int numID = cmdOutput.length()/9;
  int numID = sizeof(cmdOutput) / sizeof(cmdOutput[0]);
  int startNum = 0;
  int endNum = 0;
  for(int i=0; i<numID; i++){
    startNum = i*8+i;
    endNum = startNum+8;
    //int index = i+1;
    //validIDs[index]= cmdOutput.substring(startNum,endNum);
    validIDs[i]= cmdOutput.substring(startNum,endNum);
    //validIDs[i] = cmdOutput[startNum] + cmdOutput[startNum+1] + cmdOutput[startNum+2] + cmdOutput[startNum+3] + cmdOutput[startNum+4] + cmdOutput[startNum+5] + cmdOutput[startNum+6] + cmdOutput[startNum+8] ;
    //Serial.println(validIDs[index]);
  }
  //Serial.println(numID);
  return numID;
}

