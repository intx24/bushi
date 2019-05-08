import logging

from backend.infra.repository import FirebaseRepository
from backend.infra.response import SuccessResponse, ErrorResponse

_JSON_KEY_ICON_URL = 'icon_url'
_JSON_KEY_ICON_EMOJI = 'icon_emoji'
_JSON_KEY_NAME = 'name'
_JSON_KEY_RESPONSE = 'response_text'

_logger = logging.getLogger(__name__)


def _is_valid_icons(json_object):
    has_url = len(json_object.get(_JSON_KEY_ICON_URL, '').strip()) != 0
    has_emoji = len(json_object.get(_JSON_KEY_ICON_EMOJI, '').strip()) != 0
    return not (has_url and has_emoji)


def validate(func):
    def _validate(*args, **kwargs):
        if len(args) != 3 or args[2] is None:
            _logger.warning(f'Requested empty json for "{args[1]}"')
            return ErrorResponse()

        trigger, json_object = args[1], args[2]
        if trigger is None or len(trigger.strip()) == 0:
            _logger.warning(f'Requested empty trigger with {json_object}')
            return ErrorResponse('Cannot be set empty trigger')

        if not _is_valid_icons(json_object):
            _logger.warning(f'Requested icon emoji and ur at the same time with {json_object}')
            return ErrorResponse('Cannot be set icon emoji and url at the same time')

        emoji = json_object.get(_JSON_KEY_ICON_EMOJI, '')
        response = json_object.get(_JSON_KEY_RESPONSE, '')

        if len(response.strip()) == 0:
            _logger.warning(f'Requested empty response: {json_object}')
            return ErrorResponse('Cannot be set empty response')

        if len(emoji) > 0 and (not (emoji.startswith(':') or emoji.endswith(':'))):
            _logger.warning(f'Requested non-emoji format string with {json_object}')
            return ErrorResponse('Icon emoji should start and end with ":"')

        _logger.info(f'Pass validation: {trigger}, {json_object}')
        return func(*args, **kwargs)

    return _validate


class ApiHandler:

    def __init__(self):
        self._firebase_repo = FirebaseRepository()

    @validate
    def create(self, trigger, json_object):
        err = self._firebase_repo.create(trigger, json_object)
        if err is None:
            _logger.info(f'Success create trigger of {trigger}: {json_object}')
            return SuccessResponse()
        else:
            _logger.warning(f'Failed to create "{trigger}"'
                            f' as {json_object} (reason: {err})')
            return ErrorResponse(err)

    @validate
    def update(self, trigger, json_object):
        err = self._firebase_repo.update(trigger, json_object)
        if err is None:
            _logger.info(f'Success update trigger of {trigger}: {json_object}')
            return SuccessResponse()
        else:
            _logger.warning(f'Failed to update "{trigger}"'
                            f' as {json_object} (reason: {err})')
            return ErrorResponse(err)

    def delete(self, trigger):
        err = self._firebase_repo.delete(trigger)
        if err is None:
            _logger.info(f'Success delete trigger of {trigger}')
            return SuccessResponse()
        else:
            _logger.warning(f'Failed to delete "{trigger}" (reason: {err})')
            return ErrorResponse(err)
