import logging

import bottle
import firebase_admin
from bottle import request, response
from firebase_admin import credentials

from backend.domain.api_handler import ApiHandler
from backend.domain.event_handler import SlackEventHandlerContainer
from backend.infra.response import ErrorResponse

logging.basicConfig(level=logging.INFO)
_cred = credentials.Certificate('credential.json')
firebase_admin.initialize_app(_cred, {
    'databaseURL': 'https://bushi-py.firebaseio.com/'
})
_handler_container = SlackEventHandlerContainer()
_api_handler = ApiHandler()
_logger = logging.getLogger(__name__)

app = bottle.Bottle()


def enable_cors(func):
    def _enable_cors(*args, **kwargs):
        response.headers['Access-Control-Allow-Origin'] = 'https://bushi-py.appspot.com'
        response.headers['Access-Control-Allow-Methods'] = 'PUT, PATCH, DELETE, OPTIONS'
        response.headers['Access-Control-Allow-Headers'] = (
            'Origin, Accept, Content-Type, X-Requested-With, X-CSRF-Token'
        )
        if request.method != 'OPTIONS':
            return func(*args, **kwargs)
    return _enable_cors


@app.route('/bushi/', method='post')
def bushi():
    event_callback = request.json
    handler = _handler_container.get_handler(event_callback)
    result = handler.handle_event(event_callback)
    response.set_header('Content-Type', 'application/json')
    return result.to_json()


@app.route('/bushi/', method='get')
def bushi_edit():
    bottle.redirect('/www/')


@app.route('/api/trigger/<trigger>', method='put')
@enable_cors
def create(trigger):
    return _api_handler.create(trigger, request.json).to_json()


@app.route('/api/trigger/<trigger>', method='patch')
@enable_cors
def update(trigger):
    return _api_handler.update(trigger, request.json).to_json()


@app.route('/api/trigger/<trigger>', method='delete')
@enable_cors
def delete(trigger):
    return _api_handler.delete(trigger)


@app.route('/api/trigger/<trigger>', method='options')
@enable_cors
def options(_):
    return


if __name__ == '__main__':
    app.run(debug=True, reloader=True)
