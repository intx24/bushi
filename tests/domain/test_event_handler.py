import unittest
from unittest.mock import patch, Mock

from backend.domain.event_handler import SlackEventHandler, ChallengeEventHandler, CommonEventHandler, \
    SlackEventHandlerContainer
from backend.infra.response import ErrorResponse, SuccessResponse


class HandlerContainerTest(unittest.TestCase):

    def setUp(self):
        self._init_patch()
        self.target = SlackEventHandlerContainer()

    def _init_patch(self):
        self.patchers = [
            patch('backend.infra.repository.db'),
            patch('backend.infra.repository.EnvRepository'),
        ]
        self.patched = [p.start() for p in self.patchers]
        self.patched[0].reference.return_value.get.return_value = (Mock(), Mock())

    def test_get_handler_returns_ChallengeEventHandler(self):
        result = self.target.get_handler(
            {'type': 'url_verification'}
        )
        self.assertIsInstance(result, ChallengeEventHandler)

    def test_get_handler_returns_MessageEventHandler(self):
        result = self.target.get_handler(
            {'type': 'event_callback'}
        )
        self.assertIsInstance(result, SlackEventHandler)

    def test_get_handler_returns_CommonEventHandler(self):
        events = [
            {},
            {'type': 'bushi'}
        ]
        for e in events:
            result = self.target.get_handler(e)
            self.assertIsInstance(result, CommonEventHandler)

    def test_get_handler_returns_same_object_on_called_with_same_type(self):
        event_types = ['url_verification' 'event_callback', 'bad_type']
        for t in event_types:
            result1 = self.target.get_handler({'type': t})
            result2 = self.target.get_handler({'type': t})
            self.assertIs(result1, result2)

    def tearDown(self):
        for p in self.patchers:
            p.stop()


class CommonEventHandlerTest(unittest.TestCase):

    def test_handle_event_returns_ErrorResponse(self):
        result = CommonEventHandler().handle_event({})
        self.assertIsInstance(result, ErrorResponse)


class ChallengeEventHandlerTest(unittest.TestCase):

    def setUp(self):
        self.patcher = patch('backend.infra.repository.os')
        mock_os = self.patcher.start()
        mock_os.environ = {'BOT_TOKEN': 'valid_token'}

    def test_handle_event_returns_ChallengeResponse(self):
        result = ChallengeEventHandler().handle_event({
            'token': 'valid_token',
            'challenge': 'challenge test',
            'type': 'url_verification'
        })
        self.assertEqual('{"challenge": "challenge test"}', result.to_json())

    def test_handle_event_returns_ErrorResponse_when_token_is_missing(self):
        result = ChallengeEventHandler().handle_event({
            'challenge': 'challenge test',
            'type': 'url_verification'
        })
        self.assertIsInstance(result, ErrorResponse)

    def test_handle_event_returns_ErrorResponse_when_challenge_is_missing(self):
        result = ChallengeEventHandler().handle_event({
            'token': 'valid_token',
            'type': 'url_verification'
        })
        self.assertIsInstance(result, ErrorResponse)

    def tearDown(self):
        self.patcher.stop()


class MessageEventHandlerTest(unittest.TestCase):
    TEST_MESSAGE_EVENT = {
        'token': 'valid token',
        'team_id': 'TA3V6662W',
        'event': {
            'text': 'test with mock',
            'user': 'user1',
            'type': 'message',
            'ts': 100,
            'channel': 'general'
        }
    }

    TEST_BOT_MESSAGE_EVENT = {
        'token': 'valid token',
        'team_id': 'TA3V6662W',
        'event': {
            'text': 'test with mock',
            'user': 'user1',
            'subtype': 'bot_message',
            'ts': 100,
            'channel': 'general'
        }
    }

    def setUp(self):
        self.init_patch()
        self.target = SlackEventHandler()
        self.mock_trigger_repo = self.target._trigger_repo
        self.mock_slack_repo = self.target._slack_repo
        self.mock_trigger_repo.get.return_value = {'mock': {
            'name': 'name1',
            'response_text': 'hello!',
            'icon_emoji': ':emoji:',
        }}
        self.mock_validate_token = self.patched[2]
        self.mock_validate_token.return_value = True

    def init_patch(self):
        self.patchers = [
            patch('backend.domain.event_handler.FirebaseRepository'),
            patch('backend.domain.event_handler.SlackRepository'),
            patch('backend.domain.event_handler.EventHandler._validate_token')
        ]
        self.patched = [p.start() for p in self.patchers]

    def test_handle_event_calls_each_method_once_with_valid_args(self):
        self.mock_trigger_repo.get_processed.return_value = 0
        result = self.target.handle_event(self.TEST_MESSAGE_EVENT)
        self.assertIsInstance(result, SuccessResponse)
        self.mock_trigger_repo.get.assert_called_once()
        self.mock_slack_repo.post_message.assert_called_once()
        args, _ = self.target._slack_repo.post_message.call_args
        self._assertEqualRequest(args, 'hello!')

    def test_handle_event_does_nothing_when_request_is_already_processed(self):
        self.mock_trigger_repo.get_processed.return_value = self.TEST_MESSAGE_EVENT['event']['ts']
        result = self.target.handle_event(self.TEST_MESSAGE_EVENT)
        self.assertIsInstance(result, SuccessResponse)
        self.mock_trigger_repo.get.assert_not_called()
        self.mock_slack_repo.post_message.assert_not_called()

    def test_handle_event_does_not_process_when_event_contains_subtype(self):
        result = self.target.handle_event(self.TEST_BOT_MESSAGE_EVENT)
        self.assertIsInstance(result, SuccessResponse)
        self.mock_trigger_repo.get.assert_not_called()
        self.mock_slack_repo.post_message.assert_not_called()

    def _assertEqualRequest(self, args, response_text):
        self.assertEqual(len(args), 1)
        self.assertEqual(args[0]._text, response_text)
        self.assertEqual(args[0]._channel, 'general')
        self.assertEqual(args[0]._name, 'name1')
        self.assertEqual(args[0]._icon_url, None)
        self.assertEqual(args[0]._icon_emoji, ':emoji:')

    def tearDown(self):
        for p in self.patchers:
            p.stop()
