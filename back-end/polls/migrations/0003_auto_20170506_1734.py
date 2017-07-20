# -*- coding: utf-8 -*-
# Generated by Django 1.10.6 on 2017-05-06 21:34
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('polls', '0002_auto_20170320_1910'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='information',
            name='location',
        ),
        migrations.AddField(
            model_name='information',
            name='latitude',
            field=models.TextField(default=''),
        ),
        migrations.AddField(
            model_name='information',
            name='longitude',
            field=models.TextField(default=''),
        ),
    ]