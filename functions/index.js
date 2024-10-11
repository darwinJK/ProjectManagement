/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 *
 *  Ensure the user's token is saved in Firestore or Realtime Database
 *
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendLoginNotification = functions.auth.user().onCreate((user) => {
  const fcmToken = user.fcmToken;

  const payload = {
    notification: {
      title: "Login Successful",
      body: "You have successfully logged in!",
    },
  };

  return admin.messaging()
      .sendToDevice(fcmToken, payload)
      .then((response) => {
        console.log("Successfully sent message:", response);
      })
      .catch((error) => {
        console.error("Error sending message:", error);
      });
});
