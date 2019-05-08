var config = {
  apiKey: "AIzaSyCMtTZ0mFBPyUx_Un_mkUtHPeuj3b6OLgY",
  authDomain: "bushi-py.firebaseapp.com",
  databaseURL: "https://bushi-py.firebaseio.com",
  projectId: "bushi-py",
  storageBucket: "bushi-py.appspot.com",
  messagingSenderId: "340006566869"
};

BASE_URL = 'http://localhost:8080/api/trigger/'

function DataHandler() {

  this.database = firebase.database()
  this.triggers = []

  riot.observable(this)
  this.database.ref('/triggers').on('value', (snapshot) => {
    let items = snapshot.val()
    this.triggers = Object.keys(items).map(t => {
      items[t].trigger = t
      return items[t]
    });
    this.trigger('itemUpdated')
  });

  this.add = function(trigger, data, callback) {
    fetch(BASE_URL + trigger, {
        method: 'PUT',
        mode: 'cors',
        credentials: 'omit',
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        },
        body: JSON.stringify(data)
    })
    .then(res => res.json().then(json => {
        if (json.ok === false) {
            callback(json.message)
        } else {
           callback()
        }
    }))
    .catch(err => callback(err))
  }
}
