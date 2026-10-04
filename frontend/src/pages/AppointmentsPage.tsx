import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";

type Appointment = {
  appointmentId: number;
  slotId: number;
  serviceName: string;
  slotDate: string;
  startTime: string;
  endTime: string;
  bookedAt: string;
  appointmentStatus: string;
};

export default function AppointmentsPage() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAppointments();
  }, []);

  async function loadAppointments() {
    setLoading(true);
    setMessage("");

    try {
      const response = await fetch("/api/appointments/me", {
        credentials: "include",
      });

      if (response.status === 401 || response.status === 403) {
        setMessage("You must be logged in as a customer.");
        setAppointments([]);
        return;
      }

      if (!response.ok) {
        setMessage("Unable to load appointments.");
        setAppointments([]);
        return;
      }

      const data: Appointment[] = await response.json();

      setAppointments(data);

      if (data.length === 0) {
        setMessage("You do not have any appointments.");
      }
    } catch {
      setMessage("Unable to connect to the server.");
    } finally {
      setLoading(false);
    }
  }

  async function cancelAppointment(appointmentId: number) {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this appointment?"
    );

    if (!confirmed) {
      return;
    }

    setMessage("");

    try {
      const response = await fetch(
        `/api/appointments/${appointmentId}`,
        {
          method: "DELETE",
          credentials: "include",
        }
      );

      if (response.status === 401 || response.status === 403) {
        setMessage("You are not allowed to cancel this appointment.");
        return;
      }

      if (response.status === 404) {
        setMessage("Appointment not found.");
        return;
      }

      if (!response.ok) {
        setMessage("Unable to cancel appointment.");
        return;
      }

      setMessage("Appointment cancelled.");

      await loadAppointments();
    } catch {
      setMessage("Unable to connect to the server.");
    }
  }

  return (
    <div className="page-shell">
      <Navbar />

      <main className="page-content">
        <div className="page-title">
          <h1>My Appointments</h1>
          <p>
            View your upcoming and previous appointments.
          </p>
        </div>

        <div className="message-box">
          {loading ? "Loading appointments..." : message}
        </div>

        <div className="appointments-list">
          {appointments.map((appointment) => (
            <div
              key={appointment.appointmentId}
              className="appointment-card"
            >
              <div className="appointment-card-header">
                <div>
                  <strong>
                    {appointment.serviceName}
                  </strong>

                  <div className="appointment-date">
                    {appointment.slotDate}
                  </div>
                </div>

                <span className="status-badge">
                  {appointment.appointmentStatus}
                </span>
              </div>

              <div className="appointment-card-body">
                <div className="appointment-detail">
                  <span>Time</span>

                  <strong>
                    {formatTime(appointment.startTime)}
                    {" - "}
                    {formatTime(appointment.endTime)}
                  </strong>
                </div>

                <div className="appointment-detail">
                  <span>Appointment</span>

                  <strong>
                    #{appointment.appointmentId}
                  </strong>
                </div>
              </div>

              {appointment.appointmentStatus === "BOOKED" && (
                <button
                  className="cancel-button"
                  type="button"
                  onClick={() =>
                    cancelAppointment(
                      appointment.appointmentId
                    )
                  }
                >
                  Cancel Appointment
                </button>
              )}
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

function formatTime(time: string) {
  const [hourString, minute] = time.split(":");

  let hour = Number(hourString);

  const suffix = hour >= 12 ? "PM" : "AM";

  hour = hour % 12;

  if (hour === 0) {
    hour = 12;
  }

  return `${hour}:${minute} ${suffix}`;
}