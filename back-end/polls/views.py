from django.shortcuts import render
from django.http import HttpResponse
from polls.models import Information
from django.core import serializers
import json

def add(request):
	age = request.POST.get("age", False)
	gender = request.POST.get("gender", False)
	mood = request.POST.get("mood", False)
	latitude = request.POST.get("latitude", False)
	longitude = request.POST.get("longitude", False)
	city = request.POST.get("city", False)
	day = request.POST.get("day", False)
	month = request.POST.get("month", False)
	year = request.POST.get("year", False)
	i = Information(age = age, gender = gender, mood = mood, city = city,
		latitude = latitude, longitude = longitude, day = day, month = month,
		year = year)
	i.save()
	return HttpResponse("Yes")

def retreive(request):
	#Retreiving all entries as json array
	rows = Information.objects.all()
	data = serializers.serialize('json', list(rows))
	return HttpResponse(data)

def retreive_all(request):
	#Retreiving all entries as json array
	rows = Information.objects.all()
	data = serializers.serialize('json', list(rows))

	#Initializing variables
	jarr = json.loads(data)
	totalMales = 0
	totalFemales = 0
	happyMales = 0
	happyFemales = 0
	happyBounds = 0
	lowerAgeBound = int(float(request.POST.get("lowerAgeBound", False)))
	upperAgeBound = int(float(request.POST.get("upperAgeBound", False)))
	cityDict = {}

	#Starting for loop to iterate over json objects in order to
	#find how many happy people there are and in which city there
	#is the most
	for index in jarr:
		fields = index['fields']
		age = fields['age']
		mood = fields['mood']
		gender = fields['gender']
		city = fields['city']

		#If male
		if gender == 0:
			totalMales+=1

	        #If happy
			if mood == 1:
				happyMales+=1

				#If within age bounds
				if (lowerAgeBound <= age) and (upperAgeBound >= age):
					print True
					happyBounds+=1
				else:
					print False


				if city in cityDict:
					cityDict[city]+=1
				else:
					cityDict[city] = 1
	    #If female
		elif gender == 1:
			totalFemales+=1

			#If happy
			if mood == 1:
				happyFemales+=1
				
				#If within age bounds
				if (lowerAgeBound <= age) and (upperAgeBound >= age):
					print True
					happyBounds+=1
				else:
					print False

				if city in cityDict:
					cityDict[city]+=1
				else:
					cityDict[city] = 1


	#Finding happiest city and number of happy males and females
	happyCity = max(cityDict.iterkeys(), key=(lambda key: cityDict[key]))

	return HttpResponse(json.dumps({"happyMales":happyMales, "happyFemales":happyFemales, "happyCity":happyCity, "totalMales":totalMales, "totalFemales":totalFemales, "happyBounds":happyBounds}))
