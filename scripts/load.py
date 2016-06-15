from locust import HttpLocust, TaskSet, task

class UserBehavior(TaskSet):

    @task(2)
    def hello_secured(self):
        self.client.get("/api/secured", auth=("joao", "1234"))

    @task(1)
    def printshops(self):
        self.client.get("/printshops")

class ProxyPrintUser(HttpLocust):
    task_set = UserBehavior
    min_wait=5000
    max_wait=9000
