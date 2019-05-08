import logging
from abc import abstractmethod, ABCMeta

from backend.infra.repository import FirebaseRepository, SlackRepository, EnvRepository
from backend.infra.request import SlackPostMessageRequest
from backend.infra.response import SuccessResponse, ErrorResponse, ChallengeResponse


class SlackEventHandlerContainer:
    _JSON_KEY_TYPE = 'type'

    _TYPE_URL_VERIFICATION = 'url_verification'
    _TYPE_EVENT_CALLBACK = 'event_callback'
    _BAD_EVENT = 'bad_event'

    def __init__(self):
        self._handlers = {
            self._TYPE_URL_VERIFICATION: ChallengeEventHandler(),
            self._TYPE_EVENT_CALLBACK: SlackEventHandler(),
            self._BAD_EVENT: CommonEventHandler()
        }

    def get_handler(self, json_object):
        if json_object is None:
            return self._handlers[self._BAD_EVENT]
        event_type = json_object.get(self._JSON_KEY_TYPE, None)
        return self._handlers.get(event_type, self._handlers[self._BAD_EVENT])


class EventHandler(metaclass=ABCMeta):
    _TOKEN_KEY = 'token'

    def __init__(self):
        self._env_repository = EnvRepository()

    @abstractmethod
    def handle_event(self, event):
        pass

    def _validate_token(self, event):
        return event.get(self._TOKEN_KEY, None) == self._env_repository.get_bot_token()


class CommonEventHandler(EventHandler):

    def __init__(self):
        super().__init__()
        self._logger = logging.getLogger(self.__class__.__name__)

    def handle_event(self, event):
        self._logger.warning(f'Unknown event received: {event}')
        return ErrorResponse()


class ChallengeEventHandler(EventHandler):
    _CHALLENGE_KEY = 'challenge'

    def __init__(self):
        super().__init__()
        self._logger = logging.getLogger(self.__class__.__name__)

    def handle_event(self, event):
        if self._validate(event):
            self._logger.info(f'Challenge request received')
            return ChallengeResponse(event[self._CHALLENGE_KEY])
        else:
            self._logger.warning(f'Invalid challenge event: {event}')
            return ErrorResponse()

    def _validate(self, event):
        return all((
            self._TOKEN_KEY in event,
            self._CHALLENGE_KEY in event,
            self._validate_token(event)
        ))


class SlackEventHandler(EventHandler):

    def __init__(self):
        super().__init__()
        self._logger = logging.getLogger(self.__class__.__name__)
        self._trigger_repo = FirebaseRepository()
        self._slack_repo = SlackRepository()
        self._last_event_ts = 0

    def handle_event(self, event_callback):
        """Handle slack message event from Event API.

        :param event_callback: 'event_callback' event from slack
        :return: always SuccessResponse
        """
        if not self._should_handle_event(event_callback):
            return SuccessResponse()

        event = event_callback['event']
        self._logger.info(f'Received message event: {event}')
        text = event['text']
        response_object = self._find_trigger(text)
        if response_object is None:
            return ErrorResponse()
        self._logger.info(f'Found response: {response_object}')
        channel = event['channel']
        self._slack_repo.post_message(
            self._create_request(channel, response_object)
        )
        return SuccessResponse()

    def _find_trigger(self, text):
        triggers = self._trigger_repo.get()
        key = next(filter(lambda t: t in text, triggers), None)
        return triggers.get(key, None)

    @staticmethod
    def _create_request(channel, response_obj):
        return SlackPostMessageRequest(
            channel=channel,
            text=response_obj['response_text'],
            name=response_obj.get('name', ''),
            icon_url=response_obj.get('icon_url', ''),
            icon_emoji=response_obj.get('icon_emoji', '')
        )

    def _should_handle_event(self, event_callback):
        if not self._validate_event(event_callback):
            self._logger.warning('Invalid event_callback')
            return False

        event = event_callback['event']
        if not self._validate_message_event(event):
            self._logger.warning('Invalid event')
            return False

        sub_type = event.get('subtype', None)
        if sub_type is not None:
            self._logger.info('The event is not user posted message event')
            return False

        event_ts = float(event['ts'])
        if event_ts <= self._trigger_repo.get_processed():
            self._logger.info('Already processed')
            return False

        self._trigger_repo.update_processed(event_ts)
        return True

    def _validate_event(self, event_callback):
        return all((
            self._validate_token(event_callback),
            event_callback.get('team_id', None) == 'TA3V6662W',
            'event' in event_callback,
        ))

    @staticmethod
    def _validate_message_event(event):
        return all((
            event.get('type', None) == 'message',
            'text' in event,
            'ts' in event,
            'channel' in event,
        ))
