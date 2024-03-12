// https://stackoverflow.com/a/59445276/19020549
const Firestore = require('@google-cloud/firestore');

const firestore = new Firestore({
  projectId: process.env.GOOGLE_CLOUD_PROJECT,
});

exports.addUserToDB = user => {
  console.log(`Function triggered by change to user: ${user.uid}`);
  console.log(`Created at: ${user.metadata.createdAt}`);

  firestore
    .collection('users')
    .doc(user.uid)
    .set(JSON.parse(JSON.stringify(user)));
};
