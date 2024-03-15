# MelodyShare

<img align="right" alt="App logo" width="200px" src="app/src/main/ic_launcher-playstore.png" />

An Android app for sharing melodies with friends!

CA submission for SETU's Mobile Application Development module.

_(Here is the assignment due date)_

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/ZX5kW5CC)

## Setup

### Firebase

> [!NOTE]
> Remember to generate and add the SHA to your Android app settings in Firebase,e.g.:
> `./gradlew signingReport`

This app uses [Firebase](https://firebase.google.com/) so you need to set-up a project and place the generated `google-services.json` into the `app/` directory.

### Google Cloud Functions

This project contains some Google Cloud Functions you need to setup in `google_cloud_functions/`:

- `addUserToDB`
    - Environment: 1st gen
    - Trigger: Firebase Authentication - create

## Contributing

- **Linting:** run the `ktlintFormat` Gradle task.
- **Documentation:** run the `dokkaHtml` Gradle task.
  - Dokka output will be generated to: `app/build/dokka/html/` 
- **Testing:** run the `test` Gradle task.
- **Test Coverage:** run the `connectedCheck` Gradle task.
  - Jacoco report will be generated to: `app/build/reports/androidTests/connected/debug/`

## Wireframes

Here are some of the original wireframes/sketches of the design for the app.

|                         App                         |                                Sequencer                                |
|:---------------------------------------------------:|:-----------------------------------------------------------------------:|
| ![app_wireframe.png](.github/img/app_wireframe.png) | ![melody-sequencer-sketch.jpg](.github/img/melody-sequencer-sketch.jpg) |

## License

The code in this repository is licensed under the [MIT License](https://opensource.org/license/mit), see [LICENSE.md](./LICENSE.md) for more details.
