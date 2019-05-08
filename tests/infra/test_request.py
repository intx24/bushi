import json
import unittest

from backend.infra import request


class SlackPostMessageRequestTest(unittest.TestCase):

    def test_to_json_returns_valid_json(self):
        values = [
            ['general', 'hello!'],
            ['general', 'hello!', 'bushi'],
            ['general', 'hello!', 'bushi', 'https://google.com'],
            ['general', 'hello!', 'bushi', '', 'https://google.com']
        ]
        expect = [
            json.dumps({'channel': 'general', 'text': 'hello!'}),
            json.dumps({'channel': 'general', 'text': 'hello!', 'username': 'bushi'}),
            json.dumps({'channel': 'general', 'text': 'hello!',
                        'username': 'bushi', 'icon_url': 'https://google.com'}),
            json.dumps({'channel': 'general', 'text': 'hello!',
                        'username': 'bushi', 'icon_emoji': 'https://google.com'}),
        ]
        for v, e in zip(values, expect):
            self.assertEqual(e, request.SlackPostMessageRequest(*v).to_json())

    def test_init_raises_ValueError_when_url_and_emoji_are_passed_same_time(self):
        with self.assertRaises(ValueError):
            request.SlackPostMessageRequest(
                channel='general',
                text='hello!',
                name='bushi',
                icon_url='https://google.com',
                icon_emoji=':bushi:'
            )

