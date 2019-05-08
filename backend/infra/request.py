import json
from abc import ABCMeta, abstractmethod


class BaseRequest(metaclass=ABCMeta):

    @abstractmethod
    def to_json(self):
        pass


class SlackPostMessageRequest(BaseRequest):

    def __init__(self, channel, text, name=None, icon_url='', icon_emoji=''):
        self._channel = channel
        self._text = text
        self._name = name
        icon_url = None if len(icon_url) == 0 else icon_url
        icon_emoji = None if len(icon_emoji) == 0 else icon_emoji
        if icon_url is not None and icon_emoji is not None:
            raise ValueError('Cannot set icon url and emoji at the same time')
        self._icon_url = icon_url
        self._icon_emoji = icon_emoji

    def to_json(self):
        value = {
            'channel': self._channel,
            'text': self._text
        }
        if self._name is not None:
            value['username'] = self._name

        if self._icon_url is not None:
            value['icon_url'] = self._icon_url
        elif self._icon_emoji is not None:
            value['icon_emoji'] = self._icon_emoji

        return json.dumps(value, ensure_ascii=False)

