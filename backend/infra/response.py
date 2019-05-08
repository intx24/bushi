import json
from abc import ABCMeta, abstractmethod


class BaseResponse(metaclass=ABCMeta):

    @abstractmethod
    def to_json(self):
        pass


class ChallengeResponse(BaseResponse):

    def __init__(self, challenge_str):
        self._challenge_str = challenge_str

    def to_json(self):
        return json.dumps({'challenge': self._challenge_str},
                          ensure_ascii=False)


class SuccessResponse(BaseResponse):

    def to_json(self):
        return json.dumps({'ok': True}, ensure_ascii=False)


class ErrorResponse(BaseResponse):

    def __init__(self, message=None):
        self._message = message

    def to_json(self):
        response = {'ok': False}
        if self._message is not None:
            response['message'] = self._message
        return json.dumps(response, ensure_ascii=False)
