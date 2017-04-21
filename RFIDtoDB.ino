
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
// If defined then it doesn't push the invalid swipes to the database
// If not defined then it will push all swipes to database.
#define invalid2DB 
const int MAX_STUDENTS = 50;
//const int UPDATE_VALID_IDS = 10; //number of seconds between updates

MFRC522 rfid(SS_PIN, RST_PIN); // Instance of the class

byte nuidPICC[4]; // RFID in bytes
char hexRFID[9]; // RFID in hex
char validIDs[MAX_STUDENTS][8]; // valid RFID table 

void setup() {
  //Serial.begin(9600);
  SPI.begin(); // Init SPI bus
  rfid.PCD_Init(); // Init MFRC522
  Bridge.begin();  // Init Bridge to AR9331
  getStudentTable();
}

void loop() {
  // Look for new cards
  if ( ! rfid.PICC_IsNewCardPresent())
    return;

  // Verify if an RFID has been read
  if ( ! rfid.PICC_ReadCardSerial())
    return;

  // populates hexRFID with hex values from the read RFID bytes  
  getHexRFID(rfid.uid.uidByte[0],rfid.uid.uidByte[1],rfid.uid.uidByte[2],rfid.uid.uidByte[3]); 
  
  //If invalid2DB is defined then only push valid swipes to the database
  #ifdef invalid2DB
    // populates the valid IDs table from database and returns how many valid IDs there are
    //newTime = millis();
    //if(newTime-lastTime<UPDATE_VALID_IDS*1000){
    int numIDs = getValidIDs();
    //  lastTime = millis();
    //}
    bool invalid = true;
    char validator[9]; //single RFID from list of valid IDs
    int startNum; // Index in the table
    // implement with while loop?

    // checking to see if valid ID
    for(int i = 0; i < numIDs; i++){
      startNum = i*8+i;
      pch = strchr( validIDs[i], chr );
      strncpy(validIDs[i],validator,9);
            if(validator==hexRFID){
                    invalid = false;
                    i = numIDs;
            }            
    }
    // If it is valid then go ahead and send the data
    if (!invalid)
    {      
      sendData();
      delay(1000);      
    }
  //If invalid2DB isn't defined then push all swipes to database  
  #else    
    sendData();
    delay(1000);
  #endif
}

// This function call the linkmysql.lua
void sendData() {
  Process logdata;
  logdata.begin("lua");
  logdata.addParameter("/root/linkmysql.lua");  //
  logdata.addParameter(hexRFID);  //
  logdata.run();  // run the command 'lua linkmysql.lua XXXXXXXX'
  while(logdata.running());
}

void getStudentTable()
{
  String studentOutput="";
  Process receivedata;
  receivedata.begin("lua");
  receivedata.addParameter("/root/querymysql.lua");
  receivedata.run();
  // read the output of the command 'lua querymysql.lua'
  while (receivedata.available() > 0) {
    char c = receivedata.read();
    studentOutput.concat(c);
  }
}

// This gets the valid IDs and puts them into a table and returns the number of valid IDs
int getValidIDs()
{
  int j=0;
  Process iddata;
  iddata.begin("lua");
  iddata.addParameter("/root/idquerymysql.lua");
  iddata.run();
  char cmdOutput[9*MAX_STUDENTS];
  while (iddata.available() > 0) {
    char c = iddata.read();
    cmdOutput[j] = c;
    j++;
  }
  int numID = j/9;
  int startNum = 0;
  int endNum = 0;
  for(int i=0; i<numID; i++){
    startNum = i*8+i;
    validIDs[i][0] = cmdOutput[startNum]; 
    validIDs[i][1] = cmdOutput[startNum+1]; 
    validIDs[i][2] = cmdOutput[startNum+2]; 
    validIDs[i][3] = cmdOutput[startNum+3]; 
    validIDs[i][4] = cmdOutput[startNum+4]; 
    validIDs[i][5] = cmdOutput[startNum+5];
    validIDs[i][6] = cmdOutput[startNum+6];
    validIDs[i][7] = cmdOutput[startNum+7];
  }
  return numID;
}

// This converts the read in bytes to a hex character array
void getHexRFID(byte byte0, byte byte1, byte byte2, byte byte3)
{
  byte uidByte[] = {byte0, byte1, byte2, byte3};
  byte uidSize = sizeof(uidByte);

  // destination array; space for 8 representations of a nibble plus terminating '\0'
  char dest[9];

  // initialise character array
  memset(dest, 0, sizeof(dest));
  // test the size of the character array
  
  for (int cnt = 0; cnt < uidSize; cnt++)
  {
    // convert byte to its ascii representation
    sprintf(&dest[cnt * 2], "%02X", uidByte[cnt]);
  }
  strncpy(hexRFID,dest,9);
}
