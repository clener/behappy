from __future__ import unicode_literals
from django.db import models

class Information(models.Model):
	age = models.IntegerField()
	gender = models.IntegerField()
	mood = models.IntegerField()
	latitude = models.TextField(default = "")
	longitude = models.TextField(default = "")
	city = models.TextField(default = "")
	day = models.IntegerField(default = -1)
	month = models.IntegerField(default = -1)
	year = models.IntegerField(default = -1)

	def __str__(self):
		return ("age: " + str(self.age) + " gender: " + str(self.gender) +\
			" mood: " + str(self.mood) + " latitude: " + str(self.latitude) +\
			" longitude: " + str(self.longitude) + " day: " + str(self.day) +\
			" city" + str(self.city) + " month: " + str(self.month) +\
			" year: " + str(self.year))
