"""
Created by Jason
2018/08/18
Purpose: Email function.
"""

from __future__ import unicode_literals

import threading

from django.conf import settings
from django.core.mail import EmailMultiAlternatives
from django.template.loader import render_to_string

subject = "New user registered on MacQuest Event Website"


# Send email in thread.
class SendRegisterEmailinThread(threading.Thread):
    """send html email"""

    def __init__(self, subject, send_from, to_list, ctx, email_template_text, email_template_html, fail_silently=True):
        self.subject = subject
        self.email_text_content = render_to_string(email_template_text, ctx)
        self.email_html_content = render_to_string(email_template_html, ctx)
        self.send_from = send_from
        self.to_list = to_list
        self.fail_silently = fail_silently  # 默认发送异常不报错
        threading.Thread.__init__(self)

    def run(self):
        # msg = EmailMessage(self.subject, self.html_content, self.send_from, self.to_list)
        msg = EmailMultiAlternatives(subject, self.email_text_content, self.send_from, self.to_list)
        msg.attach_alternative(self.email_html_content, "text/html")
        msg.send(self.fail_silently)
