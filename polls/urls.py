from django.conf.urls import url
from . import views

urlpatterns = [
	#url(r'^$', views.index, name = 'index'),
	url(r'^add/$', views.add),
	url(r'^retreive/$', views.retreive),
	url(r'^retreive_all/$', views.retreive_all),
]