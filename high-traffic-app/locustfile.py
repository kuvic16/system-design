import random
import string
from typing import List

from locust import HttpUser, between, task


class JobApplicationUser(HttpUser):
    """Load-test user for job application CRUD endpoints."""

    host = "http://localhost:8080"
    wait_time = between(1, 3)

    def on_start(self) -> None:
        self.application_ids: List[int] = []

    def _build_payload(self) -> dict:
        suffix = "".join(random.choices(string.ascii_lowercase + string.digits, k=6))
        return {
            "name": f"User {suffix}",
            "email": f"{suffix}@example.com",
            "jobId": random.randint(1, 1000),
            "resumeUrl": f"https://example.com/resumes/{suffix}.pdf",
        }

    @task(3)
    def create_application(self) -> None:
        payload = self._build_payload()
        with self.client.post(
            "/api/job-applications",
            json=payload,
            name="POST /api/job-applications",
            catch_response=True,
        ) as response:
            if response.status_code in [200, 202]:
                response_data = response.json()
#                 app_id = response_data.get("id")
#                 if isinstance(app_id, int):
#                     self.application_ids.append(app_id)
                response.success()
            else:
                response.failure(f"Expected 201, got {response.status_code}")

    @task(5)
    def get_all_applications(self) -> None:
        self.client.get("/api/job-applications", name="GET /api/job-applications")

#     @task(4)
#     def get_application_by_id(self) -> None:
#         if not self.application_ids:
#             return
#
#         app_id = random.choice(self.application_ids)
#         self.client.get(
#             f"/api/job-applications/{app_id}",
#             name="GET /api/job-applications/{id}",
#         )
#
#     @task(2)
#     def update_application(self) -> None:
#         if not self.application_ids:
#             return
#
#         app_id = random.choice(self.application_ids)
#         payload = self._build_payload()
#         self.client.put(
#             f"/api/job-applications/{app_id}",
#             json=payload,
#             name="PUT /api/job-applications/{id}",
#         )
#
#     @task(1)
#     def delete_application(self) -> None:
#         if not self.application_ids:
#             return
#
#         app_id = random.choice(self.application_ids)
#         with self.client.delete(
#             f"/api/job-applications/{app_id}",
#             name="DELETE /api/job-applications/{id}",
#             catch_response=True,
#         ) as response:
#             if response.status_code == 204:
#                 self.application_ids.remove(app_id)
#                 response.success()
#             elif response.status_code == 404:
#                 # If it was deleted elsewhere, remove local reference.
#                 self.application_ids.remove(app_id)
#                 response.success()
#             else:
#                 response.failure(f"Unexpected status code {response.status_code}")

