import logging
import os

import requests
from firebase_admin import db
from firebase_admin.db import ApiCallError


class FirebaseRepository:

    def __init__(self):
        self._logger = logging.getLogger(self.__class__.__name__)
        ref = db.reference('/triggers')
        self._responses, self._etag = ref.get(etag=True)

    def get(self):
        self._get_if_changed()
        return self._responses

    def get_processed(self):
        ref = db.reference('/processed')
        return ref.get()

    def update_processed(self, ts):
        def update(value):
            if value is not None or ts < value:
                return
            else:
                return ts

        ref = db.reference('/processed')
        try:
            ref.transaction(update)
        except (db.TransactionError, ValueError):
            pass

    def _get_if_changed(self):
        ref = db.reference('/triggers')
        changed, values, etag = ref.get_if_changed(etag=self._etag)
        if changed:
            self._logger.info('Update cached triggers')
            self._responses = values
            self._etag = etag

    def create(self, trigger, new_object):
        ref = db.reference('/triggers/' + trigger)

        def update(value):
            if value is None:
                self._logger.info(
                    f'Create new object for trigger "{trigger}": {new_object}'
                )
                return new_object
            else:
                self._logger.warning(
                    'Transaction aborted. '
                    f'Trigger already exists: "{trigger}"'
                )
                return

        try:
            ref.transaction(update)
            self._get_if_changed()
            return None
        except db.TransactionError as e:
            self._logger.warning('Transaction error on create')
            return 'Transaction error'
        except ValueError:
            return 'The trigger already exists'

    def delete(self, trigger):
        self._get_if_changed()
        ref = db.reference('/triggers/' + trigger)
        try:
            ref.delete()
            del self._responses[trigger]
            self._logger.info(f'Delete trigger: "{trigger}"')
            return None
        except (ApiCallError, KeyError) as e:
            return 'Api call error'

    def update(self, trigger, new_object):
        self._get_if_changed()
        ref = db.reference('/triggers/' + trigger)
        try:
            ref.set(new_object)
            self._logger.info(
                f'Response object for "{trigger}" is updated: {new_object}'
            )
            self._responses[trigger] = new_object
            return None
        except ApiCallError as e:
            self._logger.warning('Transaction error on create')
            return 'Api call error'


class SlackRepository:
    _POST_MESSAGE_ENDPOINT = 'https://slack.com/api/chat.postMessage'

    def __init__(self):
        self._logger = logging.getLogger(self.__class__.__name__)
        token = EnvRepository().get_post_token()
        self._header = {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json; charset=utf-8'
        }

    def post_message(self, req):
        request_json = req.to_json()
        res = requests.post(self._POST_MESSAGE_ENDPOINT,
                            headers=self._header,
                            data=request_json.encode('utf-8'))
        self._logger.info(f'Post new message: {request_json}')
        response_json = res.json()
        is_success = response_json.get('ok', False)
        if not is_success:
            self._logger.warning(f'Failed to post message: {response_json}')
        return res.json().get('ok', False)


class EnvRepository:
    _KEY_BOT_TOKEN = 'BOT_TOKEN'
    _KEY_POST_TOKEN = 'POST_TOKEN'

    def __init__(self):
        self._environ = os.environ

    def get_bot_token(self):
        return self._environ[self._KEY_BOT_TOKEN]

    def get_post_token(self):
        return self._environ[self._KEY_POST_TOKEN]
