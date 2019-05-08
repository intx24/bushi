import unittest
from backend.infra import response


class ChallengeResponseTest(unittest.TestCase):

    def test_get_json_returns_valid_json(self):
        result = response.ChallengeResponse('challenge request').to_json()
        self.assertEqual('{"challenge": "challenge request"}', result)


class SuccessResponseTest(unittest.TestCase):

    def test_get_json_returns_valid_json(self):
        self.assertEqual('{"ok": true}', response.SuccessResponse().to_json())


class ErrorResponseTest(unittest.TestCase):

    def test_get_json_returns_valid_json(self):
        self.assertEqual('{"ok": false}', response.ErrorResponse().to_json())

    def test_to_json_returns_message_containing_json(self):
        target = response.ErrorResponse('some error reason')
        expected = '{"ok": false, "message": "some error reason"}'
        self.assertEqual(expected, target.to_json())


