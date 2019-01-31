from locust import HttpLocust, TaskSet, task

class ReservationSet(TaskSet):
    @task(1)
    def doReservation(self):
        self.client.post("/reservations", json={"email":"locust@test.com", "name":"Locust Load Test", "checkIn":"2019-02-10", "checkOut":"2019-02-13"})

class MyLocust(HttpLocust):
    task_set = ReservationSet
    min_wait = 5000
    max_wait = 15000