const Firestore = require('@google-cloud/firestore');

const firestore = new Firestore({
  projectId: process.env.GOOGLE_CLOUD_PROJECT,
});

const admin = require('firebase-admin');
admin.initializeApp();

/**
 * Triggered by a change to a Firestore document.
 *
 * @param {!Object} event Event payload.
 * @param {!Object} context Metadata for the event.
 */
exports.sendFCM = async (event, context) => {
  // https://cloud.google.com/functions/docs/calling/cloud-firestore-1st-gen
  const resource = context.resource;
  // log out the resource string that triggered the function
  console.log('Function triggered by change to: ' +  resource);
  // now log the full event object
  console.log(JSON.stringify(event));

  // TODO: Add proper validation
  const senderUser = (await firestore
    .collection('users')
    .doc(event.value.fields.senderUid.stringValue)
    .get())
    .data()

  const receiverUser = (await firestore
    .collection('users')
    .doc(event.value.fields.receiverUid.stringValue)
    .get())
    .data()

  const melodyData = (await firestore
    .collection('melodies')
    .doc(event.value.fields.melodyId.stringValue)
    .get())
    .data()

  console.log(JSON.stringify(senderUser));
  console.log(JSON.stringify(receiverUser));
  console.log(JSON.stringify(melodyData));

  const payload = {
    topic: "shares",
    data: {
      senderUid: senderUser.uid,
      senderName: senderUser.displayName,
      senderPhoto: senderUser.photoURL,
      receiverUid: receiverUser.uid,
      receiverName: receiverUser.displayName,
      receiverPhoto: receiverUser.photoURL,
      melodyId: melodyData.id,
      melodyTitle: melodyData.title
    }
  };

  // TODO: Only send if both users have each other as friends.
  const response = await admin.messaging().send(payload);
};
