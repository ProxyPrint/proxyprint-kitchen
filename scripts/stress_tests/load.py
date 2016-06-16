from locust import HttpLocust, TaskSet, task
from faker import Factory
import requests
import random

fake = Factory.create()
headers = {"content-type": "application/json"}

class UserBehavior(TaskSet):

    @task(2)
    def login(self):
        data = {"username": "joao", "password": "1234"}
        self.client.post("/login",data=data)

    @task(1)
    def printshops(self):
        self.client.get("/printshops")

    """
    Print Request 1st step - Get nearest printshops
    """
    @task(3)
    def get_nearest_printshops(self):
        data = {"latitude": random.randrange(1,40), "longitude": random.randrange(1,40)}
        self.client.get("/printshops/nearest",data=data)


class ProxyPrintUser(HttpLocust):
    task_set = UserBehavior
    min_wait=5000
    max_wait=9000
