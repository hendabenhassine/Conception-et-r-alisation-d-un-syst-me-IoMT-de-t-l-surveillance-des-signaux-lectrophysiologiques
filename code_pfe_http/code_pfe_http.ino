#include <WiFi.h>
#include <HTTPClient.h>

#define SAMPLE_RATE_ECG 125
#define SAMPLE_RATE_EEG 256
#define BAUD_RATE 115200
#define ECG_INPUT_PIN A0
#define EEG_INPUT_PIN 34
#define LED_PIN 32

// Ubidots Data
String ecgVarId = "donnees_ecg";
String eegVarId = "donnees_eeg";
String token = "BBFF-6O22nWHv61gGfofZ8sXEl2qEywi5z3";

const char* ssid = "TT_1220";
const char* password = "igwpmuf95c";

void setup() {
  Serial.begin(BAUD_RATE);
  pinMode(LED_PIN, OUTPUT);

  // Connexion au WiFi
  connectToWiFi();
}

void loop() {
  static unsigned long past_ecg = 0;
  static unsigned long past_eeg = 0;
  unsigned long present = micros();
  unsigned long interval_ecg = present - past_ecg;
  unsigned long interval_eeg = present - past_eeg;
  past_ecg = present;
  past_eeg = present;
  static long timer_ecg = 0;
  static long timer_eeg = 0;
  timer_ecg -= interval_ecg;
  timer_eeg -= interval_eeg;

  if (timer_ecg < 0) {
    timer_ecg = 1000000 / SAMPLE_RATE_ECG;
    float ecg = analogRead(ECG_INPUT_PIN);
    float signal_ecg = ECGFilter(ecg);
    Serial.print("ECG: ");
    Serial.print(signal_ecg);
    Serial.print("  ");

    if (timer_eeg < 0) {
      timer_eeg = 1000000 / SAMPLE_RATE_EEG;
      float eeg = analogRead(EEG_INPUT_PIN) ;
      float signal_eeg = EEGFilter(eeg);
      Serial.print("EEG: ");
      Serial.print(signal_eeg);

      // Envoyer les données d'ECG et EEG à Ubidots
      saveValues(ecgVarId, signal_ecg, eegVarId, signal_eeg);
    }

    Serial.println();
  }

  digitalWrite(LED_PIN, HIGH);  // Allumer la LED
}

float ECGFilter(float input) {
  float output = input;
  {
    static float z1, z2;
    float x = output - 0.70682283 * z1 - 0.15621030 * z2;
    output = 0.28064917 * x + 0.56129834 * z1 + 0.28064917 * z2;
    z2 = z1;
    z1 = x;
  }
  {
    static float z1, z2;
    float x = output - 0.95028224 * z1 - 0.54073140 * z2;
    output = 1.00000000 * x + 2.00000000 * z1 + 1.00000000 * z2;
    z2 = z1;
    z1 = x;
  }
  {
    static float z1, z2;
    float x = output - -1.95360385 * z1 - 0.95423412 * z2;
    output = 1.00000000 * x + -2.00000000 * z1 + 1.00000000 * z2;
    z2 = z1;
    z1 = x;
  }
  {
    static float z1, z2;
    float x = output - -1.98048558 * z1 - 0.98111344 * z2;
    output = 1.00000000 * x + -2.00000000 * z1 + 1.00000000 * z2;
    z2 = z1;
    z1 = x;
  }
  return output;
}

float EEGFilter(float input) {
  float output = input;
  {
    static float z1, z2;
    float x = output - -0.95391350 * z1 - 0.25311356 * z2;
    output = 0.00735282 * x + 0.01470564 * z1 + 0.00735282 * z2;
    z2 = z1;
    z1 = x;
  }
  {
    static float z1, z2;
    float x = output - -1.20596630 * z1 - 0.60558332 * z2;
    output = 1.00000000 * x + 2.00000000 * z1 + 1.00000000 * z2;
    z2 = z1;
    z1 = x;
  }
  {
    static float z1, z2;
    float x = output - -1.97690645 * z1 - 0.97706395 * z2;
    output = 1.00000000 * x + -2.00000000 * z1 + 1.00000000 * z2;
    z2 = z1;
    z1 = x;
  }
  {
    static float z1, z2;
    float x = output - -1.99071687 * z1 - 0.99086813 * z2;
    output = 1.00000000 * x + -2.00000000 * z1 + 1.00000000 * z2;
    z2 = z1;
    z1 = x;
  }
  return output;
}


void connectToWiFi() {
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }

  Serial.println("Connected to WiFi");
}

void saveValues(String varId1, float value1, String varId2, float value2) {
  WiFiClient client;

  if (!client.connect("industrial.api.ubidots.com", 80)) {
    Serial.println("Failed to connect to Ubidots");
    return;
  }

  String url = "/api/v1.6/devices/NeuroSight";
  String data = "{\"" + varId1 + "\":" + String(value1) + ", \"" + varId2 + "\":" + String(value2) + "}";

  client.print("POST " + url + " HTTP/1.1\r\n");
  client.print("Host: industrial.api.ubidots.com\r\n");
  client.print("X-Auth-Token: " + token + "\r\n");
  client.print("Content-Type: application/json\r\n");
  client.print("Content-Length: ");
  client.print(data.length());
  client.print("\r\n\r\n");
  client.print(data);

  Serial.println("Sending data to Ubidots...");
  Serial.println(data);

  while (client.connected()) {
    if (client.available()) {
      String response = client.readStringUntil('\n');
      Serial.println(response);
      break;
    }
  }

  client.stop();
  Serial.println("Data sent to Ubidots");
}
