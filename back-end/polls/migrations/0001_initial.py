# -*- coding: utf-8 -*-
# Generated by Django 1.10.6 on 2017-03-13 20:39
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Information',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('age', models.IntegerField()),
                ('votes', models.IntegerField(default=0)),
                ('mood', models.BooleanField()),
                ('location', models.TextField()),
                ('gender', models.BooleanField()),
            ],
        ),
    ]
