import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";

type Slot = {
  slotId: number;
  barberName: string;
  serviceName: string;
  slotDate: string;
  startTime: string;
  endTime: string;
};

export default function ConfirmationPage() {
  const [appointment, setAppointment] = useState<Slot | null>(null);

  useEffect(() => {
    const savedAppointment =
      sessionStorage.getItem("confirmedAppointment");

    if (savedAppointment) {
      setAppointment(JSON.parse(savedAppointment));
    }
  }, []);

  return (
    <div className="page-shell">
      <Navbar />

      <main className="page-content confirmation-content">
        <div className="confirmation-icon">
          ✓
        </div>

        <div className="page-title">
          <h1>Appointment Confirmed</h1>
          <p>Your appointment has been successfully booked.</p>
        </div>

        {appointment ? (
          <div className="appointment-summary">
            <div className="summary-row">
              <span>Barber</span>
              <strong>{appointment.barberName}</strong>
            </div>

            <div className="summary-row">
              <span>Service</span>
              <strong>{appointment.serviceName}</strong>
            </div>

            <div className="summary-row">
              <span>Date</span>
              <strong>{appointment.slotDate}</strong>
            </div>

            <div className="summary-row">
              <span>Time</span>
              <strong>
                {formatTime(appointment.startTime)}
                {" - "}
                {formatTime(appointment.endTime)}
              </strong>
            </div>
          </div>
        ) : (
          <div className="message-box">
            No confirmed appointment found.
          </div>
        )}

        <Link
          to="/appointments"
          className="primary-button"
        >
          View My Appointments
        </Link>

        <Link
          to="/"
          className="secondary-link"
        >
          Back to Home
        </Link>
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