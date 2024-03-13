# MelodyShare

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/ZX5kW5CC)

## Setup

### Firebase

> [!NOTE]
> Remember to generate and add the SHA to your Android app settings in Firebase,e.g.:
> `./gradlew signingReport`

This app uses [Firebase](https://firebase.google.com/) so you need to set-up a project and place the generated `google-services.json` into the `app/` directory.

### Google Cloud Functions

This project contains some Google Cloud Functions you need to setup in `./google_cloud_functions/`:

- `addUserToDB`
    - Environment: 1st gen
    - Trigger: Firebase Authentication - create

## License

The code in this repository is licensed under the [MIT License](https://opensource.org/license/mit), see [LICENSE.md](./LICENSE.md) for more details.
