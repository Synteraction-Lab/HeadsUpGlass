# coding=utf-8

import requests
import time

DEVICE_IP_GLASS = '192.168.210.231'

MAX_RETRY_ATTEMPT = 3


def get_notification_url():
    return f'http://{DEVICE_IP_GLASS}:8080/notifiers/12/'


def get_display_url():
    return f'http://{DEVICE_IP_GLASS}:8080/displays/10/'


def sleep_seconds(seconds=0.2):
    count = 0
    delay_seconds = 0.05
    total_count = seconds * 20

    while count < total_count:
        count += 1
        time.sleep(delay_seconds)


# return True if success else False
def send_request(url, data):
    print('sendRequest: {}, {}'.format(url, data))

    try:
        x = requests.post(url, data=str(data).encode('ascii', 'ignore'), timeout=3.5)
        print("{} \n".format(x.status_code))
        return True
    except Exception as e:
        print('Failed to send request', e.__class__)
        return False


def send_notification_data(notification):
    attempt = 0
    success = False
    while not success and attempt < MAX_RETRY_ATTEMPT:
        success = send_request(get_notification_url(), notification)
        attempt += 1

        if not success and attempt < MAX_RETRY_ATTEMPT:
            sleep_seconds(0.8)

    return success


NOTIFICATIONS = [
    {
        "type": 0,  # toast notification
        "id": 1,
        "message": "Please recharge before 5% left",
    },
    {
        "type": 1,  # heads-up notification
        "id": 2,
        "duration": 6000,
        "when": 0,
        "smallIcon": "#ffffffff acc",
        "appName": "Contact",
        "title": "Details have updated",
        "message": "Please refresh app to reload",
    },
    {
        "type": 1,  # heads-up notification
        "id": 3,
        "duration": 6000,
        "when": 3000,
        "smallIcon": "#ffffffff chat",
        "appName": "Messenger",
        "title": "John sent a message",
        "message": "I am reaching the destination",
    },
]

notification_count = 0


def display_next_notification():
    global notification_count

    if notification_count >= len(NOTIFICATIONS):
        notification_count = 0

    send_notification_data(NOTIFICATIONS[notification_count])

    notification_count += 1

    sleep_seconds(8)


_res = ''
while _res != 'n':
    _res = input("Continue? (y/n)")

    if _res == 'y':
        display_next_notification()
