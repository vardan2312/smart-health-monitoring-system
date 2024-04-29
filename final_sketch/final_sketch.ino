#include <ESP8266WiFi.h>
#include <Firebase_ESP_Client.h>
#include <DHT.h>
#include <PulseSensorPlayground.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_Sensor.h>
#define SCREEN_WIDTH 128 
#define SCREEN_HEIGHT 64 

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);

#define DHTPIN D4
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);


PulseSensorPlayground pulseSensor; 
#define API_KEY "AIzaSyBHl_e8b39qcICn4ssYAJApCrEAtJSiOpU"
// Enter Realtime Database URL
#define DATABASE_URL "capstone-65c3c-default-rtdb.asia-southeast1.firebasedatabase.app/"
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
const char* ssid = "Vardan";  
const char* password = "vardan2312"; 

unsigned long previousMillisGetHR = 0; //--> will store the last time Millis (to get Heartbeat) was updated.
unsigned long previousMillisHR = 0; //--> will store the last time Millis (to get BPM) was updated.

const long intervalGetHR = 10; //--> Interval for reading heart rate (Heartbeat) = 10ms.
const long intervalHR = 10000; //--> Interval for obtaining the BPM value based on the sample is 10 seconds.

const int PulseSensorHRWire = A0; //--> PulseSensor connected to ANALOG PIN 0 (A0 / ADC 0).
const int LED_D1 = LED_BUILTIN; //--> LED to detect when the heart is beating. The LED is connected to PIN D1 (GPIO5) on the NodeMCU ESP12E.
int Threshold = 400; //--> Determine which Signal to "count as a beat" and which to ignore.

int cntHB = 0; //--> Variable for counting the number of heartbeats.
boolean ThresholdStat = true; //--> Variable for triggers in calculating heartbeats.
int BPMval = 0; //--> Variable to hold the result of heartbeats calculation.

FirebaseData Firebase_dataObject;
FirebaseAuth auth;
FirebaseConfig config;
String database_path;
String UID;


bool signupOK = false;
String heart_rate_path = "/heartbeat";
String temperature_path = "/temperature";
String humidity_path = "/humidity";


void GetHeartRate() {
  //----------------------------------------Process of reading heart rate.
  unsigned long currentMillisGetHR = millis();

  if (currentMillisGetHR - previousMillisGetHR >= intervalGetHR) {
    previousMillisGetHR = currentMillisGetHR;

    int PulseSensorHRVal = analogRead(PulseSensorHRWire);

    if (PulseSensorHRVal > Threshold && ThresholdStat == true) {
      cntHB++;
      ThresholdStat = false;
      // digitalWrite(LED_D1,HIGH);
    }

    if (PulseSensorHRVal < Threshold) {
      ThresholdStat = true;
      // digitalWrite(LED_D1,LOW);
    }
  }
  //----------------------------------------

  //----------------------------------------The process for getting the BPM value.
  unsigned long currentMillisHR = millis();

  if (currentMillisHR - previousMillisHR >= intervalHR) {
    previousMillisHR = currentMillisHR;

    BPMval = (cntHB * 12); //--> The taken heart rate is for 10 seconds. So to get the BPM value, the total heart rate in 10 seconds x 6.
    Serial.print("BPM : ");
    Serial.println(BPMval);
    
    cntHB = 0;
  }
  //----------------------------------------
}

void handleHeartRate() {
   FirebaseJson json;
   float temperature = dht.readTemperature();
   float humidity = dht.readHumidity();
  // digitalWrite(ON_Board_LED, LOW); //--> "ON_Board_LED" will be turned on when the request from the browser / client starts.
   String BPMvalSend = String(BPMval); 
  // server.send(200, "text/plane", BPMvalSend); //--> Sends BPM value to client request / browser.
  // digitalWrite(ON_Board_LED, HIGH); //--> Turn off the LED because the client request / browser has been processed.
    json.set(temperature_path.c_str(), String(temperature));
    json.set(humidity_path.c_str(), String(humidity));
    json.set(heart_rate_path.c_str(), String(BPMvalSend));
    // json.set(time_path.c_str(), String(epoch_time)); // Add epoch time to JSON data

    if (isnan(temperature) || isnan(humidity)) {
    Serial.println("Failed");
  }
  // clear display
  display.clearDisplay();
  
  // display temperature
  display.setTextSize(1);
  display.setCursor(0,0);
  display.print("Temperature: ");
  display.setTextSize(2);
  display.setCursor(0,10);
  display.print(temperature);
  display.print(" ");
  display.setTextSize(1);
  display.cp437(true);
  display.write(167);
  display.setTextSize(2);
  display.print("C");
  
  // display humidity
  display.setTextSize(1);
  display.setCursor(0, 35);
  display.print("Humidity: ");
  display.setTextSize(2);
  display.setCursor(0, 45);
  display.print(humidity);
  display.print(" %"); 

  //display heart rate

  // display.setTextSize(1);
  // display.setCursor(0, 35);
  // display.print("heart rate: ");
  // display.setTextSize(2);
  // display.setCursor(0, 45);
  // display.print(BPMvalSend);
  // display.print(" %"); 


  
  display.display();

    // Send data to Firebase Realtime Database
    String parent_path = database_path;
    Serial.printf("Set JSON...%s\n", Firebase.RTDB.setJSON(&Firebase_dataObject, parent_path.c_str(), &json) ? "ok" : Firebase_dataObject.errorReason().c_str());

}


void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  
  dht.begin();
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;);
  }
  delay(2000);
  display.clearDisplay();
  display.setTextColor(WHITE);
   // Initialize the DHT sensor
  pulseSensor.analogInput(PulseSensorHRWire); // Initialize the Pulse Sensor
  
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi ..");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print('.');
    delay(1000);
  }
  Serial.println(WiFi.localIP());
  Serial.println();
  
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", "")){
    Serial.println("ok");
    signupOK = true;
  }
  else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  Firebase.reconnectWiFi(true);
  Firebase_dataObject.setResponseSize(4096);

  config.token_status_callback = tokenStatusCallback; 
  // config.max_token_generation_retry = 5;

  Firebase.begin(&config,&auth);

  database_path = "/DHT";

}

void loop() {
  // put your main code here, to run repeatedly:
   GetHeartRate();
   handleHeartRate();
   delay(20);
}
