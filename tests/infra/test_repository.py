import unittest
from unittest.mock import patch

from firebase_admin.db import TransactionError

from backend.infra.repository import FirebaseRepository, SlackRepository
from backend.infra.request import SlackPostMessageRequest


class FirebaseRepositoryTest(unittest.TestCase):
    TEST_OBJECT = {
        'text': 'hello!',
        'name': 'bushi',
        'icon_url': 'https://google.com',
        'icon_emoji': ':bushi:'
    }

    def setUp(self):
        self.transaction_error = TransactionError('')
        self.patcher = patch('backend.infra.repository.db.reference')
        self.mock_ref = self.patcher.start().return_value

    def test_constructor_calls_get_once(self):
        self._init_reference_mock({})
        FirebaseRepository()
        self.mock_ref.get.assert_called_once()

    def test_not_creates_object_when_trigger_already_exists(self):
        initial_data = {'newTrigger': {
            'text': 'world',
            'name': 'bushi2',
            'icon_url': 'https://yahoo.com',
            'icon_emoji': ':bushi2:'
        }}
        self._init_reference_mock(initial_data)
        self.mock_ref.return_value.transaction.side_effect = TransactionError('')

        target = FirebaseRepository()
        target.create('newTrigger', self.TEST_OBJECT)
        function = self.mock_ref.transaction.call_args[0][0]
        self.assertIsNone(function(self.TEST_OBJECT))

        target_cache = target.get()
        self.assertEqual(initial_data, target_cache)
        self.mock_ref.transaction.assert_called_once()
        self.mock_ref.get.assert_called_once()

    def _init_reference_mock(self, initial_data):
        self.mock_ref.get.return_value = (initial_data, 'etag')
        self.mock_ref.get_if_changed.return_value = False, None, 'etag'

    def tearDown(self):
        self.patcher.stop()


class SlackRepositoryTest(unittest.TestCase):

    def setUp(self):
        self._init_patch() 
        self.test_request = SlackPostMessageRequest(
            channel='general',
            text='this is test request',
            name='bushi',
            icon_emoji=':bushi:'
        )

    def _init_patch(self):
        self.patchers = [
            patch('backend.infra.repository.requests.post'),
            patch('backend.infra.repository.os')
        ]
        self.patched = [p.start() for p in self.patchers]
        self.mock_post = self.patched[0]
        self.patched[0].return_value.get.return_value = True
        self.patched[1].environ = {'POST_TOKEN': 'valid_token'}

    def test_post_message_calls_only_with_authorization_header(self):
        SlackRepository().post_message(self.test_request)
        self.mock_post.assert_called_once()
        _, kwargs = self.mock_post.call_args
        headers = kwargs['headers']
        self.assertEqual(headers['Content-Type'], 'application/json; charset=utf-8')
        self.assertEqual(headers['Authorization'], 'Bearer valid_token')
        self.assertEqual(self.test_request.to_json(), kwargs['data'].decode('utf-8'))

    def test_post_message_returns_True_when_ok_in_response_json_is_True(self):
        self.mock_post.return_value.json.return_value = {"ok": True}
        is_success = SlackRepository().post_message(self.test_request)
        self.mock_post.assert_called_once()
        self.assertTrue(is_success)

    def test_post_message_returns_False_when_ok_in_response_json_is_False(self):
        self.mock_post.return_value.json.return_value = {"ok": False}
        is_success = SlackRepository().post_message(self.test_request)
        self.mock_post.assert_called_once()
        self.assertFalse(is_success)

    def tearDown(self):
        for p in self.patchers:
            p.stop()
