# PersonalSafetyApp

## **Overview**

The Personal Safety App is a mobile application developed as a graduation project using Kotlin for Android. This app is designed to enhance the safety and well-being of individuals, particularly those who may be in vulnerable situations or live alone. By using modern technology, the app aims to provide users and their loved ones with peace of mind through easy to use emergency response and real-time location sharing capabilities.

## **Key Objectives**

- **Immediate Contact in Emergencies:** The app allows users to quickly reach out to their contacts that are in their friend list when they feel unsafe, sending them their information such as user's real-time location, blood type, history of disease etc.
- **Monitoring the Safety of Loved Ones:** By knowing your loved ones whereabouts, you can make sure your loved ones are safe. Especially, individuals who live alone or elderly family members.
- **User Friendly Operation:** Simple UI/UX that help users to have quick access to emergency functions without complicated navigation.

## **Core Features**

1. **User Authentication:** Implemented using **Firebase Authentication**, allowing secure login and registiration. Users can sign in using email and password.

2.  **Real-Time Locaion Sharing:** Utilizes the **Google Cloud Maps API** along with **Fused Location Provider** to obtain the user's current location. Continuously updates location data in the background using a **foreground service**, ensuring that even if the user leaves the app, their location is still trackable. Location data is stored and updated in **Firebase Realtime Database**, enabling contacts to view the user's last known position in Maps UI by Maps Fragment.

3. **SOS Functionality:** The app has a SOS button that, when pressed, triggers an emergency alert. This sends an email to selected contacts, including a link with the user's precise location on a map. This feature ensures reliable communication in critical moments without need for complex user actions.

4. **Firebase RealTime Database Integration:** Stores user information, contact details and real-time location data. It supports data synchronization to provide instant updates across different devices and ensure that contacts can access up-to-date information.

## **Benefits and Use Cases**

- **Emergency Situations:** Users can quickly send their real-time location to friends, family or guardians, providing vital information during emergencies.
- **Elderly Monitoring:** The app can be used by family members to track the location of elderly relatives, ensuring their safety.
- **Peace of Mind for Solo Travelers:** Individuals traveling alone can rely on the app to keep their loved ones informed of their location.

## **Technologies and Tools**

- **Kotlin:** Main programming language used for app development, chosen for its modern, concise and safe syntax.
- **Android Studio:** IDE used for building and testing the application.
- **Firebase Authentication:** Provides secure and easy user sign-in.
- **Firebase Realtime Database:** Used for real-time data storage and retrieval, allowing instant location updates.
- **Google Cloud Maps API:** Integrates mapping and location-based services for real-time tracking.

## **Security and Privacy**

The app is designed with user privacy in mind:
- User authentication ensures that only verified users can access and interact with the app.
- Location data is securely managed through Firebase, which adheres to modern security standarts.



>[!NOTE]
>You can clone this repository and check it out. You should connect the project to Firebase and make sure you have all dependencies that you need. You should replace API key part in your manifest file with your own Google Cloud API key.



