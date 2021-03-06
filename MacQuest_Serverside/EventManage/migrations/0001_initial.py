# Generated by Django 2.1.4 on 2018-12-09 00:54

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion
import django.utils.timezone


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='ParentEvent',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100, unique=True)),
                ('description', models.TextField(max_length=255)),
                ('authentication', models.CharField(choices=[('Public', 'Public'), ('Private', 'Private')], default='Public', max_length=8)),
                ('private_password', models.CharField(blank=True, max_length=100)),
                ('start_time', models.DateTimeField(blank=True, null=True)),
                ('end_time', models.DateTimeField(blank=True, null=True)),
                ('isActive', models.BooleanField(default=False)),
                ('owner', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
                ('viewer', models.ManyToManyField(related_name='viewer', to=settings.AUTH_USER_MODEL)),
                ('waitingList', models.ManyToManyField(related_name='waitingList', to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='SubEvent',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100)),
                ('isActive', models.BooleanField(default=True)),
                ('location_building', models.CharField(max_length=100)),
                ('location_lat', models.FloatField()),
                ('location_lng', models.FloatField()),
                ('floor', models.IntegerField()),
                ('room', models.CharField(blank=True, max_length=100)),
                ('desc_has_url', models.BooleanField(default=False)),
                ('desc_url', models.URLField()),
                ('desc_show_in_app', models.CharField(max_length=100)),
                ('desc_show_in_website', models.CharField(max_length=999)),
                ('action_title', models.CharField(blank=True, max_length=100)),
                ('action_has_action', models.BooleanField(default=False)),
                ('action_desc', models.CharField(blank=True, max_length=100)),
                ('action_has_url', models.BooleanField(default=False)),
                ('action_url', models.URLField()),
                ('start_time', models.DateTimeField()),
                ('end_time', models.DateTimeField()),
                ('parentEvent', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='EventManage.ParentEvent')),
            ],
        ),
        migrations.CreateModel(
            name='UserLocation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('DeviceID', models.UUIDField()),
                ('time', models.DateTimeField(default=django.utils.timezone.now)),
            ],
        ),
    ]
