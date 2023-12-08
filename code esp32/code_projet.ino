#include <WiFi.h>
#include <WiFiUdp.h>
#include <PubSubClient.h>
#include <NTPClient.h>
#include <Adafruit_SSD1306.h> // Bibliothèque pour l'afficheur OLED

#define SAMPLE_RATE_ECG 125
#define SAMPLE_RATE_EEG 256
#define BAUD_RATE 115200
#define ECG_INPUT_PIN A0
#define EEG_INPUT_PIN 34
#define LED_PIN 32 // Pin de la LED

#define WIFISSID "TT_1220"
#define PASSWORD  "igwpmuf95c"
#define TOKEN "BBFF-6O22nWHv61gGfofZ8sXEl2qEywi5z3"
#define MQTT_CLIENT_NAME "henda"
#define ECG_VARIABLE_LABEL "donnees_ecg"
#define EEG_VARIABLE_LABEL "donnees_eeg"
#define DEVICE_LABEL "NeuroSight"


const char* mqttBroker = "industrial.api.ubidots.com";
char topic[200];
char payload[1000];
char str_ecg[10];
char str_eeg[10];
double epochseconds = 0;
double epochmilliseconds = 0;
double current_millis = 0;
double current_millis_at_sensordata = 0;
double timestampp = 0;
int j = 0;

WiFiClient ubidots;
PubSubClient client(ubidots);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

#define OLED_WIDTH 128
#define OLED_HEIGHT 64
Adafruit_SSD1306 display(OLED_WIDTH, OLED_HEIGHT, &Wire, -1); // Initialisation de l'afficheur OLED

void callback(char* topic, byte* payload, unsigned int length) {
  char p[length + 1];
  memcpy(p, payload, length);
  p[length] = NULL;
  Serial.write(payload, length);
  Serial.println(topic);
}

void reconnect() {
  while (!client.connected()) {
    Serial.println("Tentative de connexion MQTT...");
    if (client.connect(MQTT_CLIENT_NAME, TOKEN, "")) {
      Serial.println("Connecté");
    } else {
      Serial.print("Échec, rc=");
      Serial.print(client.state());
      Serial.println(" Réessayer dans 2 secondes");
      delay(2000);
    }
  }
}

void setup() {
  Serial.begin(BAUD_RATE);
  WiFi.begin(WIFISSID, PASSWORD);
  pinMode(ECG_INPUT_PIN, INPUT);
  pinMode(EEG_INPUT_PIN, INPUT);
  pinMode(LED_PIN, OUTPUT); // Configuration du pin de la LED en sortie
  digitalWrite(LED_PIN, HIGH); // Allumer la LED
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println("");
  Serial.println("WiFi Connecté");
  Serial.println("Adresse IP : ");
  Serial.println(WiFi.localIP());
  timeClient.begin();
  client.setServer(mqttBroker, 1883);
  client.setCallback(callback);
  timeClient.update();
  epochseconds = timeClient.getEpochTime();
  epochmilliseconds = epochseconds * 1000;
  Serial.print("epochmilliseconds=");
  Serial.println(epochmilliseconds);
  current_millis = millis();
  Serial.print("current_millis=");
  Serial.println(current_millis);

  // Initialisation de l'afficheur OLED
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("Erreur lors de l'initialisation de l'afficheur OLED"));
    while (true);
  }

  display.clearDisplay();
  display.setTextColor(WHITE);
  display.setTextSize(1);
}

void loop() {
  if (!client.connected()) {
    reconnect();
    j = 0;
  }
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
    sprintf(topic, "%s%s", "/v1.6/devices/", DEVICE_LABEL);
    sprintf(payload, "{\"%s\": %f}", ECG_VARIABLE_LABEL, signal_ecg);
    Serial.print("Publication du message ECG sur le topic : ");
    Serial.println(topic);
    Serial.print("Payload : ");
    Serial.println(payload);
    client.publish(topic, payload);
    client.loop();

    // Affichage du signal ECG sur l'afficheur OLED
    display.clearDisplay();
    display.setCursor(0, 0);
    display.print("ECG: ");
    display.println(signal_ecg);
    display.display();
  }
  if (timer_eeg < 0) {
    timer_eeg = 1000000 / SAMPLE_RATE_EEG;
    float eeg = analogRead(EEG_INPUT_PIN);
    float signal_eeg = EEGFilter(eeg);
    sprintf(topic, "%s%s", "/v1.6/devices/", DEVICE_LABEL);
    sprintf(payload, "{\"%s\": %f}", EEG_VARIABLE_LABEL, signal_eeg);
    Serial.print("Publication du message EEG sur le topic : ");
    Serial.println(topic);
    Serial.print("Payload : ");
    Serial.println(payload);
    client.publish(topic, payload);
    client.loop();

    // Affichage du signal EEG sur l'afficheur OLED
    display.clearDisplay();
    display.setCursor(0, 20);
    display.print("EEG: ");
    display.println(signal_eeg);
    display.display();
  }
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
